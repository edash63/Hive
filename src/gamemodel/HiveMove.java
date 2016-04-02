package gamemodel;

import exception.HiveException;
import javafx.scene.control.Label;
import pawn.HivePawn;

/**
 * Created by Wout Slabbinck on 21/03/2016.
 */
public class HiveMove {
    private static int moveCount = 0;
    private int number;
    private String pawnDescr, destinationDescription;
    private HivePawn pawn;
    private BoardPosition startPosition, destinationPosition;
    private Label moveLabel;

    HiveMove(String moveDescr) throws HiveException {

        moveCount += 1;
        number = moveCount;

        moveLabel = new Label(moveDescr);

        String[] parts = moveDescr.split("[ ]+");

        pawnDescr = parts[0];
        if (parts.length > 1)
            destinationDescription = parts[1];

        pawn = HiveBoard.getInstance().find(pawnDescr);
        // startPosition and destinationPosition will be determined later
    }

    public Label getMoveLabel() {
        return moveLabel;
    }

    public void advance(boolean executeViewer) throws HiveException {
        // The game must be in the correct state for the move
        if (destinationPosition == null) {
            // advance is called for the first time
            startPosition = pawn.getPosition();
            destinationPosition = HiveBoard.getInstance().findNewPosition(destinationDescription);
        }
        pawn.move(destinationPosition, executeViewer);
    }

    public void takeBack() throws HiveException {
        destinationPosition.setPawn(null);
        pawn.move(startPosition, true);
    }


}

