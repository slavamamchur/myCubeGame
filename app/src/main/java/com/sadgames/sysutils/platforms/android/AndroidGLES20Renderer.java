package com.sadgames.sysutils.platforms.android;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.View;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidGL20;
import com.badlogic.gdx.backends.android.surfaceview.GdxEglConfigChooser;
import com.badlogic.gdx.math.WindowedMean;
import com.sadgames.dicegame.ui.framework.MapGLSurfaceView;
import com.sadgames.gl3dengine.glrender.GLES20JniWrapper;
import com.sadgames.gl3dengine.glrender.GLRendererInterface;
import com.sadgames.gl3dengine.glrender.GdxExt;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

public class AndroidGLES20Renderer implements GLSurfaceView.Renderer { //TODO: replace with AndroidGraphics from libGDX

    public static volatile boolean enforceContinuousRendering = false;

    private GLRendererInterface glInternalRenderer;
    private final Context app;
    private EGLContext eglContext;
    private final View view;
    private boolean isContinuous = true;
    private volatile boolean created = false;
    private volatile boolean running = false;
    private volatile boolean pause = false;
    private volatile boolean resume = false;
    private volatile boolean destroy = false;

    protected final AndroidApplicationConfiguration config;
    protected WindowedMean mean = new WindowedMean(5);

    public Object synch = new Object();

    public AndroidGLES20Renderer(Context context, GLRendererInterface renderer, AndroidApplicationConfiguration config,  boolean focusableView) {
        AndroidGL20.init();

        glInternalRenderer = renderer;
        app = context;
        this.config = config;
        view = createGLSurfaceView();

        preserveEGLContextOnPause();

        if (focusableView) {
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
        }
    }

    public AndroidGLES20Renderer(Context context, GLRendererInterface renderer) {
        this(context, renderer, new AndroidApplicationConfiguration(), true);
    }

    public Context getApp() {
        return app;
    }
    public EGLContext getEglContext() {
        return eglContext;
    }
    public View getView() {
        return view;
    }

    private void setupGL() {
        GdxExt.gl20 = new AndroidGL20();
        GdxExt.gl = GdxExt.gl20;  //TODO: add input

        GLES20JniWrapper.setGlEngine(GdxExt.gl);
    }

    private void preserveEGLContextOnPause() {
            try {
                view.getClass().getMethod("setPreserveEGLContextOnPause", boolean.class).invoke(view, true);
            } catch (Exception e) {}
    }

    protected GLSurfaceView.EGLConfigChooser getEglConfigChooser () {
        return new GdxEglConfigChooser(config.r, config.g, config.b, config.a, config.depth, config.stencil, config.numSamples);
    }

    protected View createGLSurfaceView() {
        GLSurfaceView.EGLConfigChooser configChooser = getEglConfigChooser();
        GLSurfaceView view = new MapGLSurfaceView(app, config.resolutionStrategy);

        if (configChooser != null)
            view.setEGLConfigChooser(configChooser);
        else
            view.setEGLConfigChooser(config.r, config.g, config.b, config.a, config.depth, config.stencil);

        view.setRenderer(this);

        return view;
    }

    public void setContinuousRendering (boolean isContinuous) {
        if (view != null) {
            // ignore setContinuousRendering(false) while pausing
            this.isContinuous = enforceContinuousRendering || isContinuous;
            int renderMode = this.isContinuous ? GLSurfaceView.RENDERMODE_CONTINUOUSLY : GLSurfaceView.RENDERMODE_WHEN_DIRTY;

            ((GLSurfaceView)view).setRenderMode(renderMode);
            mean.clear();
        }
    }

    public boolean isContinuousRendering () {
        return isContinuous;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        eglContext = ((EGL10)EGLContext.getEGL()).eglGetCurrentContext();
        setupGL();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (created == false) {
            glInternalRenderer.onSurfaceCreated();
            created = true;
            synchronized (this) {
                running = true;
            }
        }

        glInternalRenderer.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        boolean lrunning = false;
        boolean lpause = false;
        boolean ldestroy = false;
        boolean lresume = false;

        synchronized (synch) {
            lrunning = running;
            lpause = pause;
            ldestroy = destroy;
            lresume = resume;

            if (resume) {
                resume = false;
            }

            if (pause) {
                pause = false;
                synch.notifyAll();
            }

            if (destroy) {
                destroy = false;
                synch.notifyAll();
            }
        }

        if (lresume) {
            /*SnapshotArray<LifecycleListener> lifecycleListeners = app.getLifecycleListeners();
            synchronized (lifecycleListeners) {
                LifecycleListener[] listeners = lifecycleListeners.begin();
                for (int i = 0, n = lifecycleListeners.size; i < n; ++i) {
                    listeners[i].resume();
                }
                lifecycleListeners.end();
            }
            app.getApplicationListener().resume();
            Gdx.app.log(LOG_TAG, "resumed");*/
        }

        if (lrunning) {
            /*synchronized (app.getRunnables()) {
                app.getExecutedRunnables().clear();
                app.getExecutedRunnables().addAll(app.getRunnables());
                app.getRunnables().clear();
            }

            for (int i = 0; i < app.getExecutedRunnables().size; i++) {
                try {
                    app.getExecutedRunnables().get(i).run();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }*/

            /*app.getInput().processEvents();
            frameId++;*/

            glInternalRenderer.onDrawFrame();
        }

        if (lpause) {
            /*SnapshotArray<LifecycleListener> lifecycleListeners = app.getLifecycleListeners();
            synchronized (lifecycleListeners) {
                LifecycleListener[] listeners = lifecycleListeners.begin();
                for (int i = 0, n = lifecycleListeners.size; i < n; ++i) {
                    listeners[i].pause();
                }
            }
            app.getApplicationListener().pause();
            Gdx.app.log(LOG_TAG, "paused");*/
        }

        if (ldestroy) {
            /*SnapshotArray<LifecycleListener> lifecycleListeners = app.getLifecycleListeners();
            synchronized (lifecycleListeners) {
                LifecycleListener[] listeners = lifecycleListeners.begin();
                for (int i = 0, n = lifecycleListeners.size; i < n; ++i) {
                    listeners[i].dispose();
                }
            }
            app.getApplicationListener().dispose();
            Gdx.app.log(LOG_TAG, "destroyed");*/

            glInternalRenderer.onDispose();
        }
    }

    public void resume () {
        synchronized (synch) {
            running = true;
            resume = true;
        }
    }

    public void pause () {
        synchronized (synch) {
            if (!running) return;
            running = false;
            pause = true;
            while (pause) {
                try {
                    // TODO: fix deadlock race condition with quick resume/pause.
                    // Temporary workaround:
                    // Android ANR time is 5 seconds, so wait up to 4 seconds before assuming
                    // deadlock and killing process. This can easily be triggered by opening the
                    // Recent Apps list and then double-tapping the Recent Apps button with
                    // ~500ms between taps.
                    synch.wait(4000);
                    if (pause) {
                        // pause will never go false if onDrawFrame is never called by the GLThread
                        // when entering this method, we MUST enforce continuous rendering
                        //Gdx.app.error(LOG_TAG, "waiting for pause synchronization took too long; assuming deadlock and killing");
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                } catch (InterruptedException ignored) {
                    //Gdx.app.log(LOG_TAG, "waiting for pause synchronization failed!");
                }
            }
        }
    }

    public void destroy () {
        synchronized (synch) {
            running = false;
            destroy = true;

            while (destroy) {
                try {
                    synch.wait();
                } catch (InterruptedException ex) {
                    //Gdx.app.log(LOG_TAG, "waiting for destroy synchronization failed!");
                }
            }
        }
    }

}
