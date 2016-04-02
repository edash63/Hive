package gamecontroller;

import exception.HiveException;
import gamemodel.HiveBoard;
import gamemodel.HiveMove;
import gameview.HivePawnViewer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
/**
 * Created by Wout Slabbinck on 31/03/2016.
 */

public class HiveController {
    private HiveBoard game;
    private Label startLabel;

    public VBox moveListPane;
    public Pane boardPane;
    public Pane freePawnPane;

    public HiveController() {
        game = HiveBoard.getInstance();
    }

    @FXML
    public void initialize(){

        HivePawnViewer.setBoardPane(boardPane);
        HivePawnViewer.setFreePawnPane(freePawnPane);
        HivePawnViewer.setMoveListPane(moveListPane);

        game = HiveBoard.getInstance();
        game.InitializeViewer(startLabel);

        startLabel = new Label("Start");
        moveListPane.getChildren().add(startLabel);
        ArrayList<HiveMove> moveList = game.getHiveMoveList();
        for (HiveMove move : moveList) {
            Label label = move.getMoveLabel();
            moveListPane.getChildren().add(move.getMoveLabel());
        }
    }

    @FXML
    public void handleToStartOfGameButtonAction() throws HiveException  {
        game.gotoStartOfGame();
    }

    @FXML
    public void handlePrevMoveButtonAction() throws HiveException {
        if (game.hasPreviousMove())
            game.takebackMove();
    }

    @FXML
    public void handleNextMoveButtonAction() throws HiveException {
        if (game.hasNextMove())
            game.advanceMove();
    }

    @FXML
    public void handleToEndOfGameButtonAction() throws HiveException {
        game.gotoEndOfGame();
    }
}
