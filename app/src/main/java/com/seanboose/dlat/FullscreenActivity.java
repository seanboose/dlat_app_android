package com.seanboose.dlat;

import com.seanboose.dlat.util.SystemUiHider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        Button interactive = (Button) findViewById(R.id.interactiveButton);
        interactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("FullscreenActivity", "Starting OpenGL activity");
                Intent getOpenGLIntent = new Intent(getApplicationContext(), OpenGLES20Activity.class);
                startActivity(getOpenGLIntent);
            }
        });
        Button nonInteractive = (Button) findViewById(R.id.nonInteractiveButton);
        nonInteractive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("FullscreenActivity", "Starting Animation activity");
                Intent getAnimationIntent = new Intent(getApplicationContext(), AnimationActivity.class);
                startActivity(getAnimationIntent);
            }
        });

    }
}