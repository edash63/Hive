package gamemodel;

import exception.HiveException;
import gamemodel.pawn.HivePawn;

/**
 * Created by Wout Slabbinck on 31/03/2016.
 */
public class BoardPosition {
    private final int col, row;
    private HivePawn pawn;


    public BoardPosition(int x, int y) {
        this.pawn = null;
        this.row = y;
        this.col = x;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public HivePawn getPawn() {
        return pawn;
    }

    public void setPawn(HivePawn pawn) {
        this.pawn = pawn;
    }

    public BoardPosition PosUpperLeft() throws HiveException {
        return HiveBoard.getInstance().position(col, row -1);
    }

    public BoardPosition PosLevelLeft() throws HiveException {
        return HiveBoard.getInstance().position(col -1, row);
    }

    public BoardPosition PosLowerLeft() throws HiveException {
        return HiveBoard.getInstance().position(col -1, row +1);
    }

    public BoardPosition PosUpperRight() throws HiveException {
        return HiveBoard.getInstance().position(col +1, row -1);
    }

    public BoardPosition PosLevelRight() throws HiveException {
        return HiveBoard.getInstance().position(col +1, row);
    }

    public BoardPosition PosLowerRight() throws HiveException {
        return HiveBoard.getInstance().position(col, row +1);
    }

    private class NeighbourContext {
        int count;
        int groupCount;
        boolean inGroup;

        public NeighbourContext() {
            count = 0;
            groupCount = 0;
            inGroup = false;
        }

        public int getCount() { return count; }

        public int getGroupCount() { return groupCount; }

        public void evaluatePosition (BoardPosition position) {
            if (position.getPawn() != null) {
                count += 1;
                if (!inGroup) {
                    groupCount += 1;
                    inGroup = true;
                }
            } else {
                inGroup = false;
            }
        }

        public void reEvaluateFirstPosition(BoardPosition position) {
            if (position.getPawn() != null && inGroup) {
                groupCount -= 1;
            }
        }

        public void evaluateNeighbours(BoardPosition position) throws HiveException {
            evaluatePosition(position.PosLowerLeft());
            evaluatePosition(position.PosLevelLeft());
            evaluatePosition(position.PosUpperLeft());
            evaluatePosition(position.PosUpperRight());
            evaluatePosition(position.PosLevelRight());
            evaluatePosition(position.PosLowerRight());
            reEvaluateFirstPosition(position.PosLowerLeft());
        }
    }

    public boolean hasCorridor() throws HiveException {
        NeighbourContext context = new NeighbourContext();

        context.evaluateNeighbours(this);

        if (context.getCount() <= 2)
            return true;
        else if (context.getCount() == 3 && context.getGroupCount() <= 2)
            return true;
        else if (context.getCount() == 4 && context.getGroupCount() == 1)
            return true;
        else
            return false;
    }
}
