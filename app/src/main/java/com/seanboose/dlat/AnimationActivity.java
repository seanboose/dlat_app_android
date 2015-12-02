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


    private MovingSquaresView _squares;
    private View _mainView;
    private Handler _timer;

    private int _black = Color.BLACK;
    private int _white = Color.WHITE;
    private int _color = _white;

    private boolean _filled = false;
    private boolean _first = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        _squares = (MovingSquaresView) findViewById(R.id.squaresView);
        _squares.reset(_color);
        _timer = new Handler();

        _mainView = findViewById(R.id.animationMain);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        if(hasFocus) {
            startAnimation();
        }
    }

    private void startAnimation(){
        int background = _white;
        if(_color == _white) background = _black;
        _mainView.setBackgroundColor(background);

        AnimatorSet animator = _squares.exposeAnimatorSet();

        if(_first) {
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    getNextColor();
                    _squares.reset(_color);
                    startAnimation();

                }
            });

//            _first = false;
        }

        animator.start();

//        _timer.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.v("AnimationActivity", "_timer.run()");
//                startAnimation();
//            }
//        }, 500);

//        Log.v("AnimationActivity", "Animation started");
    }

    private void getNextColor(){
        if (_color == _black) _color = _white;
        else _color = _black;
    }

    public class squareRepeater implements Runnable {
        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

            startAnimation();


        }
    }
}
