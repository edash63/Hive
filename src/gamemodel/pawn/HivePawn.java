package gamemodel.pawn;

import gamemodel.BoardPosition;
import gameview.HivePawnSprite;

import java.util.Observable;

/**
 * Created by Wout Slabbinck on 19/03/2016.
 */
public abstract class HivePawn extends Observable {
    protected final int number;
    protected final char color;
    protected final char type;
    protected final String description;

    protected HivePawnSprite sprite;
    protected BoardPosition position;

    public HivePawn(char color, char type, int number) {
        this.number = number;
        this.color = color;
        this.type = type;
        if (type == 'Q')
            description = "" + color + type;
        else
            description = "" + color + type + number;

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
            return "(" + position.getRow() + "," + position.getCol() + ")"; // Coordinate system as in assignment
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
