import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class GitHubRepos {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Solicitar el nombre de usuario de GitHub
        System.out.print("Ingresa el nombre de usuario de GitHub: ");
        String username = scanner.nextLine();

        // Validar si el usuario ingresó un nombre
        if (username.isEmpty()) {
            System.out.println("No se proporcionó ningún nombre de usuario.");
            return;
        }

        // Mostrar repositorios
        getRepos(username);
    }

    public static void getRepos(String username) {
        try {
            // URL de la API de GitHub
            String apiUrl = "https://api.github.com/users/" + username + "/repos";
            URL url = new URL(apiUrl);

            // Establecer la conexión
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            // Leer la respuesta de la API
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            // Cerrar la conexión
            conn.disconnect();

            // Convertir la respuesta a un string
            String jsonResponse = sb.toString();

            // Extraer información básica manualmente (sin usar JSON)
            extractRepoInfo(jsonResponse);

        } catch (Exception e) {
            System.out.println("Usuario no encontrado o error al acceder a la API.");
        }
    }

    public static void extractRepoInfo(String jsonResponse) {
        // Buscar los nombres de los repositorios
        String[] repos = jsonResponse.split("\\},\\{");
        if (repos.length == 0) {
            System.out.println("No se encontraron repositorios.");
            return;
        }

        System.out.println("Repositorios encontrados:");

        for (String repo : repos) {
            // Extraer el nombre del repositorio
            String name = extractValue(repo, "\"name\":\"", "\",");
            String description = extractValue(repo, "\"description\":\"", "\",");
            String stars = extractValue(repo, "\"stargazers_count\":", ",");

            // Mostrar la información del repositorio
            System.out.println("\nNombre: " + (name.isEmpty() ? "Desconocido" : name));
            System.out.println("Descripción: " + (description.isEmpty() ? "No hay descripción disponible" : description));
            System.out.println("Estrellas: " + (stars.isEmpty() ? "0" : stars));
        }
    }

    public static String extractValue(String text, String startDelimiter, String endDelimiter) {
        int startIndex = text.indexOf(startDelimiter);
        if (startIndex != -1) {
            startIndex += startDelimiter.length();
            int endIndex = text.indexOf(endDelimiter, startIndex);
            if (endIndex != -1) {
                return text.substring(startIndex, endIndex).replace("\\\"", "\"");
            }
        }
        return "";
    }
}
