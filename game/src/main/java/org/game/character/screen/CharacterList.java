package org.game.character.screen;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.game.character.controller.CharacterController;
import org.game.character.model.Character;
import org.game.utils.Config;

import java.util.List;

public class CharacterList extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargar la imagen de fondo
        Image image = new Image(getClass().getResourceAsStream("/org/game/images/characters.jpeg"));
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true); // Mantiene la proporción de la imagen
        imageView.setFitWidth(primaryStage.getWidth()); // Ajuste inicial al ancho de la ventana
        imageView.setFitHeight(primaryStage.getHeight()); // Ajuste inicial al alto de la ventana

        // Escuchar cambios en el tamaño de la ventana y ajustar la imagen
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> imageView.setFitWidth(newVal.doubleValue()));
        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> imageView.setFitHeight(newVal.doubleValue()));

        // Crear el StackPane para colocar el fondo y la cuadrícula de imágenes encima
        StackPane root = new StackPane(imageView);

        Config.createProgressBar(root);

        // Cargar personajes en un hilo separado
        new Thread(() -> {
            try {
                Thread.sleep(10000L);  // Simular tiempo de carga
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            List<Character> characters = CharacterController.getCharacters(Config.ACCESS_TOKEN);

            // Usar Platform.runLater para actualizar la interfaz de usuario en el hilo principal
            Platform.runLater(() -> {
                // Mostrar los personajes en la UI
                showCharacters(root, characters);
            });
        }).start();

        // Configurar la escena y el escenario
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Comenzar la animación de los puntos
    }

    // Método para mostrar los personajes en la interfaz
    private void showCharacters(StackPane root, List<Character> characters) {
        if (characters == null) {
            System.out.println("Error al obtener personajes de la API");
            return;
        }

        // Crear el GridPane para las imágenes de los personajes
        VBox vbox = new VBox();
        for (int i = 0; i < characters.size(); i++) {
            Character character = characters.get(i);
            if (character.getImage() != null) {
                // Decodificar la imagen desde el byte[] a Image
                Image characterImage = Config.convertByteArrayToImage(character.getImage());
                ImageView characterImageView = new ImageView(characterImage);
                characterImageView.setPreserveRatio(true);

                // Posicionar las imágenes de los personajes
                vbox.getChildren().add(characterImageView);
            }
        }

        // Colocar el VBox con los personajes en el StackPane
        root.getChildren().add(vbox);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
