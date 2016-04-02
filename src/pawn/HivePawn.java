package pawn;

import gamemodel.BoardPosition;
import gameview.HivePawnViewer;

/**
 * Created by Wout Slabbinck on 19/03/2016.
 */
public abstract class HivePawn {
    protected int number;
    protected char color;
    protected char type;
    protected String description;

    protected HivePawnViewer hpViewer;
    protected BoardPosition position;

    public HivePawn(char color, char type, int number) {
        this.number = number;
        this.color = color;
        this.type = type;
        description = "" + color + type + number;

        hpViewer = new HivePawnViewer(this);
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

    public String getDescription() {
        return description;
    }

    public String positionDescription() {
        if (position == null)
            return "(-,-)";
        else
            return "(" + position.getY() + "," + position.getX() + ")"; // Coordinate system as in assignment
    }

    public void move(BoardPosition destinationPosition, boolean executeViewer) {
        if (position != null)
            position.setPawn(null);

        // you can move a pawn off the board when taking back moves
        if (destinationPosition != null)
            destinationPosition.setPawn(this);

        position = destinationPosition;

        if (executeViewer)
            hpViewer.move();
    }

    public void placePawnViewer() {
        hpViewer.place();
    }
}
