package util;

import java.util.Scanner;

public class CLI {
    private static CLI instance;
    Scanner scanner;

    public CLI() {
        scanner = new Scanner(System.in);
        instance = this;
    }

    public static CLI getInstance() {
        if (instance == null) {
            throw new RuntimeException("throw new RuntimeException(\"CLI is not initialized.");
        }
        return instance;
    }

    public String prompt(String question, Object... args) {
        System.out.println("[?] " + String.format(question, args));
        System.out.print("> ");

        return scanner.nextLine();
    }

    public String[] promptSplit(String question, String separator, Object... args) {
        String ans = prompt(question, args);
        String[] arr = ans.split(separator);

        for(int i = 0; i < arr.length; i++) {
            arr[i] = arr[i].trim();
        }

        return arr;
    }

    public int options(String[] options) {
        while (true) {
            for (int i = 0; i < options.length; i++) {
                System.out.printf("[%d] %s \n", i + 1, options[i]);
            }

            String ans = prompt("Please select an item [%d-%d]:", 1, options.length);

            try {
                int index = Integer.parseInt(ans);
                if (index <= 0 || index > options.length) {
                    throw new RuntimeException("Invalid input");
                }

                return index - 1;
            } catch (Exception e) {
                System.out.println("Invalid input: " + ans);
                continue;
            }
        }
    }

    public static void main(String[] args) {
        CLI cli = new CLI();

        String[] options = new String[]{
                "Are you gay?",
                "Are you not gay?"
        };

        System.out.println("User picked" + cli.options(options));
    }

    public void close() {
        scanner.close();
    }
}
