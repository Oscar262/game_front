package org.game.utils;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {

    public static void saveFileToken(JwtOutput jwtOutput){
        try (FileWriter fileWriter = new FileWriter("src/main/resources/org/game/token.txt")){
            fileWriter.append(jwtOutput.getAccessToken());
            fileWriter.append("\n");
            fileWriter.append(String.valueOf(jwtOutput.getExpiredDate()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static FileToken readFileToken() {
        FileToken fileToken = new FileToken();
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/org/game/token.txt"))) {
            // Leer la única línea del archivo
            List<String> lines = br.lines().collect(Collectors.toList());

            // Imprimir la línea leída
            if (!lines.isEmpty()) {
                fileToken.setToken(lines.get(0));
                fileToken.setExpiredDate(LocalDateTime.parse(lines.get(1)));
            } else {
                System.out.println("El archivo está vacío.");
            }
        } catch (IOException e) {
            e.printStackTrace();  // Manejo de errores
        }
        return fileToken;
    }

    public static class FileToken {
        private String token;
        private LocalDateTime expiredDate;

        public FileToken() {
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public LocalDateTime getExpiredDate() {
            return expiredDate;
        }

        public void setExpiredDate(LocalDateTime expiredDate) {
            this.expiredDate = expiredDate;
        }
    }
}
