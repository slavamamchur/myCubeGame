package com.cubegames.slava.cubegame.gl_render;

import android.opengl.Matrix;

import java.util.Arrays;

import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.ANIMATION_FRAME_DURATION;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLAnimationType.ROLL_ANIMATION;

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

    public GLAnimation(GLRenderConsts.GLAnimationType animationType, float fromY, float fromZ, float toZ, long animationDuration) {
        internalInit(animationType, animationDuration);

        this.fromY = fromY;
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
            /*case ZOOM_ANIMATION:
                Matrix.translateM(internalMatrix, 0, 0, fromY, fromZ);
                break;*/
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

            case ZOOM_ANIMATION:
                float currentZ = fromZ + getCurrentZ(currentFrame);
                float currentY = currentZ * fromY / fromZ;
                Matrix.translateM(modelMatrix, 0, 0, currentY - fromY, currentZ - fromZ);
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
                //Matrix.rotateM(modelMatrix, 0, -80, 0, 1, 0);

                if (repeatStep < 2) {
                    Matrix.translateM(modelMatrix, 0, -0.1f * (repeatStep + 1), 0f, 0f);
                    Matrix.rotateM(modelMatrix, 0, (90 * repeatStep) + rollingAngle, 0, 0, 1); //vector
                    Matrix.translateM(modelMatrix, 0, 0.1f * (repeatStep + 1), 0f, 0f);
                }
                else  if (repeatStep < 3){
                    Matrix.translateM(modelMatrix, 0, -0.1f * (repeatStep + 1), 0.2f, 0f);
                    Matrix.rotateM(modelMatrix, 0, (90 * repeatStep) + rollingAngle, 0, 0, 1);
                    Matrix.translateM(modelMatrix, 0, 0.1f * (repeatStep + 1), -0.2f, 0f);
                }
                else {
                    Matrix.translateM(modelMatrix, 0, -0.2f * (repeatStep + 1), 0f, 0f);
                    Matrix.rotateM(modelMatrix, 0, (90 * repeatStep) + rollingAngle, 0, 0, 1);
                    Matrix.translateM(modelMatrix, 0, -0.1f * (repeatStep + 1), 0f, 0f);
                }

                break;
        }

        inProgress = currentFrame < (getFrameCount() - 1);

        if (!inProgress) {
            repeatCount--;

            if (repeatCount > 0) {
                if (!animationType.equals(ROLL_ANIMATION))
                    for (int i = 0; i < 16; i++)
                        internalMatrix[i] = modelMatrix[i];
                //else
                    //internalMatrix[12] = 0.2f;

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
