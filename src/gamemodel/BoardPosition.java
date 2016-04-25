package gamemodel;

import exception.HiveException;
import gamemodel.pawn.HivePawn;

/**
 * Created by Wout Slabbinck on 31/03/2016.
 */
public class BoardPosition {
    private final int x, y;
    private HivePawn pawn;


    public BoardPosition(int x, int y) {
        this.pawn = null;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public HivePawn getPawn() {
        return pawn;
    }

    public void setPawn(HivePawn pawn) {
        this.pawn = pawn;
    }

    public BoardPosition PosUpperLeft() throws HiveException {
        return HiveBoard.getInstance().position(x, y-1);
    }

    public BoardPosition PosLevelLeft() throws HiveException {
        return HiveBoard.getInstance().position(x-1, y);
    }

    public BoardPosition PosLowerLeft() throws HiveException {
        return HiveBoard.getInstance().position(x-1, y+1);
    }

    public BoardPosition PosUpperRight() throws HiveException {
        return HiveBoard.getInstance().position(x+1, y-1);
    }

    public BoardPosition PosLevelRight() throws HiveException {
        return HiveBoard.getInstance().position(x+1, y);
    }

    public BoardPosition PosLowerRight() throws HiveException {
        return HiveBoard.getInstance().position(x, y+1);
    }
}
