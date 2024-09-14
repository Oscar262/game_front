module org.game {
    requires javafx.controls;
    requires javafx.fxml;
    exports org.game.utils;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires javafx.web;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;
    requires com.google.gson;

    opens org.game to javafx.fxml;
    opens org.game.oauth to com.google.gson;
    exports org.game;

}