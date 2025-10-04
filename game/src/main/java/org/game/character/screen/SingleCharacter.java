package org.game.character.screen;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.game.character.controller.CharacterController;
import org.game.character.model.Character;
import org.game.utils.Config;
import org.game.utils.Pair;

public class SingleCharacter extends Application {

    private int col = 0;
    private int row = 0;

    @Override
    public void start(Stage stage) throws Exception {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(20);
        gridPane.setVgap(20);

        Button loadButton = new Button("Cargar Personaje");
        loadButton.setOnAction(event -> loadCharacter(gridPane));

        VBox root = new VBox(20, loadButton, gridPane);

        Scene scene = new Scene(root, 1600, 500);
        stage.setScene(scene);
        stage.setTitle("Batch de Personajes concurrentes");
        stage.show();
    }

    private void loadCharacter(GridPane gridPane) {
        new Thread(() -> {
            Character character = CharacterController.newCharacter(Config.ACCESS_TOKEN);

            if (character != null && character.getImageActive() != null) {
                Pair<Boolean, String> imageBytes = character.getImageActive();

                Platform.runLater(() -> {
                    gridPane.getChildren().clear(); // Limpiar imágenes previas
                    int width = 300;
                    int height = 400;

                    Image image = Config.decodeBase64ToFXImage(imageBytes.getValue());
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(width);
                    imageView.setFitHeight(height);
                    imageView.setPreserveRatio(true);

                    gridPane.add(imageView, col, row);
                    col++;
                    row++;
                });
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
