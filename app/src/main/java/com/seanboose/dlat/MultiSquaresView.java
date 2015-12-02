package com.seanboose.dlat;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;

/**
 * Created by Sean Boose on 11/30/15.
 */
public class MultiSquaresView extends View {

    private final static int ANIMATION_DURATION = 1000;

    private final Paint _brushOne = new Paint();
    private final Paint _brushTwo = new Paint();
    private final Path _pathOne = new Path();
    private final Path _pathTwo = new Path();
    private final LinearInterpolator _interpolator = new LinearInterpolator();

    private float _height = 0;
    private float _width = 0;

    //TODO: temp fix for colors; will make list later
    private int _color = 0;

    private int _numSquares = 6;
    private float _stepWidth = 0;
    private float _stepHeight = 0;

    public MultiSquaresView(Context context) {
        super(context);
        init();
    }

    public MultiSquaresView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiSquaresView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        _brushOne.setAntiAlias(true);
        _brushOne.setStyle(Paint.Style.FILL_AND_STROKE);
        _brushTwo.setAntiAlias(true);
        _brushTwo.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public void updateDimensions(){
        _stepHeight = getHeight() / _numSquares;
        _stepWidth = getWidth() / _numSquares;
    }

    //TODO: change to add colors to a list
    public void setColor(int color){
        _brushOne.setColor(color);
        _brushTwo.setColor(getSecondColor(color));
    }

    public void setHeight(float height){
        _height = height;
        invalidate();
    }

    public void setWidth(float width){
        _width = width;
        invalidate();
    }

    public void reset(int color){
        Log.v("MultiSquares", "reset()");
        setColor(color);
        _width = 0;
        _height = 0;
    }

    public AnimatorSet exposeAnimatorSet(){
        float finalWidth = getWidth() / _numSquares;
        float finalHeight = getHeight() / _numSquares;

        ObjectAnimator heightAnimator = ObjectAnimator.ofFloat(this, "height", 0, finalHeight);
        ObjectAnimator widthAnimator = ObjectAnimator.ofFloat(this, "width", 0, finalWidth);
        heightAnimator.setDuration(ANIMATION_DURATION);
        widthAnimator.setDuration(ANIMATION_DURATION);
        heightAnimator.setInterpolator(_interpolator);
        widthAnimator.setInterpolator(_interpolator);

        AnimatorSet fullAnimator = new AnimatorSet();
        fullAnimator.playTogether(heightAnimator, widthAnimator);

        return fullAnimator;
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);

        float maxWidth = getWidth();
        float maxHeight = getHeight();

        _pathOne.reset();
        _pathTwo.reset();

        float left;
        float currentRight;
        float right;
        float bottom;
        float topLeft;
        float currentTopRight;
        float topRight;

        for(int i=0; i<_numSquares; i+=2){
            left = i * _stepWidth;
            currentRight = left + _width;
            right = (i + 1) * _stepWidth;
            bottom = maxHeight;

            topLeft = i * _stepHeight;
            currentTopRight = i * _stepHeight + _height;
            topRight = (i + 1) * _stepHeight;

            _pathOne.moveTo(left, topLeft);
            _pathOne.lineTo(left, bottom);
            _pathOne.lineTo(currentRight, bottom);
            _pathOne.lineTo(currentRight, currentTopRight);
            _pathOne.lineTo(left, topLeft);

            _pathTwo.moveTo(currentRight, currentTopRight);
            _pathTwo.lineTo(currentRight, bottom);
            _pathTwo.lineTo(right, bottom);
            _pathTwo.lineTo(right, topRight);
            _pathTwo.lineTo(currentRight, currentTopRight);
        }

        for(int i=1; i<_numSquares; i+=2){
            left = i * _stepWidth;
            currentRight = left + _width;
            right = (i + 1) * _stepWidth;
            bottom = maxHeight;

            topLeft = i * _stepHeight;
            currentTopRight = i * _stepHeight + _height;
            topRight = (i + 1) * _stepHeight;

            _pathTwo.moveTo(left, topLeft);
            _pathTwo.lineTo(left, bottom);
            _pathTwo.lineTo(currentRight, bottom);
            _pathTwo.lineTo(currentRight, currentTopRight);
            _pathTwo.lineTo(left, topLeft);

            _pathOne.moveTo(currentRight, currentTopRight);
            _pathOne.lineTo(currentRight, bottom);
            _pathOne.lineTo(right, bottom);
            _pathOne.lineTo(right, topRight);
            _pathOne.lineTo(currentRight, currentTopRight);

        }

        canvas.drawPath(_pathOne, _brushOne);
        canvas.drawPath(_pathTwo, _brushTwo);




//        _path.moveTo(0, 0);
//        _path.lineTo(0, _height);
//        _path.lineTo(_width, _height);
//        _path.lineTo(0, 0);
//
//        _path.moveTo(maxWidth, maxHeight);
//        _path.lineTo(maxWidth, maxHeight - _height);
//        _path.lineTo(maxWidth - _width, maxHeight - _height);
//        _path.lineTo(maxWidth, maxHeight);


    }

    private int getSecondColor(int color){
        if(color == Color.BLACK) {
            return Color.WHITE;
        }
        return Color.BLACK;
    }

}
