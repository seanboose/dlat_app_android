package com.seanboose.dlat;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import javax.microedition.khronos.opengles.GL10;


/**
 * Created by Sean Boose on 11/6/15.
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Triangle mTriangle;
    private Square mSquare;
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private float[] mTriangleRotationMatrix = new float[16];
    private float[] mSquareRotationMatrix = new float[16];

    private float mBGR = 0.0f;
    private float mBGG = 0.0f;
    private float mBGB = 0.0f;
    private float mBGRDirection = 1.0f;
    private float mBGGDirection = 2.0f;
    private float mBGBDirection = 3.0f;

    public volatile float mTriangleAngle;



    public void onDrawFrame(GL10 unused) {

        float[] triangleScratch = new float[16];
        float[] squareScratch = new float[16];

        updateBackgroundColor();
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // Draw shape
//        mTriangle.draw(mMVPMatrix);


        // Create a rotation transformation for the square
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);
        Matrix.setRotateM(mSquareRotationMatrix, 0, angle, 0, 0, -1.0f);

        Matrix.setRotateM(mTriangleRotationMatrix, 0, mTriangleAngle, 0, 0, -1.0f);

        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(triangleScratch, 0, mMVPMatrix, 0, mTriangleRotationMatrix, 0);
        Matrix.multiplyMM(squareScratch, 0, mMVPMatrix, 0, mSquareRotationMatrix, 0);

        // Draw shapes
        mSquare.draw(squareScratch);
        mTriangle.draw(triangleScratch);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
        // Set the background frame color
//        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES30.glClearColor(mBGR, mBGG, mBGB, 1.0f);

        mTriangle = new Triangle();
        mSquare = new Square();
    }


    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES30.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES30.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES30.GL_FRAGMENT_SHADER)
        int shader = GLES30.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);

        return shader;
    }

    public void updateBackgroundColor(float[] values){
        mBGR = values[0];
        mBGG = values[1];
        mBGB = values[2];
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
        GLES30.glClearColor(mBGR, mBGG, mBGB, 1.0f);
    }


    public float getTriangleAngle() {
        return mTriangleAngle;
    }

    public void setTriangleAngle(float angle) {
        mTriangleAngle = angle;
    }
}