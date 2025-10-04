package org.game.character.screen;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.game.character.controller.CharacterController;
import org.game.character.model.Character;
import org.game.utils.Config;
import org.game.utils.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CharacterList extends Application {

    private GridPane characterGrid;
    private ScrollPane scrollPane;
    private TextField searchField;
    private ProgressIndicator loadingIndicator;

    private List<ToggleButton> typeButtons = new ArrayList<>();
    private boolean allLoaded = false;
    private int currentPage = 0;
    private CompletableFuture<Void> currentLoadingTask;
    private volatile boolean taskActive = false;
    private boolean flipAllState = false;
    private boolean loading = false;

    private static final int CARDS_PER_ROW = 3;
    private static final double CARD_WIDTH = 350;
    private static final double CARD_HEIGHT = 450;
    private static final double GRID_GAP = 20;

    private ScheduledExecutorService autoLoadExecutor;

    @Override
    public void start(Stage primaryStage) {

        // Fondo
        ImageView background = new ImageView(new Image(getClass().getResourceAsStream("/org/game/images/characters.jpeg")));
        background.setPreserveRatio(false);

        StackPane root = new StackPane();
        root.getChildren().add(background);

        // Buscador y filtros
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(30, 40, 30, 40));

        searchField = new TextField();
        searchField.setPromptText("Nombre del personaje...");

        HBox typeFilterBox = new HBox(15);
        typeFilterBox.setPadding(new Insets(0, 0, 0, 10));
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

        Button searchButton = new Button("Buscar");
        searchButton.setOnAction(e -> applyFilters());

        Button flipAllButton = new Button("Voltear Todas");
        flipAllButton.setOnAction(e -> {
            flipAllState = !flipAllState;
            characterGrid.getChildren().forEach(node -> {
                if (node instanceof VBox) {
                    VBox card = (VBox) node;
                    FlippableImageView img = (FlippableImageView) card.getChildren().get(0);
                    if (img.isFlipped() != flipAllState) img.flip();
                }
            });
        });

        searchBox.getChildren().addAll(searchField, typeFilterBox, searchButton, flipAllButton);

        // Grid de personajes
        characterGrid = new GridPane();
        characterGrid.setAlignment(Pos.TOP_CENTER);

        scrollPane = new ScrollPane(characterGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        final double SIDE_MARGIN = 30; // margen mínimo desde los bordes de la pantalla

        characterGrid.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double totalWidth = newWidth.doubleValue();
            int numColumns = CARDS_PER_ROW;
            double totalCardWidth = numColumns * CARD_WIDTH;

            double remainingSpace = totalWidth - totalCardWidth;
            double hgap = remainingSpace / (numColumns + 1);
            if (hgap < GRID_GAP) hgap = GRID_GAP;
            characterGrid.setHgap(hgap);

            double scrollbarWidth = 15;
            for (Node n : scrollPane.lookupAll(".scroll-bar")) {
                if (n instanceof ScrollBar) {
                    ScrollBar sb = (ScrollBar) n;
                    if (sb.getOrientation() == Orientation.VERTICAL) {
                        scrollbarWidth = sb.getWidth();
                        break;
                    }
                }
            }

            double leftPadding = SIDE_MARGIN;
            double rightPadding = SIDE_MARGIN + scrollbarWidth / 2;
            characterGrid.setPadding(new Insets(30, rightPadding, 30, leftPadding));
        });

        scrollPane.addEventFilter(javafx.scene.input.ScrollEvent.SCROLL, event -> {
            double deltaY = event.getDeltaY() * 4;
            scrollPane.setVvalue(scrollPane.getVvalue() - deltaY / characterGrid.getHeight());
            event.consume();
        });

        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setVisible(false);

        StackPane scrollStack = new StackPane(scrollPane, loadingIndicator);
        StackPane.setAlignment(loadingIndicator, Pos.CENTER);

        VBox mainContainer = new VBox(15, searchBox, scrollStack);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setFillWidth(true);
        mainContainer.setPadding(new Insets(20, 0, 20, 0));

        root.getChildren().add(mainContainer);

        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/org/game/css/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);

        background.fitWidthProperty().bind(scene.widthProperty());
        background.fitHeightProperty().bind(scene.heightProperty());

        primaryStage.show();

        // Carga inicial
        loadCharacters(0);

        // Precarga automática en segundo plano cada 2 segundos si hay más por cargar
        autoLoadExecutor = Executors.newSingleThreadScheduledExecutor();
        autoLoadExecutor.scheduleWithFixedDelay(() -> {
            if (!allLoaded && !loading) {
                loadCharacters(currentPage + 1);
            }
        }, 2, 2, TimeUnit.SECONDS);

        primaryStage.setOnCloseRequest(e -> {
            if (currentLoadingTask != null && !currentLoadingTask.isDone()) {
                taskActive = false;
                currentLoadingTask.cancel(true);
            }
            autoLoadExecutor.shutdownNow();
        });
    }

    private void applyFilters() {
        currentPage = 0;
        allLoaded = false;
        characterGrid.getChildren().clear();
        loadCharacters(0);
    }

    private void loadCharacters(int page) {
        if (allLoaded || loading) return;

        loading = true;
        loadingIndicator.setVisible(true);

        String nameFilter = searchField.getText();
        List<String> selectedTypes = typeButtons.stream()
                .filter(ToggleButton::isSelected)
                .map(btn -> (String) btn.getUserData())
                .collect(Collectors.toList());

        if (currentLoadingTask != null && !currentLoadingTask.isDone()) {
            taskActive = false;
        }

        taskActive = true;

        currentLoadingTask = CompletableFuture.runAsync(() -> {
            Page<Character> newCharacters = fetchCharactersFromServer(page, nameFilter, selectedTypes);

            if (!taskActive) return;

            Platform.runLater(() -> {
                if (!taskActive) return;

                if (newCharacters.isLast()) {
                    allLoaded = true;
                } else {
                    currentPage = page;
                }
                showCharacters(newCharacters.getData());

                loading = false;
                loadingIndicator.setVisible(false);
            });
        });
    }

    private Page<Character> fetchCharactersFromServer(int page, String name, List<String> types) {
        int offset = page * 6;
        return CharacterController.getCharacters(Config.ACCESS_TOKEN, offset, name);
    }

    private void showCharacters(List<Character> characters) {
        int startIndex = characterGrid.getChildren().size();
        for (int i = 0; i < characters.size(); i++) {
            Character c = characters.get(i);

            VBox card = new VBox(5);
            card.setAlignment(Pos.CENTER);
            card.setStyle("-fx-background-color: transparent;");
            card.setPadding(new Insets(10));

            Image frontImage = Config.decodeBase64ToFXImage(c.getImageActive().getValue());
            Image backImage = new Image(getClass().getResourceAsStream("/org/game/images/home.jpeg"));

            FlippableImageView imageView = new FlippableImageView(frontImage, backImage);
            imageView.setFitWidth(CARD_WIDTH);
            imageView.setFitHeight(CARD_HEIGHT);
            imageView.setFlipped(flipAllState);
            imageView.setOnMouseClicked(e -> imageView.flip());

            Label nameLabel = new Label(c.getName());
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

            card.getChildren().addAll(imageView, nameLabel);

            int row = (startIndex + i) / CARDS_PER_ROW;
            int col = (startIndex + i) % CARDS_PER_ROW;

            characterGrid.add(card, col, row);
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
