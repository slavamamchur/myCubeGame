package com.sadgames.sysutils;

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
    void glBindFramebuffer(int id);
    void glViewport(int x, int y, int width, int height);
    void glSetClearColor(float r, float g, float b, float a);
    void glClear();

    /** textures processing */
    void glActiveTexture(int slot);
    void glBindTexture2D(int id);
}
