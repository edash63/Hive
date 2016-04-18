package gamecontroller;

import exception.HiveException;
import gamemodel.HiveBoard;
import gamemodel.HiveMove;
import gameview.HivePawnSprite;
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

    public VBox moveListPane;
    public Pane boardPane;
    public Pane freePawnPane;

    public HiveController() {
        game = HiveBoard.getInstance();
    }

    @FXML
    public void initialize(){

        HivePawnSprite.setBoardPane(boardPane);
        HivePawnSprite.setFreePawnPane(freePawnPane);

        game = HiveBoard.getInstance();
        game.InitializeViewer();

        Label startLabel = new Label("Start");
        startLabel.getStyleClass().add("focusedlabel");
        moveListPane.getChildren().add(startLabel);
        ArrayList<HiveMove> moveList = game.getHiveMoveList();
        for (HiveMove move : moveList) {
            Label label = move.getMoveLabel();
            label.getStyleClass().add("unfocusedlabel");
            moveListPane.getChildren().add(move.getMoveLabel());
        }

        game.currentMoveIndexProperty().addListener((observable, oldValue, newValue) -> {
            changeMoveListFocus(oldValue.intValue(), newValue.intValue());
        });
    }

    private void changeMoveListFocus(int oldValue, int newValue) {
        moveListPane.getChildren().get(oldValue+1).getStyleClass().clear();
        moveListPane.getChildren().get(oldValue+1).getStyleClass().add("unfocusedlabel");
        moveListPane.getChildren().get(newValue+1).getStyleClass().clear();
        moveListPane.getChildren().get(newValue+1).getStyleClass().add("focusedlabel");
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
