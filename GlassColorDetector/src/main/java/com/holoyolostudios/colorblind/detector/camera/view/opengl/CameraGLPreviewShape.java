package com.holoyolostudios.colorblind.detector.camera.view.opengl;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by martin on 8/13/13.
 */
public class CameraGLPreviewShape {

    // Constants
    private static final int COORDS_PER_VERTEX = 3;
    private static final int VERTEX_STRIDE = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    // Square shape
    private static float squareCoords[] = { // counterclockwise
            1.0f, -1.0f, 0.0f,  // Bottom right
            -1.0f, -1.0f, 0.0f, // Bottom left
            1.0f, 1.0f, 0.0f,   // Top right
            -1.0f, 1.0f, 0.0f,  // Top left
    };

    // Texture face
    private static float textureCoords[] = {
            1.0f, 1.0f, 0.0f,   // Top right
            0.0f, 1.0f, 0.0f,  // Top left
            1.0f, 0.0f, 0.0f,  // Bottom right
            0.0f, 0.0f, 0.0f, // Bottom left
    };

    // Drawing order to specify face
    private static final short mDrawOrder[] = {0, 1, 2, 1, 2, 3};

    // Shaders
    private final String mVertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec2 inputTextureCoordinate;" +
                    "varying vec2 textureCoordinate;" +
                    "void main()" +
                    "{" +
                    "gl_Position = vPosition * uMVPMatrix;" +
                    "textureCoordinate = inputTextureCoordinate;" +
                    "}";

    private final String mFragmentShaderCode =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;" +
                    "varying vec2 textureCoordinate;                            \n" +
                    "uniform samplerExternalOES s_texture;               \n" +
                    "void main() {" +
                    "  gl_FragColor = texture2D( s_texture, textureCoordinate );\n" +
                    "}";

    // Members
    private int mProgram = -1;
    private int mPositionHandle = -1;
    private int mTextureHandle = -1;
    private int mMVPMatrixHandle = -1;
    private int mColorHandle = -1;
    private FloatBuffer mVertexBuffer = null;
    private FloatBuffer mTextureBuffer = null;
    private ShortBuffer mDrawListBuffer = null;

    /**
     * Constructor
     */
    public CameraGLPreviewShape() {

        // Allocate square
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mVertexBuffer = bb.asFloatBuffer();
        mVertexBuffer.put(squareCoords);
        mVertexBuffer.position(0);

        // Allocate square
        bb = ByteBuffer.allocateDirect(textureCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mTextureBuffer = bb.asFloatBuffer();
        mTextureBuffer.put(textureCoords);
        mTextureBuffer.position(0);

        // Allocate drawing order
        ByteBuffer dlb = ByteBuffer.allocateDirect(mDrawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        mDrawListBuffer = dlb.asShortBuffer();
        mDrawListBuffer.put(mDrawOrder);
        mDrawListBuffer.position(0);

        // Create GLES20 program
        mProgram = GLES20.glCreateProgram();

        // Compile shaders
        int mVertexShader = loadShader(GLES20.GL_VERTEX_SHADER, mVertexShaderCode);
        int mFragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, mFragmentShaderCode);

        GLES20.glAttachShader(mProgram, mVertexShader);
        GLES20.glAttachShader(mProgram, mFragmentShader);
        GLES20.glLinkProgram(mProgram);

    }

    public void onPrepare(int texture) {

        // Select the program to use for drawing
        GLES20.glUseProgram(mProgram);
        checkGlError("glUseProgram(" + mProgram + ")");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        checkGlError("glActiveTexture(" + GLES20.GL_TEXTURE0 + ")");

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture);
        checkGlError("glBindTexture(" + GLES11Ext.GL_TEXTURE_EXTERNAL_OES + ", " + texture + ")");

        // Get the handle to the vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        checkGlError("glGetAttribLocation(" + mProgram + ", \"vPosition\")");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        checkGlError("glEnableVertexAttribArray(" + mPositionHandle + ")");

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE, mVertexBuffer);
        checkGlError("glVertexAttribPointer(" + mPositionHandle + ")");

        // Setup texture
        mTextureHandle = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
        checkGlError("glGetAttribLocation(" + mProgram + ", \"inputTextureCoordinate\")");
        GLES20.glEnableVertexAttribArray(mTextureHandle);
        checkGlError("glEnableVertexAttribArray(" + mTextureHandle + ")");
        GLES20.glVertexAttribPointer(mTextureHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE, mTextureBuffer);
        checkGlError("glVertexAttribPointer(" + mTextureHandle + ")");

        // Reference color
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "s_texture");
        checkGlError("glGetUniformLocation(" + mProgram + ")");

        GLES20.glUniform1i(mColorHandle, 0);
    }

    /**
     * Compile a shader for use with the GL context
     *
     * @param type       int
     * @param shaderCode String
     * @return int
     */
    public static int loadShader(int type, String shaderCode) {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * Handle drawing self to canvas
     *
     * @param trans_mtx
     */
    public void draw(float[] trans_mtx) {

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        checkGlError("glGetUniformLocation(" + mProgram + ", uMVPMatrix)");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, trans_mtx, 0);
        checkGlError("glUniformMatrix4fv(" + mMVPMatrixHandle + ", 1, false, " + trans_mtx.toString() + ", 0");

        // Draw
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mDrawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, mDrawListBuffer);

    }

    /**
     * Any specific tear-down instructions here
     */
    public void shutdown() {
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureHandle);
        GLES20.glDisableVertexAttribArray(mColorHandle);
        if (mVertexBuffer != null) {
            mVertexBuffer.clear();
            mVertexBuffer = null;
        }
        if (mTextureBuffer != null) {
            mTextureBuffer.clear();
            mTextureBuffer = null;
        }
        if (mDrawListBuffer != null) {
            mDrawListBuffer.clear();
            mDrawListBuffer = null;
        }
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     * <p/>
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("GLCameraPreviewShape", glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

}

