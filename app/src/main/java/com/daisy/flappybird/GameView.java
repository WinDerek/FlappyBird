package com.daisy.flappybird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.widget.AppCompatDrawableManager;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.daisy.flappybird.domain.Pipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private float measuredWidth;

    private float measuredHeight;

    private SurfaceHolder surfaceHolder;

    private Paint paint;

    private Bitmap bitmap;

    // The colors
    private static final int colorPipe = Color.parseColor("#C75B39");

    // The current score
    private int score = 0;

    public int getScore() {
        return score;
    }

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
    private static final float gap = 450.0f;
    private static final float base = 100.0f;
    private float pipeWidth = 100.0f;
    private List<Pipe> pipeList;
    private static final float pipeVelocity = 3.0f;

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
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        setZOrderOnTop(true);
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);

        paint = new Paint();
        paint.setAntiAlias(true);

        // For the bird
        bitmap = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_bird);
        bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);

        // For the pipes
        pipeList = new ArrayList<Pipe>();

        setKeepScreenOn(true);
    }

    /**
     * Updates the UI.
     */
    public void update() {
        paint.setStyle(Paint.Style.FILL);

        Canvas canvas = surfaceHolder.lockCanvas();

        // Clear the canvas
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        // Draw the bird
        canvas.drawBitmap(bitmap, positionX - 100.0f / 2.0f, positionY - 100.0f / 2.0f, null);

        // Draw the pipes
        paint.setColor(colorPipe);
        List<Integer> removeList = new ArrayList<>();
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
//        velocityY += accelerationY;
        // Only accelerate velocityY when it is not too large
        if (velocityY <= 10.0F) {
            velocityY += accelerationY;
        }

        // Update the data for the pipes
        for (Pipe pipe : pipeList) {
            pipe.setPositionX(pipe.getPositionX() - pipeVelocity);
        }
        if (iteratorInt == interval) {
            addPipe();
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

        // Add the initial pipe
        addPipe();
    }

    public void jump() {
        velocityY = -13.0f;
    }

    public void setPosition(float positionX, float positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
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

    /**
     * Returns true if the bird is still alive, false otherwise.
     *
     * @return True if the bird is still alive, false otherwise.
     */
    public boolean isAlive() {
        // Check if the bird hits the pipes
        for (Pipe pipe : pipeList) {
            if ((pipe.getPositionX() >= measuredWidth / 2.0f - pipeWidth / 2.0f - 100.0f / 2.0f) &&
                    (pipe.getPositionX() <= measuredWidth / 2.0f + pipeWidth / 2.0f + 100.0f / 2.0f)) {
                if ((positionY <= measuredHeight - pipe.getHeight() - gap + 50.0f / 2.0f) ||
                        (positionY >= measuredHeight - pipe.getHeight() - 50.0f / 2.0f)) {
                    return false;
                } else {
                    if (pipe.getPositionX() - pipeVelocity <
                            measuredWidth / 2.0f - pipeWidth / 2.0f - 100.0f / 2.0f) {
                        score++;

                        // Update the score in MainActivity
                        Context context = getContext();
                        if (context instanceof GameActivity) {
                            ((GameActivity) context).updateScore(score);
                            ((GameActivity) context).playScoreMusic();
                        }
                    }
                }
            }
        }

        // Check if the bird goes beyond the border
        if ((positionY < 0.0f + 100.0f / 2.0f) || (positionY > measuredHeight - 100.0f / 2.0f)) {
            return false;
        }

        return true;
    }

    /**
     * Returns true if the pipe is out of the screen, false otherwise.
     *
     * @param pipe The pipe to be judged.
     *
     * @return True if the pipe is out of the screen, false otherwise.
     */
    private boolean isPipeOut(Pipe pipe) {
        return (pipe.getPositionX() + pipeWidth / 2.0f) < 0.0f;
    }

    /**
     * Removes all the items at the indices specified by removeList.
     *
     * @param removeList The list of indices.
     */
    private void removeItemsFromPipeList(List<Integer> removeList) {
        List<Pipe> newList = new ArrayList<>();
        int size = pipeList.size();
        for (int index = 0; index < size; index++) {
            if (!removeList.remove(Integer.valueOf(index))) {
                newList.add(pipeList.get(index));
            }
        }

        pipeList = newList;
    }

    /**
     * Resets all the data of the over game.
     */
    public void resetData() {
        // For the bird
        positionX = 0.0f;
        positionY = 0.0f;
        velocityX = 0.0f;
        velocityY = 0.0f;
        accelerationX = 0.0f;
        accelerationY = 0.7f;

        // For the pipes
        iteratorInt = 0;
        pipeList = new ArrayList<>();

        score = 0;

        // Set the initial position
        setPosition(measuredWidth / 2.0f, measuredHeight / 2.0f);

        // Add the initial pipe
        addPipe();
    }

    /**
     * Adds a pipe into the list of pipes.
     */
    private void addPipe() {
        pipeList.add(new Pipe(measuredWidth + pipeWidth / 2.0f,
                base + (measuredHeight - 2 * base - gap) * new Random().nextFloat()));
    }

}
