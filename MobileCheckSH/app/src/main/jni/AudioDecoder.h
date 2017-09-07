#ifndef AudioDecoder_h__
#define AudioDecoder_h__


//#include "proto.h"
#include "avcodec.h"
#define AUDIO_DECODE_SAMPLE 8000
#define AUDIO_DECODE_CHANEL 1
class AudioDecoder
{
public:
	AudioDecoder(void);
	~AudioDecoder(void);
	int start();		//<��ʼ��Ƶ����
	int stop(void);					//<ֹͣ��Ƶѹ��
	int decodeData(unsigned char *frameData,int frameSize);
	char	*audioBuf;				//<������Ƶ����
	int		audioSize;				//<������Ƶ����
private:
	AVCodecContext myAVcodec;
};
#endif // AudioDecoder_h__
