/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_sadgames_gl3dengine_glrender_GLES20JniWrapper */

#ifndef _Included_com_sadgames_gl3dengine_glrender_GLES20JniWrapper
#define _Included_com_sadgames_gl3dengine_glrender_GLES20JniWrapper
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_sadgames_gl3dengine_glrender_GLES20JniWrapper
 * Method:    glClear
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glClear
  (JNIEnv *, jclass);

/*
 * Class:     com_sadgames_gl3dengine_glrender_GLES20JniWrapper
 * Method:    glClearColor
 * Signature: (FFFF)V
 */
JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glClearColor
  (JNIEnv *, jclass, jfloat, jfloat, jfloat, jfloat);

/*
 * Class:     com_sadgames_gl3dengine_glrender_GLES20JniWrapper
 * Method:    glViewport
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glViewport
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     com_sadgames_gl3dengine_glrender_GLES20JniWrapper
 * Method:    glExtensions
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glExtensions
  (JNIEnv *, jclass);

/*
 * Class:     com_sadgames_gl3dengine_glrender_GLES20JniWrapper
 * Method:    glUseProgram
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glUseProgram
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_sadgames_gl3dengine_glrender_GLES20JniWrapper
 * Method:    glEnableFrontFacesCulling
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glEnableFrontFacesCulling
  (JNIEnv *, jclass);

/*
 * Class:     com_sadgames_gl3dengine_glrender_GLES20JniWrapper
 * Method:    glEnableBackFacesCulling
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glEnableBackFacesCulling
  (JNIEnv *, jclass);

/*
 * Class:     com_sadgames_gl3dengine_glrender_GLES20JniWrapper
 * Method:    glEnableFacesCulling
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glEnableFacesCulling
  (JNIEnv *, jclass);

/*
 * Class:     com_sadgames_gl3dengine_glrender_GLES20JniWrapper
 * Method:    glEnableDepthTest
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glEnableDepthTest
  (JNIEnv *, jclass);

/*
 * Class:     com_sadgames_gl3dengine_glrender_GLES20JniWrapper
 * Method:    glBindFramebuffer
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glBindFramebuffer
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_sadgames_gl3dengine_glrender_GLES20JniWrapper
 * Method:    glCheckFramebufferStatus
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glCheckFramebufferStatus
  (JNIEnv *, jclass);

/*
 * Class:     com_sadgames_gl3dengine_glrender_GLES20JniWrapper
 * Method:    glFramebufferAttachDepthTexture
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glFramebufferAttachDepthTexture
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_sadgames_gl3dengine_glrender_GLES20JniWrapper
 * Method:    glFramebufferAttachColorTexture
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glFramebufferAttachColorTexture
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_sadgames_gl3dengine_glrender_GLES20JniWrapper
 * Method:    glGenFrameBuffers
 * Signature: ([I)V
 */
JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glGenFrameBuffers
  (JNIEnv *, jclass, jintArray);

/*
 * Class:     com_sadgames_gl3dengine_glrender_GLES20JniWrapper
 * Method:    glDeleteFrameBuffers
 * Signature: ([I)V
 */
JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glDeleteFrameBuffers
  (JNIEnv *, jclass, jintArray);

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glDeleteBuffers
        (JNIEnv *, jclass, jintArray);

/*
 * Class:     com_sadgames_gl3dengine_glrender_GLES20JniWrapper
 * Method:    glGenRenderBuffers
 * Signature: ([I)V
 */
JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glGenRenderBuffers
  (JNIEnv *, jclass, jintArray);

/*
 * Class:     com_sadgames_gl3dengine_glrender_GLES20JniWrapper
 * Method:    glDeleteRenderBuffers
 * Signature: ([I)V
 */
JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glDeleteRenderBuffers
  (JNIEnv *, jclass, jintArray);

/*
 * Class:     com_sadgames_gl3dengine_glrender_GLES20JniWrapper
 * Method:    glBindRenderBuffer
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glBindRenderBuffer
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_sadgames_gl3dengine_glrender_GLES20JniWrapper
 * Method:    glRenderBufferStorage
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glRenderBufferStorage
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     com_sadgames_gl3dengine_glrender_GLES20JniWrapper
 * Method:    glFramebufferAttachDepthBuffer
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glFramebufferAttachDepthBuffer
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_sadgames_gl3dengine_glrender_GLES20JniWrapper
 * Method:    glGenTextures
 * Signature: ([I)V
 */
JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glGenTextures
  (JNIEnv *, jclass, jintArray);

/*
 * Class:     com_sadgames_gl3dengine_glrender_GLES20JniWrapper
 * Method:    glDeleteTextures
 * Signature: ([I)V
 */
JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glDeleteTextures
  (JNIEnv *, jclass, jintArray);

/*
 * Class:     com_sadgames_gl3dengine_glrender_GLES20JniWrapper
 * Method:    glActiveTexture
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glActiveTexture
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_sadgames_gl3dengine_glrender_GLES20JniWrapper
 * Method:    glBindTexture2D
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glBindTexture2D
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_sadgames_gl3dengine_glrender_GLES20JniWrapper
 * Method:    glBindTextureCube
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glBindTextureCube
  (JNIEnv *, jclass, jint);

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glTexImageDepth
  (JNIEnv *, jclass, jint, jint);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1TEXTURE_12D_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1TEXTURE_1CUBE_1MAP_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1TEXTURE_1CUBE_1MAP_1POSITIVE_1X_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1TEXTURE_1MIN_1FILTER_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1TEXTURE_1MAG_1FILTER_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1TEXTURE_1WRAP_1S_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1TEXTURE_1WRAP_1T_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1NEAREST_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1CLAMP_1TO_1EDGE_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1RGBA_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1UNSIGNED_1BYTE_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1UNSIGNED_1SHORT_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1LINEAR_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1BLEND_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1REPEAT_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1SRC_1ALPHA_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1ONE_1MINUS_1SRC_1ALPHA_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1ETC1_1RGB8_1OES_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1ELEMENT_1ARRAY_1BUFFER_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1TRIANGLES_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1TRIANGLE_1STRIP_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1CULL_1FACE_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1STATIC_1DRAW_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1COMPILE_1STATUS_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1LINK_1STATUS_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1VERTEX_1SHADER_1value
        (JNIEnv *, jclass);

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1FRAGMENT_1SHADER_1value
        (JNIEnv *, jclass);

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glTexParameteri
        (JNIEnv *, jclass, jint, jint, jint);

#ifdef __cplusplus
}
#endif
#endif
