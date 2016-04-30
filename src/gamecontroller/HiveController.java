package gamecontroller;

import exception.HiveException;
import gamemodel.HiveBoard;
import gamemodel.HiveMove;
import gamemodel.HiveMoveList;
import gameview.HivePawnSprite;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

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

    private Label focusLabel;
    private HiveMove focusMove;

    public HiveController() {
        game = HiveBoard.getInstance();
    }

    @FXML
    public void initialize(){

        HivePawnSprite.setBoardPane(boardPane);
        HivePawnSprite.setFreePawnPane(freePawnPane);

        game = HiveBoard.getInstance();

        game.getWhitePawns().forEach(pawn -> { HivePawnSprite sprite = new HivePawnSprite(pawn); sprite.place(); } );
        game.getBlackPawns().forEach(pawn -> { HivePawnSprite sprite = new HivePawnSprite(pawn); sprite.place(); } );

        Label startLabel = new Label("Start");
        startLabel.getStyleClass().add("focusedlabel");
        moveListPane.getChildren().add(startLabel);
        focusLabel = startLabel;
        focusMove = null;

        for (HiveMove move : game.getMoves().getMoveList()) {
            Label label = new Label(move.getMoveDescription());
            label.getStyleClass().add("unfocusedlabel");
            moveListPane.getChildren().add(label);
        }
        game.getMoves().addObserver(this);
    }

    @FXML
    public void handleToStartOfGameButtonAction() throws HiveException  {
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
        focusLabel.getStyleClass().clear();
        focusLabel.getStyleClass().add("unfocusedlabel");
        if (focusMove != null && focusMove.isErroneous())
            focusLabel.getStyleClass().add("erroneous");

// @todo :  object is nu currentMoveIndex; te vervangen door current move
        int moveIndex = (Integer) object;
        focusLabel = (Label) moveListPane.getChildren().get(moveIndex  + 1); // Startlabel is at index 0
        focusLabel.getStyleClass().clear();
        focusLabel.getStyleClass().add("focusedlabel");
        if (moveIndex >= 0) {
            focusMove = ((HiveMoveList) observable).getMoveList().get(moveIndex);
            if (focusMove.isErroneous()) {
                focusLabel.setText(focusMove.getMoveDescription());
                focusLabel.getStyleClass().add("erroneous");
            }
        } else
            focusMove = null;
    }
}
