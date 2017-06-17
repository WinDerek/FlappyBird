package com.daisy.flappybird;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private GameView gameView;
    private TextView textViewScore;

    private Timer timer;

    private int score = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case UPDATE:
                    if (gameView.isAlive()) {
                        gameView.update();
                    } else {
                        // Derek is debugging...
                        Log.i("DerekDick", "MainActivity game over");

                        // Cancel the timer
                        timer.cancel();
//                        timer.purge();

                        // Derek is debugging...
                        Log.i("DerekDick", "MainActivity timer cancelled");

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                        alertDialog.setTitle("GAME OVER");
                        alertDialog.setMessage("Score: " + String.valueOf(gameView.getScore()) +
                                "\n" + "Would you like to RESTART?");
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.this.restartGame();
                            }
                        });
                        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.this.onBackPressed();
                            }
                        });
                        alertDialog.show();
                    }

                    break;

                case RESET_SCORE:
                    textViewScore.setText("0");

                    break;

                default:
                    break;
            }
        }
    };

    // The what values of the messages
    private static final int UPDATE = 0x00;
    private static final int RESET_SCORE = 0x01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the status bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        // Initialize the private views
        initViews();

        // Set the Timer
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Sleep for 3 seconds for the Surface to initialize
                    Thread.sleep(3000);
                } catch (Exception exception) {
                    exception.printStackTrace();
                } finally {
                    setNewTimer();
                }
            }
        }).start();

        // Test
        gameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        gameView.jump();

                        break;

                    case MotionEvent.ACTION_UP:


                        break;

                    default:
                        break;
                }

                return true;
            }
        });
    }

    private void initViews() {
        /* Initializes the private views */

        gameView = (GameView) findViewById(R.id.game_view);
        textViewScore = (TextView) findViewById(R.id.text_view_score);
    }

    private void setNewTimer() {
        /* Sets the Timer to update the UI of the GameView  */

        // Derek is debugging...
        Log.i("DerekDick", "MainActivity setNewTimer");

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Send the message to the handler to update the UI of the GameView
                MainActivity.this.handler.sendEmptyMessage(UPDATE);

                // For garbage collection
                System.gc();
            }
        }, 0, 17);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        timer.cancel();
        timer.purge();
    }

    @Override
    protected void onPause() {
        super.onPause();

//        timer.cancel();
//        timer.purge();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

//        setNewTimer();
    }

    public void updateScore(int score) {
        /* Updates the displayed score */

        textViewScore.setText(String.valueOf(score));
    }

    private void restartGame() {
        /* Restarts the game */

        // Reset all the data of the over game in the GameView
        gameView.resetData();

//        // Refresh the TextView for displaying the score
//        textViewScore.setText("0");
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(RESET_SCORE);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Sleep for 3 seconds
                    Thread.sleep(3000);
                } catch (Exception exception) {
                    exception.printStackTrace();
                } finally {
                    setNewTimer();
                }
            }
        }).start();
    }
}
