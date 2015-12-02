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
//        _brushOne.setColor(color);
//        _brushTwo.setColor(getSecondColor(color));

        _brushOne.setColor(Color.WHITE);
        _brushTwo.setColor(Color.BLACK);
    }

    public void setHeight(float height){
        _height = height;
        invalidate();
    }

    public void setWidth(float width){
        _width = width;
        invalidate();
    }

    public AnimatorSet exposeAnimatorSet(){
        float finalWidth = 2 * getWidth() / _numSquares;
        float finalHeight = 2 * getHeight() / _numSquares;

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


        _pathOne.reset();
        _pathTwo.reset();

        float left;
        float middle;
        float right;
        float bottom = getHeight();
        float topLeft;
        float topMiddle;
        float topRight;

        float remWidth;
        float remHeight;

        for(int i=-1; i<=_numSquares; i+=2) {
            remWidth = _stepWidth - _width;
            remHeight = _stepHeight - _height;

            left = i * _stepWidth - remWidth;
            middle = i * _stepWidth + _width;
            right = (i+2) * _stepWidth - remWidth;

            topLeft = i * _stepHeight - remHeight;
            topMiddle = i * _stepHeight + _height;
            topRight = (i+2) * _stepHeight - remHeight;

            _pathOne.moveTo(left, topLeft);
            _pathOne.lineTo(left, bottom);
            _pathOne.lineTo(middle, bottom);
            _pathOne.lineTo(middle, topMiddle);
            _pathOne.lineTo(left, topLeft);

            _pathTwo.moveTo(middle, topMiddle);
            _pathTwo.lineTo(middle, bottom);
            _pathTwo.lineTo(right, bottom);
            _pathTwo.lineTo(right, topRight);
            _pathTwo.lineTo(middle, topMiddle);
        }
        canvas.drawPath(_pathOne, _brushOne);
        canvas.drawPath(_pathTwo, _brushTwo);
    }

    private int getSecondColor(int color){
        if(color == Color.BLACK) {
            return Color.WHITE;
        }
        return Color.BLACK;
    }

}
