package hive;

import exception.HiveException;
import gamemodel.HiveGame;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

/**
 * @author Wout Slabbinck
 */
public class Hive extends Application {
    private String fileName, testmodusName;
    private boolean testmodus = false;

    private static void error(String message) {
        System.err.println("ERROR: " + message);
        Platform.exit();
    }


    @Override
    public void init() {
        List<String> argList = getParameters().getRaw();

        if (argList.size() == 0) {
            fileName = null;
        } else {
            fileName = argList.get(0);
            if (argList.size() >= 2) {
                testmodusName = argList.get(1);
                testmodus = true;
            }

            // filename needs .txt ending
            int length = fileName.length();
            String fileEnd;
            if (length < 5) {
                fileName = fileName + ".txt";
            } else {
                fileEnd = fileName.substring(length - 4, length);
                if (!fileEnd.equals(".txt")) {
                    fileName += ".txt";
                }
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        HiveGame game = HiveGame.getInstance(); // there is only one game
        BufferedReader reader = null;

        if (fileName == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("There is a problem.");
            alert.setContentText("The name of a testfile must be provided as application argument");

            alert.showAndWait();
            System.exit(0);
        }

        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            while (line != null) {
                game.addMove(line);
                line = reader.readLine();
            }
            reader.close();
            reader = null;

            Parent root = FXMLLoader.load(getClass().getResource("Hive.fxml"));
            primaryStage.setTitle("Hive");
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

            primaryStage.show();

            if (testmodus) {
                game.gotoEndOfGame();
                WritableImage image = new WritableImage(1600, 800);
                scene.snapshot(image);
                File output = new File(testmodusName);
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", output);
                game.transferPieces();
                System.exit(0);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
