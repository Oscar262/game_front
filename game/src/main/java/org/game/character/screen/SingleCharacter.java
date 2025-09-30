package org.game.character.screen;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.game.character.controller.CharacterController;
import org.game.character.model.Character;
import org.game.utils.Config;

public class SingleCharacter extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Llamamos a la API para crear un nuevo personaje
        Character character = CharacterController.newCharacter(Config.ACCESS_TOKEN);

        if (character == null || character.getImage() == null) {
            System.err.println("No se pudo obtener la imagen del personaje.");
            return;
        }

        // Convertimos los bytes de la imagen a Image de JavaFX
        Image characterImage = Config.convertByteArrayToImage(character.getImage());

        // Creamos un ImageView igual que en CharacterList
        ImageView characterImageView = new ImageView(characterImage);
        characterImageView.setFitWidth(400);      // Ajusta según necesites
        characterImageView.setFitHeight(600);     // Ajusta según necesites
        characterImageView.setPreserveRatio(true);

        // Creamos un contenedor para mostrar el personaje
        StackPane root = new StackPane();
        root.getChildren().add(characterImageView);

        // Creamos la escena
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Personaje");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
