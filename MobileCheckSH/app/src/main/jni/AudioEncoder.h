#ifndef AudioEncoder_h__
#define AudioEncoder_h__
#include "avcodec.h"
typedef struct tag_audio_profile{
	enum CodecID codecID;
	unsigned int code_type;			//<编码方式
	unsigned int sample_rate;		//<采样率
	unsigned int channels;			//<声道 1单声道 2立体声
	unsigned int frame_size;		//<单位byte
	unsigned int bitspersample;		//<位数
	unsigned int duration;			//<采样时间间隔,单位ms
	unsigned char *encoded_frame;		//<数据
	unsigned int encodedSize;
}audio_profile;

#define STATE_PAUSE		1
#define STATE_RUN		0
class AudioEncoder
{

public:
	AudioEncoder();
	int start();
	int svc(void);
	int encode(unsigned char *data,int dataSize);
	audio_profile audio_setting;	//<视频参数
private:
	unsigned char	*audio_buf;				//<捕获视频缓冲
	AVCodecContext myAVcodec;
	int state;
private:
	int end_loop;
};
#endif // AudioEncoder_h__
