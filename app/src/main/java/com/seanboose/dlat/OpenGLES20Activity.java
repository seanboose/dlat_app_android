package com.seanboose.dlat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import java.io.IOException;


/**
 * Created by Sean Boose on 11/6/15.
 */
public class OpenGLES20Activity extends Activity implements SensorEventListener{

    private MyGLSurfaceView mGLView;
    private float[] mRotationMatrix;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer = null;
    private Sensor mMagnetometer = null;
    private Sensor mLightMeter = null;
    private float[] mAccelValues;
    private float[] mMagValues;
    private float mLightValue = 0.0f;

    private float mMaxAccel = 10;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);

        Log.v("dlat", "created OpenGLES20 activity");

        mRotationMatrix = new float[9];
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelValues = new float[3];
        mMagValues = new float[3];

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            mLightMeter = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mLightMeter, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor == mAccelerometer){
            mAccelValues = event.values;
            mAccelValues[0] = 5 * mAccelValues[0] / mMaxAccel;
            mAccelValues[1] = 5 * mAccelValues[1] / mMaxAccel;
            mAccelValues[2] = 5 * mAccelValues[2] / mMaxAccel;
            mAccelValues[0] = Math.min(1.0f, mAccelValues[0]);
            mAccelValues[1] = Math.min(1.0f, mAccelValues[1]);
            mAccelValues[2] = Math.min(1.0f, mAccelValues[2]);
            mGLView.mRenderer.updateBackgroundColor(mAccelValues);
        }
        if(event.sensor == mMagnetometer){
            mMagValues = event.values;
        }
        if(event.sensor == mLightMeter){
            Log.v("light:", "" + event.values[0]);
            mLightValue = event.values[0] / 1000.0f;
            float[] squareColors =  {mLightValue, mLightValue, mLightValue, 1.0f};
            mGLView.mRenderer.updateSquareColor(squareColors);
        }

        if(mAccelerometer != null && mMagnetometer != null) {
            mSensorManager.getRotationMatrix(mRotationMatrix, null, mAccelValues, mMagValues);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    public void onResume() {
        super.onResume();
        Log.v("dlat", "onResume()");
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mLightMeter, SensorManager.SENSOR_DELAY_NORMAL);

//        mGLView.mRenderer.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v("dlat", "onPause()");
        mSensorManager.unregisterListener(this);
        mGLView.mRenderer.onPause();
    }

//    @Override
//    public void onBackPressed() {
//        mGLView.mRenderer.onPause();
//        super.onBackPressed();
//        finish();
//    }


    class MyGLSurfaceView extends GLSurfaceView {

        public MyGLRenderer mRenderer;

        public MyGLSurfaceView(Context context){
            super(context);

            // Create an OpenGL ES 2.0 context
            setEGLContextClientVersion(2);

            mRenderer = new MyGLRenderer();
            setRenderer(mRenderer);
        }

        private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
        private float mPreviousX;
        private float mPreviousY;

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            // MotionEvent reports input details from the touch screen
            // and other input controls. In this case, you are only
            // interested in events where the touch position changed.

            float x = e.getX();
            float y = e.getY();

            switch (e.getAction()) {
                case MotionEvent.ACTION_MOVE:

                    float dx = x - mPreviousX;
                    float dy = y - mPreviousY;

                    // reverse direction of rotation above the mid-line
                    if (y > getHeight() / 2) {
                        dx = dx * -1 ;
                    }

                    // reverse direction of rotation to left of the mid-line
                    if (x < getWidth() / 2) {
                        dy = dy * -1 ;
                    }

                    mRenderer.setTriangleAngle(
                            mRenderer.getTriangleAngle() +
                                    ((dx + dy) * TOUCH_SCALE_FACTOR));
                    requestRender();
            }
            mPreviousX = x;
            mPreviousY = y;
            return true;
        }
    }


}