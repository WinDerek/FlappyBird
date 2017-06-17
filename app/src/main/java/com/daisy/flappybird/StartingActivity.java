package com.daisy.flappybird;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);
    }

    public void goToTouchActivity(View view) {
        /* On click of the button touch control */

        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("Mode", "Touch");
        startActivity(intent);
    }

    public void goToVoiceActivity(View view) {
        /* On click of the button voice control */

        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("Mode", "Voice");
        startActivity(intent);
    }
}
