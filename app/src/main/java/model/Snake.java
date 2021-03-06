package model;

import android.graphics.Point;

import java.util.ArrayList;

import model.enumeration.Direction;

/**
 * Created by Zsolt on 2015.03.06..
 * <p/>
 * Represents a snake with coordinates and a direction
 */
public class Snake {

    private ArrayList<Position> body;
    private Direction direction;

    /**
     * Sets the snake's initial values
     */
    public Snake(ArrayList<Position> body, Direction direction) {
        this.body = body;
        this.direction = direction;
    }

    public ArrayList<Position> getBody() {
        return body;
    }

    public void setBody(ArrayList<Position> body) {
        this.body = body;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * Moves the snake toward the current direction
     */
    public void step() {
        Position head = getHead();

        switch (direction) {
            case LEFT:
                body.add(0, new Position(head.x - 1, head.y));
                break;
            case RIGTH:
                body.add(0, new Position(head.x + 1, head.y));
                break;
            case UP:
                body.add(0, new Position(head.x, head.y - 1));
                break;
            case DOWN:
                body.add(0, new Position(head.x, head.y + 1));
                break;
            default:
                break;
        }
    }

    public Position getHead() {
        return body.get(0);
    }

    public void removeTail() {
        body.remove(body.size() - 1);
    }
}
