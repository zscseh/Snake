package model;

import android.graphics.Point;

import java.util.Random;

import model.Board;
import model.Food;
import model.Position;
import model.Snake;
import model.enumeration.BoardElement;

/**
 * Created by Zsolt on 2015.03.14..
 * <p/>
 * Manages the food
 */
public class FoodManager {

    private Board board;
    private Food food;

    public FoodManager(Board board, Snake snake) {
        this.board = board;
        createFood(snake);
    }

    public Food getFood() {
        return food;
    }

    /**
     * Creates a food randomly on a floor
     */
    public void createFood(Snake snake) {
        Random random = new Random();
        int x = random.nextInt(15);
        int y = random.nextInt(9);

        while (!checkCoordinates(x, y, snake)) {
            x = random.nextInt(15);
            y = random.nextInt(9);
        }

        food = new Food(x, y);
    }

    /**
     * Checks whether the coordinates are not on a wall or the snake
     */
    private boolean checkCoordinates(int x, int y, Snake snake) {
        if (board.getFields()[x][y] == BoardElement.WALL) {
            return false;
        }

        for (Position position : snake.getBody()) {
            if (position.equals(x, y)) {
                return false;
            }
        }

        return true;
    }
}
