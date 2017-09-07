 /*
 * utils for libavcodec
 * Copyright (c) 2001 Fabrice Bellard.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
 
/**
 * @file utils.c
 * utils.
 */
 
#include "avcodec.h"
#include <errno.h>
#include <assert.h>
#include <stdio.h>

void *av_mallocz(unsigned int size)
{
    void *ptr;
    ptr = malloc(size);
    memset(ptr, 0, size);
    return ptr;
}
void *av_malloc(unsigned int size)
{
    void *ptr;
    ptr = malloc(size);
    return ptr;
}
void *av_realloc(void *ptr, unsigned int size)
{
  void * ret;
  ret = realloc(ptr, size);
  return ret;
}
void av_free(void *ptr)
{
  /* XXX: this test should not be needed on most libcs */
  if (ptr) {
    free(ptr);
  }
}
char *av_strdup(const char *s)
{
    char *ptr;
    int len;
    len = strlen(s) + 1;
    ptr = av_malloc(len);
    if (!ptr)
        return NULL;
    memcpy(ptr, s, len);
    return ptr;
}

/**
 * realloc which does nothing if the block is large enough
 */
void *av_fast_realloc(void *ptr, unsigned int *size, unsigned int min_size)
{
    if(min_size < *size) 
        return ptr;
    
    *size= min_size + 10*1024;

    return av_realloc(ptr, *size);
}


/* allocation of static arrays - do not use for normal allocation */
static unsigned int last_static = 0;
static char*** array_static = NULL;
static const unsigned int grow_static = 64; // ^2
void *__av_mallocz_static(void** location, unsigned int size)
{
    unsigned int l = (last_static + grow_static) & ~(grow_static - 1);
    void *ptr = av_mallocz(size);
    if (!ptr)
	return NULL;

    if (location)
    {
	if (l > last_static)
	    array_static = av_realloc(array_static, l);
	array_static[last_static++] = (char**) location;
	*location = ptr;
    }
    return ptr;
}
/* free all static arrays and reset pointers to 0 */
void av_free_static(void)
{
    if (array_static)
    {
	unsigned i;
	for (i = 0; i < last_static; i++)
	{
	    av_free(*array_static[i]);
            *array_static[i] = NULL;
	}
	av_free(array_static);
	array_static = 0;
    }
    last_static = 0;
}

/* cannot call it directly because of 'void **' casting is not automatic */
void __av_freep(void **ptr)
{
    av_free(*ptr);
    *ptr = NULL;
}

/* encoder management */
AVCodec *first_avcodec=NULL;

void register_avcodec(AVCodec *format)
{
    AVCodec **p;
    p = &first_avcodec;
    while (*p != NULL) p = &(*p)->next;
    *p = format;
    format->next = NULL;
}

typedef struct InternalBuffer{
    int last_pic_num;
    uint8_t *base[4];
    uint8_t *data[4];
}InternalBuffer;


void avcodec_default_release_buffer(AVCodecContext *s, AVFrame *pic){
    int i;
    InternalBuffer *buf, *last, temp;

    assert(pic->type==FF_BUFFER_TYPE_INTERNAL);
    assert(s->internal_buffer_count);

    buf = NULL; /* avoids warning */
    for(i=0; i<s->internal_buffer_count; i++){ //just 3-5 checks so is not worth to optimize
        buf= &((InternalBuffer*)s->internal_buffer)[i];
        if(buf->data[0] == pic->data[0])
            break;
    }
    assert(i < s->internal_buffer_count);
    s->internal_buffer_count--;
    last = &((InternalBuffer*)s->internal_buffer)[s->internal_buffer_count];

    temp= *buf;
    *buf= *last;
    *last= temp;

    for(i=0; i<3; i++){
        pic->data[i]=NULL;
//        pic->base[i]=NULL;
    }
//printf("R%X\n", pic->opaque);
}

enum PixelFormat avcodec_default_get_format(struct AVCodecContext *s, enum PixelFormat * fmt){
    return fmt[0];
}

/**
 * allocates a AVPFrame and set it to defaults.
 * this can be deallocated by simply calling free() 
 */
AVFrame *avcodec_alloc_frame(void){
    AVFrame *pic= av_mallocz(sizeof(AVFrame));
    
    return pic;
}

int avcodec_open(AVCodecContext *avctx, AVCodec *codec)
{
    int ret;

	printf ("avcodec_open\n");
    if(avctx->codec)
        return -1;

    avctx->codec = codec;
    avctx->codec_id = codec->id;
    avctx->frame_number = 0;
    if (codec->priv_data_size > 0) {
        avctx->priv_data = av_mallocz(codec->priv_data_size);
        if (!avctx->priv_data) 
            return -ENOMEM;
    } else {
        avctx->priv_data = NULL;
    }
    ret = avctx->codec->init(avctx);
    if (ret < 0) {
        av_freep(&avctx->priv_data);
        return ret;
    }
    return 0;
}

int avcodec_encode_audio(AVCodecContext *avctx, uint8_t *buf, int buf_size, 
                         const short *samples)
{
    int ret;

    ret = avctx->codec->encode(avctx, buf, buf_size, (void *)samples);
    avctx->frame_number++;
    return ret;
}


/* decode an audio frame. return -1 if error, otherwise return the
   *number of bytes used. If no frame could be decompressed,
   *frame_size_ptr is zero. Otherwise, it is the decompressed frame
   *size in BYTES. */
int avcodec_decode_audio(AVCodecContext *avctx, int16_t *samples, 
                         int *frame_size_ptr,
                         uint8_t *buf, int buf_size)
{
    int ret;
    ret = avctx->codec->decode(avctx, samples, frame_size_ptr, 
                               buf, buf_size);
    avctx->frame_number++;  
    return ret;
}

int avcodec_close(AVCodecContext *avctx)
{
    if (avctx->codec->close)
        avctx->codec->close(avctx);
    av_freep(&avctx->priv_data);
    avctx->codec = NULL;
    return 0;
}

AVCodec *avcodec_find_encoder(enum CodecID id)
{
    AVCodec *p;
    p = first_avcodec;
    while (p) {
        if (p->encode != NULL && p->id == id)
            return p;
        p = p->next;
    }
    return NULL;
}

AVCodec *avcodec_find_encoder_by_name(const char *name)
{
    AVCodec *p;
    p = first_avcodec;
    while (p) {
        if (p->encode != NULL && strcmp(name,p->name) == 0)
            return p;
        p = p->next;
    }
    return NULL;
}

AVCodec *avcodec_find_decoder(enum CodecID id)
{
    AVCodec *p;
    p = first_avcodec;
    while (p) {
        if (p->decode != NULL && p->id == id)
            return p;
        p = p->next;
    }
    return NULL;
}

AVCodec *avcodec_find_decoder_by_name(const char *name)
{
    AVCodec *p;
    p = first_avcodec;
    while (p) {
        if (p->decode != NULL && strcmp(name,p->name) == 0)
            return p;
        p = p->next;
    }
    return NULL;
}

AVCodec *avcodec_find(enum CodecID id)
{
    AVCodec *p;
    p = first_avcodec;
    while (p) {
        if (p->id == id)
            return p;
        p = p->next;
    }
    return NULL;
}

unsigned avcodec_version( void )
{
  return LIBAVCODEC_VERSION_INT;
}

unsigned avcodec_build( void )
{
  return LIBAVCODEC_BUILD;
}


/**
 * Flush buffers, should be called when seeking or when swicthing to a different stream.
 */
void avcodec_flush_buffers(AVCodecContext *avctx)
{
    if(avctx->codec->flush)
        avctx->codec->flush(avctx);
}

int64_t av_rescale(int64_t a, int b, int c){
    uint64_t h, l;
    assert(c > 0);
    assert(b >=0);
    
    if(a<0) return -av_rescale(-a, b, c);
    
    h= a>>32;
    if(h==0) return a*b/c;
    
    l= a&0xFFFFFFFF;
    l *= b;
    h *= b;

    l += (h%c)<<32;

    return ((h/c)<<32) + l/c;
}

int register_codec(){
#define PCM_CODEC(id, name) \
	register_avcodec(& name ## _encoder); \
	register_avcodec(& name ## _decoder); \
	/* adpcm codecs */
	PCM_CODEC(CODEC_ID_ADPCM_IMA_QT, adpcm_ima_qt);
	PCM_CODEC(CODEC_ID_ADPCM_IMA_WAV, adpcm_ima_wav);
	PCM_CODEC(CODEC_ID_ADPCM_IMA_DK3, adpcm_ima_dk3);
	PCM_CODEC(CODEC_ID_ADPCM_IMA_DK4, adpcm_ima_dk4);
	PCM_CODEC(CODEC_ID_ADPCM_MS, adpcm_ms);
	PCM_CODEC(CODEC_ID_ADPCM_4XM, adpcm_4xm);
#undef PCM_CODEC
	return 0	;
};
