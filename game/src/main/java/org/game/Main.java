package org.game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.game.launcher.Home;
import org.game.launcher.Login;
import org.game.oauth.controllers.OauthController;
import org.game.utils.FileUtils;
import org.game.utils.JwtOutput;

import java.net.URL;
import java.time.LocalDateTime;

public class Main extends Application {

    @Override
    public void start(Stage loadingStage) {
        // Crear y mostrar la pantalla de carga
        Image loadingImage = new Image(getClass().getResourceAsStream("images/744006.png"));
        ImageView imageView = new ImageView(loadingImage);
        StackPane loadingPane = new StackPane(imageView);
        Scene loadingScene = new Scene(loadingPane, javafx.scene.paint.Color.TRANSPARENT); // Tamaño de la pantalla de carga

        loadingStage = new Stage();
        loadingStage.initStyle(StageStyle.TRANSPARENT);
        loadingStage.setScene(loadingScene);
        loadingStage.show();

        new Thread(() -> {
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
                Home.setAccessToken(token.getToken());
                Home.launch();
                //jwtOutput = OauthController.home(token.getToken());
            } else {
                Login.launch();
                jwtOutput = OauthController.singn();
            }
           // if (jwtOutput.getStatus() == 200) {
           //     FileUtils.saveFileToken(jwtOutput);
           //     Config.ACCESS_TOKEN = jwtOutput.getAccessToken();
           //     String path = "images/login 2.jpg";
           //     Platform.runLater(() -> nextWindow(path));
           // } else if (jwtOutput.getStatus() == 401) {
           //     String path = "images/login 1.png";
           //     Platform.runLater(() -> nextWindow(path));
           // }
        }).start();


    }

    private void nextWindow(String path) {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("Nueva Ventana");
        stage.setWidth(1400);
        stage.setHeight(800);

        Image loadingImage = new Image(getClass().getResourceAsStream(path));
        ImageView imageView = new ImageView(loadingImage);
        StackPane loadingPane = new StackPane(imageView);
        Scene loadingScene = new Scene(loadingPane, javafx.scene.paint.Color.TRANSPARENT);

        stage.setScene(loadingScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
