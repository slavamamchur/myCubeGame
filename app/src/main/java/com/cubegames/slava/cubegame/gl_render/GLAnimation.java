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
    private float scaleFactor = 1f;

    private long animationDuration;
    private long startTime;

    private boolean inProgress = false;

    private float[] baseMatrix = new float[16];
    private float[] internalMatrix;

    private AnimationCallBack delegate = null;

    private short repeatCount = 1;

    public GLAnimation(GLRenderConsts.GLAnimationType animationType, float fromX, float toX, float fromY, float toY, float fromZ, float toZ, long animationDuration) {
        internalInit(animationType, animationDuration);

        this.fromX = fromX;
        this.toX = toX;
        this.fromY = fromY;
        this.toY = toY;
        this.fromZ = fromZ;
        this.toZ = toZ;
    }

    public GLAnimation(GLRenderConsts.GLAnimationType animationType, float rotationAngle, short rotationAxesMask, float scaleFactor, long animationDuration) {
        internalInit(animationType, animationDuration);

        this.rotationAngle = rotationAngle;
        this.rotationAxesMask = rotationAxesMask;
        this.scaleFactor = scaleFactor;
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
                //TODO: by pivot point
                float angle = getCurrentAngle(currentFrame);
                float radius = (float) Math.sqrt(Math.pow(scaleFactor, 2) + Math.pow(scaleFactor, 2));
                float angleCos = (float)(Math.cos(angle*Math.PI/180) * radius);
                float angleSin = (float)(Math.sin(angle*Math.PI/180) * radius);
                float offsetX = angleCos + angleSin;
                float offsetY = angleSin - angleCos;

                Matrix.translateM(modelMatrix, 0, -offsetX, -offsetY, 0);
                ///System.out.println(offsetX + ", " + offsetY);

                Matrix.rotateM(modelMatrix, 0, angle,
                        ((rotationAxesMask & ROTATE_BY_X) != 0) ? 1 : 0,
                        ((rotationAxesMask & ROTATE_BY_Y) != 0) ? 1 : 0,
                        ((rotationAxesMask & ROTATE_BY_Z) != 0) ? 1 : 0);

                break;
        }

        inProgress = currentFrame < (getFrameCount() - 1);

        if (!inProgress) {
            repeatCount--;

            if (repeatCount > 0) {
                for (int i = 0; i < 16; i++)
                    internalMatrix[i] = modelMatrix[i];

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
