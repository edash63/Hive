package gameview;

import gamemodel.BoardPosition;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import gamemodel.pawn.HivePawn;

/**
 * Created by Wout Slabbinck on 01/04/2016.
 */
public class HivePawnSprite extends Group {
    private final static double HEXAGON_SCALE_FACTOR = 75.0; // Chosen so that the images fit
    private final static double HEXAGON_WIDTH = 0.866 * HEXAGON_SCALE_FACTOR;
    private final static double HEXAGON_HEIGHT = 1.0 * HEXAGON_SCALE_FACTOR;
    private final static double FIRST_DOT_X = 22.0;
    private final static double FIRST_DOT_Y = -15.0;
    private final static double DOT_X_INCR = 0.0;
    private final static double DOT_Y_INCR = 10.0;
    private final static double DOT_RADIUS = 4.0;
    private final static double IMAGE_SCALE_FACTOR = 0.25; // Chosen so that the images fit

    public final static double VIEWERHIVEPAWN_HEIGHT = HEXAGON_HEIGHT;
    public final static double VIEWERHIVEPAWN_WIDTH = HEXAGON_WIDTH;

    // Positions on freePawnPane;
    private final static double xPosCenter = VIEWERHIVEPAWN_WIDTH * 2.0;
    private final static double xPosIncr = VIEWERHIVEPAWN_WIDTH * 1.1;
    private final static double xPosColorDistance = VIEWERHIVEPAWN_WIDTH * 3.6;
    private final static double yPosCenter = VIEWERHIVEPAWN_HEIGHT * 1.5;
    private final static double yPosIncr = VIEWERHIVEPAWN_HEIGHT * 1.5;

    private static Pane boardPane;
    private static Pane freePawnPane;

    private final HivePawn pawn;
    private final HivePawnObserver observer;

    protected boolean onBoardPane;

    public HivePawnSprite(HivePawn pawn) {

        this.pawn = pawn;
        this.observer = new HivePawnObserver(this);
        pawn.addObserver(observer);
        this.onBoardPane = false;

        Polygon hex = new Polygon(
                0.0, -HEXAGON_HEIGHT/2.0,
                HEXAGON_WIDTH/2.0, -HEXAGON_HEIGHT/4.0,
                HEXAGON_WIDTH/2.0, HEXAGON_HEIGHT/4.0,
                0.0, HEXAGON_HEIGHT/2.0,
                -HEXAGON_WIDTH/2.0, HEXAGON_HEIGHT/4.0,
                -HEXAGON_WIDTH/2.0, -HEXAGON_HEIGHT/4.0);

        if (pawn.getColor() == 'w')
            hex.getStyleClass().add("whitepawn");
        else if (pawn.getColor() == 'b')
            hex.getStyleClass().add("blackpawn");

        String imageFilename = "/images/" + pawn.getColor() + "/" + pawn.getType() + ".png";
        Image pawnImage = new Image(getClass().getResource(imageFilename).toExternalForm());

        ImageView pawnImageView = new ImageView();
        pawnImageView.setImage(pawnImage);
        pawnImageView.setFitWidth(pawnImage.getWidth()* IMAGE_SCALE_FACTOR);
        pawnImageView.setFitHeight(pawnImage.getHeight()* IMAGE_SCALE_FACTOR);
        pawnImageView.setTranslateX(-pawnImageView.getFitWidth()/2.0);
        pawnImageView.setTranslateY(-pawnImageView.getFitHeight()/2.0);
        pawnImageView.setMouseTransparent(true);
        getChildren().addAll(hex, pawnImageView);

        if (pawn.getType() != 'Q') {
            for (int i = 1; i <= pawn.getNumber(); i++) {
                Circle dot = new Circle(FIRST_DOT_X+(i-1)*DOT_X_INCR, FIRST_DOT_Y+(i-1)*DOT_Y_INCR, DOT_RADIUS);
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
        HivePawnSprite.boardPane = boardPane;
    }

    public static void setFreePawnPane(Pane freePawnPane) {
        HivePawnSprite.freePawnPane = freePawnPane;
    }

    public void place() {

        int totNrPawns = 0;

        double xPos = xPosCenter;
        double yPos = yPosCenter;

        if (pawn.getType() == 'Q') {
            yPos += 0;
            totNrPawns = 1;
        }
        else if (pawn.getType() == 'B') {
            yPos += yPosIncr;
            totNrPawns = 2;
        }
        else if (pawn.getType() == 'S') {
            yPos += 2 * yPosIncr;
            totNrPawns = 2;
        } else if (pawn.getType() == 'G') {
            yPos += 3 * yPosIncr;
            totNrPawns = 3;
        } else if (pawn.getType() == 'A') {
            yPos += 4 * yPosIncr;
            totNrPawns = 3;
        }

        xPos += ((double) pawn.getNumber() - ((double) totNrPawns + 1.0)/2.0) * xPosIncr;

        if (pawn.getColor() == 'b')
            xPos += xPosColorDistance;

        setTranslateX(xPos);
        setTranslateY(yPos);
        freePawnPane.getChildren().add(this);
    }

    public void move() {
        BoardPosition position = pawn.getPosition();

        if (position != null) {
            // destination position is on boardpane
            if (!onBoardPane) {
                // pawn is still on freePawnPane, so must be removed
                freePawnPane.getChildren().remove(this);

                boardPane.getChildren().add(this);
                onBoardPane = true;
            }

            // calculate new coordinates
            int x = position.getX();
            int y = position.getY();

            double yPane = boardPane.getHeight()/2.0 + 0.75 * y * HivePawnSprite.VIEWERHIVEPAWN_HEIGHT;
            double xPane = boardPane.getWidth()/2.0 + ((double) x + 0.5 * y) * HivePawnSprite.VIEWERHIVEPAWN_WIDTH;
            setTranslateX(xPane);
            setTranslateY(yPane);
        }
        else {
            if (onBoardPane) {
                boardPane.getChildren().remove(this);
                onBoardPane = false;
                place();
            }
        }
    }
}
