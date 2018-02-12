package com.sadgames.gl3d_engine.gl_render;

public interface GLES20APIWrapperInterface {

    /** params enable */
    void glEnableFacesCulling();
    void glEnableDepthTest();

    /** culling faces */
    void glCullFrontFace();
    void glCullBackFace();

    /** common API */
    String glExtensions();
    void glUseProgram(int id);

    void glViewport(int x, int y, int width, int height);
    void glSetClearColor(float r, float g, float b, float a);
    void glClear();

    /** textures processing */
    void glActiveTexture(int slot);
    void glBindTexture2D(int id);

    /** FBO processing */
    void glBindFramebuffer(int id);
    void glBindRenderbuffer(int id);
    void glGenFrameBuffers(int n, int[] framebuffers, int offset);
    void glGenRenderBuffers(int n, int[] framebuffers, int offset);
    void glDeleteFrameBuffers(int n, int[] framebuffers, int offset);
    void glDeleteRenderBuffers(int n, int[] framebuffers, int offset);
    boolean glCheckFramebufferStatus();
    void glFramebufferAttachDepthTexture(int texture);
    void glFramebufferAttachDepthBuffer(int buffer);
    void glFramebufferAttachColorTexture(int texture);
    void glRenderbufferStorage(int width, int height);
}
