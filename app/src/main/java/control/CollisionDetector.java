package control;

import android.graphics.Point;

import model.Board;
import model.Snake;
import model.enumeration.BoardElement;

/**
 * Created by Zsolt on 2015.03.07..
 * <p/>
 * Detects collision between the snake and a wall, or between two parts of the snake
 */
public class CollisionDetector {

    private Board board;
    private Snake snake;

    public CollisionDetector(Board board, Snake snake) {
        this.board = board;
        this.snake = snake;
    }

    /**
     * Detects collision
     */
    public boolean doesCollide() {
        if (board.getFields()[snake.getHead().x][snake.getHead().y] == BoardElement.WALL) {
            return true;
        }

        Point head = snake.getHead();

        for (Point point : snake.getBody()) {
            if (head.equals(point) && head != point) {
                return true;
            }
        }

        return false;
    }
}
