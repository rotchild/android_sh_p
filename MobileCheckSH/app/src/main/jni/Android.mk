LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := netencoder
LOCAL_SRC_FILES := H264Android.c utils.c adpcm.c netencoder.cpp AudioEncoder.cpp AudioDecoder.cpp
LOCAL_CFLAGS +=-std=gnu99  -DHAVE_AV_CONFIG_H
LOCAL_STATIC_LIBRARIES = libx264 
LOCAL_LDFLAGS += $(LOCAL_PATH)/libx264.a 
LOCAL_C_INCLUDES += D:/tools/android/android-ndk-r5c/sources/cxx-stl/stlport/stlport
LOCAL_LDLIBS := -lgcc -llog  
include $(BUILD_SHARED_LIBRARY)
