package gamemodel;

import exception.HiveException;
import gamemodel.pawn.HivePawn;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static gamemodel.BoardPosition.Direction.*;

/**
 * Created by Wout Slabbinck on 31/03/2016.
 */
public class BoardPosition {
    private final int col, row;
    private HivePawn pawn;

    public enum Direction { // (row, col, char)
        LEFT_UP(-1, 0, '\\'),
        LEFT_MID(0, -1, '-'),
        LEFT_DOWN(1, -1, '/'),
        RIGHT_DOWN(1, 0, '\\'),
        RIGHT_MID(0, 1, '-'),
        RIGHT_UP(-1, 1, '/');

        private int row, col;
        private char positioningChar;

        Direction(int row, int col, char positioningChar) {
            this.row = row;
            this.col = col;
            this.positioningChar = positioningChar;
        }
    }

    public BoardPosition(int col, int row) {
        this.pawn = null;
        this.row = row;
        this.col = col;
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

    public boolean isEmpty() {
        return (pawn == null);
    }

    public boolean isOccupied() {
        return (pawn != null);
    }

    public BoardPosition neighbour(Direction direction) throws HiveException {
        return HiveGame.getInstance().position(col + direction.col, row + direction.row);
    }

    public BoardPosition leftUp() throws HiveException {
        return neighbour(LEFT_UP);
    }

    public BoardPosition leftMid() throws HiveException {
        return neighbour(LEFT_MID);
    }

    public BoardPosition leftDown() throws HiveException {
        return neighbour(LEFT_DOWN);
    }

    public BoardPosition rightUp() throws HiveException {
        return neighbour(RIGHT_UP);
    }

    public BoardPosition rightMid() throws HiveException {
        return neighbour(RIGHT_MID);
    }

    public BoardPosition rightDown() throws HiveException {
        return neighbour(RIGHT_DOWN);
    }


    private static boolean useDirection = true;
    // onderzoekt de omgeving van een positie
    private class NeighbourContext {
        BoardPosition position;
        Set<BoardPosition> pawnNeighbours, emptyNeighbours;
        Map<BoardPosition, Set<BoardPosition>> neighbourSwarms;
        int nrNeighbourSwarms;


        public NeighbourContext(BoardPosition position) throws HiveException {
            this.position = position;
            pawnNeighbours = new HashSet<>();
            emptyNeighbours = new HashSet<>();
            neighbourSwarms = null;
            nrNeighbourSwarms = 0;
            evaluateNeighbours();
        }

        public int getCount() {
            return pawnNeighbours.size();
        }

        protected boolean hasPawnNeighbours() {
            return getCount() > 0;
        }

        public Set<BoardPosition> getEmptyNeighbours() {
            return emptyNeighbours;
        }

        public Set<BoardPosition> getPawnNeighbours() {
            return pawnNeighbours;
        }

        public void evaluatePosition(BoardPosition nb) {
            if (nb.isOccupied()) {
                pawnNeighbours.add(nb);
            } else {
                emptyNeighbours.add(nb);
            }
        }

        public void evaluatePosition(Direction direction) throws HiveException {
            BoardPosition nb = position.neighbour(direction);

            if (nb.isOccupied()) {
                pawnNeighbours.add(nb);
            } else {
                emptyNeighbours.add(nb);
            }
        }

        public void evaluateNeighbours() throws HiveException {
            if (useDirection) {
                for (Direction direction : Direction.values()) {
                    evaluatePosition(direction);
                }
            } else {
                evaluatePosition(position.leftDown());
                evaluatePosition(position.leftMid());
                evaluatePosition(position.leftUp());
                evaluatePosition(position.rightUp());
                evaluatePosition(position.rightMid());
                evaluatePosition(position.rightDown());
            }
        }

        protected Map<BoardPosition, Set<BoardPosition>> determineNeighbourSwarms() throws HiveException {
            if (neighbourSwarms == null) {
                neighbourSwarms = new HashMap<>();

                for (BoardPosition neighbour : pawnNeighbours) {
                    boolean contains = false;
                    Set<BoardPosition> swarm = null;
                    for (Map.Entry<BoardPosition, Set<BoardPosition>> entry : neighbourSwarms.entrySet()) {
                        if (entry.getValue().contains(neighbour)) {
                            contains = true;
                            swarm = entry.getValue();
                            break;
                        }
                    }

                    if (contains) {
                        neighbourSwarms.put(neighbour, swarm);
                    } else {
                        swarm = new HashSet<>();
                        position.searchGraph(neighbour, swarm);
                        neighbourSwarms.put(neighbour, swarm);
                        nrNeighbourSwarms += 1;
                    }
                }
            }

            return neighbourSwarms;
        }

        protected int getNrNeighbourSwarms() throws HiveException {
            if (neighbourSwarms == null) {
                determineNeighbourSwarms();
            }

            return nrNeighbourSwarms;
        }
    }

    public boolean hasPawnNeighbours() throws HiveException {
        return new NeighbourContext(this).hasPawnNeighbours();
    }

    public Map<BoardPosition, Set<BoardPosition>> connected() throws HiveException {
        return new NeighbourContext(this).determineNeighbourSwarms();
    }

    // geeft de samenhangende zwerm vertrekende van de 'startPosition',
    // waarbij 'anchorPosition' wordt weggelaten omdat dit het stuk is dat verplaatst zou worden.
    // Deze zwerm wordt berekend met behulp van backtracking en een zoekalgoritme voor het doorlopen van een graaf.

    public void searchGraph(BoardPosition startPosition, Set<BoardPosition> connected) throws HiveException {
        if (!connected.contains(startPosition)) {
            connected.add(startPosition);
            NeighbourContext context = new NeighbourContext(startPosition);
            Set<BoardPosition> startNeighbours = context.getPawnNeighbours();

            for (BoardPosition neighbour : startNeighbours) {
                if (!connected.contains(neighbour) && neighbour != this) {
                    searchGraph(neighbour, connected);
                }
            }
        }
    }


    //isconnected geeft een booleaanse waarde terug.
    // Als de zwerm samenhangend is na het verplaatsen van het huidige stuk, dan geeft het true terug.
    // Anders geeft het false terug.
    public boolean isConnected() throws HiveException {
        boolean isConnected = true;
        Map<BoardPosition, Set<BoardPosition>> connect = this.connected();
        NeighbourContext context = new NeighbourContext(this);
        Set<BoardPosition> pawnNeighbours = context.getPawnNeighbours();
        Set<BoardPosition> swarm = new HashSet<>();
        for (BoardPosition pawnNeighbour : pawnNeighbours) {
            if (swarm.size() == 0) {
                swarm = connect.get(pawnNeighbour);
            } else {
                Set<BoardPosition> neighbourSwarm = connect.get(pawnNeighbour);
                for (BoardPosition pawn : swarm) {
                    if (!neighbourSwarm.contains(pawn))
                        return false;
                }
                for (BoardPosition pawn : neighbourSwarm) {
                    if (!swarm.contains(pawn)) {
                        return false;
                    }
                }
            }
        }
        return isConnected;
    }

    // de methode step genereert een set die alle stappen bevat die kunnen gezet worden vanaf de huidige positie
    public Set<BoardPosition> step() throws HiveException {
        Map<BoardPosition, NeighbourContext> possibleDestinations = new HashMap<>();
        Set<BoardPosition> destinations = new HashSet<>();

        NeighbourContext context = new NeighbourContext(this);
        Set<BoardPosition> emptyNeighbours = context.getEmptyNeighbours();
        Set<BoardPosition> pawnNeighbours = context.getPawnNeighbours();
        Set<BoardPosition> edgePositions = HiveGame.getInstance().retrieveEdgePositions();


        // Kijken of er possibleDestinations zijn, als er geen zijn wordt er een lege set gereturned.
        for (BoardPosition emptyNeighbour : emptyNeighbours) {
            NeighbourContext neighbourContext = new NeighbourContext(emptyNeighbour);
            if (neighbourContext.hasPawnNeighbours()) {
                possibleDestinations.put(emptyNeighbour, neighbourContext);
            }
        }

        // volgende if is overbodig want impliciet in for-loop
        if (possibleDestinations.size() == 0) {
            return destinations;
        }

        // Kijken of er een doorgang is naar de possibleDestinations,
        for (Map.Entry<BoardPosition, NeighbourContext> entry : possibleDestinations.entrySet()) {
            boolean doorgang = false; //hernoemen
            boolean continu = false; //hernoemen
            BoardPosition possibleDestination = entry.getKey();
            NeighbourContext possibleDestinationContext = entry.getValue();

            //doorgang deel: this en possibleDestination moeten grenzen aan zelfde lege positie
            for (BoardPosition emptyNeighbour : emptyNeighbours) {
                if (possibleDestinationContext.getEmptyNeighbours().contains(emptyNeighbour)) {
                    doorgang = true;
                    break;
                }
            }
            //continu contact deel: this en possibleDestination moeten grenzen aan zelfde pawn
            for (BoardPosition pawnNeighbour : pawnNeighbours) {
                if (possibleDestinationContext.getPawnNeighbours().contains(pawnNeighbour)) {
                    continu = true;
                    break;
                }
            }

            if (doorgang && continu) {
                destinations.add(possibleDestination);
            }

        }
        return destinations;
    }

    public Set<BoardPosition> threeSteps() throws HiveException {
        Set<BoardPosition> destinations = new HashSet<>();
        HivePawn temporary = this.getPawn();
        this.setPawn(null);
        threeMoves(destinations, new HashMap<>());
        this.setPawn(temporary);
        return destinations;
    }

    private void threeMoves(Set<BoardPosition> destinations, Map<Integer, BoardPosition> currentPath) throws HiveException {
        int maxStep = 3;
        int numberOfsteps = currentPath.size();
        Set<BoardPosition> currentSteps;

        if (numberOfsteps == maxStep) {
            destinations.add(currentPath.get(maxStep));
        } else {
            if (numberOfsteps == 0) {
                currentSteps = this.step();
            } else {
                currentSteps = currentPath.get(numberOfsteps).step();
            }
            for (BoardPosition currentStep : currentSteps) {
                if (!currentPath.values().contains(currentStep) && currentStep != this) {
                    currentPath.put(numberOfsteps + 1, currentStep);
                    threeMoves(destinations, currentPath);
                    currentPath.remove(numberOfsteps + 1);
                }
            }
        }
    }

    public Set<BoardPosition> allSteps() throws HiveException {
        Set<BoardPosition> destinations = new HashSet<>();
        HivePawn temporary = this.getPawn();
        this.setPawn(null);
        allMoves(destinations, new LinkedList<>(), this);
        this.setPawn(temporary);
        return destinations;
    }

    private void allMoves(Set<BoardPosition> destinations, LinkedList<BoardPosition> currentPath,
                          BoardPosition possibleDestination) throws HiveException {
        int numberOfsteps = currentPath.size();
        Set<BoardPosition> currentSteps;

        if (!destinations.contains(possibleDestination) && possibleDestination != this) {
            destinations.add(possibleDestination);
            allMoves(destinations, currentPath, this);

        } else {
            if (numberOfsteps == 0) {
                currentSteps = this.step();
            } else {
                currentSteps = currentPath.get(numberOfsteps - 1).step();
            }
            for (BoardPosition currentStep : currentSteps) {
                if (!currentPath.contains(currentStep) && currentStep != this) {
                    currentPath.add(currentStep);
                    allMoves(destinations, currentPath, currentStep);
                }
            }
        }
    }

    public Set<BoardPosition> grasshopperSteps() throws HiveException {
        Set<BoardPosition> destinations = new HashSet<>();

        if (useDirection) {
            for (Direction direction : Direction.values()) {
                BoardPosition pos = neighbour(direction);
                if (pos.getPawn() != null) {
                    while (pos.getPawn() != null) {
                        pos = pos.neighbour(direction);
                    }
                    destinations.add(pos);
                }
            }
        } else {
            BoardPosition leftUp = this.leftUp();
            if (leftUp.getPawn() != null) {
                while (leftUp.getPawn() != null) {
                    leftUp = leftUp.leftUp();
                }
                destinations.add(leftUp);
            }

            BoardPosition leftMid = this.leftMid();
            if (leftMid.getPawn() != null) {
                while (leftMid.getPawn() != null) {
                    leftMid = leftMid.leftMid();
                }
                destinations.add(leftMid);
            }

            BoardPosition leftDown = this.leftDown();
            if (leftDown.getPawn() != null) {
                while (leftDown.getPawn() != null) {
                    leftDown = leftDown.leftDown();
                }
                destinations.add(leftDown);
            }

            BoardPosition rightUp = this.rightUp();
            if (rightUp.getPawn() != null) {
                while (rightUp.getPawn() != null) {
                    rightUp = rightUp.rightUp();
                }
                destinations.add(rightUp);
            }

            BoardPosition rightMid = this.rightMid();
            if (rightMid.getPawn() != null) {
                while (rightMid.getPawn() != null) {
                    rightMid = rightMid.rightMid();
                }
                destinations.add(rightMid);
            }

            BoardPosition rightDown = this.rightDown();
            if (rightDown.getPawn() != null) {
                while (rightDown.getPawn() != null) {
                    rightDown = rightDown.rightDown();
                }
                destinations.add(rightDown);
            }
        }
        return destinations;
    }

    // genereert alle mogelijke zetten dat een bepaalt stuk kan verzetten.
    public Set<BoardPosition> destinations() throws HiveException {
        Set<BoardPosition> destinationSet = new HashSet<>();
        char type = this.getPawn().getType();

        // testen of de zwerm samenhangend is indien het stuk verplaatst wordt.
        if (!this.isConnected()) {
            return destinationSet;
        } else {
            return pawn.destinations();
        }
    }
}
