package com.cubegames.slava.cubegame.gl_render;

import android.opengl.Matrix;

import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.ANIMATION_FRAME_DURATION;

public class GLAnimation {

    private GLRenderConsts.GLAnimationType animationType;

    private float fromX;
    private float toX;
    private float fromY;
    private float toY;
    private float fromZ;
    private float toZ;

    private long animationDuration;
    private long startTime;

    private boolean inProgress;

    private float[] baseMatrix = new float[16];

    public GLAnimation(GLRenderConsts.GLAnimationType animationType, float fromX, float toX, float fromY, float toY, float fromZ, float toZ, long animationDuration) {
        this.animationType = animationType;
        this.fromX = fromX;
        this.toX = toX;
        this.fromY = fromY;
        this.toY = toY;
        this.fromZ = fromZ;
        this.toZ = toZ;
        this.animationDuration = animationDuration;

        inProgress = false;

        Matrix.setIdentityM(baseMatrix, 0);
    }

    public void setBaseMatrix(float[] baseMatrix) {
        this.baseMatrix = baseMatrix;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    private long getFrameCount() {
        return animationDuration / ANIMATION_FRAME_DURATION;
    }
    private float getDeltaX() {
        return  (toX - fromX) / getFrameCount();
    }
    private float getDeltaY() {
        return  (toY - fromY) / getFrameCount();
    }
    private float getDeltaZ() {
        return  (toZ - fromZ) / getFrameCount();
    }

    private long getCurrentFrame() {
        return (System.currentTimeMillis() - startTime) / ANIMATION_FRAME_DURATION;
    }

    private float getCurrentX(long frame) {
        return /*fromX + */getDeltaX() * frame;
    }
    private float getCurrentY(long frame) {
        return /*fromY + */getDeltaY() * frame;
    }
    private float getCurrentZ(long frame) {
        return /*fromZ + */getDeltaZ() * frame;
    }

    public void startAnimation() {
        startTime = System.currentTimeMillis();
        inProgress = true;
    }

    public void animate(float[] modelMatrix) {
        long currentFrame = getCurrentFrame();
        float y = getCurrentY(currentFrame);
        System.out.print(currentFrame);
        System.out.print(":");
        System.out.print(y);
        System.out.println();

        for (int i = 0; i < 16; i++)
            modelMatrix[i] = baseMatrix[i];

        switch (animationType) {
            case TRANSLATE_ANIMATION:
                Matrix.translateM(modelMatrix, 0, getCurrentX(currentFrame), getCurrentY(currentFrame), getCurrentZ(currentFrame));
                break;
        }

        inProgress = currentFrame < (getFrameCount() - 1);
    }

}
