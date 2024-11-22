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

        VBox characterVBox = new VBox(75); // Espaciado entre filas
        characterVBox.setAlignment(Pos.TOP_CENTER);

        // ScrollPane con fondo transparente
        ScrollPane scrollPane = new ScrollPane(characterVBox);

        // Estilo para fondo transparente
        scrollPane.setStyle(
                "-fx-background: transparent; " +
                        "-fx-background-color: transparent; " +
                        "-fx-border-color: transparent;");
        scrollPane.setFitToWidth(true); // Ajusta el ancho al contenedor
        scrollPane.setMaxHeight(800); // Altura máxima limitada

        // Centrar el ScrollPane en la pantalla
        StackPane.setAlignment(scrollPane, Pos.BOTTOM_CENTER);
        characterVBox.setFillWidth(true);

        root.getChildren().add(scrollPane);

        // Detectar cuando llegamos al final del scroll
        Platform.runLater(() -> {
            ScrollBar verticalScrollBar = getVerticalScrollBar(scrollPane);
            if (verticalScrollBar != null) {
                verticalScrollBar.valueProperty().addListener((obs, oldValue, newValue) -> {
                    if (!isLoading && newValue.doubleValue() == 1.0) { // Al llegar al final
                        loadMoreCharacters(characterVBox, scrollPane.getWidth(), timeline, pointBox, root);
                    }
                });
            }
        });

        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Siempre mostrar la barra vertical

        // Cargar personajes iniciales
        loadMoreCharacters(characterVBox, scrollPane.getWidth(), timeline, pointBox, root);

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
        // Calcular el número de columnas basándose en el tamaño de la ventana
        int columns = 3; // Ancho de cada imagen (275px) y el espacio disponible en la ventana
        int row = 0;
        int column = 0;

        // Crear un GridPane para distribuir las imágenes
        GridPane gridPane = new GridPane();
        gridPane.getChildren().clear();
        // Ajustar el espacio entre las columnas
        double imageWidth = 275;
        gridPane.setHgap(175); // Espacio entre las columnas

        // Ajustar el espacio entre las filas
        gridPane.setVgap(75); // Espacio entre filas, ajusta este valor a lo que necesites

        // Asegurarse de que las celdas del GridPane crezcan
        //gridPane.setPrefWidth(windowWidth);

        for (Character character : characters) {
            if (character.getImage() != null) {
                Image characterImage = Config.convertByteArrayToImage(character.getImage());
                ImageView characterImageView = new ImageView(characterImage);
                characterImageView.setFitHeight(500); // Ajusta el tamaño de la imagen del personaje
                characterImageView.setFitWidth(275); // Ajusta el ancho de la imagen del personaje

                // Imagen del borde
                Image borderImage = new Image(getClass().getResourceAsStream("/org/game/images/silver.png"));
                ImageView borderImageView = new ImageView(borderImage);
                borderImageView.setFitHeight(538);// Un poco más grande que el botón
                borderImageView.setFitWidth(478); // Ajusta según el tamaño del borde

                // Crear un botón transparente con la imagen del personaje
                Button button = new Button();
                button.setStyle("-fx-background-color: transparent;");
                button.setGraphic(characterImageView);


                // Crear un StackPane para superponer el borde y el botón
                StackPane buttonWithBorder = new StackPane();
                buttonWithBorder.getChildren().addAll(button, borderImageView); // La imagen del borde debajo del botón
                buttonWithBorder.setAlignment(Pos.CENTER); // Asegurar alineación

                // Configurar la acción al hacer clic en el botón
                button.setOnMouseClicked(e -> System.out.println("Imagen clickeada"));

                // Agregar el StackPane al GridPane
                gridPane.add(buttonWithBorder, column, row);

                column++;
                if (column == columns) {
                    column = 0;
                    row++;
                }
            }
        }

        // Asegurarse de que el GridPane siempre llene el espacio disponible
        HBox hBox = new HBox(gridPane);
        hBox.setAlignment(Pos.CENTER);
        characterVBox.getChildren().add(hBox);
    }


    private ScrollBar getVerticalScrollBar(ScrollPane scrollPane) {
        return (ScrollBar) scrollPane.lookup(".scroll-bar:vertical");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
