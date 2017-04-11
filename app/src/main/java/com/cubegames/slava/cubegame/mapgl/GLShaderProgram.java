package com.cubegames.slava.cubegame.mapgl;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glUseProgram;
import static com.cubegames.slava.cubegame.Utils.readTextFromRaw;

public abstract class GLShaderProgram {

    private int programId;
    protected Map<String, GLShaderParam> params = new HashMap<>();

    public GLShaderProgram(Context context, int vShaderId, int fShaderId) {
        programId = createProgram(createShader(context, GL_VERTEX_SHADER, vShaderId),
                                  createShader(context, GL_FRAGMENT_SHADER, fShaderId));

        createParams();
    }

    public int getProgramId() {
        return programId;
    }

    public void useProgram() {
        glUseProgram(programId);
    }
    public abstract void createParams();
    public GLShaderParam paramByName (String name) {
        return params.get(name);
    }

    public static int createProgram(int vertexShaderId, int fragmentShaderId) {
        final int programId = glCreateProgram();
        if (programId == 0) {
            return 0;
        }

        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);

        glLinkProgram(programId);
        final int[] linkStatus = new int[1];
        glGetProgramiv(programId, GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            glDeleteProgram(programId);
            return 0;
        }
        return programId;
    }

    public static int createShader(Context context, int type, int shaderRawId) {
        return createShader(type, readTextFromRaw(context, shaderRawId));
    }

    static int createShader(int type, String shaderText) {
        final int shaderId = glCreateShader(type);
        if (shaderId == 0) {
            return 0;
        }
        glShaderSource(shaderId, shaderText);
        glCompileShader(shaderId);
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderId, GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            glDeleteShader(shaderId);
            return 0;
        }
        return shaderId;
    }
}
