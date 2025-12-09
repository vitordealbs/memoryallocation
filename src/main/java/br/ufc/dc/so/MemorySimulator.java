package br.ufc.dc.so;

import java.util.Scanner;

/**
 * Classe principal que implementa a interface CLI do simulador.
 *
 * Comandos disponíveis:
 * - init <tamanho>: Inicializa a memória
 * - alloc <tamanho> <algoritmo>: Aloca memória (algoritmo: first, best, worst)
 * - freeid <id>: Libera bloco pelo ID
 * - freeaddr <endereco>: Libera bloco pelo endereço
 * - show: Exibe mapa da memória
 * - stats: Exibe estatísticas
 * - help: Mostra ajuda
 * - exit: Sai do programa
 */
public class MemorySimulator {
    private MemoryManager memoryManager;
    private Scanner scanner;

    public MemorySimulator() {
        this.memoryManager = new MemoryManager();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Inicia o loop principal da CLI.
     */
    public void start() {
        System.out.println("========================================");
        System.out.println("  Simulador de Alocação de Memória");
        System.out.println("  Sistemas Operacionais - UFC");
        System.out.println("========================================");
        System.out.println("Digite 'help' para ver os comandos disponíveis.\n");

        boolean running = true;

        while (running) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            String[] tokens = input.split("\\s+");
            String command = tokens[0].toLowerCase();

            try {
                switch (command) {
                    case "init":
                        handleInit(tokens);
                        break;

                    case "alloc":
                        handleAlloc(tokens);
                        break;

                    case "freeid":
                        handleFreeId(tokens);
                        break;

                    case "freeaddr":
                        handleFreeAddr(tokens);
                        break;

                    case "show":
                        memoryManager.show();
                        break;

                    case "stats":
                        memoryManager.stats();
                        break;

                    case "help":
                        showHelp();
                        break;

                    case "exit":
                    case "quit":
                        running = false;
                        System.out.println("Encerrando simulador...");
                        break;

                    default:
                        System.out.println("Comando desconhecido: '" + command + "'. Digite 'help' para ajuda.");
                }
            } catch (Exception e) {
                System.out.println("Erro ao executar comando: " + e.getMessage());
            }

            System.out.println();
        }

        scanner.close();
    }

    /**
     * Processa o comando 'init'.
     */
    private void handleInit(String[] tokens) {
        if (tokens.length < 2) {
            System.out.println("Uso: init <tamanho>");
            return;
        }

        try {
            int size = Integer.parseInt(tokens[1]);
            memoryManager.init(size);
        } catch (NumberFormatException e) {
            System.out.println("Erro: Tamanho deve ser um número inteiro.");
        }
    }

    /**
     * Processa o comando 'alloc'.
     */
    private void handleAlloc(String[] tokens) {
        if (tokens.length < 3) {
            System.out.println("Uso: alloc <tamanho> <algoritmo>");
            System.out.println("Algoritmos: first, best, worst");
            return;
        }

        try {
            int size = Integer.parseInt(tokens[1]);
            String algStr = tokens[2].toLowerCase();

            FitAlgorithm algorithm;
            switch (algStr) {
                case "first":
                    algorithm = FitAlgorithm.FIRST_FIT;
                    break;
                case "best":
                    algorithm = FitAlgorithm.BEST_FIT;
                    break;
                case "worst":
                    algorithm = FitAlgorithm.WORST_FIT;
                    break;
                default:
                    System.out.println("Algoritmo inválido. Use: first, best ou worst");
                    return;
            }

            memoryManager.alloc(size, algorithm);
        } catch (NumberFormatException e) {
            System.out.println("Erro: Tamanho deve ser um número inteiro.");
        }
    }

    /**
     * Processa o comando 'freeid'.
     */
    private void handleFreeId(String[] tokens) {
        if (tokens.length < 2) {
            System.out.println("Uso: freeid <id>");
            return;
        }

        try {
            int id = Integer.parseInt(tokens[1]);
            memoryManager.freeId(id);
        } catch (NumberFormatException e) {
            System.out.println("Erro: ID deve ser um número inteiro.");
        }
    }

    /**
     * Processa o comando 'freeaddr'.
     */
    private void handleFreeAddr(String[] tokens) {
        if (tokens.length < 2) {
            System.out.println("Uso: freeaddr <endereco>");
            return;
        }

        try {
            int address = Integer.parseInt(tokens[1]);
            memoryManager.freeAddr(address);
        } catch (NumberFormatException e) {
            System.out.println("Erro: Endereço deve ser um número inteiro.");
        }
    }

    /**
     * Exibe a ajuda com todos os comandos disponíveis.
     */
    private void showHelp() {
        System.out.println("Comandos disponíveis:");
        System.out.println("  init <tamanho>              - Inicializa a memória com o tamanho especificado");
        System.out.println("  alloc <tamanho> <algoritmo> - Aloca bloco de memória");
        System.out.println("                                Algoritmos: first, best, worst");
        System.out.println("  freeid <id>                 - Libera bloco pelo ID");
        System.out.println("  freeaddr <endereco>         - Libera bloco pelo endereço");
        System.out.println("  show                        - Exibe mapa visual da memória");
        System.out.println("  stats                       - Exibe estatísticas de uso");
        System.out.println("  help                        - Mostra esta ajuda");
        System.out.println("  exit                        - Sai do programa");
        System.out.println("\nExemplo de uso:");
        System.out.println("  > init 64");
        System.out.println("  > alloc 10 first");
        System.out.println("  > alloc 8 first");
        System.out.println("  > freeid 2");
        System.out.println("  > alloc 6 best");
        System.out.println("  > show");
        System.out.println("  > stats");
    }

    /**
     * Método main - ponto de entrada do programa.
     */
    public static void main(String[] args) {
        MemorySimulator simulator = new MemorySimulator();
        simulator.start();
    }
}
