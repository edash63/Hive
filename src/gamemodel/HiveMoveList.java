package gamemodel;

import exception.HiveException;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by Wout Slabbinck on 20/04/16.
 */
public class HiveMoveList extends Observable {
    private static int START_MOVE_LIST = -1;

    private ArrayList<HiveMove> moveList;
    private int currentMoveIndex;
    private boolean erroneousMoves;
    private int errorNr;

    public HiveMoveList() {
        moveList = new ArrayList<>();
        currentMoveIndex = START_MOVE_LIST;
        erroneousMoves = false;
        errorNr = 0;
    }

    public void addMove(String moveDescription) throws HiveException {
        try {
            moveList.add(new HiveMove(moveDescription));
        } catch (HiveException e) {
            erroneousMoves = true;
            errorNr = 1;
        }
    }

    public boolean hasNextMove() {
        return (currentMoveIndex < moveList.size() - 1);
    }

    public boolean hasPreviousMove() {
        return (currentMoveIndex > START_MOVE_LIST);
    }

    public void advance() {
        if (hasNextMove()) {
            currentMoveIndex += 1;
            try {
                moveList.get(currentMoveIndex).advance();
            } catch (HiveException e) {
                erroneousMoves = true;
            }
            setChanged();
            notifyObservers(currentMoveIndex);
        }
    }

    public void takeback() {
        if (hasPreviousMove()) {
            moveList.get(currentMoveIndex).takeBack();
            currentMoveIndex -= 1;
            setChanged();
            notifyObservers(currentMoveIndex);
        }
    }
}
