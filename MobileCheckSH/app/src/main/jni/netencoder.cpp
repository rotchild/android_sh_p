#include <unistd.h>
#include "NetEncoder.h"
#include "camera.h"
#include <pthread.h>
#include <sys/socket.h>
#include <sys/endian.h>
#include <stdlib.h>
#include <errno.h>
#include <linux/tcp.h>
#include <arpa/inet.h>
#include <string.h>
#include <stdio.h>
#include "proto.h"
#include "jniLog.h"
#include "fcntl.h"
#include "AudioEncoder.h"
#include "AudioDecoder.h"
#define AUDIOPACKETMAX 2048
JavaVM* gVm = NULL;
JNIEnv* gEnv = NULL;
jobject gAudioDataObj;
jmethodID gAddAudioDataMethod;
jbyteArray gAudioData;
int audioPlayback = 0;
Camera camera;
pthread_t net_pid;
pthread_t test_pid;
int audioSocket;
bool bUdp = true;
int workFlag = 0;
unsigned short serverPort = 9188;
RendStreamDesc m_LocalStreamDesc;
pthread_mutex_t f_lock;
int start = 0;
AudioEncoder audioEncoder;
AudioDecoder audioDecoder;
char buff[255];

void CreateCameraDesc();
int Java_cx_mobilechecksh_mvideo_androidcamera_AudioPlaybak_registAudioPlay(JNIEnv* env,	jobject obj) {
	jclass objClass;
	env->GetJavaVM(&gVm);
	gAudioDataObj = env->NewGlobalRef(obj);
//	audioDecoder.audioSize=2034;
//	env->SetByteArrayRegion(gAudioData,0,audioDecoder.audioSize,(jbyte*)audioDecoder.audioBuf);
//	gEnv->CallVoidMethod(gAudioDataObj,gAddAudioDataMethod,audioDecoder.audioSize,gAudioData);
	audioPlayback = 1;
	return 0;
}
int Java_cx_mobilechecksh_mvideo_androidcamera_NetCameraService_registAudioPlay(JNIEnv* env,
		jobject obj) {
	jclass objClass;
	env->GetJavaVM(&gVm);
	gAudioDataObj = env->NewGlobalRef(obj);
	audioPlayback = 1;
	return 0;
}
JNIEXPORT jint JNICALL Java_cx_mobilechecksh_mvideo_androidcamera_NetEncoder_setVideoFormat(JNIEnv *, jobject, jint width, jint height, jint fps, jint bitrate) {
	camera.width = width;
	camera.height = height;
	camera.framerate = fps;
	camera.bitrate = bitrate;
	CreateCameraDesc();
	return 0;
}
JNIEXPORT jint JNICALL Java_cx_mobilechecksh_mvideo_androidcamera_NetEncoder_setSign(JNIEnv *, jobject, jint sign) {
	camera.sign = sign;
	CreateCameraDesc();
	return 0;
}
void *netThread(void *arg);
void *recvThread(void *arg);
JNIEXPORT jint JNICALL Java_cx_mobilechecksh_mvideo_androidcamera_NetEncoder_startWork(
		JNIEnv *, jobject, jboolean isUdp) {
	unsigned char b = isUdp;
	if(b) {
		bUdp=true;
	}else {
		bUdp=false;
	}

	if (workFlag == 1) { //已经开始
		LOGE("netencoder work started yet");
		return 0;
	}
	workFlag = 1;
	//if (start==0)
	{
		int rt = pthread_mutex_init(&f_lock, NULL);
		if (rt != 0)
			return 0;
	}
	pthread_create(&net_pid, NULL, netThread, NULL);
	audioSocket = socket(AF_INET, SOCK_DGRAM, 0);
	audioEncoder.start();
	audioDecoder.start();
	pthread_create(&net_pid, NULL, recvThread, NULL);

	LOGE("netencoder work start");
	return 0;
}
void CloseTrans();
JNIEXPORT jint JNICALL Java_cx_mobilechecksh_mvideo_androidcamera_NetEncoder_stopWork(
		JNIEnv *, jobject) {
	if (workFlag == 0) {
		return 0; //已经停止
	}
	CloseTrans();
	workFlag = 0;
	pthread_mutex_destroy(&f_lock);
	return 0;
}

JNIEXPORT jint JNICALL Java_cx_mobilechecksh_mvideo_androidcamera_NetEncoder_setServer(
		JNIEnv *env, jobject, jstring ip) {
	LOGE("set server");
	const char *c_ip;
	c_ip = env->GetStringUTFChars(ip, 0);
	camera.server_addr.sin_family = AF_INET;
	camera.server_addr.sin_addr.s_addr = inet_addr(c_ip);
	camera.server_addr.sin_port = htons(serverPort);
	env->ReleaseStringUTFChars(ip, c_ip);
	return 0;
}

void CloseTrans() {
	sprintf(buff, "closetrans");
	LOGD(buff);
	if (camera.sockid == 0)
		return;
	close(camera.sockid);
	camera.sockid = 0;
	camera.connetFlag = false;
	pthread_mutex_lock(&f_lock);
	camera.bufferList.clear();
	pthread_mutex_unlock(&f_lock);
}

int reConnect() {
	if (camera.sockid > 0) {
		CloseTrans();
	}

	//连接方式 TCP
	char msg[255];
	camera.sockid = socket(AF_INET, SOCK_STREAM, 0);
	if (camera.sockid <= 0) {
		sprintf(msg, "socket error %d\n", errno);
		LOGE(msg);
		return 0;
	}

	//设置socket的缓冲区大小64k
	int size = 128 * 1024;
	int ret = setsockopt(camera.sockid, SOL_SOCKET, SO_SNDBUF,
			(const char *) &size, sizeof(int));
	if (ret == -1) {
		LOGE("Couldn't setsockopt(SO_SNDBUF)\n");
		return 0;
	}

	int flag = 1;
	ret = setsockopt(camera.sockid, IPPROTO_TCP, TCP_NODELAY, (char *) &flag,
			sizeof(flag));
	if (ret == -1) {
		LOGE("Couldn't setsockopt(TCP_NODELAY)\n");
		return 0;
	}

	struct timeval timeout = { 10, 0 }; //3s
	ret = setsockopt(camera.sockid, SOL_SOCKET, SO_SNDTIMEO, &timeout,
			sizeof(timeout));
	if (ret == -1) {
		LOGE("Couldn't setsockopt(SO_SNDTIMEO)\n");
		return 0;
	}

	ret = setsockopt(camera.sockid, SOL_SOCKET, SO_RCVTIMEO, &timeout,
			sizeof(timeout));
	if (ret == -1) {
		LOGE("Couldn't setsockopt(SO_RCVTIMEO)\n");
		return 0;
	}

	int flags = fcntl(camera.sockid, F_GETFL, 0);
	flags &= ~O_NONBLOCK;
	fcntl(camera.sockid, F_SETFL, flags);

	ret = connect(camera.sockid, (struct sockaddr *) &(camera.server_addr),
			sizeof(struct sockaddr_in));
	if (ret < 0) {
		LOGE("connect fail\n");
		CloseTrans();
		return 0;
	}

	camera.connetFlag = true;
	LOGD("conneted\n");
	return 0;
}

int UdpConnect() {
	if (camera.sockid > 0) {
		CloseTrans();
	}

	//连接方式 UDP
	char msg[255];
	camera.sockid = socket(AF_INET, SOCK_DGRAM, 0);
	if (camera.sockid <= 0) {
		sprintf(msg, "socket error %d\n", errno);
		LOGE(msg);
		return 0;
	}

	int ret;
	//设置socket的缓冲区大小32k
	int size = 32 * 1024;
	ret = setsockopt(camera.sockid, SOL_SOCKET, SO_SNDBUF, (const char *) &size,
			sizeof(int));
	if (ret == -1) {
		LOGE("Couldn't setsockopt(SO_SNDBUF)\n");
		return 0;
	}
	struct sockaddr_in tempAddr;
	tempAddr.sin_family = AF_INET;
	tempAddr.sin_addr.s_addr = INADDR_ANY;
	tempAddr.sin_port = 0;

	int flags = fcntl(camera.sockid, F_GETFL, 0);
//	sprintf (buff,"sock opt =%d",flags);
//	LOGD(buff);
	fcntl(camera.sockid, F_SETFL, flags | O_NOFOLLOW | O_NOATIME);

	ret = bind(camera.sockid, (struct sockaddr *) &(tempAddr),
			sizeof(struct sockaddr_in));
	if (ret < 0) {
		LOGE("connect fail\n");
		CloseTrans();
		return 0;
	}

	camera.connetFlag = true;
	return 0;
}

unsigned char changeFlag = 0;
void CreateCameraDesc() {

	//生成流描述
	m_LocalStreamDesc.LocalStreamDesc.dwCommandHead.bCommandVersion =
			COMMANDVERSION01;
	m_LocalStreamDesc.LocalStreamDesc.dwCommandHead.bCommandType =
			COMMAND_LIVESTREAMDESC;
	m_LocalStreamDesc.LocalStreamDesc.dwCommandHead.wCommandLen =
			htons(sizeof(RendStreamDesc));
	m_LocalStreamDesc.LocalStreamDesc.wClientID = 0;
	m_LocalStreamDesc.LocalStreamDesc.dwCameraSign = htonl(camera.sign);
	m_LocalStreamDesc.LocalStreamDesc.bMediaNumber = 3;
	m_LocalStreamDesc.LocalStreamDesc.bChangeFlag = changeFlag++;

	//状态控制流描述
	m_LocalStreamDesc.ControlMediaDesc.bBitrate = 0;
	m_LocalStreamDesc.ControlMediaDesc.bMediaID = 0;
	m_LocalStreamDesc.ControlMediaDesc.bPayload = 255; //0;
	m_LocalStreamDesc.ControlMediaDesc.bQualityClass = 2; //1;

	m_LocalStreamDesc.VideoMediaDesc.bBitrate = 0;
	m_LocalStreamDesc.VideoMediaDesc.bMediaID = 1; //视频为1 音频为2
	m_LocalStreamDesc.VideoMediaDesc.bPayload = VT_H264;
	m_LocalStreamDesc.VideoMediaDesc.bQualityClass = 1; //i p都要填1 只要i填0
	VideoMediaInfo myVideoMediaInfo;
	myVideoMediaInfo.bFrameRate = camera.framerate; //25;
	myVideoMediaInfo.bHeight = camera.height / 8; //96/8;
	myVideoMediaInfo.bWidth = camera.width / 8; //128/8;
	myVideoMediaInfo.bExtend = 1;
	myVideoMediaInfo.wOffset =
			htons((BYTE*)(&m_LocalStreamDesc.ExtendDesc1)-(BYTE*)(&m_LocalStreamDesc));
	myVideoMediaInfo.wSize = htons(camera.videoDescSize);
	memcpy(&m_LocalStreamDesc.VideoMediaDesc.pszMediaSpcialInfo,
			&myVideoMediaInfo, sizeof(VideoMediaInfo));

	m_LocalStreamDesc.AudioMediaDesc.bBitrate = 0;
	m_LocalStreamDesc.AudioMediaDesc.bMediaID = 2; //视频为1 音频为2
	m_LocalStreamDesc.AudioMediaDesc.bPayload = AT_ADPCM;
	m_LocalStreamDesc.AudioMediaDesc.bQualityClass = 0; //i p都要填1 只要i填0
	AudioMediaInfo myAudioMediaInfo;
	myAudioMediaInfo.dwSamplepersecond =
			htonl(audioEncoder.audio_setting.sample_rate); //25;
	myAudioMediaInfo.wBitsPerSample =
			htons(audioEncoder.audio_setting.bitspersample);
	myAudioMediaInfo.wChannel = htons(audioEncoder.audio_setting.channels);
	memcpy(&m_LocalStreamDesc.AudioMediaDesc.pszMediaSpcialInfo,
			&myAudioMediaInfo, sizeof(myAudioMediaInfo));

	memcpy(&m_LocalStreamDesc.ExtendDesc1, camera.videoDescData,
			camera.videoDescSize);
}

unsigned int GetTickCount() {
	struct timeval tv;
	if (gettimeofday(&tv, NULL) != 0)
		return 0;
	return (tv.tv_sec * 1000) + (tv.tv_usec / 1000);
}
int checkWrite(int sockfd) {
	fd_set fdW;
	FD_ZERO(&fdW);
	FD_SET(sockfd, &fdW);
	struct timeval timeout = { 0, 0 };
	int rt = select(sockfd + 1, NULL, &fdW, NULL, &timeout);
	if (rt == -1)
		return -1;
	if (rt == 0) {
		return 0;
	}
	return 1;
}

long UdpSendData(int sockid, struct sockaddr_in server_addr, char *lpData,
		int dwLength) ///<-1:失败 0：阻塞 >0:正常发送
		{
	if (sockid == 0) {
		LOGD("send error");
		return -1;
	}

	int len = 0;
	int sockaddrlen = sizeof(struct sockaddr_in);
	DWORD start = GetTickCount();
	len = sendto(sockid, lpData, dwLength, 0, (struct sockaddr *) &server_addr,
			sockaddrlen);
	sprintf(buff, "send time %u", GetTickCount() - start);
	if (GetTickCount() - start > 5)
		LOGD(buff);
	if (len != dwLength)
		LOGD("send error");
	return 1;
}
//int sendtoTimeOut(int sockid, const void *lpData, size_t dwLength, int flag, const struct sockaddr * server_addr, socklen_t sockaddrlen,int timeout){
//	sendto();
//}
long SendData(int sockid, struct sockaddr_in server_addr, char *lpData,
		int dwLength) ///<-1:失败 0：阻塞 >0:正常发送
		{
	if (sockid == 0) {
		return -1;
	}

	int len = 0;
	int sockaddrlen = sizeof(struct sockaddr_in);
	DWORD start = GetTickCount();
	len = sendto(sockid, lpData, dwLength, 0, (struct sockaddr *) &server_addr,
			sockaddrlen);
	if (len != dwLength) {
		camera.connetFlag = false;
		sprintf(buff, "send data error time=%u", GetTickCount() - start);
		LOGD(buff);
		CloseTrans();
		return -1;
	}
	return 1;
}

int SendDesc() {
	int tmpSize = sizeof(RendStreamDesc);
	//发流描述
	int rt;
	rt = SendData(camera.sockid, camera.server_addr,
			(char *) &m_LocalStreamDesc, tmpSize);
	return rt;
}
#define VideoDataBlockSize 512
#include <math.h>
int sentPack = 0;
int sendFrame() {
	if (camera.bufferList.empty()) {
		return 0;
	}
	//构造头部
	FrameBuffer frameBuffer = (*(camera.bufferList.begin()));
	pthread_mutex_lock(&f_lock);
	camera.bufferList.pop_front();
	pthread_mutex_unlock(&f_lock);

	int tmpLen = frameBuffer.dataLen; //EncoderDataLen+4个字节的时间字幕+4个字节的数据长度;

	int tmpSendPos = 0;
	int tmpPackSize = 0;

	int tmpPackIndex = 0;
	int tmpPackNum = 0;
	tmpPackNum = (int) ceil((float) tmpLen / VideoDataBlockSize);

	//数据包头
	RenderDatabuff NetSendData;
	NetSendData.MediaHead.dwCommandHead.bCommandVersion = COMMANDVERSION01;
	NetSendData.MediaHead.dwCommandHead.bCommandType = COMMAND_ENCODER_DATA;
	NetSendData.MediaHead.bitChangeFlag = 0;
	NetSendData.MediaHead.bitFrameClass = frameBuffer.frameType; //0;//FrameType;//音频不用填 视频要填 0-I 1-P
	NetSendData.MediaHead.bitFrameSeq = camera.videoFrameSeq;
	NetSendData.MediaHead.bitMediaID = 1; //视频为1 音频为2
	NetSendData.MediaHead.dwCameraSign = htonl(camera.sign);
	NetSendData.MediaHead.bPackNum = tmpPackNum; //没有分包 视频要填
	NetSendData.MediaHead.dwTimestamp = htonl(frameBuffer.timeStamp);
	NetSendData.MediaHead.dwFirstDWORD =
			htonl(NetSendData.MediaHead.dwFirstDWORD);

	while (tmpLen > 0) {
		NetSendData.MediaHead.bPacketIndex = tmpPackIndex; //没有分包 视频要填
		NetSendData.MediaHead.wSequenceNum = htons(camera.videoSequenceNum);

		if (tmpLen > VideoDataBlockSize) {
			memcpy(NetSendData.MediaData, frameBuffer.frameBuf + tmpSendPos,
					VideoDataBlockSize);
			tmpLen = tmpLen - VideoDataBlockSize;
			tmpSendPos = tmpSendPos + VideoDataBlockSize;
			NetSendData.MediaHead.dwCommandHead.wCommandLen =
					htons(VideoDataBlockSize+sizeof(PayloadFormatHead));
		} else {
			memcpy(NetSendData.MediaData, frameBuffer.frameBuf + tmpSendPos,
					tmpLen);
			NetSendData.MediaHead.dwCommandHead.wCommandLen =
					htons(tmpLen+sizeof(PayloadFormatHead));
			tmpLen = 0;
			tmpSendPos = tmpSendPos + tmpLen;
		}
		int rt = SendData(camera.sockid, camera.server_addr,
				(char *) &NetSendData,
				ntohs(NetSendData.MediaHead.dwCommandHead.wCommandLen));
		if (rt < 0) {
			return -1;
		}
		camera.videoSequenceNum++;
		tmpPackIndex++;
	}
	camera.videoFrameSeq++;
	return 1;
}

#define TIMESLICE 50
int UdpSendFrame() {
	if (camera.bufferList.empty()) {
		return 0;
	}
	//计算本次可以发送包数量，将所有的包数量分为50个时隙，计算最多可以发几个包
	pthread_mutex_lock(&f_lock);
	FrameBuffer frameBuffer = (*(camera.bufferList.begin()));
	camera.bufferList.pop_front();
	pthread_mutex_unlock(&f_lock);

	int tmpLen = frameBuffer.dataLen; //EncoderDataLen+4个字节的时间字幕+4个字节的数据长度;
	int tmpSendPos = 0;
	int tmpPackIndex = 0;
	int tmpPackNum = 0;
	tmpPackNum = (int) ceil((float) tmpLen / VideoDataBlockSize);

	//数据包头
	RenderDatabuff NetSendData;
	NetSendData.MediaHead.dwCommandHead.bCommandVersion = COMMANDVERSION01;
	NetSendData.MediaHead.dwCommandHead.bCommandType = COMMAND_ENCODER_DATA;
	NetSendData.MediaHead.bitChangeFlag = 0;
	NetSendData.MediaHead.bitFrameClass = frameBuffer.frameType; //0;//FrameType;//音频不用填 视频要填 0-I 1-P
	NetSendData.MediaHead.bitFrameSeq = camera.videoFrameSeq;
	NetSendData.MediaHead.bitMediaID = 1; //视频为1 音频为2
	NetSendData.MediaHead.dwCameraSign = htonl(camera.sign);
	NetSendData.MediaHead.bPackNum = tmpPackNum; //没有分包 视频要填
	NetSendData.MediaHead.dwTimestamp = htonl(frameBuffer.timeStamp);
	NetSendData.MediaHead.dwFirstDWORD =
			htonl(NetSendData.MediaHead.dwFirstDWORD);

	while (tmpLen > 0) {
		NetSendData.MediaHead.bPacketIndex = tmpPackIndex; //没有分包 视频要填
		NetSendData.MediaHead.wSequenceNum = htons(camera.videoSequenceNum);

		if (tmpLen > VideoDataBlockSize) {
			memcpy(NetSendData.MediaData, frameBuffer.frameBuf + tmpSendPos,
					VideoDataBlockSize);
			tmpLen = tmpLen - VideoDataBlockSize;
			tmpSendPos = tmpSendPos + VideoDataBlockSize;
			NetSendData.MediaHead.dwCommandHead.wCommandLen =
					htons(VideoDataBlockSize+sizeof(PayloadFormatHead));
		} else {
			//memcpy(NetSendData.MediaData,frameBuffer.frameBuf+tmpSendPos,tmpLen);
			memcpy(NetSendData.MediaData, frameBuffer.frameBuf + tmpSendPos,
					VideoDataBlockSize);
			NetSendData.MediaHead.dwCommandHead.wCommandLen =
					htons(tmpLen+sizeof(PayloadFormatHead));
			tmpLen = 0;
			tmpSendPos = tmpSendPos + tmpLen;
		}
		UdpSendData(camera.sockid, camera.server_addr, (char *) &NetSendData,
				ntohs(NetSendData.MediaHead.dwCommandHead.wCommandLen));
		camera.videoSequenceNum++;
		tmpPackIndex++;
	}
	camera.videoFrameSeq++;
	return 1;
}

int sendAudioData(int iSize, unsigned char *data) {
	RenderDatabuff NetSendData;
	NetSendData.MediaHead.dwCommandHead.bCommandVersion = COMMANDVERSION01;
	NetSendData.MediaHead.dwCommandHead.bCommandType = COMMAND_ENCODER_DATA;
	NetSendData.MediaHead.bitChangeFlag = 0;
	NetSendData.MediaHead.bitFrameClass = 0;
	NetSendData.MediaHead.bitFrameSeq = camera.audioFrameSeq++; //音频不用填 视频要填
	NetSendData.MediaHead.bitMediaID = 2; //视频为1 音频为2
	NetSendData.MediaHead.dwCameraSign = htonl(camera.sign);
	NetSendData.MediaHead.bPackNum = 1; //没有分包 视频要填
	NetSendData.MediaHead.dwTimestamp = htonl(GetTickCount());
	NetSendData.MediaHead.dwFirstDWORD = htonl(
			NetSendData.MediaHead.dwFirstDWORD);

	NetSendData.MediaHead.bPacketIndex = 0;
	NetSendData.MediaHead.wSequenceNum = htons(camera.audioSequenceNum++);
	NetSendData.MediaHead.dwCommandHead.wCommandLen = htons(
			iSize + sizeof(PayloadFormatHead)+4);
	int loadSize = iSize + 4;
	memcpy(NetSendData.MediaData, &loadSize, 4);
	memcpy(NetSendData.MediaData + 4, data, iSize);
	sendto(audioSocket, (char*) &NetSendData,
			ntohs(NetSendData.MediaHead.dwCommandHead.wCommandLen), 0,
			(sockaddr *) &camera.server_addr, sizeof(camera.server_addr));
	return 0;
}

int encoderAudioFrame(int size, unsigned char* data) {
	audioEncoder.encode(data, size);

//	if (filenum < 100) {
//		char tempstring[50];
//		sprintf(tempstring, "/sdcard/webcamera/netAudio/%d.pcm", filenum);
//		netfp = fopen(tempstring, "w");
//		fwrite(audioEncoder.audio_setting.encoded_frame, sizeof(unsigned char),
//				audioEncoder.audio_setting.encodedSize, netfp);
//		filenum++;
//		fclose(netfp);
//	}

	sendAudioData(audioEncoder.audio_setting.encodedSize,
			audioEncoder.audio_setting.encoded_frame);
	return 0;
}

unsigned int audioPacket = 0;
unsigned int audioCheck = 0;
int doAdpcm(IpcReport *controlData) {
	if (gEnv == NULL) {
		return 0;
	}
	audioDecoder.decodeData((unsigned char *) controlData->pszReportInfo,
			htons(controlData->wReportSize));
	gEnv->SetByteArrayRegion(gAudioData, 0, audioDecoder.audioSize,
			(jbyte*) audioDecoder.audioBuf);
	gEnv->CallVoidMethod(gAudioDataObj, gAddAudioDataMethod,
			audioDecoder.audioSize, gAudioData);
	return 0;
}

int OnControlData(EncoderControl *controlData) {
	char buff[255];
	pIpcReport data;
	data = (pIpcReport) controlData->bControlData;
	if (htonl(controlData->dwCameraSign) != camera.sign)
		return 0;
	switch (data->bReportType) {
	case CONTROL_ADPCM_AUDIO:
		data->wReportSize =
				htons(htons(controlData->dwCommandHead.wCommandLen)-20);
		doAdpcm(data);
		break;
	default:
		LOGD("Unkown control cmd %");
		break;
	}
	return 0;
}

int OnCameraData(char* buf) {
	//LOGD("OnCameraData");
	CommandHead *cmdhead = (CommandHead *) buf;
	switch (cmdhead->bCommandVersion) {
	case COMMANDVERSION01: //当前版本
		switch (cmdhead->bCommandType) {
		case COMMAND_DOWNLOADLIVE:
			break;
		case COMMAND_ENCODER_CONTROL:
			return OnControlData((EncoderControl *) buf);
			break;
//		case COMMAND_CAPTURE_REQUEST:
//			return OnCapture((CaptureRequest *)cmddata);
		default:
			LOGD( "unkown data type %x\n");
			return 0;
			break;
		}
		break;
	default:
		LOGD( "version error \n");
		break;
	}
	return 0;
}
void dumpMessage(void *data, int size) {
	char buff[3000];
	buff[0] = 0;
	for (int i = 0; i < size; i++) {
		sprintf(buff + strlen(buff), "%2x ", ((char*) data)[i]);
	}
	LOGD(buff);
}
void *udprecvThread(void *arg) {
	MaxPayload m_inputCMD;
	EncoderControl payLoad;
	int m_lastSendCmdPos = 0;
	char buff[255];
	LOGD("recvThread");
	sprintf(buff, "gVm=%x\n", gVm);
	LOGD(buff);
	if (gVm != NULL) {
		gVm->AttachCurrentThread(&gEnv, NULL);
		jclass objClass = gEnv->GetObjectClass(gAudioDataObj);
		if (objClass == NULL)
			return NULL;

		gAddAudioDataMethod = gEnv->GetMethodID(objClass, "addAudioData",
				"(I[B)V");
		if (gAddAudioDataMethod == NULL)
			return NULL;
		gAudioData = gEnv->NewByteArray(AUDIOPACKETMAX);

	}

	while (workFlag) {
		if (camera.sockid <= 0 || camera.connetFlag == false) {
			usleep(20 * 1000);
			continue;
		}
		fd_set fdR;
		FD_ZERO(&fdR);
		FD_SET(camera.sockid, &fdR);
		struct timeval timeout = { 5, 0 };
		int rt = select(camera.sockid + 1, &fdR, NULL, NULL, &timeout);
		if (rt < 0) {
			camera.connetFlag = false;
			continue;
		}
		if (rt == 0)
			continue; //超时
		if (FD_ISSET(camera.sockid,&fdR)) {
			//尽量读入数据
			int commandLen;
			rt = recv(camera.sockid, &m_inputCMD, sizeof(m_inputCMD), 0);
			if (rt <= 0) {
				camera.connetFlag = false;
				sprintf(buff, "recv head error");
				LOGD(buff);
				continue;
			}
			OnCameraData((char*) (&m_inputCMD));
		}
	}
	audioDecoder.stop();
	if (gVm != NULL) {
		gEnv->DeleteGlobalRef(gAudioDataObj);
		gVm->DetachCurrentThread();
		gAudioData = NULL;
	}
}
void *recvThread(void *arg) {
	MaxPayload m_inputCMD;
	EncoderControl payLoad;
	int m_lastSendCmdPos = 0;
	char buff[255];
	LOGD("recvThread");
	sprintf(buff, "gVm=%x\n", gVm);
	LOGD(buff);
	if (gVm != NULL) {
		gVm->AttachCurrentThread(&gEnv, NULL);
		jclass objClass = gEnv->GetObjectClass(gAudioDataObj);
		if (objClass == NULL)
			return NULL;

		gAddAudioDataMethod = gEnv->GetMethodID(objClass, "addAudioData",
				"(I[B)V");
		if (gAddAudioDataMethod == NULL)
			return NULL;
		gAudioData = gEnv->NewByteArray(AUDIOPACKETMAX);

	}

	while (workFlag) {
		if (camera.sockid <= 0 || camera.connetFlag == false) {
			usleep(20 * 1000);
			continue;
		}
		fd_set fdR;
		FD_ZERO(&fdR);
		FD_SET(camera.sockid, &fdR);
		struct timeval timeout = { 5, 0 };
		int rt = select(camera.sockid + 1, &fdR, NULL, NULL, &timeout);
		if (rt < 0) {
			camera.connetFlag = false;
			sprintf(buff, "recv select error");
			LOGD(buff);
			continue;
		}
		if (rt == 0)
			continue; //超时
		if (FD_ISSET(camera.sockid,&fdR)) {
			//尽量读入数据
			char recvbuff[532 * 3];
			int commandLen;
			memset(recvbuff, 0, sizeof(recvbuff));
			int recvSize = min(sizeof(recvbuff),
					sizeof(MaxPayload) - m_lastSendCmdPos); //防止溢出
			rt = recv(camera.sockid, recvbuff, recvSize, 0);
			if (rt <= 0) {
				camera.connetFlag = false;
				sprintf(buff, "recv head error");
				LOGD(buff);
				continue;
			}
			memcpy((char *) (&m_inputCMD) + m_lastSendCmdPos, recvbuff, rt);
			commandLen = htons(m_inputCMD.payloadHead.dwCommandHead.wCommandLen);
			m_lastSendCmdPos += rt;
			while (m_lastSendCmdPos > sizeof(CommandHead)
					&& m_lastSendCmdPos >= commandLen) {
				if (commandLen < sizeof(CommandHead)
						|| commandLen > (sizeof(MaxPayload) - sizeof(int))) {
					LOGD("Error:commandLen error");
					camera.connetFlag = false;
					continue;
				}
				OnCameraData((char*) (&m_inputCMD));
				memcpy((char*) (&m_inputCMD),
						(char*) (&m_inputCMD) + commandLen,
						m_lastSendCmdPos - commandLen); //剩下的搬到头上去
				m_lastSendCmdPos -= commandLen;
				commandLen =
						htons(m_inputCMD.payloadHead.dwCommandHead.wCommandLen);
			}
		}

//		if (FD_ISSET(camera.sockid,&fdR)){
//			//读入头
//			rt=recv(camera.sockid,&payLoad.dwCommandHead,sizeof(CommandHead),0);
//			if (rt!=sizeof(CommandHead)){
//				camera.connetFlag=false;
//				sprintf (buff,"recv head error");
//				LOGD(buff);
//				continue;
//			}
//			int dataSize=htons(payLoad.dwCommandHead.wCommandLen)-sizeof(CommandHead);
//			if (dataSize>MAXPAYLOADDATASIZE) continue;
//			rt=recv(camera.sockid,&(payLoad.dwCameraSign),dataSize,0);
////			dumpMessage(&payLoad,rt+sizeof(CommandHead));
//			if (rt!=dataSize){
//				camera.connetFlag=false;
//				sprintf (buff,"recv body error %d dataSize=%d",rt,dataSize);
//				LOGD(buff);
//				continue;
//			}
//			OnCameraData((char*)(&payLoad));
//		}
	}
	audioDecoder.stop();
	if (gVm != NULL) {
		gEnv->DeleteGlobalRef(gAudioDataObj);
		gVm->DetachCurrentThread();
		gAudioData = NULL;
	}
}

void TcpWorker() {
	DWORD dwStartTimes = 0;
	camera.connetFlag = false;
	while (workFlag) {
		int rt = 0;
		if (!camera.connetFlag) {
			sprintf(buff, "reConnect");
			LOGD(buff);
			reConnect();
			if (camera.connetFlag) {
				CreateCameraDesc();
				SendDesc();
			} else {
				usleep(6000 * 1000);
			}
			continue;
		}
		if ((GetTickCount() - dwStartTimes) > 5000) { //每5s发送一次流描述和状态流//准备改为连接时发送一次
			dwStartTimes = GetTickCount();
			SendDesc();
		}
		if (!camera.bufferList.empty()) {
			rt = sendFrame();
		} else {
			usleep(10 * 1000);
		}
	}
	CloseTrans();
}

void UdpWorker() {
	DWORD dwStartTimes = 0;
	while (workFlag) {
		int rt = 0;
		if (!camera.connetFlag) {
			sprintf(buff, "reConnect");
			LOGD(buff);
			UdpConnect();
			if (camera.connetFlag) {
				CreateCameraDesc();
				SendDesc();
			}
			if (camera.connetFlag==false) {
				usleep(6000 * 1000);
			}
			continue;
		}
		if ((GetTickCount() - dwStartTimes) > 5000) { //每5s发送一次流描述和状态流//准备改为连接时发送一次
			dwStartTimes = GetTickCount();
			SendDesc();
		}
		if (!camera.bufferList.empty()) {
			rt = UdpSendFrame();
		}
		usleep(1000 / TIMESLICE * 1000);
	}
	CloseTrans();
}
void *netThread(void *arg) {
	LOGD("NetThread begin\n");
	if(!bUdp){
		LOGE("now tcping");
		TcpWorker();
	}else{
		LOGE("now udping");
		UdpWorker();
	}

	return NULL;
}
int organizeVideoList() {
	pthread_mutex_lock(&f_lock);
	if (camera.bufferList.size() < camera.framerate * 3) {
		pthread_mutex_unlock(&f_lock);
		return 0;
	}
	FrameBufferList::iterator iterLost, iterIframe;
	iterIframe = camera.bufferList.begin();
	iterLost = iterIframe;
	iterIframe++;
	for (; iterIframe != camera.bufferList.end(); iterIframe++) {
		if (iterIframe->frameType == 0) { //found
			camera.bufferList.erase(iterLost);
			break;
		}
		iterLost = iterIframe;
	}
	pthread_mutex_unlock(&f_lock);
	return 0;
}

//int addFrame(int type, int size, signed char* data) {
//	FrameBuffer tempFrame;
//	tempFrame.dataLen = size + 8;
//	if (tempFrame.dataLen>sizeof(tempFrame.frameBuf)) {
//		LOGD("two big");
//		return 0;
//	}
//	tempFrame.frameType = type;
//	tempFrame.timeStamp = GetTickCount();
//	int pos = 0;
//	DWORD timeStamp = htonl(tempFrame.timeStamp);
//	memcpy(tempFrame.frameBuf + pos, &timeStamp, 4);
//	pos += 4;
//	DWORD dwSize = htonl(size + 8);
//	memcpy(tempFrame.frameBuf + pos, &dwSize, 4);
//	pos += 4;
//	memcpy(tempFrame.frameBuf + pos, data, size);
//	pthread_mutex_lock(&f_lock);
//	camera.bufferList.push_back(tempFrame);
//	pthread_mutex_unlock(&f_lock);
//	return 0;
//}

int addFrame(int type, int size, signed char* data) {
	organizeVideoList();
	FrameBuffer tempFrame;
	tempFrame.dataLen = size + 8;
	if (tempFrame.dataLen > sizeof(tempFrame.frameBuf))
		LOGD("two big");
	tempFrame.frameType = type;
	tempFrame.timeStamp = GetTickCount();
	int pos = 0;
	DWORD timeStamp = htonl(tempFrame.timeStamp);
	memcpy(tempFrame.frameBuf + pos, &timeStamp, 4);
	pos += 4;
	DWORD dwSize = htonl(size + 8);
	memcpy(tempFrame.frameBuf + pos, &dwSize, 4);
	pos += 4;
	memcpy(tempFrame.frameBuf + pos, data, size);
	pthread_mutex_lock(&f_lock);
	camera.bufferList.push_back(tempFrame);
	pthread_mutex_unlock(&f_lock);
	return 0;
}
JNIEXPORT jint JNICALL Java_cx_mobilechecksh_mvideo_androidcamera_NetEncoder_addFrame(
		JNIEnv *env, jobject obj, jint frameType, jint frameLen,
		jbyteArray framedata) {
	signed char *data = env->GetByteArrayElements(framedata, 0);
	addFrame(frameType, frameLen, data);
	env->ReleaseByteArrayElements(framedata, data, 0);
	return 0;
}

JNIEXPORT jint JNICALL Java_cx_mobilechecksh_mvideo_androidcamera_NetEncoder_addAudioFrame(
		JNIEnv *env, jobject obj, jint frameSize, jbyteArray framedata) {
	signed char *data = env->GetByteArrayElements(framedata, 0);
	//int size=env->GetArrayLength(framedata);
	encoderAudioFrame(frameSize, (unsigned char*) data);
	env->ReleaseByteArrayElements(framedata, data, 0);
	return 0;
}

JNIEXPORT jint JNICALL Java_cx_mobilechecksh_mvideo_androidcamera_NetEncoder_setVideoDesc(
		JNIEnv *env, jobject obj, jint descSize, jbyteArray descData) {
	signed char *data = env->GetByteArrayElements(descData, 0);
	camera.videoDescSize = descSize;
	memcpy(camera.videoDescData, data, descSize);
	env->ReleaseByteArrayElements(descData, data, 0);
	CreateCameraDesc();
	return 0;
}

