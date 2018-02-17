#include "com_sadgames_gl3dengine_glrender_GLES20JniWrapper.h"

#include <GLES2/gl2.h>

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glClear
        (JNIEnv *, jclass) {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glClearColor
        (JNIEnv *, jclass, jfloat red, jfloat green , jfloat blue , jfloat alpha) {
    glClearColor(red, green, blue, alpha);
}
