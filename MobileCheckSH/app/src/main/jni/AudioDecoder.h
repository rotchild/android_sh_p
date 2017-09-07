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
	int start();		//<¿ªÊ¼ÒôÆµ½âÂë
	int stop(void);					//<Í£Ö¹ÒôÆµÑ¹Ëõ
	int decodeData(unsigned char *frameData,int frameSize);
	char	*audioBuf;				//<²¶»ñÊÓÆµ»º³å
	int		audioSize;				//<²¶»ñÊÓÆµ»º³å
private:
	AVCodecContext myAVcodec;
};
#endif // AudioDecoder_h__
