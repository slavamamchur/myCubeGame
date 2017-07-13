package com.cubegames.slava.cubegame.gl_render;

import android.opengl.Matrix;

import java.util.Arrays;

import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.ANIMATION_FRAME_DURATION;

public class GLAnimation {
    public final static short ROTATE_BY_X = 0x1;
    public final static short ROTATE_BY_Y = 0x2;
    public final static short ROTATE_BY_Z = 0x4;

    private GLRenderConsts.GLAnimationType animationType;

    private float fromX;
    private float toX;
    private float fromY;
    private float toY;
    private float fromZ;
    private float toZ;

    private float rotationAngle = 0;
    private short rotationAxesMask = 0;
    private float ppX = 0f;
    private float ppY = 0f;
    private float objectRadius = 1f;
    private float rollingAngle = 0f;

    private long animationDuration;
    private long startTime;

    private boolean inProgress = false;

    private float[] baseMatrix = new float[16];
    private float[] internalMatrix;

    private AnimationCallBack delegate = null;

    private short repeatCount = 1;
    private short repeatStep = 0;

    public GLAnimation(GLRenderConsts.GLAnimationType animationType, float fromX, float toX, float fromY, float toY, float fromZ, float toZ, long animationDuration) {
        internalInit(animationType, animationDuration);

        this.fromX = fromX;
        this.toX = toX;
        this.fromY = fromY;
        this.toY = toY;
        this.fromZ = fromZ;
        this.toZ = toZ;
    }

    public GLAnimation(GLRenderConsts.GLAnimationType animationType, float rotationAngle, short rotationAxesMask, long animationDuration) {
        internalInit(animationType, animationDuration);

        this.rotationAngle = rotationAngle;
        this.rotationAxesMask = rotationAxesMask;
    }

    public GLAnimation(GLRenderConsts.GLAnimationType animationType, short rotationAxesMask, float ppX, float ppY, float objectRadius, long animationDuration) {
        internalInit(animationType, animationDuration);

        this.rotationAngle = 90f;
        this.rotationAxesMask = rotationAxesMask;
        this.ppX = ppX;
        this.ppY = ppY;
        this.objectRadius = objectRadius;
    }

    private void internalInit(GLRenderConsts.GLAnimationType animationType, long animationDuration) {
        this.animationType = animationType;
        this.animationDuration = animationDuration;
        Matrix.setIdentityM(baseMatrix, 0);
    }

    public void setBaseMatrix(float[] baseMatrix) {
        this.baseMatrix = baseMatrix;
    }
    public void setRepeatCount(short repeatCount) {
        this.repeatCount = repeatCount;
    }

    public float getRotationAngle() {
        return rotationAngle;
    }
    public float getPpX() {
        return ppX;
    }
    public float getPpY() {
        return ppY;
    }
    public float getObjectRadius() {
        return objectRadius;
    }
    public float getRollingAngle() {
        return rollingAngle;
    }
    public short getRepeatStep() {
        return repeatStep;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    private long getFrameCount() {
        return Math.round(animationDuration * 1.0f / ANIMATION_FRAME_DURATION) -1;
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
    private float getDeltaAngle() {
        return  (rotationAngle) / getFrameCount();
    }

    private long getCurrentFrame() {
        return (System.currentTimeMillis() - startTime) / ANIMATION_FRAME_DURATION;
    }

    private float getCurrentX(long frame) {
        return getDeltaX() * frame;
    }
    private float getCurrentY(long frame) {
        return getDeltaY() * frame;
    }
    private float getCurrentZ(long frame) {
        return getDeltaZ() * frame;
    }
    private float getCurrentAngle(long frame) {
        return getDeltaAngle() * frame;
    }

    public void startAnimation(AnimationCallBack delegate) {
        this.delegate = delegate;
        internalMatrix = null;
        internalMatrix = Arrays.copyOf(baseMatrix, 16);

        switch (animationType) {
            case TRANSLATE_ANIMATION:
                Matrix.translateM(internalMatrix, 0, fromX, fromY, fromZ);
                break;
        }

        startTime = System.currentTimeMillis();
        inProgress = true;
    }

    public void animate(float[] modelMatrix) {
        long currentFrame = getCurrentFrame();

        for (int i = 0; i < 16; i++)
            modelMatrix[i] = internalMatrix[i];

        switch (animationType) {
            case TRANSLATE_ANIMATION:
                Matrix.translateM(modelMatrix, 0, getCurrentX(currentFrame), getCurrentY(currentFrame), getCurrentZ(currentFrame));
                break;

            case ROTATE_ANIMATION:
                float angle = getCurrentAngle(currentFrame);

                Matrix.rotateM(modelMatrix, 0, angle,
                        ((rotationAxesMask & ROTATE_BY_X) != 0) ? 1 : 0,
                        ((rotationAxesMask & ROTATE_BY_Y) != 0) ? 1 : 0,
                        ((rotationAxesMask & ROTATE_BY_Z) != 0) ? 1 : 0);

                break;

            case ROLL_ANIMATION:
                rollingAngle = getCurrentAngle(currentFrame);

                //float radius = 0.1f;
                float angleCos = (float)(Math.cos(rollingAngle*Math.PI/180) /* * radius*/);
                float angleSin = (float)(Math.sin(rollingAngle*Math.PI/180) /* * radius*/);
                float offsetX = angleCos * 1.0f - 1.0f;
                float offsetY = -angleSin * 1.0f; //angleSin - angleCos;

                Matrix.rotateM(modelMatrix, 0, rollingAngle,
                        ((rotationAxesMask & ROTATE_BY_X) != 0) ? 1 : 0,
                        ((rotationAxesMask & ROTATE_BY_Y) != 0) ? 1 : 0,
                        ((rotationAxesMask & ROTATE_BY_Z) != 0) ? 1 : 0);

                Matrix.translateM(modelMatrix, 0, -offsetX, -offsetY, offsetY);

                break;
        }

        inProgress = currentFrame < (getFrameCount() - 1);

        if (!inProgress) {
            repeatCount--;

            if (repeatCount > 0) {
                for (int i = 0; i < 16; i++)
                    internalMatrix[i] = modelMatrix[i];

                repeatStep++;
                startTime = System.currentTimeMillis();
                inProgress = true;
            }
            else
                 if (delegate != null)
                    delegate.onAnimationEnd();
        }
    }

    public interface AnimationCallBack {
        void onAnimationEnd();
    }

}
