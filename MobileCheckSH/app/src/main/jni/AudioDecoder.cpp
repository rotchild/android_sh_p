#include "AudioDecoder.h"
#include "avcodec.h" 
#include <string.h>

AudioDecoder::AudioDecoder(void){
	register_codec();
	memset(&myAVcodec,0,sizeof(myAVcodec));
};
AudioDecoder::~AudioDecoder(void){
	delete[] audioBuf;
	return;
};


int AudioDecoder::start()
{
	myAVcodec.sample_rate=AUDIO_DECODE_SAMPLE;
	myAVcodec.channels=AUDIO_DECODE_CHANEL;
	avcodec_open(&myAVcodec,avcodec_find_decoder(CODEC_ID_ADPCM_IMA_WAV));
	audioBuf=new char[2048];	
	return 0;
}

int AudioDecoder::stop(void)
{
	return 0;
}

int AudioDecoder::decodeData(unsigned char *frameData,int frameSize){
	avcodec_decode_audio(&myAVcodec,(short *)audioBuf,&audioSize,frameData,frameSize);
	return audioSize;
}
