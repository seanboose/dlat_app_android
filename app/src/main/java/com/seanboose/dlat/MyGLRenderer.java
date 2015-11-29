package com.seanboose.dlat;

import android.media.MediaRecorder;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;


/**
 * Created by Sean Boose on 11/6/15.
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Triangle mTriangleFront;
    private Triangle mTriangleBack;
    private Square mSquare;
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private float[] mTriangleFrontRotationMatrix = new float[16];
    private float[] mTriangleBackRotationMatrix = new float[16];
    private float[] mSquareRotationMatrix = new float[16];
    private float[] mSquareColors = new float[4];

    private SoundMeter mSoundMeter;

    private float mBGR = 0.0f;
    private float mBGG = 0.0f;
    private float mBGB = 0.0f;

    private float mTriR = 0.0f;
    private float mTriG = 0.0f;
    private float mTriB = 0.0f;
    private float mTriRDirection = 1.0f;
    private float mTriGDirection = 2.0f;
    private float mTriBDirection = 3.0f;

    private boolean paused = false;

    public volatile float mTriangleFrontAngle;

    public void onDrawFrame(GL10 unused) {

        float[] triangleFrontScratch = new float[16];
        float[] triangleBackScratch = new float[16];
        float[] squareScratch = new float[16];

        updateBackTriangleColor();
        updateBackgroundColor();
        mSquare.updateColors(mSquareColors);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // Draw shape
//        mTriangleFront.draw(mMVPMatrix);


        // Create a rotation transformation for the square
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);
        Matrix.setRotateM(mSquareRotationMatrix, 0, angle, 0, 0, -1.0f);
        Matrix.setRotateM(mTriangleBackRotationMatrix, 0, -angle, 0, 0, -1.0f);

        Matrix.setRotateM(mTriangleFrontRotationMatrix, 0, mTriangleFrontAngle, 0, 0, -1.0f);

        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(triangleFrontScratch, 0, mMVPMatrix, 0, mTriangleFrontRotationMatrix, 0);
        Matrix.multiplyMM(triangleBackScratch, 0, mMVPMatrix, 0, mTriangleBackRotationMatrix, 0);
        Matrix.multiplyMM(squareScratch, 0, mMVPMatrix, 0, mSquareRotationMatrix, 0);

        if(!paused && mSoundMeter.isOn()) {
            float scale = (float)mSoundMeter.getAmplitudeEMA();
            float[] colors = {scale*1.0f, scale*1.0f, scale*1.0f, 1.0f};
            mTriangleFront.updateColors(colors);
        }


        // Draw shapes
        mSquare.draw(squareScratch);
        mTriangleBack.draw(triangleBackScratch);
        mTriangleFront.draw(triangleFrontScratch);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
        Log.v("MyGLRenderer", "onSurfaceCreated");
        // Set the background frame color
        GLES30.glClearColor(mBGR, mBGG, mBGB, 1.0f);

        mTriangleFront = new Triangle();
        mTriangleBack = new Triangle();
        float[] backColors = {1.0f, 1.0f, 1.0f, 0.5f};
        mTriangleBack.updateColors(backColors);
        mSquare = new Square();

        mSoundMeter = new SoundMeter();
        try{mSoundMeter.start();}
        catch(IOException e){e.printStackTrace();}

    }


    public void onSurfaceChanged(GL10 unused, int width, int height) {
        Log.v("MyGLRenderer", "onSurfaceChanged");
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

    public void updateBackgroundColor(){
        GLES30.glClearColor(mBGR, mBGG, mBGB, 1.0f);
    }

    private void updateBackTriangleColor(){
        // Redraw background color
        mTriR = mTriR + 0.01f*mTriRDirection;
        mTriG = mTriG + 0.01f*mTriGDirection;
        mTriB = mTriB + 0.01f*mTriBDirection;

        // Red scale control
        if(mTriR >= .99f && mTriRDirection > 0.0f){
            mTriRDirection = -1.0f;
            mTriR = 1.0f;
        }
        else if(mTriR <= .01f && mTriRDirection < 0.0f){
            mTriRDirection = 1.0f;
            mTriR = 0.0f;
        }

        // Green scale control
        if(mTriG >= .99f && mTriGDirection > 0.0f){
            mTriGDirection = -2.0f;
            mTriG = 1.0f;
        }
        else if(mTriG <= .01f && mTriGDirection < 0.0f){
            mTriGDirection = 2.0f;
            mTriG = 0.0f;
        }

        // Blue scale control
        if(mTriB >= .99f && mTriBDirection > 0.0f){
            mTriBDirection = -3.0f;
            mTriB = 1.0f;
        }
        else if(mTriB <= .01f && mTriBDirection < 0.0f){
            mTriBDirection = 3.0f;
            mTriB = 0.0f;
        }
        float[] colors = {mTriR, mTriG, mTriB, 1.0f};
        mTriangleBack.updateColors(colors);
    }

    public void updateSquareColor(float[] colors){
        mSquareColors = colors;
    }

    public float getTriangleAngle() {
        return mTriangleFrontAngle;
    }

    public void setTriangleAngle(float angle) {
        mTriangleFrontAngle = angle;
    }

    public void onPause(){
        Log.v("MyGLRenderer", "onPause");
        paused = true;
        if(mSoundMeter != null) {
            mSoundMeter.stop();
        }
    }

    public void onResume(){
        Log.v("MyGLRenderer", "onResume");
        paused = false;
        if(mSoundMeter != null) {
            try {
                mSoundMeter.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class SoundMeter {
        static final private double EMA_FILTER = 0.2;

        private MediaRecorder mRecorder = null;
        private double mEMA = 0.0;

        public void start() throws IOException {
            Log.v("SoundMeter", "start()");
            if (mRecorder == null) {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setOutputFile("/dev/null");
                mRecorder.prepare();
                mRecorder.start();
                mEMA = 0.0;
            }
            else {
                Log.v("SoundMeter", "called start without mRecorder!=null");
            }
        }
        public void stop() {
            Log.v("SoundMeter", "stop()");
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
            }
            else{
                Log.v("SoundMeter", "called stop with mRecorder == null");
            }

            Log.v("SoundMeter", "Leaving stop()");
        }
        public double getAmplitude() {
            if (mRecorder != null)
                return  (mRecorder.getMaxAmplitude()/2700.0);
            else
                return 0;
        }
        public double getAmplitudeEMA() {
            double amp = getAmplitude();
            mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
            return mEMA;
        }

        public boolean isOn(){
            return mRecorder != null;
        }
    }

}