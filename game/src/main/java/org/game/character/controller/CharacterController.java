package org.game.character.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.ArrayList;
import java.util.List;

public class CharacterController {

    private static final String URL = "http://localhost:8080";
    public static List<Character> getCharacters(String accessToken) {
        String apiUrl = URL + "/character";
        List<Character> characters = null;
        HttpClient client = HttpClient.newHttpClient();

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
                Page<Character> characterPage = objectMapper.readValue(response.body(), objectMapper.getTypeFactory().constructParametricType(Page.class, Character.class));
                characters = characterPage.getData();
            } else {
                System.err.println("Error al obtener personajes: " + response.body());
            }

        } catch (URISyntaxException e) {
            System.err.println("URL de la API inválida: " + e.getMessage());
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al realizar la solicitud HTTP: " + e.getMessage());
        }
        return characters;
    }
}