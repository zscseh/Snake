package control;

import android.util.Log;

import connection.Packet;
import connection.SnakePacket;
import model.Snake;
import model.enumeration.Direction;

/**
 * Created by Zsolt on 2015.04.21..
 * <p/>
 * Manages the game, if the devies is slave
 */
public class SlaveGameManager extends GameManager {

    public SlaveGameManager(SnakeManager snakeOneManager, SnakeManager snakeTwoManager, FoodManager foodManager) {
        super(snakeOneManager, snakeTwoManager, foodManager);
    }

    @Override
    public void send() {
        sendPacket();
        snakeTwoManager.validateDirection();
    }

    /**
     * Steps the game...
     */
    public void step() {
        Log.v("type", "SLAVE");
//        SnakePacket packet = (SnakePacket) transferThread.getPacket();
        SnakePacket packet = getLastPacket();
        if (packet != null) {
            Log.v("timer_sync", "STEP - " + packet.getDirection());
            snakeOneManager.buildSnake(packet.getCorners());
//            snakeOneManager.setSnake(packet.getBody());
            snakeOneManager.getSnake().setDirection(packet.getDirection());
            setDirection(packet.getDirection());
            snakeOneManager.validateDirection();

            foodManager.getFood().setX(packet.getFoodX());
            foodManager.getFood().setY(packet.getFoodY());

            snakeOneManager.step();
            snakeTwoManager.step();

            if (!snakeOneManager.eat(foodManager.getFood())) {
                snakeOneManager.removeTail();
            }

            if (!snakeTwoManager.eat(foodManager.getFood())) {
                snakeTwoManager.removeTail();
            }
        }
    }

    /**
     * Sets the SnakeManager's direction
     */
    private void setDirection(Direction direction) {
        switch (direction) {
            case RIGTH:
                snakeOneManager.setRight();
                break;
            case LEFT:
                snakeOneManager.setLeft();
                break;
            case DOWN:
                snakeOneManager.setDown();
                break;
            case UP:
                snakeOneManager.setUp();
                break;
            default:
                break;
        }
    }

    /**
     * Sends a packet with the direction and the food's coordinates
     */
    private void sendPacket() {
//        Snake snake = new Snake(snakeTwoManager.getSnake().getBody(), snakeTwoManager.getSnake().getDirection());
//        SnakeManager snakeManager = new SnakeManager(snake);
//        snakeManager.step();
//        if (!snakeManager.eat(foodManager.getFood())) {
//            snakeManager.removeTail();
//        }
//
//        SnakePacket packet = new SnakePacket(snakeManager.getCorners(), snakeManager.getDirection(), foodManager.getFood().getX(), foodManager.getFood().getY());
        SnakePacket packet = new SnakePacket(snakeTwoManager.getCorners(), snakeTwoManager.getDirection(), foodManager.getFood().getX(), foodManager.getFood().getY());
//        SnakePacket packet = new SnakePacket(snakeTwoManager.getSnake().getBody(), snakeTwoManager.getDirection(), foodManager.getFood().getX(), foodManager.getFood().getY());

        transferThread.write(packet);
    }
}
