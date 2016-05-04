package gamecontroller;

import exception.HiveException;
import gamemodel.BoardPosition;
import gamemodel.HiveGame;
import gamemodel.HiveMove;
import gamemodel.HiveMoveList;
import gamemodel.pawn.HivePawn;
import gameview.HivePawnViewer;

import gameview.HivePositionViewer;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import utils.ResourceUtils;

import java.util.*;

import static gameview.HivePawnViewer.*;

/**
 * Created by Wout Slabbinck on 31/03/2016.
 */

public class HiveController implements Observer {
    private HiveGame game;

    public VBox moveListPane;
    public Pane boardPane;
    public Pane freePawnPane;

    private Label focusLabel;
    private HiveMove focusMove;

    private HashSet<HivePositionViewer> positionViewerList;

    public HiveController() {
        game = HiveGame.getInstance();
        positionViewerList = new HashSet<>();
    }

    @FXML
    public void initialize() {
        HivePawnViewer.setController(this);
        HivePositionViewer.setController(this);

        game = HiveGame.getInstance();

        game.getWhitePawns().forEach(pawn -> {
            HivePawnViewer hpv = new HivePawnViewer(pawn);
            hpv.placeOnFreePawnPane();
        });
        game.getBlackPawns().forEach(pawn -> {
            HivePawnViewer hpv = new HivePawnViewer(pawn);
            hpv.placeOnFreePawnPane();
        });

        Label startLabel = new Label("Start");
        startLabel.setMinWidth(80.0);
        moveListPane.getChildren().add(startLabel);
        startLabel.getStyleClass().add("focusedlabel");
        focusLabel = startLabel;
        focusMove = null;

        for (HiveMove move : game.getMoves().getMoveList()) {
            Label label = new Label(move.getMoveDescription());
            label.setMinWidth(80.0);
            moveListPane.getChildren().add(label);
        }
        game.getMoves().addObserver(this);
    }


    @FXML
    public void handleToStartOfGameButtonAction() throws HiveException {
        game.gotoStartOfGame();
    }

    @FXML
    public void handlePrevMoveButtonAction() throws HiveException {
        game.takebackMove();
    }

    @FXML
    public void handleNextMoveButtonAction() throws HiveException {
        game.advanceMove();
    }

    @FXML
    public void handleToEndOfGameButtonAction() throws HiveException {
        game.gotoEndOfGame();
    }

    @Override
    public void update(Observable observable, Object object) {
        focusLabel.getStyleClass().remove("focusedlabel");

        int moveIndex = (Integer) object;
        focusLabel = (Label) moveListPane.getChildren().get(moveIndex + 1); // Startlabel is at index 0
        focusLabel.getStyleClass().add("focusedlabel");
        if (moveIndex >= 0) {
            focusMove = ((HiveMoveList) observable).getMoveList().get(moveIndex);
            if (focusMove.isErroneous()) {
                if (!focusLabel.getStyleClass().contains("erroneous")) {
                    focusLabel.getStyleClass().add("erroneous");
                    focusLabel.setTooltip(new Tooltip(ResourceUtils.translate(focusMove.getErrorCode())));
                }
            }
        } else {
            focusMove = null;
        }
    }

    public void highlightPositions(Set<BoardPosition> positionList) {
        for (HivePositionViewer pv : positionViewerList) {
            if (pv.isOnBoardPane()) {
                boardPane.getChildren().remove(pv);
                pv.setPosition(null);
            }
        }

        Iterator<BoardPosition> posIter = positionList.iterator();
        Iterator<HivePositionViewer> pvIter = positionViewerList.iterator();
        boolean pvlistExhausted = false;

        while (posIter.hasNext()) {
            BoardPosition pos = posIter.next();
            HivePositionViewer posvwr = null;

            if (!pvlistExhausted && pvIter.hasNext()) {
                posvwr = pvIter.next();
            } else {
                pvlistExhausted = true;
                posvwr = new HivePositionViewer();
                positionViewerList.add(posvwr);
            }
            posvwr.placeOnBoardPane(pos);
        }
    }
}
