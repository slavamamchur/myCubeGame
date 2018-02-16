package com.sadgames.gl3dengine.glrender;

public interface GLES20APIWrapperInterface {

    /** params enable / disable */
    void glEnableFacesCulling();
    void glEnableDepthTest();
    void glCullFrontFace();
    void glCullBackFace();

    /** common API */
    String glExtensions();
    void glViewport(int x, int y, int width, int height);
    void glSetClearColor(float r, float g, float b, float a);
    void glClear();

    /** GSHL API */
    void glUseProgram(int id);

    /** textures processing */
    void glGenTextures(int n, int[] textures, int offset);
    void glDeleteTextures(int n, int[] textures, int offset);
    void glActiveTexture(int slot);
    void glBindTexture2D(int id);
    void glBindTextureCube(int id);

    /** FBO processing */
    void glBindFramebuffer(int id);
    void glBindRenderBuffer(int id);
    void glGenFrameBuffers(int n, int[] framebuffers, int offset);
    void glGenRenderBuffers(int n, int[] framebuffers, int offset);
    void glDeleteFrameBuffers(int n, int[] framebuffers, int offset);
    void glDeleteRenderBuffers(int n, int[] framebuffers, int offset);
    boolean glCheckFramebufferStatus();
    void glFramebufferAttachDepthTexture(int texture);
    void glFramebufferAttachDepthBuffer(int buffer);
    void glFramebufferAttachColorTexture(int texture);
    void glRenderBufferStorage(int width, int height);
}
