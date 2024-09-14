package org.game.oauth.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.game.oauth.UserInput;
import org.game.utils.JwtOutput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OauthController {
    @FXML
    private Label welcomeText;

    private static final String url = "http://localhost:8080";

    @FXML
    public static JwtOutput singn() {
        String apiUrl = url + "/signin"; // Reemplaza con tu URL de API
        JwtOutput jwtOutput = new JwtOutput();
        UserInput userInput = new UserInput();
        userInput.setEmail("os.sanav.26@gmail.com");
        userInput.setPassword("a");
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();
        String jsonInputString = gson.toJson(userInput);

        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(apiUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonInputString))
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (response.statusCode() == 200)
                jwtOutput = objectMapper.readValue(response.body(), JwtOutput.class);
            else {
                jwtOutput.setMessage(response.body());
                jwtOutput.setStatus(response.statusCode());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return jwtOutput;
    }
}