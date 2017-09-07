#ifndef AudioEncoder_h__
#define AudioEncoder_h__
#include "avcodec.h"
typedef struct tag_audio_profile{
	enum CodecID codecID;
	unsigned int code_type;			//<���뷽ʽ
	unsigned int sample_rate;		//<������
	unsigned int channels;			//<���� 1������ 2������
	unsigned int frame_size;		//<��λbyte
	unsigned int bitspersample;		//<λ��
	unsigned int duration;			//<����ʱ����,��λms
	unsigned char *encoded_frame;		//<����
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
	audio_profile audio_setting;	//<��Ƶ����
private:
	unsigned char	*audio_buf;				//<������Ƶ����
	AVCodecContext myAVcodec;
	int state;
private:
	int end_loop;
};
#endif // AudioEncoder_h__
