package gamecontroller;

import exception.HiveException;
import gamemodel.HiveBoard;
import gamemodel.HiveMove;
import gameview.HivePawnSprite;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Wout Slabbinck on 31/03/2016.
 */

public class HiveController implements Observer {
    private HiveBoard game;

    public VBox moveListPane;
    public Pane boardPane;
    public Pane freePawnPane;

    private Node focus;

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
        focus = startLabel;

        ArrayList<HiveMove> moveList = game.getHiveMoveList();
        for (HiveMove move : moveList) {
            Label label = new Label(move.getMoveDescription());
            label.getStyleClass().add("unfocusedlabel");
            moveListPane.getChildren().add(label);
        }

        game.addObserver(this);
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

    @Override
    public void update(Observable observable, Object object) {
        focus.getStyleClass().clear();
        focus.getStyleClass().add("unfocusedlabel");
        focus = moveListPane.getChildren().get((Integer) object + 1);
        focus.getStyleClass().clear();
        focus.getStyleClass().add("focusedlabel");
    }
}
