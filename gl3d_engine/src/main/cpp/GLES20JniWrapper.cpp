#include <jni.h>

#include <GLES2/gl2.h>

extern "C"

JNICALL void Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glClear(JNIEnv *, jobject) {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
}
