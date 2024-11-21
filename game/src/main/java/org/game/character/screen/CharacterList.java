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

import java.util.ArrayList;
import java.util.List;

public class CharacterList extends Application {

    private int offset = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargar la imagen de fondo
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

        VBox characterVBox = new VBox(50);
        characterVBox.setAlignment(Pos.CENTER);

        Region leftMargin = new Region();
        leftMargin.setMinWidth(150);
        Region rightMargin = new Region();
        rightMargin.setMinWidth(150);

        StackPane.setMargin(characterVBox, new javafx.geometry.Insets(350, 190, 100, 190));
        root.getChildren().add(characterVBox);

        Button start = new Button("Iniciar");
        start.setId("startButton");
        start.getStyleClass().add("startButton");

        List<Button> charactersButtons = new ArrayList<>();

        new Thread(() -> {
            Page<Character> characters = CharacterController.getCharacters(Config.ACCESS_TOKEN, offset);
            Platform.runLater(() -> {
                showCharacters(characterVBox, characters.getData(), primaryStage.getWidth(), charactersButtons);
                if (characters.getTotal() < offset + 5)
                    root.getChildren().remove(start);
                else
                    root.getChildren().add(start);

                root.getChildren().addAll(charactersButtons);
                timeline.stop();
                root.getChildren().remove(pointBox);
            });
        }).start();

        start.setOnMouseClicked(x -> {
            characterVBox.getChildren().clear();
            root.getChildren().add(pointBox);
            new Thread(() -> {
                offset += 5;
                Page<Character> characters = CharacterController.getCharacters(Config.ACCESS_TOKEN, offset);
                root.getChildren().removeAll(charactersButtons);

                Platform.runLater(() -> {
                    showCharacters(characterVBox, characters.getData(), primaryStage.getWidth(), charactersButtons);
                    if (characters.getTotal() <= offset + 5)
                        root.getChildren().remove(start);

                    root.getChildren().addAll(charactersButtons);
                    timeline.stop();
                    root.getChildren().remove(pointBox);
                });
            }).start();
        });

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);

        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (!characterVBox.getChildren().isEmpty() && characterVBox.getChildren().get(0) instanceof GridPane) {
                GridPane gridPane = (GridPane) characterVBox.getChildren().get(0);
                double totalWidth = newVal.doubleValue();
                double imageWidth = 275; // Ancho fijo de las imágenes
                int columns = 5; // Número de columnas
                double availableWidth = totalWidth - (columns * imageWidth);
                double dynamicHGap = availableWidth / (columns + 1);
                gridPane.setHgap(dynamicHGap);
            }
        });

        primaryStage.show();
    }

    private void showCharacters(VBox characterVBox, List<Character> characters, double windowWidth, List<Button> charactersButtons) {
        if (characters == null) {
            System.out.println("Error al obtener personajes de la API");
            return;
        }

        int columns = 5;
        int rows = 1;
        int row = 0;
        int column = 0;

        GridPane gridPane = new GridPane();
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

        for (Character character : characters) {
            if (character.getImage() != null) {
                Image characterImage = Config.convertByteArrayToImage(character.getImage());
                ImageView characterImageView = new ImageView(characterImage);
                characterImageView.setFitHeight(500);
                characterImageView.setFitWidth(imageWidth);

                Button button = new Button();

// Crear el StackPane para la imagen encima del botón
                StackPane buttonContent = new StackPane();
                buttonContent.getChildren().add(characterImageView); // Overlay encima de la imagen del personaje

// Establecer el StackPane como el gráfico del botón
                button.setGraphic(buttonContent);
                button.setStyle("-fx-background-color: transparent;"); // Para hacer el fondo del botón transparente

// Imagen que va encima del botón
                Image overlayImage = new Image(getClass().getResourceAsStream("/org/game/images/1 none.png"));
                ImageView overlayImageView = new ImageView(overlayImage);
                overlayImageView.setFitWidth(275);
                overlayImageView.setFitHeight(500);  // Ajustar tamaño según lo necesites
                //overlayImageView.setPreserveRatio(true);

                buttonContent.getChildren().add(overlayImageView);

// Agregar el botón al GridPane
                gridPane.add(button, column, row);
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

    public static void main(String[] args) {
        launch(args);
    }
}
