package com.sadgames.gl3dengine.glrender;

import com.badlogic.gdx.ApplicationAdapter;

public class GdxApplicationAdapter extends ApplicationAdapter {

    private GLRendererInterface glInternalRenderer;

    public GdxApplicationAdapter(GLRendererInterface renderer) {
        this.glInternalRenderer = renderer;
    }

    @Override
    public void create() {
        glInternalRenderer.onSurfaceCreated();
    }

    @Override
    public void resize(int width, int height) {
        glInternalRenderer.onSurfaceChanged(width, height);
    }

    @Override
    public void render() {
        glInternalRenderer.onDrawFrame();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void dispose() {
        super.dispose(); //TODO: free resources
    }
}
