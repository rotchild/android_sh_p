#include "AudioEncoder.h"
#include "avcodec.h"
#include <stdlib.h>
#include <stdio.h>
#include "jniLog.h"
#include <string.h>
//typedef struct {
//	enum CodecID codecID;
//	BYTE wtwh_id;
//}Codec_Map;


//AudioEncoder::~AudioEncoder(void){
//	return;
//};

AudioEncoder::AudioEncoder(void){
	register_codec();
	memset(&myAVcodec,0,sizeof(myAVcodec));
	return;
};
int AudioEncoder::start()
{
	LOGD("ff");
	audio_setting.sample_rate=8000;
	audio_setting.channels=1;
	audio_setting.bitspersample=16;
	audio_setting.codecID=CODEC_ID_ADPCM_IMA_WAV;


	//����codec
	myAVcodec.sample_rate=audio_setting.sample_rate;
	myAVcodec.channels=audio_setting.channels;

	register_codec();
	avcodec_open(&myAVcodec, avcodec_find_encoder(audio_setting.codecID));
	audio_setting.frame_size=myAVcodec.frame_size * 2 * myAVcodec.channels;

	if (audio_setting.frame_size==2) audio_setting.frame_size=512;
	audio_buf=new unsigned char[audio_setting.frame_size];
	audio_setting.encoded_frame=audio_buf;
	state=STATE_RUN;
	return 0;
}

int AudioEncoder::encode(unsigned char *data,int dataSize){
	audio_setting.encodedSize= avcodec_encode_audio(&myAVcodec, this->audio_setting.encoded_frame, dataSize, (short *)data);
	return audio_setting.encodedSize;
}

