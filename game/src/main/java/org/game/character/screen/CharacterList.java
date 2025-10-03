package org.game.character.screen;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.game.character.controller.CharacterController;
import org.game.character.model.Character;
import org.game.utils.Config;
import org.game.utils.Page;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CharacterList extends Application {

    private TilePane characterTilePane;
    private ScrollPane scrollPane;
    private TextField searchField;

    private List<ToggleButton> typeButtons = new ArrayList<>();
    private List<CheckMenuItem> subtypeItems = new ArrayList<>();

    private boolean allLoaded = false;
    private int currentPage = 0;
    private CompletableFuture<Void> currentLoadingTask;
    private boolean flipAllState = false;

    @Override
    public void start(Stage primaryStage) {

        // Fondo
        ImageView background = new ImageView(new Image(getClass().getResourceAsStream("/org/game/images/characters.jpeg")));
        background.setPreserveRatio(false);
        background.setFitWidth(1200);
        background.setFitHeight(800);

        // StackPane principal: fondo + contenido
        StackPane root = new StackPane();
        root.getChildren().add(background);

        // Buscador y filtros
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(20));

        searchField = new TextField();
        searchField.setPromptText("Nombre del personaje...");

        // Filtros de tipo
        HBox typeFilterBox = new HBox(10);
        String[] typeNames = {"Tipo1", "Tipo2", "Tipo3"};
        String[] typeImages = {"/org/game/images/744006.png", "/org/game/images/744006.png", "/org/game/images/744006.png"};

        for (int i = 0; i < typeNames.length; i++) {
            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(typeImages[i])));
            icon.setFitWidth(40);
            icon.setFitHeight(40);

            ToggleButton btn = new ToggleButton();
            btn.setGraphic(icon);
            btn.setUserData(typeNames[i]);
            btn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

            btn.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    btn.setStyle("-fx-background-color: transparent; -fx-border-color: deepskyblue; -fx-border-width: 2;");
                } else {
                    btn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
                }
            });

            typeButtons.add(btn);
            typeFilterBox.getChildren().add(btn);
        }

        // Botones Buscar y Voltear
        Button searchButton = new Button("Buscar");
        searchButton.setOnAction(e -> {
            if (currentLoadingTask != null && !currentLoadingTask.isDone()) {
                currentLoadingTask.cancel(true);
            }
            applyFilters();
        });

        Button flipAllButton = new Button("Voltear Todas");
        flipAllButton.setOnAction(e -> {
            flipAllState = !flipAllState;
            characterTilePane.getChildren().forEach(node -> {
                if (node instanceof VBox) {
                    VBox card = (VBox) node;
                    FlippableImageView img = (FlippableImageView) card.getChildren().get(0);
                    if (img.isFlipped() != flipAllState) img.flip();
                }
            });
        });

        searchBox.getChildren().addAll(searchField, typeFilterBox, searchButton, flipAllButton);

        // Contenedor de personajes
        characterTilePane = new TilePane();
        characterTilePane.setHgap(160);
        characterTilePane.setVgap(160);
        characterTilePane.setPadding(new Insets(160));
        characterTilePane.setPrefColumns(3);
        characterTilePane.setTileAlignment(Pos.CENTER);
        characterTilePane.setPrefTileWidth(200);
        characterTilePane.setPrefTileHeight(250);
        characterTilePane.setStyle("-fx-background-color: transparent;"); // transparente para que se vea el fondo

        scrollPane = new ScrollPane(characterTilePane);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;"); // transparente

        scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0.9 && !allLoaded) {
                loadCharacters(currentPage + 1);
            }
        });

        VBox mainContainer = new VBox(10, searchBox, scrollPane);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setFillWidth(true);
        mainContainer.setPadding(new Insets(10));
        mainContainer.setStyle("-fx-background-color: transparent;"); // transparente también

        root.getChildren().add(mainContainer);

        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/org/game/css/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);

        background.fitWidthProperty().bind(scene.widthProperty());
        background.fitHeightProperty().bind(scene.heightProperty());

        primaryStage.show();

        loadCharacters(0);

        primaryStage.setOnCloseRequest(e -> {
            if (currentLoadingTask != null && !currentLoadingTask.isDone()) {
                currentLoadingTask.cancel(true);
            }
        });
    }

    private void applyFilters() {
        currentPage = 0;
        allLoaded = false;
        characterTilePane.getChildren().clear();
        loadCharacters(0);
    }

    private void loadCharacters(int page) {
        String nameFilter = searchField.getText();

        List<String> selectedTypes = new ArrayList<>();
        for (ToggleButton btn : typeButtons) if (btn.isSelected()) selectedTypes.add((String) btn.getUserData());

        currentLoadingTask = CompletableFuture.runAsync(() -> {
            List<Character> newCharacters = fetchCharactersFromServer(page, nameFilter, selectedTypes, new ArrayList<>());

            Platform.runLater(() -> {
                if (newCharacters.isEmpty()) allLoaded = true;
                else {
                    showCharacters(newCharacters);
                    currentPage = page;
                }
            });
        });
    }

    private List<Character> fetchCharactersFromServer(int page, String name, List<String> types, List<String> subtypes) {
        int offset = page * 6;
        Page<Character> characterPage = CharacterController.getCharacters(Config.ACCESS_TOKEN, offset, name);
        return characterPage != null && characterPage.getData() != null ? characterPage.getData() : new ArrayList<>();
    }

    private void showCharacters(List<Character> characters) {
        for (Character c : characters) {
            VBox card = new VBox(5);
            card.setAlignment(Pos.CENTER);
            card.setStyle("-fx-background-color: transparent;"); // transparente
            card.setPadding(new Insets(0));

            byte[] frontBytes = Base64.getDecoder().decode(c.getImageActive().getValue());
            Image frontImage = Config.decodeBase64ToFXImage(c.getImageActive().getValue());
            Image backImage = new Image(getClass().getResourceAsStream("/org/game/images/home.jpeg"));

            FlippableImageView imageView = new FlippableImageView(frontImage, backImage);
            imageView.setFitWidth(350);
            imageView.setFitHeight(350);
            imageView.setFlipped(flipAllState);
            imageView.setOnMouseClicked(e -> imageView.flip());

            Label nameLabel = new Label(c.getName());
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;"); // visible sobre fondo

            card.getChildren().addAll(imageView, nameLabel);
            characterTilePane.getChildren().add(card);
        }
    }

    private static class FlippableImageView extends ImageView {
        private final Image frontImage;
        private final Image backImage;
        private boolean flipped = false;

        public FlippableImageView(Image frontImage, Image backImage) {
            super(frontImage);
            this.frontImage = frontImage;
            this.backImage = backImage;
        }

        public boolean isFlipped() {
            return flipped;
        }

        public void setFlipped(boolean flipped) {
            this.flipped = flipped;
            setImage(flipped ? backImage : frontImage);
        }

        public void flip() {
            setFlipped(!flipped);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
