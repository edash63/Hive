package gamemodel;

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
}
