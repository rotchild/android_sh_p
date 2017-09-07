#ifndef CAMERA_H
#define CAMERA_H
#include <sys/socket.h>
#include "proto.h"
#include <linux/in.h>
#include <list>
#define BUFFERSIZE 2;
typedef struct tagFrameBuffer{
	int					dataLen;
	DWORD				timeStamp;
	int					frameType;					//0:video 1:audio
	char				frameBuf[65535*2];			//ʣ�෢������
}FrameBuffer;
#if !defined (STLPORT) || defined(_STLP_USE_NAMESPACES)
	using namespace std;
#endif

typedef list<FrameBuffer> FrameBufferList;
class Camera{
public:
	int width;
	int height;
	int framerate;
	int bitrate;
	unsigned int sign;
	int sockid;
	struct sockaddr_in server_addr;//�������ĵ�ַ�ṹ
 	int	HaveRevDownloadQuest;//=FALSEû���ܵ����������� ��TRUE�Ѿ��ܵ�����������
	int	connetFlag;
	FrameBufferList bufferList;
	int videoFrameSeq;
	int videoSequenceNum;
	int audioFrameSeq;
	int audioSequenceNum;
	short videoDescSize;
	BYTE videoDescData[256];
};
typedef struct tagRendStreamDesc
{
	StreamDesc	LocalStreamDesc;
	MediaDesc   ControlMediaDesc;
	MediaDesc   VideoMediaDesc;
	MediaDesc   AudioMediaDesc;
	MediaDesc   ExtendDesc1;
	MediaDesc   ExtendDesc2;
	MediaDesc   ExtendDesc3;
	MediaDesc   ExtendDesc4;
} RendStreamDesc;

typedef struct tagRenderDatabuff
{
	PayloadFormatHead	MediaHead;
	BYTE                MediaData[1024*2];
} RenderDatabuff;

#endif
