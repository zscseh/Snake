package view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import connection.ConnectionProperties;
import connection.enumeration.DeviceType;
import model.CollisionDetector;
import control.TimingThread;
import control.TouchControl;
import model.Game;
import model.enumeration.Player;

/**
 * Created by Zsolt on 2015.03.06..
 * <p/>
 * Draws the game
 * This is the MultiplayerActivity's view
 */
public class GameView extends View {

    private BoardView boardView;
    private SnakeView snakeOneView;
    private SnakeView snakeTwoView;
    private FoodView foodView;

    private TouchControl touchControl;

    private CollisionDetector collisionDetector;

    private TimingThread timingThread;

    private Paint textPaint;
    private Paint backgroundPaint;

    private boolean gameOver;

    /**
     * Creates other view and control objects, then starts the game
     */
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        int blockSize = ScreenResolution.getInstance().getY() / 9;

        boardView = new BoardView(blockSize, Game.getInstance().getBoard());
        snakeOneView = new SnakeView(blockSize, Game.getInstance().getSnakeOne(), Player.ONE);
        snakeTwoView = new SnakeView(blockSize, Game.getInstance().getSnakeTwo(), Player.TWO);
        foodView = new FoodView(blockSize, Game.getInstance().getFoodManager());

        touchControl = new TouchControl();

        collisionDetector = new CollisionDetector(Game.getInstance().getBoard(), Game.getInstance().getSnakeOne(), Game.getInstance().getSnakeTwo());

        timingThread = new TimingThread(this);
        timingThread.start();

        textPaint = new Paint();
        textPaint.setColor(Color.DKGRAY);
        textPaint.setTextSize(144);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.argb(127, 255, 255, 255));

        gameOver = false;
    }

    /**
     * Draws the game
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        boardView.draw(canvas);
        snakeOneView.draw(canvas);
        snakeTwoView.draw(canvas);
        foodView.draw(canvas);

        if (collisionDetector.doesCollide()) {
            gameOver = true;
        }

        if (gameOver) {
            pause();

            //** Coordinates of the center */
            int x = (ScreenResolution.getInstance().getX() / 2) - 349;
            int y = (ScreenResolution.getInstance().getY() / 2) + 53;

            canvas.drawRect(0, 0, ScreenResolution.getInstance().getX(), ScreenResolution.getInstance().getY(), backgroundPaint);
            canvas.drawText("Game Over", x, y, textPaint);
        }
    }

    //** Sets the snake's direction defined by the swipe gesture */
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                touchControl.setLastDown(event);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                touchControl.setLastUp(event);
                if (ConnectionProperties.getInstance().getDeviceType() == DeviceType.SERVER)
                    touchControl.setDirection(Game.getInstance().getSnakeOneManager());
                else
                    touchControl.setDirection(Game.getInstance().getSnakeTwoManager());
            }
        return true;
    }

    public void pause() {
        timingThread.setPauseSignal(true);
    }

    public void resume() {
        timingThread.setPauseSignal(false);
    }

    public void stop() {
        timingThread.requestStop();
    }
}
