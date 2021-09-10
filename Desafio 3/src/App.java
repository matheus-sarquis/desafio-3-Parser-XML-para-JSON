import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        File entrada = new File("HTML.xml");
        Queue<String> texto = new LinkedList<>();

        if (entrada.exists()) {
            try (Scanner scanner = new Scanner(entrada)) {
                while (scanner.hasNext()) {
                    texto.add(scanner.nextLine());
                }
            }

            String result = convertJson(texto);

            try {
                entrada = new File("Final.json");
                entrada.createNewFile();
                try (FileWriter escritor = new FileWriter("Final.json")) {
                    escritor.write(result);
                }
                System.out.println("\nArquivo criado!");
            } catch (IOException erro) {
                System.out.println("Não foi possível criar o arquivo!");
            }

        } else {
            System.out.println("Não foi possível encontrar o arquivo de entrada, colocar o nome de 'HTML.xml'");
        }
    }

    public static String convertJson(Queue<String> entrada) {
        String result = "";
        boolean elements = false;
        boolean list = false;
        boolean openTag = false;
        String now;
        String before = "";

        result += "{";

        while (!entrada.isEmpty()) {
            String linha = entrada.poll();
            linha = linha.substring(1);
            if (linha.contains("<")) {
                // Tem abre e fecha tag
                if (elements) {
                    result += ",";
                }
                result += System.lineSeparator();
                elements = true;
                result += "\"" + linha.substring(0, linha.indexOf('>')) + "\": \""
                        + linha.substring(linha.indexOf('>') + 1, linha.indexOf('<')) + "\"";
                openTag = false;
            } else if (linha.contains("/")) {
                // É um fecha tag
                elements = false;
                now = linha.substring(1, linha.length() - 1);
                if (list && !before.equals(now)) {
                    result += System.lineSeparator() + "]";
                    list = false;
                }
                result += System.lineSeparator() + "}";
                openTag = false;
            } else {
                // É um abre tag
                elements = false;
                now = linha.substring(0, linha.length() - 1);
                if (before.equals(now)) {
                    result += "," + System.lineSeparator() + "{";
                } else {
                    if (!before.equals("") && openTag) {
                        int indice = result.lastIndexOf(before) + before.length() + 2;
                        result = result.substring(0, indice) + result.substring(indice + 2);
                        list = false;
                    }
                    if (list) {
                        result += System.lineSeparator() + "]";
                    }
                    result += System.lineSeparator() + "\"" + now + "\": [ {";
                    list = true;
                }
                before = now;
                openTag = true;
            }
        }
        result += System.lineSeparator() + "}";

        return result;
    }
}