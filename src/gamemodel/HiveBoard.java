package gamemodel;

import exception.HiveException;
import hive.TransferPiece;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Label;
import pawn.*;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by Wout Slabbinck on 31/03/2016.
 */
public class HiveBoard {
    // constants
    private static final int HALF_BOARD_SIZE = 25;
    private static final int FULL_BOARD_SIZE = HALF_BOARD_SIZE * 2 + 1;

    private static int START_MOVE_LIST = -1;

    // fields
    private BoardPosition[][] board;

    private ArrayList<HivePawn> whitePawns, blackPawns;

    private ArrayList<HiveMove> hiveMoveList;
    private int currentMoveIndex;
    private IntegerProperty curMoveIndex;

    private boolean first = true; // only the first move can have no destination

    //methods
    private static HiveBoard ourInstance = new HiveBoard();

    public static HiveBoard getInstance() {
        return ourInstance;
    }

    private HiveBoard() {
        board = new BoardPosition[FULL_BOARD_SIZE][FULL_BOARD_SIZE];
        for (int i = 0; i < FULL_BOARD_SIZE; i++) {
            for (int j = 0; j < FULL_BOARD_SIZE; j++) {
                board[i][j] = new BoardPosition(i - HALF_BOARD_SIZE, j - HALF_BOARD_SIZE);
            }
        }
        whitePawns = new ArrayList<>();
        blackPawns = new ArrayList<>();
        createPawns(whitePawns, 'w');
        createPawns(blackPawns, 'b');

        hiveMoveList = new ArrayList<>();
        currentMoveIndex = START_MOVE_LIST;
        curMoveIndex = new SimpleIntegerProperty(START_MOVE_LIST);
    }

    private void createPawns(ArrayList<HivePawn> pawnList, char color) {
        pawnList.add(new HPQueen(color));
        pawnList.add(new HPSpider(color, 1));
        pawnList.add(new HPSpider(color, 2));
        pawnList.add(new HPBeetle(color, 1));
        pawnList.add(new HPBeetle(color, 2));
        pawnList.add(new HPAnt(color, 1));
        pawnList.add(new HPAnt(color, 2));
        pawnList.add(new HPAnt(color, 3));
        pawnList.add(new HPGrasshopper(color, 1));
        pawnList.add(new HPGrasshopper(color, 2));
        pawnList.add(new HPGrasshopper(color, 3));
    }

    public void InitializeViewer(){
        whitePawns.forEach(pawn -> pawn.placePawnViewer());
        blackPawns.forEach(pawn -> pawn.placePawnViewer());
    }

    public ArrayList<HiveMove> getHiveMoveList() {
        return hiveMoveList;
    }

    public IntegerProperty currentMoveIndexProperty() { return curMoveIndex; }

    public void addMove(String moveDescription) throws HiveException {
        hiveMoveList.add(new HiveMove(moveDescription));
    }

    private void advanceMove(boolean executeViewer) throws HiveException {
        if (hasNextMove()) {
            currentMoveIndex += 1;
            hiveMoveList.get(currentMoveIndex).advance(executeViewer);
            if (executeViewer)
                curMoveIndex.set(currentMoveIndex);
        }
    }

    public void advanceMove() throws HiveException {
        advanceMove(true);
    }

    public void takebackMove() throws HiveException {
        if (hasPreviousMove()) {
            hiveMoveList.get(currentMoveIndex).takeBack();
            currentMoveIndex -= 1;
            curMoveIndex.set(currentMoveIndex);
        }
    }

    public boolean hasNextMove() {
        return (currentMoveIndex < hiveMoveList.size()-1);
    }

    public boolean hasPreviousMove() {
        return (currentMoveIndex > START_MOVE_LIST);
    }

    public void gotoStartOfGame() throws HiveException {
        while (hasPreviousMove())
            takebackMove();
    }

    public void gotoEndOfGame() throws HiveException {
        while (hasNextMove())
            advanceMove(true);
    }

    public void resolveMoves() throws HiveException {
        while (hasNextMove())
            advanceMove(false);

        // restore board to initial state
        for (int i = 0; i < FULL_BOARD_SIZE; i++) {
            for (int j = 0; j < FULL_BOARD_SIZE; j++) {
                board[i][j].setPawn(null);
            }
        }

        whitePawns.forEach(pawn -> pawn.setPosition(null));
        blackPawns.forEach(pawn -> pawn.setPosition(null));

        currentMoveIndex = START_MOVE_LIST;
    }

    public void place(String pawnDescr, String placeDescr) throws HiveException {

        HivePawn pawn = find(pawnDescr);
        BoardPosition boardPosition = findNewPosition(placeDescr);
        if (boardPosition.getPawn() != null)
            throw new HiveException("Position already taken: " + pawnDescr + " " + placeDescr);
        else
            pawn.setPosition(boardPosition);
    }

    public HivePawn find(String description) throws HiveException {
        char color = description.charAt(0);
        ArrayList<HivePawn> pawnList = null;

        if (color == 'w')
            pawnList = whitePawns;
        else if (color == 'b')
            pawnList = blackPawns;
        else {
            throw new HiveException("Incorrect pawn color: " + color);
        }

        for (HivePawn pc : pawnList) {
            if (description.equalsIgnoreCase(pc.getDescription()))
                return pc;
        }

        throw new HiveException("Incorrect pawn getDescription: " + description);
    }

    public BoardPosition findNewPosition(String destinationDescription) throws HiveException {
        //posLeft bepaalt waar het richtingsteken staat. Indien die links staat is het true
        boolean posLeft;
        char firstChar, lastChar;
        int xCo = 0;
        int yCo = 0;
        String anchorPieceDescription;

        if (destinationDescription == null) {
            if (first) {
                first = false;
                return board[HALF_BOARD_SIZE][HALF_BOARD_SIZE];
            }
            else {
                throw new HiveException("There can only be one pawn without destination. (line "+ (currentMoveIndex+1)+")");
            }
        } else {
            int length = destinationDescription.length();
            firstChar = destinationDescription.charAt(0);
            lastChar = destinationDescription.charAt(length - 1);

            if (firstChar == '-' || firstChar == '/' || firstChar == '\\') {
                anchorPieceDescription = destinationDescription.substring(1);
                posLeft = true;
            } else {
                anchorPieceDescription = destinationDescription.substring(0, length - 1);
                posLeft = false;
            }

            if (posLeft) {
                if (firstChar == '-') {
                    xCo = -1;
                    yCo = 0;
                }
                if (firstChar == '/') {
                    xCo = -1;
                    yCo = 1;
                }
                if (firstChar == '\\') {
                    xCo = 0;
                    yCo = -1;
                }
            } else {
                if (lastChar == '-') {
                    xCo = 1;
                    yCo = 0;
                }
                if (lastChar == '/') {
                    xCo = 1;
                    yCo = -1;
                }
                if (lastChar == '\\') {
                    xCo = 0;
                    yCo = 1;
                }
            }

        }
        BoardPosition boardPos = find(anchorPieceDescription).getPosition();
        if (boardPos == null)
            throw new HiveException("Destination references pawn that is not placed yet: " + destinationDescription);
        int xPos = boardPos.getX() + xCo;
        int yPos = boardPos.getY() + yCo;

        // Check if the new position is within bounds
        if ((xPos < -HALF_BOARD_SIZE) || (xPos > HALF_BOARD_SIZE) || (yPos < -HALF_BOARD_SIZE) || (yPos > HALF_BOARD_SIZE))
            throw new HiveException("New destination is too far from the initial position: " + destinationDescription);

        // Check if there is no pawn on those coördinates
        if (board[xPos + HALF_BOARD_SIZE][yPos + HALF_BOARD_SIZE].getPawn()!= null){
            throw new HiveException("There is a pawn on this position already: " + destinationDescription);
        }

        // return the new position
        return board[xPos + HALF_BOARD_SIZE][yPos + HALF_BOARD_SIZE];

    }

    public void printPawns() {
        for (HivePawn pawn : whitePawns) System.out.println(pawn.getDescription() + ":" + pawn.positionDescription());
        for (HivePawn pawn : blackPawns) System.out.println(pawn.getDescription() + ":" + pawn.positionDescription());
    }

    public void transferPieces() {
        TreeSet<TransferPiece> list = new TreeSet<>();
        // tree zorgt voor gesoorteerde lijst van de pawns
        for (HivePawn pawn : whitePawns) {
            BoardPosition pos = pawn.getPosition();
            if (pos != null)
                list.add(new TransferPiece(pawn.getType(), pawn.getColor(), pawn.getNumber(), pos.getY(), pos.getX()));
        }
        for (HivePawn pawn : blackPawns) {
            BoardPosition pos = pawn.getPosition();
            if (pos != null)
                list.add(new TransferPiece(pawn.getType(), pawn.getColor(), pawn.getNumber(), pos.getY(), pos.getX()));
        }
        System.out.println(list.toString());
    }
}
