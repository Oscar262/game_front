package org.game.launcher;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.game.utils.Config;
import org.game.utils.FileUtils;
import org.game.utils.JwtOutput;

import java.net.URL;
import java.time.LocalDateTime;

public class Home extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        // Crear y mostrar la pantalla de carga
        Image loadingImage = new Image(getClass().getResourceAsStream("/org/game/images/login 2.jpg"));
        ImageView imageView = new ImageView(loadingImage);
        StackPane loadingPane = new StackPane(imageView);
        imageView.setFitHeight(800.0);
        imageView.setFitWidth(1000.0);

        Label signin = new Label("Iniciar sesión");
        signin.setId("signin");

        TextField username = new TextField();
        username.getStyleClass().add("textField"); // Aplica la clase CSS
        username.setPromptText("Username / email");
        username.setId("username");
        username.setMaxWidth(300);  // Ancho máximo
        username.setMaxHeight(50);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Ingrese su contraseña aquí");
        passwordField.getStyleClass().add("textField"); // Aplica la clase CSS
        passwordField.setPromptText("Contraseña");
        passwordField.setId("password");
        passwordField.setMaxWidth(300);  // Ancho máximo
        passwordField.setMaxHeight(50);

        // Crear el botón con el ícono de ojo
        Button eyeButton = new Button();
        Image eyeClosed = new Image(getClass().getResourceAsStream("/org/game/images/eye_closed.png")); // Imagen del ojo cerrado
        Image eyeOpen = new Image(getClass().getResourceAsStream("/org/game/images/eye_open.png"));   // Imagen del ojo abierto
        ImageView eyeIcon = new ImageView(eyeClosed);
        eyeButton.setGraphic(eyeIcon);
        eyeButton.setId("eye");

        // Crear el TextField para mostrar la contraseña
        TextField passwordTextField = new TextField();
        passwordTextField.setVisible(false);
        passwordTextField.setPromptText("Ingrese su contraseña aquí");
        passwordTextField.getStyleClass().add("textField"); // Aplica la clase CSS
        passwordTextField.setPromptText("Contraseña");
        passwordTextField.setId("password");
        passwordTextField.setMaxWidth(300);  // Ancho máximo
        passwordTextField.setMaxHeight(50);

        // Alternar entre PasswordField y TextField
        eyeButton.setOnAction(event -> {
            if (passwordTextField.isVisible()) {
                passwordTextField.setVisible(false);
                passwordField.setVisible(true);
                eyeIcon.setImage(eyeClosed); // Cambiar a ojo cerrado
            } else {
                passwordField.setVisible(false);
                passwordTextField.setVisible(true);
                passwordTextField.setText(passwordField.getText()); // Copiar texto de PasswordField a TextField
                eyeIcon.setImage(eyeOpen); // Cambiar a ojo abierto
            }
        });

        // Sincronizar el contenido entre los campos
        passwordField.textProperty().addListener((obs, oldText, newText) -> {
            passwordTextField.setText(newText);
        });
        passwordTextField.textProperty().addListener((obs, oldText, newText) -> {
            passwordField.setText(newText);
        });


        Button close = new Button("X");
        close.setId("closeButton");
        close.getStyleClass().add("controlButton");
        Button minimize = new Button("_");
        minimize.setId("minimizeButton");
        minimize.getStyleClass().add("controlButton");
        Button enterArrow = new Button("➡");
        enterArrow.setId("arrowButton");
        enterArrow.getStyleClass().add("arrowButton");

        Button registerButton = new Button("Registrarse ->");
        registerButton.setId("registerButton");

        StackPane.setAlignment(enterArrow, Pos.CENTER_LEFT);
        StackPane.setAlignment(imageView, Pos.CENTER_RIGHT);
        StackPane.setAlignment(signin, Pos.CENTER_LEFT);
        StackPane.setAlignment(username, Pos.CENTER_LEFT);
        StackPane.setAlignment(passwordField, Pos.CENTER_LEFT);
        StackPane.setAlignment(passwordTextField, Pos.CENTER_LEFT);
        StackPane.setAlignment(eyeButton, Pos.CENTER_LEFT);
        StackPane.setAlignment(registerButton, Pos.CENTER_LEFT);
        loadingPane.getChildren().addAll(enterArrow, close, minimize, signin, username, passwordField, passwordTextField, eyeButton, registerButton);
        String css = getClass().getResource("/org/game/css/style.css").toExternalForm();

        Scene loadingScene = new Scene(loadingPane, javafx.scene.paint.Color.TRANSPARENT);
        loadingScene.getStylesheets().add(css);
        stage.setHeight(800.0);
        stage.setWidth(1400.0);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(loadingScene);
        stage.show();
        loadingScene.getRoot().requestFocus();

        enterArrow.setOnMouseClicked(e -> {
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


