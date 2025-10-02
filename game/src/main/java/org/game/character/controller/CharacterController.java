package org.game.character.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.game.character.model.Character;
import org.game.utils.Page;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CharacterController {

    private static final String URL = "http://localhost:8080";

    public static Page<Character> getCharacters(String accessToken, int offset, String textField) {
        StringBuilder apiUrl = new StringBuilder(URL).append("/character?limit=6&offset=" + offset);
        if (textField != null && !textField.isBlank())
            apiUrl.append("&name=eq:" + textField);
        HttpClient client = HttpClient.newHttpClient();

        Page<Character> characterPage = null;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(apiUrl.toString()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            if (response.statusCode() == 200) {
                // Deserializar la respuesta JSON en un objeto Page<Character>
                characterPage = objectMapper.readValue(response.body(), objectMapper.getTypeFactory().constructParametricType(Page.class, Character.class));
            } else {
                System.err.println("Error al obtener personajes: " + response.body());
            }

        } catch (URISyntaxException e) {
            System.err.println("URL de la API inválida: " + e.getMessage());
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al realizar la solicitud HTTP: " + e.getMessage());
        }
        return characterPage;
    }

    public static Character getCharacter(String accessToken, Long id) {
        String apiUrl = URL + "/character/" + id;
        HttpClient client = HttpClient.newHttpClient();

        Character character = null;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            if (response.statusCode() == 200) {
                // Deserializar la respuesta JSON en un objeto Page<Character>
                character = objectMapper.readValue(response.body(), Character.class);
            } else {
                System.err.println("Error al obtener personajes: " + response.body());
            }

        } catch (URISyntaxException e) {
            System.err.println("URL de la API inválida: " + e.getMessage());
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al realizar la solicitud HTTP: " + e.getMessage());
        }
        return character;
    }

    public static Character newCharacter(String accessToken) {
        String apiUrl = URL + "/character";
        HttpClient client = HttpClient.newHttpClient();
        Character character = null;

        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Usamos Jackson igual que en getCharacters
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                character = objectMapper.readValue(response.body(), Character.class);
            } else {
                System.err.println("Error al crear personaje: " + response.statusCode() + " - " + response.body());
            }

        } catch (URISyntaxException e) {
            System.err.println("URL de la API inválida: " + e.getMessage());
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al realizar la solicitud HTTP: " + e.getMessage());
        }

        return character;
    }

}
