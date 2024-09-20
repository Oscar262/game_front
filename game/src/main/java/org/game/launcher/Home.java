package org.game.launcher;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.game.oauth.controllers.OauthController;
import org.game.utils.Config;
import org.game.utils.FileUtils;
import org.game.utils.JwtOutput;


public class Home extends Application {
    private static String accessToken;

    @Override
    public void start(Stage stage) throws Exception {
        // Crear y mostrar la pantalla de carga
        Image loadingImage = new Image(getClass().getResourceAsStream("/org/game/images/home.jpeg"));
        Image loadingImage2 = new Image(getClass().getResourceAsStream("/org/game/images/home_2.png"));
        ImageView imageView = new ImageView(loadingImage);
        ImageView imageView2 = new ImageView(loadingImage2);
        StackPane loadingPane = new StackPane(imageView);
        imageView2.setOpacity(0);

        Button start = new Button("Iniciar");
        start.setId("startButton");
        start.getStyleClass().add("startButton");
        Button close = new Button("X");
        close.setId("closeButton");
        close.getStyleClass().add("controlButton");
        close.setOnMouseClicked(e -> stage.close());
        Button minimize = new Button("_");
        minimize.setId("minimizeButton");
        minimize.getStyleClass().add("controlButton");
        minimize.setOnMouseClicked(e -> stage.setIconified(true));
        loadingPane.getChildren().addAll(start, close, minimize);
        // JwtOutput jwtOutput = OauthController.home(accessToken);

        loadingPane.getChildren().add(imageView2);

        start.setOnMouseClicked(e -> {
            loadingPane.setStyle("-fx-background-color: black;");
            JwtOutput jwtOutput = OauthController.home(accessToken);

            if (jwtOutput.getStatus() == 200) {
                FileUtils.saveFileToken(jwtOutput);
                Config.ACCESS_TOKEN = jwtOutput.getAccessToken();
                // TODO: falta añadir llamada a la siguiente pantalla
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.2), imageView2);
                fadeIn.setFromValue(0.0); // Comienza invisible
                fadeIn.setToValue(1.0); // Termina visible
                fadeIn.play();

                fadeIn.setOnFinished(finishEvent -> {
                    FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.4), imageView2);
                    fadeOut.setFromValue(1.0); // Comienza visible
                    fadeOut.setToValue(0.0); // Termina invisible
                    fadeOut.play();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    closeHomeSceen(loadingPane);
                });
            } else {
                //TODO: falta añadir label con error
            }
        });

        String css = getClass().getResource("/org/game/css/style.css").toExternalForm();

        StackPane.setAlignment(start, Pos.CENTER_LEFT);
        Scene loadingScene = new Scene(loadingPane, javafx.scene.paint.Color.TRANSPARENT);
        loadingScene.getStylesheets().add(css);
        stage.setHeight(800.0);
        stage.setWidth(1400.0);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(loadingScene);
        stage.show();

    }

    private void closeHomeSceen(StackPane loadingPane) {
        ScaleTransition sceneScaleTransition = new ScaleTransition(Duration.seconds(0.5), loadingPane.getScene().getRoot());
        sceneScaleTransition.setFromX(1);
        sceneScaleTransition.setFromY(1);
        sceneScaleTransition.setToX(0);
        sceneScaleTransition.setToY(0);
        sceneScaleTransition.setCycleCount(1);

        sceneScaleTransition.play();

        sceneScaleTransition.setOnFinished(event -> {
            loadingPane.setLayoutX(0);
            loadingPane.setLayoutY(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static String getAccessToken() {
        return accessToken;
    }

    public static void setAccessToken(String accessToken) {
        Home.accessToken = accessToken;
    }
}
