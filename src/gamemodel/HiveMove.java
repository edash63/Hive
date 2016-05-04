package gamemodel;

import exception.ErrorCode;
import exception.HiveException;
import gamemodel.pawn.HivePawn;
import hive.Hive;

import java.util.ArrayList;

/**
 * Created by Wout Slabbinck on 21/03/2016.
 */

public class HiveMove {
    /*
     * Een HiveMove wordt aangemaakt van een 'moveDescription' string
     * Bij de creatie wordt deze string opgeslagen zonder verdere behandeling
     * Bij de eerste oproep van advance() wordt de HiveMove verder bepaald:
     *  - moveDescription wordt gesplits in de beschrijving van de pawn en die van de bestemming
     *  - de pawn wordt opgezocht
     *  - de start position wordt opgeslagen; dit is nodig zetten te kunnen terugnemen
     *  - de bestemming wordt bepaald en opgeslagen
     * Als fouten of exceptions optreden, wordt de oorzaak opgeslagen en wordt de exception verdergeworpen
     */

    private static int moveCount = 0;
    private int moveNr;
    private final String originalDescription;
    private HivePawn pawn;
    private BoardPosition startPosition, destinationPosition;
    private ErrorCode errorCode;

    HiveMove(String moveDescription) {
        moveCount += 1;
        moveNr = moveCount;
        errorCode = ErrorCode.NO_ERROR;

        originalDescription = moveDescription;

        // pawn, startPosition en destinationPosition worden bepaald bij de eerstge oproep van advance()
        pawn = null;
        startPosition = null;
        destinationPosition = null;
    }

    private boolean isMoveResolved() {
        return (pawn != null) || isErroneous();
    }

    public String getMoveDescription() {
        return originalDescription;
    }

    public boolean isErroneous() {
        return errorCode != ErrorCode.NO_ERROR;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void advance() throws HiveException {
        if (!isMoveResolved()) {
            String[] parts = originalDescription.split("[ ]+");

            if (parts.length == 0) {
                errorCode = ErrorCode.EMPTY_MOVE;
                throw new HiveException(errorCode);
            } else {
                try {
                    HiveGame game = HiveGame.getInstance();

                    pawn = game.find(parts[0]);
                    startPosition = pawn.getPosition();
                    String destinationDescription = (parts.length == 1) ? null : parts[1];
                    destinationPosition = game.findNewPosition(destinationDescription);
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
