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

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glViewport
        (JNIEnv *, jclass, jint x, jint y, jint width, jint height) {
    glViewport(x, y, width, height);
}

JNIEXPORT jstring JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glExtensions
        (JNIEnv *env, jclass) {
    return env->NewStringUTF((const char*)glGetString(GL_EXTENSIONS));
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glUseProgram
        (JNIEnv *, jclass, jint id) {
    glUseProgram(id);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glEnableFrontFacesCulling
        (JNIEnv *, jclass) {
    glCullFace(GL_FRONT);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glEnableBackFacesCulling
        (JNIEnv *, jclass) {
    glCullFace(GL_BACK);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glEnableFacesCulling
        (JNIEnv *, jclass) {
    glEnable(GL_CULL_FACE);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glEnableDepthTest
        (JNIEnv *, jclass) {
    glEnable(GL_DEPTH_TEST);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glBindFramebuffer
        (JNIEnv *, jclass, jint id) {
    glBindFramebuffer(GL_FRAMEBUFFER, id);
}

JNIEXPORT jboolean JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glCheckFramebufferStatus
        (JNIEnv *, jclass) {
    return (jboolean) (glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glFramebufferAttachDepthTexture
        (JNIEnv *, jclass, jint textureId) {
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, textureId, 0);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glFramebufferAttachColorTexture
        (JNIEnv *, jclass, jint textureId) {
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureId, 0);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glGenFrameBuffers
        (JNIEnv *env, jclass, jint count, jintArray framebuffers) {
    jboolean isCopy;
    jint* buffer = (jint*) env->GetPrimitiveArrayCritical(framebuffers, &isCopy);
    glGenFramebuffers(count, (GLuint *) buffer);
    env->ReleasePrimitiveArrayCritical(framebuffers, buffer, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glDeleteFrameBuffers
        (JNIEnv *env, jclass, jint count, jintArray framebuffers) {
    jboolean isCopy;
    jint* buffer = (jint*) env->GetPrimitiveArrayCritical(framebuffers, &isCopy);
    glDeleteFramebuffers(count, (GLuint *) buffer);
    env->ReleasePrimitiveArrayCritical(framebuffers, buffer, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glGenTextures
        (JNIEnv *env, jclass, jint count, jintArray textures) {
    jboolean isCopy;
    jint* buffer = (jint*) env->GetPrimitiveArrayCritical(textures, &isCopy);
    glGenTextures(count, (GLuint *) buffer);
    env->ReleasePrimitiveArrayCritical(textures, buffer, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glDeleteTextures
        (JNIEnv *env, jclass, jint count, jintArray textures) {
    jboolean isCopy;
    jint* buffer = (jint*) env->GetPrimitiveArrayCritical(textures, &isCopy);
    glDeleteTextures(count, (GLuint *) buffer);
    env->ReleasePrimitiveArrayCritical(textures, buffer, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glActiveTexture
        (JNIEnv *, jclass, jint slot) {
    glActiveTexture(GL_TEXTURE0 + slot);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glBindTexture2D
        (JNIEnv *, jclass, jint id) {
    glBindTexture(GL_TEXTURE_2D, id);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glBindTextureCube
        (JNIEnv *, jclass, jint id) {
    glBindTexture(GL_TEXTURE_CUBE_MAP, id);
}
