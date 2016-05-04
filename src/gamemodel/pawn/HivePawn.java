package gamemodel.pawn;

import exception.HiveException;
import gamemodel.BoardPosition;

import java.util.Observable;
import java.util.Set;

/**
 * Created by Wout Slabbinck on 19/03/2016.
 */
public abstract class HivePawn extends Observable {
    protected final int number;
    protected final char color;
    protected final char type;
    protected final String description;

    protected BoardPosition position;

    public HivePawn(char color, char type, int number) {
        this.number = number;
        this.color = color;
        this.type = type;
        if (type == 'Q') {
            description = "" + color + type;
        } else {
            description = "" + color + type + number;
        }

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

    public BoardPosition getPosition() {
        return position;
    }

    public boolean onBoard() { return position != null; }

    public String getDescription() {
        return description;
    }

    public String positionDescription() {
        if (!onBoard()) {
            return "(-,-)";
        } else {
            return "(" + position.getRow() + "," + position.getCol() + ")";
        }
    }

    public void move(BoardPosition destinationPosition) {
        if (onBoard()) {
            position.setPawn(null);
        }

        // Een pawn kan van het bord verwijderd worden bij het terugnemen van zetten
        if (destinationPosition != null) {
            destinationPosition.setPawn(this);
        }
        position = destinationPosition;

        setChanged();
        notifyObservers();
    }

    public abstract Set<BoardPosition> destinations() throws HiveException;
}
