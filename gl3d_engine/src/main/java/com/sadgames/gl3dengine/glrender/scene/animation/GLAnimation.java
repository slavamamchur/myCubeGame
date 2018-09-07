package com.sadgames.gl3dengine.glrender.scene.animation;

import org.luaj.vm2.Globals;

import javax.vecmath.Vector3f;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLAnimationType;

public class GLAnimation {
    public final static short ROTATE_BY_X = 0b001;
    public final static short ROTATE_BY_Y = 0b010;
    public final static short ROTATE_BY_Z = 0b100;

    private GLAnimationType animationType;
    private AnimationCallBack delegate = null;
    private Globals luaEngine = null;
    private String luaDelegate = null;

    private float fromX;
    private float toX;
    private float fromY;
    private float toY;
    private float fromZ;
    private float toZ;
    private float zoomLevel;
    private float rotationAngle = 0;
    private short rotationAxesMask = 0;
    private long animationDuration;
    private long startTime;
    private boolean inProgress = false;
    private short repeatCount = 0;
    private short repeatStep = 0;

    public GLAnimation(float fromX, float toX, float fromY, float toY, float fromZ, float toZ, long animationDuration) {
        internalInit(GLAnimationType.TRANSLATE_ANIMATION, animationDuration);

        this.fromX = fromX;
        this.toX = toX;
        this.fromY = fromY;
        this.toY = toY;
        this.fromZ = fromZ;
        this.toZ = toZ;
    }

    public GLAnimation(float zoomLevel, long animationDuration) {
        internalInit(GLAnimationType.ZOOM_ANIMATION, animationDuration);

        this.zoomLevel = zoomLevel - 1f;
    }

    public GLAnimation(float rotationAngle, short rotationAxesMask, long animationDuration) {
        internalInit(GLAnimationType.ROTATE_ANIMATION, animationDuration);

        this.rotationAngle = rotationAngle;
        this.rotationAxesMask = rotationAxesMask;
    }

    private void internalInit(GLAnimationType animationType, long animationDuration) {
        this.animationType = animationType;
        this.animationDuration = animationDuration;
    }

    public Globals getLuaEngine() {
        return luaEngine;
    }
    public void setLuaEngine(Globals luaEngine) {
        this.luaEngine = luaEngine;
    }

    public void setRepeatCount(short repeatCount) {
        this.repeatCount = repeatCount;
    }
    public float getRotationAngle() {
        return rotationAngle;
    }
    public short getRepeatStep() {
        return repeatStep;
    }
    public boolean isInProgress() {
        return inProgress;
    }

    private float getCurrentProgress() {
        return (System.currentTimeMillis() - startTime) * 1.0f / animationDuration;
    }

    private float getCurrentX(float currentPos) {
        return (toX - fromX) * currentPos;
    }
    private float getCurrentY(float currentPos) {
        return (toY - fromY) * currentPos;
    }
    private float getCurrentZ(float currentPos) {
        return (toZ - fromZ) * currentPos;
    }
    private float getCurrentAngle(float currentPos) {
        return rotationAngle * currentPos;
    }
    private float getCurrentZoom(float currentPos) {
        return 1f + zoomLevel * currentPos;
    }

    public void startAnimation(IAnimatedObject animatedObject, AnimationCallBack delegate) {
        this.delegate = delegate;

        switch (animationType) {
            case TRANSLATE_ANIMATION:
                animatedObject.setPosition(new Vector3f(fromX, fromY, fromZ));

                break;
        }

        startTime = System.currentTimeMillis();
        inProgress = true;
    }


    @SuppressWarnings("unused") public void startAnimation(IAnimatedObject animatedObject, String luaDelegate) {
        this.luaDelegate = luaDelegate;

        switch (animationType) {
            case TRANSLATE_ANIMATION:
                animatedObject.setPosition(new Vector3f(fromX, fromY, fromZ));

                break;
        }

        startTime = System.currentTimeMillis();
        inProgress = true;
    }

    public void animate(IAnimatedObject animatedObject) {
        float currentPos = getCurrentProgress();
        currentPos = currentPos > 1.0f ? 1.0f : currentPos;

        switch (animationType) {
            case TRANSLATE_ANIMATION:
                animatedObject.setPosition(new Vector3f(fromX + getCurrentX(currentPos),
                                                        fromY + getCurrentY(currentPos),
                                                        fromZ + getCurrentZ(currentPos)));

                break;

            case ZOOM_ANIMATION:
                animatedObject.setZoomLevel(getCurrentZoom(currentPos));

                break;

            case ROTATE_ANIMATION:
                animatedObject.setRotation(getCurrentAngle(currentPos), rotationAxesMask);

                break;

        }

        inProgress = currentPos < 1.0f;

        if (!inProgress) {
            repeatCount--;

            if (repeatCount >= 0) {
                repeatStep++;
                startTime = System.currentTimeMillis();
                inProgress = true;
            }
            else {
                animatedObject.onAnimationEnd();

                if (delegate != null)
                    delegate.onAnimationEnd();
                else if (luaEngine != null && luaDelegate != null)
                    luaEngine.get(luaDelegate).call();
            }
        }
    }

    public interface AnimationCallBack {
        void onAnimationEnd();
    }

    public interface IAnimatedObject {
        float[] getTransformationMatrix();
        void setPosition(Vector3f position);
        void setRotation(float angle, short rotationAxesMask);
        void setZoomLevel(float zoomLevel);
        void onAnimationEnd();
    }
}
