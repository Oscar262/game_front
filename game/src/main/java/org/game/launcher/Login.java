package org.game.launcher;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.game.oauth.controllers.OauthController;
import org.game.utils.Config;
import org.game.utils.FileUtils;
import org.game.utils.JwtOutput;

import javax.swing.*;
import java.net.URL;
import java.time.LocalDateTime;

public class Login extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Crear y mostrar la pantalla de carga
        Image loadingImage = new Image(getClass().getResourceAsStream("/org/game/images/login 1.png"));
        ImageView imageView = new ImageView(loadingImage);
        StackPane loadingPane = new StackPane(imageView);

        Button start = new Button("Iniciar");
        start.setId("startButton");
        start.getStyleClass().add("startButton");
        Button close = new Button("X");
        close.setId("closeButton");
        close.getStyleClass().add("controlButton");
        Button minimize = new Button("_");
        minimize.setId("minimizeButton");
        minimize.getStyleClass().add("controlButton");
        loadingPane.getChildren().addAll(start, close, minimize);


        String css = getClass().getResource("/org/game/css/style.css").toExternalForm();

        StackPane.setAlignment(start, Pos.CENTER_LEFT);
        Scene loadingScene = new Scene(loadingPane, javafx.scene.paint.Color.TRANSPARENT);
        loadingScene.getStylesheets().add(css);
        stage.setHeight(800.0);
        stage.setWidth(1400.0);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(loadingScene);
        stage.show();

        start.setOnMouseClicked(e -> {
            URL url = getClass().getResource("token.txt");
            boolean haveToken = false;
            JwtOutput jwtOutput = new JwtOutput();
            FileUtils.FileToken token = null;
            if (url != null) {
                token = FileUtils.readFileToken();
                if (token.getExpiredDate().isAfter(LocalDateTime.now()))
                    haveToken = true;
            }
            if (haveToken) {
          //      jwtOutput = OauthController.home(token.getToken());
            } else {
             //   jwtOutput = OauthController.singn();
            }
            if (jwtOutput.getStatus() == 200) {
                FileUtils.saveFileToken(jwtOutput);
                Config.ACCESS_TOKEN = jwtOutput.getAccessToken();
                String path = "images/login 2.jpg";
                //Platform.runLater(() -> nextWindow(path));
            } else if (jwtOutput.getStatus() == 401) {
                String path = "images/login 1.png";
                //Platform.runLater(() -> nextWindow(path));
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

}
