package gamemodel;

import exception.HiveException;
import gamemodel.pawn.HivePawn;

import javafx.scene.control.Label;

/**
 * Created by Wout Slabbinck on 21/03/2016.
 */

public class HiveMove {
    /*
     * A move is created from a 'moveDescription' string
     * At creation, this string is stored, but no further processing is performed
     * At the first invocation of 'advance()', the move is resolved:
     *  - the move description is split into the pawn description and the destination description
     *  - the pawn is retrieved
     *  - the start position is stored
     *  - the destination is determined and stored
     * If errors/exceptions are encounterd, the error is stored and the exception is thrown further
     */

    public final static int ERROR_EMPTY_MOVE = 1;
    public final static int ERROR_INVALID_PAWN = 2;

    private static int moveCount = 0;
    private int moveNr;
    private final String originalDescription;
//    private final String destinationDescription;
    private HivePawn pawn;
    private BoardPosition startPosition, destinationPosition;
    private int errorCode;

    HiveMove(String moveDescription) throws HiveException {
        moveCount += 1;
        moveNr = moveCount;
        errorCode = 0;

        originalDescription = moveDescription;

        pawn = null;
        startPosition = null;
        destinationPosition = null;

        /*
        String[] parts = moveDescription.split("[ ]+");

        if (parts.length > 1)
            destinationDescription = parts[1];
        else
            destinationDescription = null;

        try {
            if (parts.length == 0) {
                errorCode = ERROR_EMPTY_MOVE;
                pawn = null;
                startPosition = null;
                destinationPosition = null;
            }
            else {
                pawn = HiveBoard.getInstance().find(parts[0]);
                startPosition = pawn.getPosition();
                destinationPosition = HiveBoard.getInstance().findNewPosition(destinationDescription);

                pawn.move(destinationPosition);
            }
        } catch (HiveException e) {
            startPosition = null;
            destinationPosition = null;
            errorCode = ERROR_INVALID_PAWN;

            throw e;
        }
        */
        // startPosition and destinationPosition will be determined later
    }

    private boolean isMoveResolved() {
        return (pawn != null) || (errorCode != 0);
    }

    public String getMoveDescription() { return originalDescription; }

    public boolean isErroneous() { return errorCode != 0; }

    public int getErrorCode() { return errorCode; }


    public void advance() throws HiveException {
        if (!isMoveResolved()) {
            String[] parts = originalDescription.split("[ ]+");

            if (parts.length == 0) {
                errorCode = HiveException.ERROR_EMPTY_MOVE;
                throw new HiveException(HiveException.ERROR_EMPTY_MOVE);
            } else {
                try {
                    pawn = HiveBoard.getInstance().find(parts[0]);
                    startPosition = pawn.getPosition();
                    String destinationDescription = (parts.length == 1) ? null : parts[1];
                    destinationPosition = HiveBoard.getInstance().findNewPosition(destinationDescription);
                } catch (HiveException e) {
                    errorCode = 25;
                    throw e;
                }
            }
        }

        pawn.move(destinationPosition);
    }

    public void takeBack() {
//        destinationPosition.setPawn(null);
        pawn.move(startPosition);
    }
}

