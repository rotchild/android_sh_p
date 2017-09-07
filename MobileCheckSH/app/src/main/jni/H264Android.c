#include <jni.h>
#include "common/common.h"+
#include "x264.h"
#include "jniLog.h"
typedef struct
{
	x264_param_t * param;
    x264_t *handle;
    x264_picture_t * picture;
    x264_nal_t  *nal;
} Encoder;


int Java_cx_mobilechecksh_mvideo_androidcamera_H264Encoder_CompressBegin(JNIEnv* env, jobject thiz,jint width, jint height,jint fps,jint bitrate,jbyteArray header) {
	Encoder * en = (Encoder *) malloc(sizeof(Encoder));
	int rt;
	en->param = (x264_param_t *) malloc(sizeof(x264_param_t));
	en->picture = (x264_picture_t *) malloc(sizeof(x264_picture_t));
	x264_param_default(en->param);
	//ultrafast superfast veryfast faster  fast  medium slow slower veryslow placebo
	x264_param_default_preset(en->param, "veryfast", "zerolatency");
	//x264_param_default_preset(en->param, "medium", "zerolatency");
	x264_param_apply_profile(en->param,"baseline");
    //x264_param_apply_profile(en->param,"high");
   // en->param->b_cabac = 1;

//	rt=x264_param_apply_profile(en->param,"main");
	en->param->i_threads=1;
   // en->param->i_threads=4;
	en->param->i_log_level = X264_LOG_NONE;
	en->param->i_width = width; //set frame width
	en->param->i_height = height; //set frame height
	en->param->rc.i_lookahead =0;

	en->param->i_fps_num =fps;
	en->param->i_fps_den = 1;
	// Intra refres:
	en->param->i_keyint_max = 2*fps;
	//en->param->i_scenecut_threshold = 0;
	//en->param->b_intra_refresh = 1;
	//Rate control:

	en->param->rc.i_rc_method=X264_RC_ABR;//参数i_rc_method表示码率控制，CQP(恒定质量)，CRF(恒定码率)，ABR(平均码率)
	en->param->rc.i_bitrate = bitrate;
	en->param->rc.i_vbv_max_bitrate = bitrate*1.2;
	//For streaming:
	en->param->b_repeat_headers = 0;
	en->param->b_annexb = 1;

	if ((en->handle = x264_encoder_open(en->param)) == 0) {
		return 0;
	}
	/* Create a new pic */
	x264_picture_alloc(en->picture, X264_CSP_NV12, en->param->i_width,
			en->param->i_height);

	//get pps&sps
    x264_nal_t *nal;
    int i_nal;
    x264_encoder_headers( en->handle, &nal, &i_nal );
	jbyte * headerData = (jbyte*)(*env)->GetByteArrayElements(env, header, 0);
	headerData[0]=0;
	jbyte *datapos=headerData;
	for(int i=0;i<2;i++){
		memcpy(datapos,nal[i].p_payload,nal[i].i_payload);
		headerData[0]+=nal[i].i_payload;
		headerData[i+1]=nal[i].i_payload;
		datapos=(headerData+headerData[0]);
	}
    (*env)->ReleaseByteArrayElements(env, header, headerData, 0);
	return (int) en;
}

int Java_cx_mobilechecksh_mvideo_androidcamera_H264Encoder_CompressEnd(JNIEnv* env, jobject thiz,jint handle)
{
	Encoder * en = (Encoder *) handle;
	if(en->picture)
	{
		x264_picture_clean(en->picture);
		free(en->picture);
		en->picture	= 0;
	}
	if(en->param)
	{
		free(en->param);
		en->param=0;
	}
	if(en->handle)
	{
		x264_encoder_close(en->handle);
	}
	free(en);
	return 0;
}
void dumpyuv(unsigned char *indata,int datalen,char *fname)
{
	FILE *fp=fopen(fname,"w+");
	fwrite(indata,datalen,1,fp);
	fclose(fp);
}
void NV21ToNV12(unsigned char *indata,int datalen){
//	static int count=0;
//	if (count==0){
//		dumpyuv(indata,datalen,"//sdcard//nv21.yuv");
//	}
	int wordlen=datalen/4;
	int wordleft=datalen-wordlen*4;
	unsigned int *worddata=(unsigned int*)indata;
	for (int i=0;i<wordlen;i++){
		int temp=*worddata;
		*worddata=((*worddata&0xff00ff00)>>8)+((*worddata&0x00ff00ff)<<8);
		worddata++;
	}
//	if (count==0){
//		dumpyuv(indata,datalen,"//sdcard//nv12.yuv");
//	}

}

jint Java_cx_mobilechecksh_mvideo_androidcamera_H264Encoder_CompressBuffer(JNIEnv* env, jobject thiz,jlong handle,
		jint type,jbyteArray in, jint insize,jbyteArray out)
{
	Encoder * en = (Encoder *) handle;
	x264_picture_t pic_out;

    int i_data=0;
	int nNal=-1;
	int result=0;
	int i=0,j=0;
	int nPix=0;

	jbyte * indata = (jbyte*)(*env)->GetByteArrayElements(env, in, 0);
	jbyte * outdata = (jbyte*)(*env)->GetByteArrayElements(env, out, 0);
	int frameSize=insize*2/3;
	NV21ToNV12(indata+frameSize,frameSize/2);
	memcpy(en->picture->img.plane[0],indata,insize);
	switch (type)
	{
	case 0:
		en->picture->i_type = X264_TYPE_P;
		break;
	case 1:
		en->picture->i_type = X264_TYPE_IDR;
		break;
	case 2:
		en->picture->i_type = X264_TYPE_I;
		break;
	default:
		en->picture->i_type = X264_TYPE_AUTO;
		break;
	}
	int framesize=x264_encoder_encode( en->handle, &(en->nal), &nNal, en->picture ,&pic_out);
    if( framesize <= 0 )
    {
        return 0;
    }
    memcpy(outdata, en->nal[0].p_payload+4, framesize-4);
    //char buff[255];
    //sprintf (buff,"frame nal=%d type=%d",nNal,en->nal[0].i_type);
    //LOGD(buff);
	//outdata[0]=en->nal[0].i_type;
    (*env)->ReleaseByteArrayElements(env, out, outdata, 0);
    (*env)->ReleaseByteArrayElements(env, in,  indata, 0);
	return framesize-4;
}


