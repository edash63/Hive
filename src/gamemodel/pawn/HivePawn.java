package gamemodel.pawn;

import gamemodel.BoardPosition;
import gameview.HivePawnSprite;

/**
 * Created by Wout Slabbinck on 19/03/2016.
 */
public abstract class HivePawn extends java.util.Observable {
    protected int number;
    protected char color;
    protected char type;
    protected String description;

    protected HivePawnSprite sprite;
    protected BoardPosition position;

    public HivePawn(char color, char type, int number) {
        this.number = number;
        this.color = color;
        this.type = type;
        description = "" + color + type + number;

//        sprite = new HivePawnSprite(this);
        position = null;
    }

    public char getColor() {
        return color;
    }

    public int getNumber() {
        return number;
    }

    public char getType() {
        return type;
    }

    public void setPosition(BoardPosition position) {
        this.position = position;
        if (position != null)
            position.setPawn(this);
    }

    public BoardPosition getPosition() {
        return position;
    }

    public boolean onBoard() { return position != null; }

    public void setSprite(HivePawnSprite sprite) {
        this.sprite = sprite;
    }

    public String getDescription() {
        return description;
    }

    public String positionDescription() {
        if (!onBoard())
            return "(-,-)";
        else
            return "(" + position.getY() + "," + position.getX() + ")"; // Coordinate system as in assignment
    }

    public void move(BoardPosition destinationPosition) {
        if (onBoard())
            position.setPawn(null);

        // you can move a pawn off the board when taking back moves
        if (destinationPosition != null)
            destinationPosition.setPawn(this);

        position = destinationPosition;

        setChanged();
        notifyObservers();
    }
}
