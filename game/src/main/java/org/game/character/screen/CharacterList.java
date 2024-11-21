package org.game.character.screen;

import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.game.character.controller.CharacterController;
import org.game.character.model.Character;
import org.game.utils.Config;
import org.game.utils.Page;

import java.util.ArrayList;
import java.util.List;

public class CharacterList extends Application {

    private int offset = 0; // Para controlar la paginación
    private boolean isLoading = false; // Evita múltiples cargas simultáneas


    @Override
    public void start(Stage primaryStage) throws Exception {
        // Configurar la imagen de fondo
        Image image = new Image(getClass().getResourceAsStream("/org/game/images/characters.jpeg"));
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(primaryStage.getWidth());
        imageView.setFitHeight(primaryStage.getHeight());

        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> imageView.setFitWidth(newVal.doubleValue()));
        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> imageView.setFitHeight(newVal.doubleValue()));

        StackPane root = new StackPane(imageView);
        HBox pointBox = new HBox(50);

        Circle[] points = Config.createProgressBar(root, pointBox);
        Timeline timeline = Config.createTimeline(points);
        timeline.play();

        VBox characterVBox = new VBox(50); // Espaciado entre filas
        characterVBox.setAlignment(Pos.TOP_CENTER);
        //characterVBox.setPrefWidth(800); // Ancho fijo para el contenido
        // ScrollPane con fondo transparente
        ScrollPane scrollPane = new ScrollPane(characterVBox);
        //scrollPane.setPrefHeight(1080);

        // Estilo para fondo transparente
        scrollPane.setStyle(
                "-fx-padding: 10px 50px 10px 175px; /* Padding interno */\n" +
                        "-fx-background: transparent; " +
                        "-fx-background-color: transparent; " +
                        "-fx-border-color: blue;");

        // Centrar el ScrollPane en la pantalla
        StackPane.setAlignment(scrollPane, Pos.BOTTOM_CENTER);

        characterVBox.setFillWidth(true);
        scrollPane.setFitToWidth(true); // Ajusta el ancho al contenedor
        scrollPane.setMaxHeight(800); // Altura máxima limitada
        root.getChildren().add(scrollPane);

        // Detectar cuando llegamos al final del scroll
        Platform.runLater(() -> {
            ScrollBar verticalScrollBar = getVerticalScrollBar(scrollPane);
            if (verticalScrollBar != null) {
                verticalScrollBar.valueProperty().addListener((obs, oldValue, newValue) -> {
                    if (!isLoading && newValue.doubleValue() == 1.0) { // Al llegar al final
                        loadMoreCharacters(characterVBox, primaryStage.getWidth(), timeline, pointBox, root);
                    }
                });
            }
        });

        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Siempre mostrar la barra vertical


        // Cargar personajes iniciales
        loadMoreCharacters(characterVBox, primaryStage.getWidth(), timeline, pointBox, root);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/org/game/css/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    private void loadMoreCharacters(VBox characterVBox, double windowWidth, Timeline timeline, HBox points, StackPane root) {
        isLoading = true; // Marcar como en proceso de carga

        new Thread(() -> {
            Platform.runLater(() -> {
                if (offset != 0) {
                    timeline.play();
                    points.setAlignment(Pos.BOTTOM_CENTER);
                    root.getChildren().add(points);
                }
            });

            Page<Character> characters = CharacterController.getCharacters(Config.ACCESS_TOKEN, offset);

            Platform.runLater(() -> {

                if (characters.getData() != null && !characters.getData().isEmpty()) {
                    List<Button> charactersButtons = new ArrayList<>();
                    showCharacters(characterVBox, characters.getData(), windowWidth, charactersButtons);
                    offset += characters.getData().size(); // Actualizar el offset
                } else {
                    System.out.println("No hay más personajes para cargar.");
                }
                isLoading = false; // Finalizar carga
                timeline.stop();
                root.getChildren().remove(points);
            });
        }).start();
    }

    private void showCharacters(VBox characterVBox, List<Character> characters, double windowWidth, List<Button> charactersButtons) {
        int columns = 4;
        int row = 0;
        int column = 0;
        int rows = 1;

        GridPane gridPane = new GridPane();
//        gridPane.getChildren().clear();  // Limpiar los elementos anteriores

        double imageWidth = 275;
        double availableWidth = windowWidth - (columns * imageWidth);
        double dynamicHGap = availableWidth / (columns + 1);
        gridPane.setHgap(dynamicHGap - 40);
        for (int i = 0; i < rows; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMaxHeight(500);
            rowConstraints.setMinHeight(500);
            gridPane.getRowConstraints().add(rowConstraints);
        }
        // Ajustar el espacio vertical entre las filas (margen entre filas)
        gridPane.setVgap(50); // Por ejemplo, 20 píxeles de margen entre filas

        // Asegurarse de que los botones se ajusten a las celdas vacías
        // Asegurarse de que los botones se ajusten a las celdas vacías
        for (Character character : characters) {
            if (character.getImage() != null) {
                Image characterImage = Config.convertByteArrayToImage(character.getImage());
                ImageView characterImageView = new ImageView(characterImage);
                characterImageView.setFitHeight(500);
                characterImageView.setFitWidth(imageWidth);

                Button button = new Button();
                StackPane buttonContent = new StackPane();
                buttonContent.getChildren().add(characterImageView);

                button.setGraphic(buttonContent);
                button.setStyle("-fx-background-color: transparent;");

                // Asegurarse de que el botón ocupe todo el espacio disponible
                GridPane.setHgrow(button, Priority.ALWAYS);
                GridPane.setVgrow(button, Priority.ALWAYS);

                // Agregar el botón al grid
                gridPane.add(button, column, row);

                button.setOnMouseClicked(e -> System.out.println(2));
                column++;
                if (column == columns) {
                    column = 0;
                    row++;
                }
            }
        }


        int totalCells = rows * columns;
        int filledCells = characters.size();
        int remainingCells = totalCells - filledCells;

        for (int i = 0; i < remainingCells; i++) {
            StackPane emptyCell = new StackPane();
            gridPane.add(emptyCell, column, row);

            column++;
            if (column == columns) {
                column = 0;
                row++;
            }
        }

        characterVBox.getChildren().add(gridPane);
    }


    private ScrollBar getVerticalScrollBar(ScrollPane scrollPane) {
        return (ScrollBar) scrollPane.lookup(".scroll-bar:vertical");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
