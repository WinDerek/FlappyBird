package com.daisy.flappybird;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class StartingActivity extends AppCompatActivity {
    private static final int RECORD_AUDIO_PERMISSION_REQUEST_CODE = 0x00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        // Hide the status bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void goToTouchActivity(View view) {
        /* On click of the button touch control */

        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("Mode", "Touch");
        startActivity(intent);
    }

    public void goToVoiceActivity(View view) {
        /* On click of the button voice control */

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.RECORD_AUDIO },
                    RECORD_AUDIO_PERMISSION_REQUEST_CODE);
        } else {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("Mode", "Voice");
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RECORD_AUDIO_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted
                    Intent intent = new Intent(this, GameActivity.class);
                    intent.putExtra("Mode", "Voice");
                    startActivity(intent);
                } else {
                    // Permission denied
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                    alertDialog.setTitle("Permission Denied...");
                    alertDialog.setMessage("Without record audio permission granted, " +
                            "you cannot play with voice control.");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                        }
                    });
                    alertDialog.show();
                }
                return;
            }
        }
    }
}
