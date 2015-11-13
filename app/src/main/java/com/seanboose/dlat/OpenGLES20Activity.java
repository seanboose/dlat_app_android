package com.seanboose.dlat;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by Sean Boose on 11/6/15.
 */
public class OpenGLES20Activity extends Activity implements SensorEventListener{

    private GLSurfaceView mGLView;
    private float[] mRotationMatrix;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer = null;
    private Sensor mMagnetometer = null;
    private float[] mAccelValues;
    private float[] mMagValues;


    @Override
    public void onCreate(Bundle savedInstanceState) {
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
            Log.v("dlat", "found magnetometer");
        }
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Log.v("dlat", "found accelerometer");
        }

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);


    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.v("dlat", "sensor event");
        if(event.sensor == mAccelerometer){
            mAccelValues = event.values;
            Log.v("dlat", "accelerometer event");
        }
        if(event.sensor == mMagnetometer){
            mMagValues = event.values;
            Log.v("dlat", "magnetometer event");
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
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v("dlat", "onPause()");
        mSensorManager.unregisterListener(this);
    }


    class MyGLSurfaceView extends GLSurfaceView {//implements SensorEventListener {

        private final MyGLRenderer mRenderer;

        public MyGLSurfaceView(Context context){
            super(context);

            // Create an OpenGL ES 2.0 context
            setEGLContextClientVersion(2);

            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mAccelValues = new float[3];
            mMagValues = new float[3];
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
                mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                Log.v("dlat", "found magnetometer");
            }
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                Log.v("dlat", "found accelerometer");
            }

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

                    mRenderer.setAngle(
                            mRenderer.getAngle() +
                                    ((dx + dy) * TOUCH_SCALE_FACTOR));
                    requestRender();
            }

            mPreviousX = x;
            mPreviousY = y;
            return true;
        }
    }
}