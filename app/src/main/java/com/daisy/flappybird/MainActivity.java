package com.daisy.flappybird;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private GameView gameView;

    private Timer timer;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case UPDATE:
                    if (gameView.isAlive()) {
                        gameView.update();
                    } else {
                        timer.cancel();
                        timer.purge();
                        setTitle("Game Over");
                    }

                    break;

                default:
                    break;
            }
        }
    };

    // The what values of the messages
    private static final int UPDATE = 0x00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 隐藏状态栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 把Activity的标题去掉
        requestWindowFeature(Window.FEATURE_NO_TITLE);

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

                    setNewTimer();
                } catch (Exception exception) {
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

        this.gameView = (GameView) this.findViewById(R.id.game_view_main_activity);
    }

    private void setNewTimer() {
        /* Sets the Timer to update the UI of the GameView  */

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Send the message to the handler to update the UI of the GameView
                MainActivity.this.handler.sendEmptyMessage(UPDATE);

                // For garbage collection
                System.gc();
            }
        }, 0, 10);
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
}
