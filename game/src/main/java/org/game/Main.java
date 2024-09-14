package org.game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
            URL url = getClass().getResource("file.txt");
            boolean haveToken = false;
            JwtOutput jwtOutput = new JwtOutput();
            if (url != null) {
                FileUtils.FileToken token = FileUtils.readFileToken();
                if (token.getExpiredDate().isAfter(LocalDateTime.now()))
                    haveToken = true;
            }
            if (haveToken) {
                //TODO: llamar a /home
            } else {
                jwtOutput = OauthController.singn();
            }
            if (jwtOutput.getStatus() == 200)
                System.out.println();
            else if (jwtOutput.getStatus() == 401)
                System.out.println();

        }).start();


    }

    private void showHomeScreen() {
        // Configurar y mostrar la ventana principal
        StackPane mainPane = new StackPane();
        Scene mainScene = new Scene(mainPane, 800, 600); // Tamaño de la ventana principal
        //primaryStage.setTitle("Main Application");
        //primaryStage.setScene(mainScene);
        //primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
