package org.game.character.screen;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.game.character.controller.CharacterController;
import org.game.character.model.Character;
import org.game.utils.Config;

import java.io.ByteArrayInputStream;
import java.util.Map;

public class SingleCharacter extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(20);
        gridPane.setVgap(20);

        Scene scene = new Scene(gridPane, 1600, 500);
        stage.setScene(scene);
        stage.setTitle("Batch de Personajes concurrentes");
        stage.show();

        String prompt = "Un paisaje futurista con montañas y un río";
        int width = 300;
        int height = 400;

        //List<CompletableFuture<Void>> futures = IntStream.range(0, 5)
        //        .mapToObj(i -> CompletableFuture.runAsync(() -> {
        //            Character character = CharacterController.newCharacter(Config.ACCESS_TOKEN);
        //            if (character != null && character.getImage() != null) {
        //                Map<Long, byte[]> imageBytes = character.getImage();
        //                Image image = new Image(new ByteArrayInputStream(imageBytes));
        //                int col = i % 3;
        //                int row = i / 3;
        //                Platform.runLater(() -> {
        //                    ImageView imageView = new ImageView(image);
        //                    imageView.setFitWidth(width);
        //                    imageView.setFitHeight(height);
        //                    imageView.setPreserveRatio(true);
        //                    gridPane.add(imageView, col, row);
        //                });
        //            }
        //        })).collect(Collectors.toList());

        //CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            Character character = CharacterController.newCharacter(Config.ACCESS_TOKEN);
            if (character != null && character.getImage() != null) {
                //for (int i = 1; i < 7; i++) {
//
                //    Map<Long, byte[]> imageBytes = character.getImage();
                //    Image image = new Image(new ByteArrayInputStream(imageBytes.get((long) i)));
                //    int col = i % 3;
                //    int row = i / 3;
                //    Platform.runLater(() -> {
                //        ImageView imageView = new ImageView(image);
                //        imageView.setFitWidth(width);
                //        imageView.setFitHeight(height);
                //        imageView.setPreserveRatio(true);
                //        gridPane.add(imageView, col, row);
                //    });
                //}

        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
