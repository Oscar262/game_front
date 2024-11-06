package org.game.character;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CharacterList extends Application {

    private static final double ASPECT_RATIO = 16.0 / 9.0; // Relación de aspecto 16:9

    @Override
    public void start(Stage primaryStage) throws Exception {
        Image image = new Image(getClass().getResourceAsStream("/org/game/images/characters.jpeg"));
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true); // Mantiene la proporción de la imagen
        imageView.setFitWidth(primaryStage.getWidth()); // Ajuste inicial al ancho de la ventana
        imageView.setFitHeight(primaryStage.getHeight()); // Ajuste inicial al alto de la ventana

        // Escuchar cambios en el tamaño de la ventana y ajustar la imagen
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            imageView.setFitWidth(newVal.doubleValue());
        });
        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            imageView.setFitHeight(newVal.doubleValue());
        });
        StackPane root = new StackPane(imageView);

        // Crear y configurar la escena
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        // Inicializar la ventana en pantalla completa
        primaryStage.setFullScreen(true);

        // Mostrar la ventana
        primaryStage.show();
    }

    // Método para cambiar el tamaño de la ventana a una resolución específica sin entrar en un bucle
    private void setWindowResolution(Stage stage, double width, double height) {
        // Solo cambiar el tamaño si es necesario
        if (stage.getWidth() != width || stage.getHeight() != height) {
            stage.setWidth(width);
            stage.setHeight(height);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
