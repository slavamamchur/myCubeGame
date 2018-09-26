# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

cmake_minimum_required(VERSION 3.4.1)

add_library(gleswrapper SHARED ../JNI_GLES_Wrapper/GLES20JniWrapper.cpp)

find_path(GLES2_INCLUDE_DIR GLES2/gl2.h HINTS ${ANDROID_NDK})
find_library(GLES2_LIBRARY libGLESv2.so HINTS ${GLES2_INCLUDE_DIR}/../lib)
target_include_directories(gleswrapper PUBLIC ${GLES2_INCLUDE_DIR})
# find_library(log-lib log)

target_link_libraries(gleswrapper ${GLES2_LIBRARY})
