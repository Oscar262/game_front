package org.game.launcher;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.game.oauth.controllers.OauthController;
import org.game.utils.Config;
import org.game.utils.FileUtils;
import org.game.utils.JwtOutput;

public class Login extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        // Crear y mostrar la pantalla de carga
        Image loadingImage = new Image(getClass().getResourceAsStream("/org/game/images/login 1.png"));
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
        ContextMenu contextMenu = new ContextMenu();
        username.setContextMenu(contextMenu);

        PasswordField passwordField = getPasswordField("Ingrese su contraseña aquí", "password", contextMenu);

        // Crear el botón con el ícono de ojo
        Button eyeButton = new Button();
        Image eyeClosed = new Image(getClass().getResourceAsStream("/org/game/images/eye_closed.png")); // Imagen del ojo cerrado
        Image eyeOpen = new Image(getClass().getResourceAsStream("/org/game/images/eye_open.png"));   // Imagen del ojo abierto
        ImageView eyeIcon = new ImageView(eyeClosed);
        eyeButton.setGraphic(eyeIcon);
        eyeButton.setId("eye");

        TextField passwordTextField = createPasswordField("Ingrese su contraseña", "password", contextMenu);

        // Alternar entre PasswordField y TextField
        eyeButtonAction(eyeButton, passwordTextField, passwordField, eyeIcon, eyeClosed, eyeOpen);

        // Sincronizar el contenido entre los campos
        passwordField.textProperty().addListener((obs, oldText, newText) -> {
            passwordTextField.setText(newText);
        });
        passwordTextField.textProperty().addListener((obs, oldText, newText) -> {
            passwordField.setText(newText);
        });

        StackPane.setAlignment(passwordField, Pos.CENTER_LEFT);
        StackPane.setAlignment(passwordTextField, Pos.CENTER_LEFT);
        StackPane.setAlignment(eyeButton, Pos.CENTER_LEFT);


        Button eyeButton2 = new Button();
        Image eyeClosed2 = new Image(getClass().getResourceAsStream("/org/game/images/eye_closed.png")); // Imagen del ojo cerrado
        Image eyeOpen2 = new Image(getClass().getResourceAsStream("/org/game/images/eye_open.png"));   // Imagen del ojo abierto
        ImageView eyeIcon2 = new ImageView(eyeClosed2);
        eyeButton2.setGraphic(eyeIcon2);
        eyeButton2.setId("eye2");

        PasswordField passwordField2 = getPasswordField("Repita su contraseña", "password2", contextMenu);

        loadingPane.getChildren().addAll(passwordField, passwordTextField);
        TextField passwordTextField2 = createPasswordField("Repita su contraseña", "password2", contextMenu);

        // Alternar entre PasswordField y TextField
        eyeButtonAction(eyeButton2, passwordTextField2, passwordField2, eyeIcon2, eyeClosed2, eyeOpen2);

        // Sincronizar el contenido entre los campos
        passwordField2.textProperty().addListener((obs, oldText, newText) -> {
            passwordTextField2.setText(newText);
        });
        passwordTextField2.textProperty().addListener((obs, oldText, newText) -> {
            passwordField2.setText(newText);
        });


        Button close = new Button("X");
        close.setId("closeButton");
        close.getStyleClass().add("controlButton");
        close.setOnMouseClicked(e -> stage.close());
        Button minimize = new Button("_");
        minimize.setId("minimizeButton");
        minimize.getStyleClass().add("controlButton");
        minimize.setOnMouseClicked(e -> stage.setIconified(true));
        Button enterArrow = new Button("➡");
        enterArrow.setId("arrowButton");
        enterArrow.getStyleClass().add("arrowButton");
        Button registerButton = new Button("Registrarse ->");
        registerButton.setId("registerButton");


        StackPane.setAlignment(enterArrow, Pos.CENTER_LEFT);
        StackPane.setAlignment(imageView, Pos.CENTER_RIGHT);
        StackPane.setAlignment(signin, Pos.CENTER_LEFT);
        StackPane.setAlignment(username, Pos.CENTER_LEFT);
        StackPane.setAlignment(registerButton, Pos.CENTER_LEFT);
        StackPane.setAlignment(passwordField2, Pos.CENTER_LEFT);
        StackPane.setAlignment(passwordTextField2, Pos.CENTER_LEFT);
        StackPane.setAlignment(eyeButton2, Pos.CENTER_LEFT);

        loadingPane.getChildren().addAll(enterArrow, close, minimize, signin, username, eyeButton, registerButton);
        String css = getClass().getResource("/org/game/css/style.css").toExternalForm();

        createScene(stage, loadingPane, css);
        Button arrow = new Button("➡");

        Label error = new Label("Error, contraseña o usuario incorrecto");
        error.setStyle("-fx-padding: 15px 20px;\n" +
                "    -fx-translate-y: 100px;\n" +
                "    -fx-translate-x: 45;\n " +
                "    -fx-text-fill: red;" +
                "    -fx-font-family: \"Arial\"; " +
                "    -fx-font-weight: italic;" +
                "    -fx-font-size: 16px;");

        registerButtonAction(registerButton, enterArrow, loadingPane, passwordField2, passwordTextField2, eyeButton2, arrow, error);

        arrow.setId("arrowButton");
        arrow.setStyle("-fx-padding: 15px 20px;\n" +
                "    -fx-translate-y: 250px;\n" +
                "    -fx-translate-x: 150px;");

        enterArrowAction(enterArrow, username, passwordField, loadingPane, error);
        enterArrowRegisterAction(arrow, username, passwordField, passwordField2, loadingPane);
    }

    private void createScene(Stage stage, StackPane loadingPane, String css) {
        Scene loadingScene = new Scene(loadingPane, javafx.scene.paint.Color.TRANSPARENT);
        applyContextMenuFilter(loadingPane, loadingScene);

        loadingScene.getStylesheets().add(css);
        stage.setHeight(800.0);
        stage.setWidth(1400.0);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(loadingScene);
        stage.show();
        loadingScene.getRoot().requestFocus();
    }

    private void applyContextMenuFilter(javafx.scene.Parent parent, Scene scene) {
        parent.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                event.consume();  // Evita que aparezca el menú contextual
            }
        });
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.C && event.isControlDown()) {
                event.consume();  // Evita que Ctrl+C funcione
            }
        });
        for (javafx.scene.Node child : parent.getChildrenUnmodifiable()) {
            if (child instanceof javafx.scene.Parent) {
                applyContextMenuFilter((javafx.scene.Parent) child, scene);
            }
        }
    }

    private static void enterArrowAction(Button enterArrow, TextField username, PasswordField passwordField, StackPane loadingPane, Label error) {
        enterArrow.setOnMouseClicked(e -> {
            JwtOutput jwtOutput = OauthController.singn(username.getText(), passwordField.getText());

            if (jwtOutput.getStatus() == 200) {
                FileUtils.saveFileToken(jwtOutput);
                Config.ACCESS_TOKEN = jwtOutput.getAccessToken();
                // TODO: falta añadir llamada a la siguiente pantalla
            } else {

                StackPane.setAlignment(error, Pos.CENTER_LEFT);
                loadingPane.getChildren().add(error);
            }
        });
    }

    private static void enterArrowRegisterAction(Button enterArrow, TextField username, PasswordField passwordField, PasswordField passwordField2, StackPane loadingPane) {
        enterArrow.setOnMouseClicked(e -> {
            //TODO: falta extender pantalla para añadir todos los campos
            if (!passwordField2.getText().equals(passwordField.getText())) {
                Label error = new Label("Error, la contraseña debe coincidir");
                error.setStyle("-fx-padding: 15px 20px;\n" +
                        "    -fx-translate-y: 100px;\n" +
                        "    -fx-translate-x: 25px;\n " +
                        "    -fx-text-fill: red;" +
                        "    -fx-font-family: \"Arial\"; " +
                        "    -fx-font-weight: italic;" +
                        "    -fx-font-size: 16px;");
                StackPane.setAlignment(error, Pos.CENTER_LEFT);
                loadingPane.getChildren().add(error);
            } else {
                JwtOutput jwtOutput = OauthController.register(username.getText(), passwordField.getText());

                if (jwtOutput.getStatus() == 200) {
                    FileUtils.saveFileToken(jwtOutput);
                    Config.ACCESS_TOKEN = jwtOutput.getAccessToken();
                    // TODO: falta añadir llamada a la siguiente pantalla
                } else {
                    //TODO: falta añadir label con error
                }
            }
        });
    }

    private static void registerButtonAction(Button registerButton, Button enterArrow, StackPane loadingPane, PasswordField passwordField2, TextField passwordTextField2, Button eyeButton2, Button arrow, Label error) {
        registerButton.setOnMouseClicked(e -> {
            loadingPane.getChildren().remove(error);
            enterArrow.setDisable(true);
            TranslateTransition transition = new TranslateTransition();
            transition.setDuration(Duration.seconds(1));
            transition.setNode(enterArrow);
            transition.setByY(75);
            loadingPane.getChildren().remove(registerButton);
            transition.setOnFinished(y -> {
                loadingPane.getChildren().addAll(passwordField2, passwordTextField2, eyeButton2);
                loadingPane.getChildren().remove(enterArrow);
                loadingPane.getChildren().add(arrow);
            });
            StackPane.setAlignment(arrow, Pos.CENTER_LEFT);
            transition.play();
        });
    }

    private static PasswordField getPasswordField(String text, String id, ContextMenu contextMenu) {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(text);
        passwordField.getStyleClass().add("textField"); // Aplica la clase CSS
        passwordField.setPromptText("Contraseña");
        passwordField.setId(id);
        passwordField.setMaxWidth(300);  // Ancho máximo
        passwordField.setMaxHeight(50);
        passwordField.setContextMenu(contextMenu);
        return passwordField;

    }

    private static TextField createPasswordField(String text, String id, ContextMenu contextMenu) {
        TextField passwordTextField = new TextField();
        passwordTextField.setVisible(false);
        passwordTextField.setPromptText(text);
        passwordTextField.getStyleClass().add("textField"); // Aplica la clase CSS
        passwordTextField.setPromptText("Contraseña");
        passwordTextField.setId(id);
        passwordTextField.setMaxWidth(300);  // Ancho máximo
        passwordTextField.setMaxHeight(50);
        passwordTextField.setContextMenu(contextMenu);
        return passwordTextField;
    }

    private static void eyeButtonAction(Button eyeButton2, TextField passwordTextField2, PasswordField passwordField2, ImageView eyeIcon2, Image eyeClosed2, Image eyeOpen2) {
        eyeButton2.setOnAction(event -> {
            if (passwordTextField2.isVisible()) {
                passwordTextField2.setVisible(false);
                passwordField2.setVisible(true);
                eyeIcon2.setImage(eyeClosed2); // Cambiar a ojo cerrado
            } else {
                passwordField2.setVisible(false);
                passwordTextField2.setVisible(true);
                passwordTextField2.setText(passwordField2.getText()); // Copiar texto de PasswordField a TextField
                eyeIcon2.setImage(eyeOpen2); // Cambiar a ojo abierto
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }

}


