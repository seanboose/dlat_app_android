package com.seanboose.dlat;

import android.opengl.EGLConfig;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import javax.microedition.khronos.opengles.GL10;


/**
 * Created by Sean Boose on 11/6/15.
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Triangle mTriangle;
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private float[] mRotationMatrix = new float[16];

    private float mBGR = 0.0f;
    private float mBGG = 0.0f;
    private float mBGB = 0.0f;
    private float mBGRDirection = 1.0f;
    private float mBGGDirection = 2.0f;
    private float mBGBDirection = 3.0f;


    public void onDrawFrame(GL10 unused) {

        float[] scratch = new float[16];

        updateBackgroundColor();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // Draw shape
//        mTriangle.draw(mMVPMatrix);


        // Create a rotation transformation for the triangle
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);
        Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 0, -1.0f);

        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        // Draw triangle
        mTriangle.draw(scratch);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
        // Set the background frame color
//        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClearColor(mBGR, mBGG, mBGB, 1.0f);

        mTriangle = new Triangle();
    }


    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    private void updateBackgroundColor(){
        // Redraw background color
        mBGR = mBGR + 0.01f*mBGRDirection;
        mBGG = mBGG + 0.01f*mBGGDirection;
        mBGB = mBGB + 0.01f*mBGBDirection;

        // Red scale control
        if(mBGR >= .99f && mBGRDirection > 0.0f){
            mBGRDirection = -1.0f;
            mBGR = 1.0f;
        }
        else if(mBGR <= .01f && mBGRDirection < 0.0f){
            mBGRDirection = 1.0f;
            mBGR = 0.0f;
        }

        // Green scale control
        if(mBGG >= .99f && mBGGDirection > 0.0f){
            mBGGDirection = -2.0f;
            mBGG = 1.0f;
        }
        else if(mBGG <= .01f && mBGGDirection < 0.0f){
            mBGGDirection = 2.0f;
            mBGG = 0.0f;
        }

        // Blue scale control
        if(mBGB >= .99f && mBGBDirection > 0.0f){
            mBGBDirection = -3.0f;
            mBGB = 1.0f;
        }
        else if(mBGB <= .01f && mBGBDirection < 0.0f){
            mBGBDirection = 3.0f;
            mBGB = 0.0f;
        }
        GLES20.glClearColor(mBGR, mBGG, mBGB, 1.0f);
    }

}