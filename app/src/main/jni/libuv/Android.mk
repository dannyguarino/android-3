LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := libuv
LOCAL_SRC_FILES := $(LOCAL_PATH)/libuv/libuv-android-$(TARGET_ARCH_ABI)/lib/libuv.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/libuv/libuv-android-$(TARGET_ARCH_ABI)/include

include $(BUILD_STATIC_LIBRARY)
