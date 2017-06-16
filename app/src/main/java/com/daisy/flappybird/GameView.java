package com.daisy.flappybird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private float measuredWidth;
    private float measuredHeight;
    private SurfaceHolder surfaceHolder;
    private Paint paint;
    private Bitmap bitmap;

    // For the bird
    private float positionX = 0.0f;
    private float positionY = 0.0f;
    private float velocityX = 0.0f;
    private float velocityY = 0.0f;
    private float accelerationX = 0.0f;
    private float accelerationY = 0.7f;

    // For the pipes
    private int iteratorInt = 0;
    private static final int interval = 150;
    private static final float gap = 300.0f;
    private float pipeWidth = 100.0f;
    private List<Pipe> pipeList;

//    private Timer timer;
//
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message message) {
//            switch (message.what) {
//                case UPDATE:
//                    GameView.this.update();
//
//                    break;
//
//                default:
//                    break;
//            }
//        }
//    };
//
//    // The what values of the messages
//    private static final int UPDATE = 0x00;

    public GameView(Context context) {
        super(context);

        // Initialize
        init();
    }

    public GameView(Context context, AttributeSet a) {
        super(context, a);

        // Initialize
        init();
    }

    public GameView(Context context, AttributeSet a, int b) {
        super(context, a, b);

        // Initialize
        init();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void init() {
        /* Initializes */

        // Derek is debugging...
        Log.i("DerekDick", "GameView init()");

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        paint = new Paint();
        paint.setAntiAlias(true);

        // For the bird
        bitmap = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_bird);

        // Derek is debugging...
        Log.i("DerekDick", "GameView init() about to new pipeList");

        // For the pipes
        pipeList = new ArrayList<Pipe>();

        // Derek is debugging...
        Log.i("DerekDick", "GameView init() pipeList is new ");

        setFocusable(true);
        setKeepScreenOn(true);

//        // Set a new Timer
//        setNewTimer();
    }

//    private void setNewTimer() {
//        /* Sets the Timer to update the UI of the GameView  */
//
//        timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                // Send the message to the handler to update the UI of the GameView
//                GameView.this.handler.sendEmptyMessage(UPDATE);
//
//                // For garbage collection
//                System.gc();
//            }
//        }, 0, 10);
//    }

    public void update() {
        /* Updates the UI */

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);

        Canvas canvas = surfaceHolder.lockCanvas();

        // Clear the canvas
        canvas.drawColor(Color.WHITE);

        // Draw the bird
        canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, 100, 100, false),
                positionX - 100.0f / 2.0f, positionY - 100.0f / 2.0f, null);

        // Draw the pipes
        paint.setColor(Color.parseColor("#A1713B"));
        List<Integer> removeList = new ArrayList<Integer>();
        int size = pipeList.size();
        for (int index = 0; index < size; index++) {
            Pipe pipe = pipeList.get(index);
            if (isPipeOut(pipe)) {
                removeList.add(index);
            } else {
                // Draw the upper part of the pipe
                canvas.drawRect(pipe.getPositionX() - pipeWidth / 2.0f,
                        0.0f,
                        pipe.getPositionX() + pipeWidth / 2.0f,
                        measuredHeight - pipe.getHeight() - gap,
                        paint);

                // Draw the lower part of the pipe
                canvas.drawRect(pipe.getPositionX() - pipeWidth / 2.0f,
                        measuredHeight - pipe.getHeight(),
                        pipe.getPositionX() + pipeWidth / 2.0f,
                        measuredHeight,
                        paint);
            }
        }
        removeItemsFromPipeList(removeList);

        surfaceHolder.unlockCanvasAndPost(canvas);

        // Update the data for the bird
        positionX += velocityX;
        positionY += velocityY;
        velocityX += accelerationX;
        velocityY += accelerationY;

        // Update the data for the pipes
        for (Pipe pipe : pipeList) {
            pipe.setPositionX(pipe.getPositionX() - 3.0f);
        }
        if (iteratorInt == interval) {
            pipeList.add(new Pipe(measuredWidth + pipeWidth / 2.0f,
                    300.0f + (measuredHeight - 600.0f) * new Random().nextFloat()));
            iteratorInt = 0;
        } else {
            iteratorInt++;
        }
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Get the measured size of the view
        measuredWidth = getMeasuredWidth();
        measuredHeight = getMeasuredHeight();

        // Set the initial position
        setPosition(measuredWidth / 2.0f, measuredHeight / 2.0f);

        // Derek is debugging...
        Log.i("DerekDick", "GameView onSizeChanged() about to pipeList.add()");
        if (pipeList == null) {
            Log.i("DerekDick", "pipeList is null!!! WTF");
        }

        // Add the initial pipe
        pipeList.add(new Pipe(measuredWidth + pipeWidth / 2.0f,
                300.0f + (measuredHeight - 600.0f) * new Random().nextFloat()));

        // Derek is debugging...
        Log.i("DerekDick", "GameView onSizeChanged()");
    }

    public void jump() {
        velocityY = -13.0f;
    }

    public void setPosition(float positionX, float positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public boolean isAlive() {
        /* Checks if the bird is still alive */

        // Check if the bird hits the pipes
        for (Pipe pipe : pipeList) {
            if ((pipe.getPositionX() >= measuredWidth / 2.0f - pipeWidth / 2.0f - 100.0f / 2.0f) &&
                    (pipe.getPositionX() <= measuredWidth / 2.0f + pipeWidth / 2.0f + 100.0f / 2.0f)) {
                if ((positionY <= measuredHeight - pipe.getHeight() - gap + 60.0f / 2.0f) ||
                        (positionY >= measuredHeight - pipe.getHeight() - 60.0f / 2.0f)) {
                    return false;
                }
            }
        }

        // Check if the bird goes beyond the border
        if ((positionY < 0.0f + 100.0f / 2.0f) || (positionY > measuredHeight - 100.0f / 2.0f)) {
            return false;
        }

        return true;
    }

    private boolean isPipeOut(Pipe pipe) {
        /* Checks if the pipe is out of the screen */

        if (pipe.getPositionX() + pipeWidth / 2.0f < 0.0f) {
            return true;
        } else {
            return false;
        }
    }

    private void removeItemsFromPipeList(List<Integer> removeList) {
        /* Removes all the items at the indices spacified by removeList */

        List newList = new ArrayList();
        int size = pipeList.size();
        for (int index = 0; index < size; index++) {
            if (!removeList.remove(new Integer(index))) {
                newList.add(pipeList.get(index));
            }
        }

        pipeList = newList;
    }
}
