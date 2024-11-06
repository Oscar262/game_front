package org.game.character.screen;

import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.game.character.controller.CharacterController;
import org.game.character.model.Character;
import org.game.utils.Config;
import org.game.utils.Page;

import java.util.List;

public class CharacterList extends Application {

    private int offset = 0;

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


        // Crear el StackPane para colocar el fondo
        StackPane root = new StackPane(imageView);
        HBox pointBox = new HBox(25);

        Circle[] points = Config.createProgressBar(root, pointBox);

        Timeline timeline = Config.createTimeline(points);
        timeline.play();

        // Crear un VBox para colocar las filas de los personajes
        VBox characterVBox = new VBox(25); // 25px de separación entre las filas
        characterVBox.setAlignment(Pos.CENTER); // Centrar las filas en el VBox

        // Crear márgenes de 75px a cada lado
        Region leftMargin = new Region();
        leftMargin.setMinWidth(300);
        Region rightMargin = new Region();
        rightMargin.setMinWidth(300);

        // Agregar el VBox al StackPane con márgenes a los lados
        StackPane.setMargin(characterVBox, new javafx.geometry.Insets(150, 90, 0, 90));
        root.getChildren().add(characterVBox);
        //TODO: crear boton de flecha
        Button start = new javafx.scene.control.Button("Iniciar");
        start.setId("startButton");
        start.getStyleClass().add("startButton");

        // Cargar personajes en un hilo separado
        new Thread(() -> {
            Page<Character> characters = CharacterController.getCharacters(Config.ACCESS_TOKEN, offset);

            Platform.runLater(() -> {
                showCharacters(characterVBox, characters.getData());
                if (characters.getTotal() < offset + 8)
                    root.getChildren().remove(start);
                else
                    root.getChildren().add(start);

                timeline.stop();
                root.getChildren().remove(pointBox);
            });
        }).start();


        start.setOnMouseClicked(x -> {
            characterVBox.getChildren().clear();
            root.getChildren().add(pointBox);
            new Thread(() -> {
                offset += 8;
                Page<Character> characters = CharacterController.getCharacters(Config.ACCESS_TOKEN, offset);

                Platform.runLater(() -> {
                    showCharacters(characterVBox, characters.getData());
                    if (characters.getTotal() < offset + 8)
                        root.getChildren().remove(start);

                    timeline.stop();
                    root.getChildren().remove(pointBox);
                });
            }).start();
        });

        // Configurar la escena y el escenario
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    private void showCharacters(VBox characterVBox, List<Character> characters) {
        if (characters == null) {
            System.out.println("Error al obtener personajes de la API");
            return;
        }

        // Número fijo de columnas (4 columnas por fila)
        int columns = 4;
        int rows = 2;  // Siempre dos filas
        int row = 0;
        int column = 0;

        // Crear un GridPane para organizar las imágenes en filas y columnas
        GridPane gridPane = new GridPane();
        gridPane.setHgap(45);  // Espacio horizontal entre imágenes
        gridPane.setVgap(25);  // Espacio vertical entre imágenes

        // Establecer las restricciones para las filas (asegurar que ambas filas tengan la misma altura)
        for (int i = 0; i < rows; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(400);  // Altura fija de 400px para las filas
            rowConstraints.setMaxHeight(400);  // Altura fija de 400px
            gridPane.getRowConstraints().add(rowConstraints);
        }

        // Agregar imágenes al GridPane
        for (Character character : characters) {
            if (character.getImage() != null) {
                Image characterImage = Config.convertByteArrayToImage(character.getImage());
                ImageView characterImageView = new ImageView(characterImage);
                characterImageView.setPreserveRatio(true);
                characterImageView.setFitHeight(400);  // Tamaño fijo de altura (400px)

                // Colocar la imagen en el GridPane en la posición correspondiente
                gridPane.add(characterImageView, column, row);

                // Avanzar al siguiente índice de columna
                column++;

                // Si se han agregado 4 imágenes en la fila, mover a la siguiente fila
                if (column == columns) {
                    column = 0;
                    row++;
                }
            }
        }

        // Asegurarse de que las celdas vacías se mantengan en el GridPane
        int totalCells = rows * columns; // Total de celdas que necesitamos: 8
        int filledCells = characters.size();  // Cuántas celdas están llenas con imágenes
        int remainingCells = totalCells - filledCells; // Celdas que faltan

        // Llenar las celdas vacías si hay menos de 8 imágenes
        for (int i = 0; i < remainingCells; i++) {
            StackPane emptyCell = new StackPane();  // Celdas vacías
            gridPane.add(emptyCell, column, row);

            column++;  // Avanzar al siguiente espacio en la fila
            if (column == columns) {  // Si la columna alcanza el límite (4), pasar a la siguiente fila
                column = 0;
                row++;
            }
        }

        // Agregar el GridPane al VBox
        characterVBox.getChildren().add(gridPane);
    }



    public static void main(String[] args) {
        launch(args);
    }
}
