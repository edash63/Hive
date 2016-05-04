package gameview;

import exception.HiveException;
import gamecontroller.HiveController;
import gamemodel.BoardPosition;
import gamemodel.pawn.HivePawn;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * Created by Wout Slabbinck on 01/04/2016.
 */

public class HivePawnViewer extends HivePositionViewer implements Observer {
    // Constanten voor de representaties van de pionnen
    private final static double FIRST_DOT_X = 22.0;
    private final static double FIRST_DOT_Y = -15.0;
    private final static double DOT_X_INCR = 0.0;
    private final static double DOT_Y_INCR = 10.0;
    private final static double DOT_RADIUS = 4.0;
    private final static double IMAGE_SCALE_FACTOR = 0.25; // Om images te doen passen

    public final static double HIVEPAWNVIEWER_HEIGHT = HEXAGON_HEIGHT;
    public final static double HIVEPAWNVIEWER_WIDTH = HEXAGON_WIDTH;

    // Constanten voor de posities op freePawnPane
    public final static double xPosCenter = HIVEPAWNVIEWER_WIDTH * 2.0;
    public final static double xPosIncr = HIVEPAWNVIEWER_WIDTH * 1.1;
    public final static double xPosColorDistance = HIVEPAWNVIEWER_WIDTH * 0.0;     // 3.6 voor groepen naasteen
    public final static double yPosCenter = HIVEPAWNVIEWER_HEIGHT * 0.75;          // 1.5 indien groepen naasteen
    public final static double yPosIncr = HIVEPAWNVIEWER_HEIGHT * 0.85;
    public final static double yPosColorDistance = HIVEPAWNVIEWER_HEIGHT * 4.6; // 5.6 voor groepen ondereen


    private static Pane boardPane;
    private static Pane freePawnPane;
    private static HiveController controller;

    private final HivePawn pawn;

    public HivePawnViewer(HivePawn pawn) {
        super(true);

        this.pawn = pawn;
        position = pawn.getPosition();

        pawn.addObserver(this);
        setOnMouseClicked(this::handleMouseClicked);

        if (pawn.getColor() == 'w')
            hex.getStyleClass().add("whitepawn");
        else if (pawn.getColor() == 'b')
            hex.getStyleClass().add("blackpawn");

        String imageFilename = "/images/" + pawn.getColor() + "/" + pawn.getType() + ".png";
        Image pawnImage = new Image(getClass().getResource(imageFilename).toExternalForm());

        ImageView pawnImageView = new ImageView();
        pawnImageView.setImage(pawnImage);
        pawnImageView.setFitWidth(pawnImage.getWidth() * IMAGE_SCALE_FACTOR);
        pawnImageView.setFitHeight(pawnImage.getHeight() * IMAGE_SCALE_FACTOR);
        pawnImageView.setTranslateX(-pawnImageView.getFitWidth() / 2.0);
        pawnImageView.setTranslateY(-pawnImageView.getFitHeight() / 2.0);
        pawnImageView.setMouseTransparent(true);
        getChildren().add(pawnImageView);

        if (pawn.getType() != 'Q') {
            for (int i = 1; i <= pawn.getNumber(); i++) {
                Circle dot = new Circle(FIRST_DOT_X + (i - 1) * DOT_X_INCR, FIRST_DOT_Y + (i - 1) * DOT_Y_INCR, DOT_RADIUS);
                if (pawn.getColor() == 'w')
                    dot.getStyleClass().add("dotwhitepawn");
                else if (pawn.getColor() == 'b')
                    dot.getStyleClass().add("dotblackpawn");
                dot.setMouseTransparent(true);

                getChildren().add(dot);
            }
        }
    }

    public static void setBoardPane(Pane boardPane) {
        HivePawnViewer.boardPane = boardPane;
    }

    public static void setFreePawnPane(Pane freePawnPane) {
        HivePawnViewer.freePawnPane = freePawnPane;
    }

    public static void setController(HiveController controller) {
        HivePawnViewer.controller = controller;
        HivePawnViewer.boardPane = controller.boardPane;
        HivePawnViewer.freePawnPane = controller.freePawnPane;
    }

    public HivePawn getPawn() {
        return pawn;
    }

    public void placeOnFreePawnPane() {
        int totNrPawns = 0;

        setPosition(null);

        double xPos = xPosCenter;
        double yPos = yPosCenter;
        boolean xOffsetAltLine = false;

        if (pawn.getType() == 'Q') {
            yPos += 0;
            totNrPawns = 1;
        } else if (pawn.getType() == 'B') {
            yPos += yPosIncr;
            totNrPawns = 2;
        } else if (pawn.getType() == 'S') {
            yPos += 2 * yPosIncr;
            totNrPawns = 2;
            xOffsetAltLine = true;
        } else if (pawn.getType() == 'G') {
            yPos += 3 * yPosIncr;
            totNrPawns = 3;
            xOffsetAltLine = true;
        } else if (pawn.getType() == 'A') {
            yPos += 4 * yPosIncr;
            totNrPawns = 3;
        }

        xPos += ((double) pawn.getNumber() - ((double) totNrPawns + 1.0) / 2.0) * xPosIncr;
        if (xOffsetAltLine) {
            xPos += xPosIncr / 2.0;
        }

        if (pawn.getColor() == 'b') {
            xPos += xPosColorDistance;
            yPos += yPosColorDistance;
        }

        setTranslateX(xPos);
        setTranslateY(yPos);
        freePawnPane.getChildren().add(this);
    }

    public void move() {
        BoardPosition position = pawn.getPosition();

        if (position != null) {
            // destination position is on boardpane
            if (!isOnBoardPane()) {
                // pawn is still on freePawnPane, so must be removed
                freePawnPane.getChildren().remove(this);
            }

            placeOnBoardPane(position);
        } else {
            if (isOnBoardPane()) {
                boardPane.getChildren().remove(this);
                placeOnFreePawnPane();
            }
        }
    }

    /*   mouseclick toont buren van elk element
        private void handleMouseClicked(MouseEvent event) {
            BoardPosition position = pawn.getPosition();
            String neighbourString = "";
            if (position != null) {
                Set<BoardPosition> neighbours = position.getNeighbours();
                for (BoardPosition neighbour : neighbours) {
                    neighbourString += neighbour.getPawn().getDescription() + neighbour.getPawn().positionDescription() + "\n";
                }
            } else {
                neighbourString = pawn.getDescription() + " has no neighbours because it's not on the board yet.";
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Neighbours");
            alert.setHeaderText("Pawn: "+ pawn.getDescription() + " " + pawn.positionDescription());
            alert.setContentText(neighbourString);

            alert.showAndWait();
            event.consume();
        }
        */
    /*
    private void handleMouseClicked(MouseEvent event) {
        BoardPosition position = pawn.getPosition();
        String tekst = "";

        if (position != null) {
            try {
                Map<BoardPosition, Set<BoardPosition>> connect = position.connected();
                Set<BoardPosition> neighbours = position.getNeighbours();

                for (BoardPosition neighbour : neighbours) {
                    tekst += neighbour.getPawn().getDescription() + ": ";
                    Set<BoardPosition> set = connect.get(neighbour);
                    for (BoardPosition pos : set) {
                        tekst += pos.getPawn().getDescription() + ", ";
                    }
                    tekst += "\n";
                }
            } catch (HiveException e) {
                tekst = "Error: " + e.getReason().name();
            }
        } else {
            tekst = pawn.getDescription() + " is not on the board yet.";
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Samenhangend");
        alert.setHeaderText(pawn.getDescription());
        alert.setContentText(tekst);

        alert.showAndWait();
        event.consume();
    }
*/
    /*
    private void handleMouseClicked(MouseEvent event) {
        BoardPosition position = pawn.getPosition();
        String tekst = "";

        if (position != null) {
            try {
                Set<BoardPosition> stappen = position.step();
                for (BoardPosition stap : stappen){
                    tekst += "- (" +stap.getRow() + ", "+ stap.getCol()+ ")\n";
                }
            } catch (HiveException e) {
                tekst = "Error: " + e.getReason().name();
            }
        } else {
            tekst = pawn.getDescription() + " is not on the board yet.";
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mogelijke positie");
        alert.setHeaderText(pawn.getDescription());
        alert.setContentText(tekst);

        alert.showAndWait();
        event.consume();
    }
    */
    private void handleMouseClicked(MouseEvent event) {
        BoardPosition position = pawn.getPosition();
        String tekst = "";
        String description = "";
        int index = 1;
        if (position != null) {
            description = ": (" + pawn.getPosition().getRow() + ", " + pawn.getPosition().getCol() + ")";
            try {
                Set<BoardPosition> stappen = position.destinations();
                controller.highlightPositions(stappen);
                for (BoardPosition stap : stappen) {
                    tekst += index + ". (" + stap.getRow() + ", " + stap.getCol() + ")\n";
                    index += 1;
                }
                if (stappen.size() == 0) {
                    tekst = "Geen zetten mogelijk";
                }
            } catch (HiveException e) {
                tekst = "Error: " + e.getReason().name();
            }
        } else {
            tekst = pawn.getDescription() + " is not on the board yet.";
        }
/*
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mogelijke zetten");
        alert.setHeaderText(pawn.getDescription() + description);
        alert.setContentText(tekst);

        alert.showAndWait();
        */
        event.consume();
    }

    @Override
    public void update(Observable observable, Object object) {
        move();
    }
}
