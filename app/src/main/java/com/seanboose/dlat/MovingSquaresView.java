package com.seanboose.dlat;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Sean Boose on 11/30/15.
 */
public class MovingSquaresView extends View {

    private final static int ANIMATION_DURATION = 1000;

    private final Paint _brush = new Paint();
    private final Path _path = new Path();
    private final LinearInterpolator _interpolator = new LinearInterpolator();

    private float _height = 0;
    private float _width = 0;

    public MovingSquaresView(Context context) {
        super(context);
        init();
    }

    public MovingSquaresView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MovingSquaresView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        _brush.setAntiAlias(true);
        _brush.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public void setColor(int color){
        _brush.setColor(color);
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
        Log.v("MovingSquares", "reset()");
        _brush.setColor(color);
        _width = 0;
        _height = 0;
    }

    public Animator exposeAnimator(){

        float finalSize = (float) Math.max(getHeight(), getWidth());

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "size", 0, finalSize);
        animator.setDuration(ANIMATION_DURATION);
        animator.setInterpolator(_interpolator);
        return animator;
    }

    public AnimatorSet exposeAnimatorSet(){
        float finalWidth = getWidth();
        float finalHeight = getHeight();

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

        _path.reset();
        _path.addRect(0, 0, _width, _height, Path.Direction.CCW);
        canvas.drawPath(_path, _brush);
    }

}
