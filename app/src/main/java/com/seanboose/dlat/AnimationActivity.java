package com.seanboose.dlat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class AnimationActivity extends Activity {


    private MovingSquaresView _squares;
    private View _mainView;

    private int _black = Color.BLACK;
    private int _white = Color.WHITE;
    private int _color = _white;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        _squares = (MovingSquaresView) findViewById(R.id.squaresView);
        _squares.reset(_color);

        _mainView = findViewById(R.id.animationMain);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        if(hasFocus) startAnimation();
    }

    private void startAnimation(){
        int background = _white;
        if(_color == _white) background = _black;
        _mainView.setBackgroundColor(background);

        Animator animator = _squares.exposeAnimator();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Log.v("AnimatorListener", "onAnimationEnd");
                if(_color == _black) _color = _white;
                else _color = _black;
                _squares.reset(_color);
                startAnimation();
            }
        });
        animator.start();
        Log.v("AnimationActivity", "Animation started");
    }
}
