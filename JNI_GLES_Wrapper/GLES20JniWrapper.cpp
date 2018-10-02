#include "com_sadgames_gl3dengine_glrender_GLES20JniWrapper.h"

#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glClear
        (JNIEnv *, jclass) {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glClearColor
        (JNIEnv *, jclass, jfloat red, jfloat green , jfloat blue , jfloat alpha) {
    glClearColor(red, green, blue, alpha);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glViewport
        (JNIEnv *, jclass, jint width, jint height) {
    glViewport(0, 0, width, height);
}

JNIEXPORT jstring JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glExtensions
        (JNIEnv *env, jclass) {
    return env->NewStringUTF((const char*)glGetString(GL_EXTENSIONS));
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glUseProgram
        (JNIEnv *, jclass, jint id) {
    glUseProgram((GLuint) id);
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
    glBindFramebuffer(GL_FRAMEBUFFER, (GLuint) id);
}

JNIEXPORT jboolean JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glCheckFramebufferStatus
        (JNIEnv *, jclass) {
    return (jboolean) (glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glFramebufferAttachDepthTexture
        (JNIEnv *, jclass, jint textureId) {
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, (GLuint) textureId, 0);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glFramebufferAttachColorTexture
        (JNIEnv *, jclass, jint textureId) {
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, (GLuint) textureId, 0);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glGenFrameBuffers
        (JNIEnv *env, jclass, jintArray framebuffers) {
    jboolean isCopy;
    jint* buffer = (jint*) env->GetPrimitiveArrayCritical(framebuffers, &isCopy);
    glGenFramebuffers(1, (GLuint *) buffer);
    env->ReleasePrimitiveArrayCritical(framebuffers, buffer, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glDeleteFrameBuffers
        (JNIEnv *env, jclass, jintArray framebuffers) {
    jboolean isCopy;
    jint* buffer = (jint*) env->GetPrimitiveArrayCritical(framebuffers, &isCopy);
    glDeleteFramebuffers(1, (GLuint *) buffer);
    env->ReleasePrimitiveArrayCritical(framebuffers, buffer, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glDeleteBuffers
        (JNIEnv *env, jclass, jintArray buffers) {
    jboolean isCopy;
    jint* buffer = (jint*) env->GetPrimitiveArrayCritical(buffers, &isCopy);
    glDeleteBuffers(1, (GLuint *) buffer);
    env->ReleasePrimitiveArrayCritical(buffers, buffer, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glGenRenderBuffers
        (JNIEnv *env, jclass, jintArray renderbuffers) {
    jboolean isCopy;
    jint* buffer = (jint*) env->GetPrimitiveArrayCritical(renderbuffers, &isCopy);
    glGenRenderbuffers(1,  (GLuint *) buffer);
    env->ReleasePrimitiveArrayCritical(renderbuffers, buffer, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glDeleteRenderBuffers
        (JNIEnv *env, jclass, jintArray renderbuffers) {
    jboolean isCopy;
    jint* buffer = (jint*) env->GetPrimitiveArrayCritical(renderbuffers, &isCopy);
    glDeleteRenderbuffers(1,  (GLuint *) buffer);
    env->ReleasePrimitiveArrayCritical(renderbuffers, buffer, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glBindRenderBuffer
        (JNIEnv *, jclass, jint id) {
    glBindRenderbuffer(GL_RENDERBUFFER, (GLuint) id);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glRenderBufferStorage
        (JNIEnv *, jclass, jint width, jint height) {
    glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, width, height);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glFramebufferAttachDepthBuffer
        (JNIEnv *, jclass, jint id) {
    glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, (GLuint) id);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glGenTextures
        (JNIEnv *env, jclass, jintArray textures) {
    jboolean isCopy;
    jint* buffer = (jint*) env->GetPrimitiveArrayCritical(textures, &isCopy);
    glGenTextures(1, (GLuint *) buffer);
    env->ReleasePrimitiveArrayCritical(textures, buffer, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glDeleteTextures
        (JNIEnv *env, jclass, jintArray textures) {
    jboolean isCopy;
    jint* buffer = (jint*) env->GetPrimitiveArrayCritical(textures, &isCopy);
    glDeleteTextures(1, (GLuint *) buffer);
    env->ReleasePrimitiveArrayCritical(textures, buffer, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glActiveTexture
        (JNIEnv *, jclass, jint slot) {
    glActiveTexture((GLenum) (GL_TEXTURE0 + slot));
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glBindTexture2D
        (JNIEnv *, jclass, jint id) {
    glBindTexture(GL_TEXTURE_2D, (GLuint) id);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glBindTextureCube
        (JNIEnv *, jclass, jint id) {
    glBindTexture(GL_TEXTURE_CUBE_MAP, (GLuint) id);
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glTexImageDepth
        (JNIEnv *, jclass, jint width, jint height) {

    glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_INT, NULL);
}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1TEXTURE_12D_1value
        (JNIEnv *, jclass) {

    return (jint) GL_TEXTURE_2D;

}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1TEXTURE_1CUBE_1MAP_1value
        (JNIEnv *, jclass) {

    return (jint) GL_TEXTURE_CUBE_MAP;

}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1TEXTURE_1CUBE_1MAP_1POSITIVE_1X_1value
        (JNIEnv *, jclass) {

    return (jint) GL_TEXTURE_CUBE_MAP_POSITIVE_X;

}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1TEXTURE_1MIN_1FILTER_1value
        (JNIEnv *, jclass) {

    return (jint) GL_TEXTURE_MIN_FILTER;

}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1TEXTURE_1MAG_1FILTER_1value
        (JNIEnv *, jclass) {

    return (jint) GL_TEXTURE_MAG_FILTER;

}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1TEXTURE_1WRAP_1S_1value
        (JNIEnv *, jclass) {

    return (jint) GL_TEXTURE_WRAP_S;

}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1TEXTURE_1WRAP_1T_1value
        (JNIEnv *, jclass) {

    return (jint) GL_TEXTURE_WRAP_T;

}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1NEAREST_1value
        (JNIEnv *, jclass) {

    return (jint) GL_NEAREST;

}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1CLAMP_1TO_1EDGE_1value
        (JNIEnv *, jclass) {

    return (jint) GL_CLAMP_TO_EDGE;

}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1RGBA_1value
        (JNIEnv *, jclass) {

    return (jint) GL_RGBA;

}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1UNSIGNED_1BYTE_1value
        (JNIEnv *, jclass) {

    return (jint) GL_UNSIGNED_BYTE;

}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1UNSIGNED_1SHORT_1value
        (JNIEnv *, jclass) {

    return (jint) GL_UNSIGNED_SHORT;

}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1LINEAR_1value
        (JNIEnv *, jclass) {

    return (jint) GL_LINEAR;

}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1BLEND_1value
        (JNIEnv *, jclass) {

    return (jint) GL_BLEND;

}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1REPEAT_1value
        (JNIEnv *, jclass) {

    return (jint) GL_REPEAT;

}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1SRC_1ALPHA_1value
        (JNIEnv *, jclass) {

    return (jint) GL_SRC_ALPHA;

}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1ONE_1MINUS_1SRC_1ALPHA_1value
        (JNIEnv *, jclass) {

    return (jint) GL_ONE_MINUS_SRC_ALPHA;

}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1ETC1_1RGB8_1OES_1value
        (JNIEnv *, jclass) {

    return (jint) GL_ETC1_RGB8_OES;

}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1ELEMENT_1ARRAY_1BUFFER_1value
        (JNIEnv *, jclass) {

    return (jint) GL_ELEMENT_ARRAY_BUFFER;

}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1TRIANGLES_1value
        (JNIEnv *, jclass) {

    return (jint) GL_TRIANGLES;
}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1TRIANGLE_1STRIP_1value
        (JNIEnv *, jclass) {

    return (jint) GL_TRIANGLE_STRIP;
}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1CULL_1FACE_1value
        (JNIEnv *, jclass) {

    return (jint) GL_CULL_FACE;
}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1STATIC_1DRAW_1value
        (JNIEnv *, jclass) {

    return (jint) GL_STATIC_DRAW;
}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1COMPILE_1STATUS_1value
        (JNIEnv *, jclass) {
    return (jint) GL_COMPILE_STATUS;
}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1LINK_1STATUS_1value
        (JNIEnv *, jclass) {
    return (jint) GL_LINK_STATUS;
}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1VERTEX_1SHADER_1value
        (JNIEnv *, jclass) {
    return (jint) GL_VERTEX_SHADER;
}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1FRAGMENT_1SHADER_1value
        (JNIEnv *, jclass) {
    return (jint) GL_FRAGMENT_SHADER;
}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1FLOAT_1value
        (JNIEnv *, jclass) {
    return (jint) GL_FLOAT;
}

JNIEXPORT jint JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_get_1GL_1ARRAY_1BUFFER_1value
        (JNIEnv *, jclass) {
    return (jint) GL_ARRAY_BUFFER;
}

JNIEXPORT void JNICALL Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glTexParameteri
        (JNIEnv *, jclass, jint target, jint pname, jint param) {

    glTexParameteri(static_cast<GLenum>(target), static_cast<GLenum>(pname), param);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glTexImage2D(JNIEnv *env, jclass,
                                                                    jint target, jint level,
                                                                    jint internalformat, jint width,
                                                                    jint height, jint border,
                                                                    jint format, jint type,
                                                                    jobject data) {

    glTexImage2D(static_cast<GLenum>(target),
                 level,
                 internalformat,
                 width,
                 height,
                 border,
                 static_cast<GLenum>(format),
                 static_cast<GLenum>(type),
                 data != NULL ? env->GetDirectBufferAddress(data) : NULL);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glCompressedTexImage2D(JNIEnv *env,
                                                                              jclass,
                                                                              jint target,
                                                                              jint level,
                                                                              jint internalformat,
                                                                              jint width,
                                                                              jint height,
                                                                              jint border,
                                                                              jint imageSize,
                                                                              jobject data) {
    glCompressedTexImage2D(
            static_cast<GLenum>(target),
            level,
            static_cast<GLenum>(internalformat),
            width,
            height,
            border,
            imageSize,
            env->GetDirectBufferAddress(data));

}

extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glBlendFunc(JNIEnv *, jclass, jint sfactor, jint dfactor) {

    glBlendFunc(static_cast<GLenum>(sfactor), static_cast<GLenum>(dfactor));

}

extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glEnable(JNIEnv *, jclass, jint cap) {

    glEnable(static_cast<GLenum>(cap));

}extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glBindBuffer(JNIEnv *, jclass, jint target, jint buffer) {

    glBindBuffer(static_cast<GLenum>(target), static_cast<GLuint>(buffer));

}

extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glDrawArrays(JNIEnv *, jclass , jint mode, jint first, jint count) {

    glDrawArrays(static_cast<GLenum>(mode), first, count);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glDrawElements(JNIEnv *, jclass,
                                                                      jint mode, jint count,
                                                                      jint type) {

    glDrawElements(static_cast<GLenum>(mode), count, static_cast<GLenum>(type), NULL);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glDisable(JNIEnv *, jclass, jint cap) {

    glDisable(static_cast<GLenum>(cap));

}

extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glGenBuffers(JNIEnv *env, jclass,
                                                                    jint n, jintArray buffers_) {
    jint *buffers = env->GetIntArrayElements(buffers_, NULL);

    glGenBuffers(n, reinterpret_cast<GLuint *>(buffers));

    env->ReleaseIntArrayElements(buffers_, buffers, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glBufferData(JNIEnv *env, jclass,
                                                                    jint target, jint size,
                                                                    jobject data, jint usage) {

    glBufferData(static_cast<GLenum>(target),
                 size,
                 env->GetDirectBufferAddress(data),
                 static_cast<GLenum>(usage));
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glAttachShader(JNIEnv *, jclass,
                                                                      jint program, jint shader) {

    glAttachShader(static_cast<GLuint>(program), static_cast<GLuint>(shader));

}

extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glCompileShader(JNIEnv *, jclass,
                                                                       jint shader) {

    glCompileShader(static_cast<GLuint>(shader));

}

extern "C"
JNIEXPORT jint JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glCreateProgram(JNIEnv *, jclass) {

    return (jint) glCreateProgram();

}

extern "C"
JNIEXPORT jint JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glCreateShader(JNIEnv *, jclass,
                                                                      jint type) {

    return (jint) glCreateShader(static_cast<GLenum>(type));

}

extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glDeleteProgram(JNIEnv *, jclass,
                                                                       jint program) {

    glDeleteProgram(static_cast<GLuint>(program));

}

extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glDeleteShader(JNIEnv *, jclass,
                                                                      jint shader) {

    glDeleteShader(static_cast<GLuint>(shader));

}

extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glDetachShader(JNIEnv *, jclass,
                                                                      jint program, jint shader) {

    glDetachShader(static_cast<GLuint>(program), static_cast<GLuint>(shader));

}

extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glGetProgramiv(JNIEnv *env, jclass,
                                                                      jint program, jint pname,
                                                                      jintArray params_) {
    jint *params = env->GetIntArrayElements(params_, NULL);

    glGetProgramiv(static_cast<GLuint>(program), static_cast<GLenum>(pname), params);

    env->ReleaseIntArrayElements(params_, params, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glGetShaderiv(JNIEnv *env, jclass,
                                                                     jint shader, jint pname,
                                                                     jintArray params_) {
    jint *params = env->GetIntArrayElements(params_, NULL);

    glGetShaderiv(static_cast<GLuint>(shader), static_cast<GLenum>(pname), params);

    env->ReleaseIntArrayElements(params_, params, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glLinkProgram(JNIEnv *, jclass,
                                                                     jint program) {

    glLinkProgram(static_cast<GLuint>(program));

}

extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glShaderSource(JNIEnv *env, jclass,
                                                                      jint shader,
                                                                      jstring string_) {
    const char *source = env->GetStringUTFChars(string_, 0);

    glShaderSource(shader, 1, &source, NULL);

    env->ReleaseStringUTFChars(string_, source);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glEnableVertexAttribArray(JNIEnv *,
                                                                                 jclass,
                                                                                 jint index) {

    glEnableVertexAttribArray(index);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glVertexAttribPointer(JNIEnv *,
                                                                             jclass,
                                                                             jint indx, jint size,
                                                                             jint type,
                                                                             jboolean normalized,
                                                                             jint stride,
                                                                             jint offset) {

    glVertexAttribPointer(static_cast<GLuint>(indx),
                          size,
                          static_cast<GLenum>(type),
                          normalized,
                          stride,
                          BUFFER_OFFSET(offset));

}

extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glVertexAttribPointerBounds(JNIEnv *env,
                                                                                   jclass,
                                                                                   jint indx,
                                                                                   jint size,
                                                                                   jint type,
                                                                                   jboolean normalized,
                                                                                   jint stride,
                                                                                   jobject ptr,
                                                                                   jint remaining) {

    glVertexAttribPointer(static_cast<GLuint>(indx),
                          size,
                          static_cast<GLenum>(type),
                          normalized,
                          stride,
                          env->GetDirectBufferAddress(ptr));

}

extern "C"
JNIEXPORT jint JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glGetAttribLocation(JNIEnv *env, jclass,
                                                                           jint program,
                                                                           jstring name_) {
    const char *name = env->GetStringUTFChars(name_, 0);
    jint result =  (jint) glGetAttribLocation(static_cast<GLuint>(program), name);
    env->ReleaseStringUTFChars(name_, name);

    return  result;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glGetUniformLocation(JNIEnv *env,
                                                                            jclass,
                                                                            jint program,
                                                                            jstring name_) {
    const char *name = env->GetStringUTFChars(name_, 0);
    jint result =  (jint) glGetUniformLocation(static_cast<GLuint>(program), name);
    env->ReleaseStringUTFChars(name_, name);

    return  result;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glUniform3fv(JNIEnv *env, jclass,
                                                                    jint location, jint count,
                                                                    jfloatArray v_) {
    jfloat *v = env->GetFloatArrayElements(v_, NULL);

    glUniform3fv(location, count, v);

    env->ReleaseFloatArrayElements(v_, v, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glUniformMatrix4fv(JNIEnv *env, jclass,
                                                                          jint location, jint count,
                                                                          jboolean transpose,
                                                                          jfloatArray value_) {
    jfloat *value = env->GetFloatArrayElements(value_, NULL);

    glUniformMatrix4fv(location, count, transpose, value);

    env->ReleaseFloatArrayElements(value_, value, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glUniform1i(JNIEnv *, jclass,
                                                                   jint location, jint x) {

    glUniform1i(location, x);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_sadgames_gl3dengine_glrender_GLES20JniWrapper_glUniform1f(JNIEnv *, jclass,
                                                                   jint location, jfloat x) {

    glUniform1f(location, x);

}