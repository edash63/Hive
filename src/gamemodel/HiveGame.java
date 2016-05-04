package gamemodel;

import exception.ErrorCode;
import exception.HiveException;
import gamemodel.pawn.*;
import hive.TransferPiece;

import java.util.*;

/**
 * Created by Wout Slabbinck on 31/03/2016.
 */
public class HiveGame extends Observable {
    // constanten
    private static final int HALF_BOARD_SIZE = 25;
    private static final int FULL_BOARD_SIZE = HALF_BOARD_SIZE * 2 + 1;

    private static int START_MOVE_LIST = -1;


    private BoardPosition[][] board;

    private ArrayList<HivePawn> whitePawns, blackPawns;

    private HiveMoveList moves;

    private boolean first = true; // only the first move can have no destination

    private static HiveGame ourInstance = new HiveGame();

    public static HiveGame getInstance() {
        return ourInstance;
    }

    private HiveGame() {
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

        moves = new HiveMoveList();
    }

    private void createPawns(ArrayList<HivePawn> pawnList, char color) {
        pawnList.add(new HPQueen(color));
        pawnList.add(new HPBeetle(color, 1));
        pawnList.add(new HPBeetle(color, 2));
        pawnList.add(new HPSpider(color, 1));
        pawnList.add(new HPSpider(color, 2));
        pawnList.add(new HPGrasshopper(color, 1));
        pawnList.add(new HPGrasshopper(color, 2));
        pawnList.add(new HPGrasshopper(color, 3));
        pawnList.add(new HPAnt(color, 1));
        pawnList.add(new HPAnt(color, 2));
        pawnList.add(new HPAnt(color, 3));
    }

    public ArrayList<HivePawn> getWhitePawns() {
        return whitePawns;
    }

    public ArrayList<HivePawn> getBlackPawns() {
        return blackPawns;
    }

    public HiveMoveList getMoves() {
        return moves;
    }

    public void addMove(String moveDescription)  {
        moves.addMove(moveDescription);
    }

    public void advanceMove() {
        moves.advance();
    }

    public void takebackMove() {
        moves.takeback();
    }

    public void gotoStartOfGame() {
        moves.gotoStartOfGame();
    }

    public void gotoEndOfGame() {
        moves.gotoEndOfGame();
    }

    public HivePawn find(String description) throws HiveException {
        char color = description.charAt(0);
        ArrayList<HivePawn> pawnList;

        if (color == 'w')
            pawnList = whitePawns;
        else if (color == 'b')
            pawnList = blackPawns;
        else {
            throw new HiveException(ErrorCode.INVALID_PAWN_COLOR);
        }

        for (HivePawn pc : pawnList) {
            if (description.equalsIgnoreCase(pc.getDescription()))
                return pc;
        }

        throw new HiveException(ErrorCode.INVALID_PAWN_DESCRIPTION);
    }

    public BoardPosition findNewPosition(String destinationDescription) throws HiveException {

        if (destinationDescription == null) {
            if (first) {
                first = false;
                return position(0, 0);
            }
            else {
                throw new HiveException(ErrorCode.MISSING_DESTINATION_AND_NOT_FIRST_LINE);
            }
        } else {
            //posLeft bepaalt waar het richtingsteken staat. Indien die links staat is het true
            boolean posLeft;
            int length = destinationDescription.length();
            char firstChar = destinationDescription.charAt(0);
            char lastChar = destinationDescription.charAt(length - 1);
            int xCo = 0;
            int yCo = 0;
            String anchorPieceDescription;

            if (firstChar == '-' || firstChar == '/' || firstChar == '\\') {
                anchorPieceDescription = destinationDescription.substring(1);
                posLeft = true;
            } else if (lastChar == '-' || lastChar == '/' || lastChar == '\\') {
                anchorPieceDescription = destinationDescription.substring(0, length - 1);
                posLeft = false;
            } else {
                throw new HiveException(ErrorCode.INVALID_POSITIONING_CHARACTER);
            }

            BoardPosition anchor = find(anchorPieceDescription).getPosition();
            if (anchor == null)
                throw new HiveException(ErrorCode.DESTINATION_REFERENCES_UNPLACED_PAWN);

            BoardPosition destination = null;
            if (posLeft) {
                if (firstChar == '-') {
                    destination = anchor.leftMid();
                } else if (firstChar == '/') {
                    destination = anchor.leftDown();
                } else {
                    destination = anchor.leftUp();
                }
            } else {
                if (lastChar == '-') {
                    destination = anchor.rightMid();
                } else if (lastChar == '/') {
                    destination = anchor.rightUp();
                } else {
                    destination = anchor.rightDown();
                }
            }

            if (destination.getPawn() != null) {
                throw new HiveException(ErrorCode.DESTINATION_ALREADY_OCCUPIED);
            }

            return destination;
        }
    }

    public BoardPosition position (int col, int row) throws HiveException {
        // Check if the new position is within bounds
        if ((col < -HALF_BOARD_SIZE) || (col > HALF_BOARD_SIZE) || (row < -HALF_BOARD_SIZE) || (row > HALF_BOARD_SIZE))
            throw new HiveException(ErrorCode.BOARD_OVERFLOW);

        return board[col + HALF_BOARD_SIZE][row + HALF_BOARD_SIZE];
    }

    public HivePawn findPawnOnBoard() {
        for (HivePawn pawn : whitePawns) {
            if (pawn.onBoard())
                return pawn;
        }

        for (HivePawn pawn : blackPawns) {
            if (pawn.onBoard())
                return pawn;
        }

        return null;
    }

    public Set<BoardPosition> retrieveEdgePositions() throws HiveException {
        HashSet<BoardPosition> positionList = new HashSet<>();

        boolean boardEmpty = true;
        int minRow = 0, minCol = 0, maxRow = 0, maxCol = 0;

        for (HivePawn pawn : whitePawns) {
            if (pawn.onBoard()) {
                boardEmpty = false;
                BoardPosition pos = pawn.getPosition();
                if (pos.getRow() < minRow)
                    minRow = pos.getRow();
                if (pos.getRow() > maxRow)
                    maxRow = pos.getRow();
                if (pos.getCol() < minCol)
                    minCol = pos.getCol();
                if (pos.getCol() > maxCol)
                    maxCol = pos.getCol();
            }
        }
        for (HivePawn pawn : blackPawns) {
            if (pawn.onBoard()) {
                boardEmpty = false;
                BoardPosition pos = pawn.getPosition();
                if (pos.getRow() < minRow)
                    minRow = pos.getRow();
                if (pos.getRow() > maxRow)
                    maxRow = pos.getRow();
                if (pos.getCol() < minCol)
                    minCol = pos.getCol();
                if (pos.getCol() > maxCol)
                    maxCol = pos.getCol();
            }
        }

        if (boardEmpty) {
            positionList.add(position(0, 0));
        } else {
            minRow -= 1;
            minCol -= 1;
            maxRow += 1;
            maxCol += 1;

            BoardPosition topleftEmptyPosition = null;

            for (int r = minRow; r <= maxRow; r++) {
                for (int c = minCol; c <= maxCol; c++) {
                    BoardPosition pos = position(c, r);
                    if (pos.isEmpty() && pos.hasPawnNeighbours()) {
                        positionList.add(pos);
                        if (topleftEmptyPosition == null)
                            topleftEmptyPosition = pos;
                    }
                }
            }

            // positionList has all empty positions that have at least one occupied neighbour
        }

        return positionList;
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
                list.add(new TransferPiece(pawn.getType(), pawn.getColor(), pawn.getNumber(), pos.getRow(), pos.getCol()));
        }
        for (HivePawn pawn : blackPawns) {
            BoardPosition pos = pawn.getPosition();
            if (pos != null)
                list.add(new TransferPiece(pawn.getType(), pawn.getColor(), pawn.getNumber(), pos.getRow(), pos.getCol()));
        }
        System.out.println(list.toString());
    }
}
