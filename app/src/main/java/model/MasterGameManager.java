package model;

import connection.SnakePacket;
import model.enumeration.Direction;

/**
 * Created by Zsolt on 2015.04.21..
 * <p/>
 * Manages the game, if the device is master
 */
public class MasterGameManager extends GameManager {

    public MasterGameManager(SnakeManager snakeOneManager, SnakeManager snakeTwoManager, FoodManager foodManager) {
        super(snakeOneManager, snakeTwoManager, foodManager);
    }

    @Override
    public void send() {
        sendPacket();
        snakeOneManager.validateDirection();
    }

    /**
     * Steps the game and send a packet to the slave device
     */
    public void step() {
        SnakePacket packet = getLastPacket();
        if (packet != null) {
            snakeTwoManager.buildSnake(packet.getCorners());

            snakeTwoManager.getSnake().setDirection(packet.getDirection());
            setDirection(packet.getDirection());
            snakeTwoManager.validateDirection();
        }

        snakeOneManager.step();
        snakeTwoManager.step();

        boolean snakeOneAte = snakeOneManager.eat(foodManager.getFood());
        if (!snakeOneAte) {
            snakeOneManager.removeTail();
        }

        boolean snakeTwoAte = snakeTwoManager.eat(foodManager.getFood());
        if (!snakeTwoAte) {
            snakeTwoManager.removeTail();
        }

        if (snakeOneAte || snakeTwoAte) {
            foodManager.createFood(snakeOneManager.getSnake());
        }
    }

    /**
     * Sets the SnakeManager's direction
     */
    private void setDirection(Direction direction) {
        switch (direction) {
            case RIGTH:
                snakeTwoManager.setRight();
                break;
            case LEFT:
                snakeTwoManager.setLeft();
                break;
            case DOWN:
                snakeTwoManager.setDown();
                break;
            case UP:
                snakeTwoManager.setUp();
                break;
            default:
                break;
        }
    }

    /**
     * Sends a packet with the direction and the food's coordinates
     */
    private void sendPacket() {
        SnakePacket packet = new SnakePacket(snakeOneManager.getCorners(), snakeOneManager.getDirection(), foodManager.getFood().getX(), foodManager.getFood().getY());
        transferThread.write(packet);
    }
}
