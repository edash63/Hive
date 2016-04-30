package gamemodel;

import exception.ErrorCode;
import exception.HiveException;
import gamemodel.pawn.HivePawn;

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
    private ErrorCode errorCode;

    HiveMove(String moveDescription) throws HiveException {
        moveCount += 1;
        moveNr = moveCount;
        errorCode = ErrorCode.NO_ERROR;

        originalDescription = moveDescription;

        // pawn, startPosition and destinationPosition will be determined at first invocation of advance()
        pawn = null;
        startPosition = null;
        destinationPosition = null;
    }

    private boolean isMoveResolved() {
        return (pawn != null) || isErroneous();
    }

    public String getMoveDescription() {
        if (!isErroneous())
            return originalDescription;
        else
            return originalDescription + " ! " + errorCode.name();
    }

    public boolean isErroneous() { return errorCode != ErrorCode.NO_ERROR; }

    public ErrorCode getErrorCode() { return errorCode; }

    public void advance() throws HiveException {
        if (!isMoveResolved()) {
            String[] parts = originalDescription.split("[ ]+");

            if (parts.length == 0) {
                errorCode = ErrorCode.EMPTY_MOVE;
                throw new HiveException(errorCode);
            } else {
                try {
                    pawn = HiveBoard.getInstance().find(parts[0]);
                    startPosition = pawn.getPosition();
                    String destinationDescription = (parts.length == 1) ? null : parts[1];
                    destinationPosition = HiveBoard.getInstance().findNewPosition(destinationDescription);
                } catch (HiveException e) {
                    errorCode = e.getReason();
                    throw e;
                }
            }
        }
        if (!isErroneous())
            pawn.move(destinationPosition);
    }

    public void takeBack() {
        if (isMoveResolved() && !isErroneous())
            pawn.move(startPosition);
    }
}

