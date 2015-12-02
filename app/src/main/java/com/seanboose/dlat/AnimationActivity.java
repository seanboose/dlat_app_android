package com.seanboose.dlat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.graphics.Color;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class AnimationActivity extends Activity {


    private MultiSquaresView _squares;
    private View _mainView;
    private Handler _timer;

    private int _black = Color.BLACK;
    private int _white = Color.WHITE;
    private int _color = _white;

    private boolean _active = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        _squares = (MultiSquaresView) findViewById(R.id.squaresView);
        _timer = new Handler();

        _mainView = findViewById(R.id.animationMain);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        if(hasFocus) {
            _squares.updateDimensions();
            startAnimation();
        }
    }

    private void startAnimation(){

        AnimatorSet animator = _squares.exposeAnimatorSet();
        _squares.setColor(0);

        if(_active) {
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    getNextColor();
//                    _squares.reset(_color);
                    startAnimation();

                }
            });
        }

        animator.start();

    }

    private void getNextColor(){
        if (_color == _black) _color = _white;
        else _color = _black;
    }

    @Override
    public void onPause(){
        super.onPause();
        _active = false;
        Log.v("AnimationActivity", "onPause");
    }

    @Override
    public void onResume(){
        super.onResume();
        _active = true;
        Log.v("AnimationActivity", "onResume");
    }
}
