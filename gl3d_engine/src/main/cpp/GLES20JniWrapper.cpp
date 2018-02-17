#include "com_sadgames_gl3dengine_glrender_GLES20JniWrapper.h"

#include <GLES2/gl2.h>
//#include <GLES2/gl2ext.h>
//#include <string>

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glClear
        (JNIEnv *, jclass) {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glClearColor
        (JNIEnv *, jclass, jfloat red, jfloat green , jfloat blue , jfloat alpha) {
    glClearColor(red, green, blue, alpha);
}

JNIEXPORT jstring JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glExtensions
        (JNIEnv *env, jclass) {
    return env->NewStringUTF((const char*)glGetString(GL_EXTENSIONS));
}
