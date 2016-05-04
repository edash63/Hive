package gameview;

import gamecontroller.HiveController;
import gamemodel.BoardPosition;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Polygon;

/**
 * @author WS
 *         3/05/2016.
 */
public class HivePositionViewer extends Group {
    protected final static double HEXAGON_RADIUS = 37.5; // Om images te doen passen binnen zeshoek
    protected final static double HEXAGON_NORMALISED_WIDTH = 0.866;
    protected final static double HEXAGON_NORMALISED_HEIGHT = 1.0;
    protected final static double HEXAGON_WIDTH = 2.0 * HEXAGON_RADIUS * HEXAGON_NORMALISED_WIDTH;
    protected final static double HEXAGON_HEIGHT = 2.0 * HEXAGON_RADIUS * HEXAGON_NORMALISED_HEIGHT;

    protected static HiveController controller;

    protected Polygon hex;  // Ref nodig om style te kunnen zetten
    protected BoardPosition position;

    protected HivePositionViewer(boolean isPawn) {
        hex = new Polygon(
                0.0, -HEXAGON_HEIGHT / 2.0,
                HEXAGON_WIDTH / 2.0, -HEXAGON_HEIGHT / 4.0,
                HEXAGON_WIDTH / 2.0, HEXAGON_HEIGHT / 4.0,
                0.0, HEXAGON_HEIGHT / 2.0,
                -HEXAGON_WIDTH / 2.0, HEXAGON_HEIGHT / 4.0,
                -HEXAGON_WIDTH / 2.0, -HEXAGON_HEIGHT / 4.0
        );
        position = null;

        getChildren().add(hex);
        if (!isPawn) {
            hex.getStyleClass().add("possible_position");
        }

        setOnMouseClicked(this::handleMouseEvent);
    }

    public HivePositionViewer() {
        this(false);
    }

    public static void setController(HiveController controller) {
        HivePositionViewer.controller = controller;
    }

    public boolean isOnBoardPane() {
        return (position != null);
    }

    public void setPosition(BoardPosition position) {
        this.position = position;
    }

    public void placeOnBoardPane(BoardPosition position) {
        if (!isOnBoardPane()) {
            controller.boardPane.getChildren().add(this);
        }
        setPosition(position);
        int row = position.getRow();
        int col = position.getCol();
        double yPane = controller.boardPane.getHeight() / 2.0 + 0.75 * row * HivePawnViewer.HIVEPAWNVIEWER_HEIGHT;
        double xPane = controller.boardPane.getWidth() / 2.0 + ((double) col + 0.5 * row) * HivePawnViewer.HIVEPAWNVIEWER_WIDTH;

        setTranslateX(xPane);
        setTranslateY(yPane);
    }

    public void removeFromBoardPane() {
        setPosition(null);
    }

    public void handleMouseEvent(MouseEvent event) {
        event.consume();
    }
}
