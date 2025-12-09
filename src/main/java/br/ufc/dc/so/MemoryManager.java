package br.ufc.dc.so;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe principal que gerencia a alocação e liberação de memória.
 *
 * Estrutura de dados utilizada:
 * - ArrayList<MemoryBlock>: Lista dinâmica de blocos de memória (livres e alocados)
 * - byte[]: Array que simula a memória física do sistema
 *
 * Decisões de implementação:
 * - Utilizamos ArrayList para facilitar a busca, inserção e remoção de blocos
 * - Cada posição do array 'memory' representa 1 byte
 * - O ID 0 é reservado para blocos livres
 * - IDs de blocos alocados começam em 1 e são incrementados sequencialmente
 */
public class MemoryManager {
    private byte[] memory;
    private int totalSize;
    private List<MemoryBlock> blocks;
    private int nextId;

    /**
     * Construtor padrão (memória não inicializada).
     */
    public MemoryManager() {
        this.blocks = new ArrayList<>();
        this.nextId = 1;
    }

    /**
     * Inicializa o vetor que simula a memória física.
     * Cria o primeiro bloco livre com todo o espaço disponível.
     *
     * @param size Tamanho total da memória em bytes
     */
    public void init(int size) {
        this.totalSize = size;
        this.memory = new byte[size];
        this.blocks = new ArrayList<>();
        this.nextId = 1;

        // Inicializa toda a memória como livre (valor 0)
        for (int i = 0; i < size; i++) {
            memory[i] = 0;
        }

        // Cria um único bloco livre com todo o espaço
        blocks.add(new MemoryBlock(0, 0, size, size, true));
        System.out.println("Memória inicializada com " + size + " bytes.");
    }

    /**
     * Executa a alocação de memória usando o algoritmo especificado.
     *
     * @param size Tamanho requerido em bytes
     * @param algorithm Algoritmo de alocação (FIRST_FIT, BEST_FIT, WORST_FIT)
     * @return ID do bloco alocado, ou -1 se não houver espaço
     */
    public int alloc(int size, FitAlgorithm algorithm) {
        if (memory == null) {
            System.out.println("Erro: Memória não inicializada. Use 'init' primeiro.");
            return -1;
        }

        if (size <= 0) {
            System.out.println("Erro: Tamanho deve ser maior que 0.");
            return -1;
        }

        // Escolhe o bloco adequado conforme o algoritmo
        MemoryBlock chosenBlock = chooseBlock(size, algorithm);

        if (chosenBlock == null) {
            System.out.println("Erro: Não há espaço suficiente para alocar " + size + " bytes.");
            return -1;
        }

        // Aloca o bloco
        int blockId = nextId++;
        int startAddr = chosenBlock.getStartAddress();

        // Se o bloco escolhido é maior que o necessário, dividimos
        if (chosenBlock.getSize() > size) {
            // Cria novo bloco livre com o espaço restante
            MemoryBlock newFreeBlock = new MemoryBlock(
                0,
                startAddr + size,
                chosenBlock.getSize() - size,
                chosenBlock.getSize() - size,
                true
            );

            // Ajusta o bloco escolhido para o tamanho alocado
            chosenBlock.setSize(size);
            chosenBlock.setUsedSize(size);
            chosenBlock.setId(blockId);
            chosenBlock.setFree(false);

            // Adiciona o novo bloco livre na lista
            int index = blocks.indexOf(chosenBlock);
            blocks.add(index + 1, newFreeBlock);
        } else {
            // Usa o bloco inteiro
            chosenBlock.setId(blockId);
            chosenBlock.setUsedSize(size);
            chosenBlock.setFree(false);
        }

        // Marca a memória física
        for (int i = startAddr; i < startAddr + size; i++) {
            memory[i] = (byte) blockId;
        }

        System.out.println("Bloco " + blockId + " alocado: " + size + " bytes em @" + startAddr +
                         " (algoritmo: " + algorithm + ")");
        return blockId;
    }

    /**
     * Seleciona o bloco ideal conforme o algoritmo de alocação.
     *
     * @param size Tamanho necessário
     * @param algorithm Algoritmo a ser aplicado
     * @return Bloco escolhido ou null se não houver espaço
     */
    private MemoryBlock chooseBlock(int size, FitAlgorithm algorithm) {
        MemoryBlock chosen = null;

        for (MemoryBlock block : blocks) {
            if (!block.isFree() || block.getSize() < size) {
                continue;
            }

            switch (algorithm) {
                case FIRST_FIT:
                    // Retorna o primeiro bloco que couber
                    return block;

                case BEST_FIT:
                    // Procura o menor bloco que couber
                    if (chosen == null || block.getSize() < chosen.getSize()) {
                        chosen = block;
                    }
                    break;

                case WORST_FIT:
                    // Procura o maior bloco disponível
                    if (chosen == null || block.getSize() > chosen.getSize()) {
                        chosen = block;
                    }
                    break;
            }
        }

        return chosen;
    }

    /**
     * Libera um bloco previamente alocado com base no ID.
     * Realiza coalescência (merge) com blocos livres adjacentes.
     *
     * @param id Identificador do bloco a ser liberado
     * @return true se liberado com sucesso, false caso contrário
     */
    public boolean freeId(int id) {
        if (memory == null) {
            System.out.println("Erro: Memória não inicializada.");
            return false;
        }

        // Procura o bloco com o ID especificado
        MemoryBlock blockToFree = null;
        int blockIndex = -1;

        for (int i = 0; i < blocks.size(); i++) {
            if (blocks.get(i).getId() == id && !blocks.get(i).isFree()) {
                blockToFree = blocks.get(i);
                blockIndex = i;
                break;
            }
        }

        if (blockToFree == null) {
            System.out.println("Erro: Bloco com ID " + id + " não encontrado ou já está livre.");
            return false;
        }

        // Marca como livre
        blockToFree.setFree(true);
        blockToFree.setId(0);
        blockToFree.setUsedSize(blockToFree.getSize());

        // Limpa a memória física
        int start = blockToFree.getStartAddress();
        int end = start + blockToFree.getSize();
        for (int i = start; i < end; i++) {
            memory[i] = 0;
        }

        // Realiza coalescência (merge) com blocos adjacentes
        coalesce(blockIndex);

        System.out.println("Bloco " + id + " liberado.");
        return true;
    }

    /**
     * Libera um bloco com base no endereço.
     *
     * @param address Endereço do bloco
     * @return true se liberado com sucesso
     */
    public boolean freeAddr(int address) {
        if (memory == null) {
            System.out.println("Erro: Memória não inicializada.");
            return false;
        }

        for (MemoryBlock block : blocks) {
            if (block.getStartAddress() == address && !block.isFree()) {
                return freeId(block.getId());
            }
        }

        System.out.println("Erro: Nenhum bloco alocado encontrado no endereço " + address);
        return false;
    }

    /**
     * Realiza a coalescência (merge) de blocos livres adjacentes.
     *
     * @param index Índice do bloco que foi liberado
     */
    private void coalesce(int index) {
        // Tenta fazer merge com o bloco anterior
        if (index > 0) {
            MemoryBlock prev = blocks.get(index - 1);
            MemoryBlock current = blocks.get(index);

            if (prev.isFree() && current.isFree() &&
                prev.getEndAddress() + 1 == current.getStartAddress()) {
                // Merge com o anterior
                prev.setSize(prev.getSize() + current.getSize());
                prev.setUsedSize(prev.getSize());
                blocks.remove(index);
                index--;
            }
        }

        // Tenta fazer merge com o próximo
        if (index < blocks.size() - 1) {
            MemoryBlock current = blocks.get(index);
            MemoryBlock next = blocks.get(index + 1);

            if (current.isFree() && next.isFree() &&
                current.getEndAddress() + 1 == next.getStartAddress()) {
                // Merge com o próximo
                current.setSize(current.getSize() + next.getSize());
                current.setUsedSize(current.getSize());
                blocks.remove(index + 1);
            }
        }
    }

    /**
     * Exibe o estado atual da memória em formato visual.
     * Linha 1: Uso físico (# para ocupado, . para livre)
     * Linha 2: IDs dos blocos
     */
    public void show() {
        if (memory == null) {
            System.out.println("Erro: Memória não inicializada.");
            return;
        }

        System.out.println("\nMapa de Memória (" + totalSize + " bytes)");
        System.out.println("------------------------------------------------------------");

        // Linha 1: Representação física
        StringBuilder physicalLine = new StringBuilder("[");
        for (int i = 0; i < totalSize; i++) {
            physicalLine.append(memory[i] == 0 ? "." : "#");
        }
        physicalLine.append("]");
        System.out.println(physicalLine);

        // Linha 2: IDs dos blocos
        StringBuilder idLine = new StringBuilder("[");
        for (int i = 0; i < totalSize; i++) {
            if (memory[i] == 0) {
                idLine.append(".");
            } else {
                idLine.append(memory[i] % 10); // Mostra apenas o último dígito do ID
            }
        }
        idLine.append("]");
        System.out.println(idLine);

        System.out.println("------------------------------------------------------------");

        // Lista de blocos ativos
        StringBuilder activeBlocks = new StringBuilder("Blocos ativos: ");
        boolean hasActiveBlocks = false;

        for (MemoryBlock block : blocks) {
            if (!block.isFree()) {
                if (hasActiveBlocks) {
                    activeBlocks.append(" | ");
                }
                activeBlocks.append(block.toString());
                hasActiveBlocks = true;
            }
        }

        if (!hasActiveBlocks) {
            activeBlocks.append("Nenhum");
        }

        System.out.println(activeBlocks);
    }

    /**
     * Calcula e exibe estatísticas de uso da memória.
     * Inclui fragmentação interna e externa.
     */
    public void stats() {
        if (memory == null) {
            System.out.println("Erro: Memória não inicializada.");
            return;
        }

        int usedSpace = 0;
        int freeSpace = 0;
        int internalFragmentation = 0;
        int freeBlocks = 0; // Número de "buracos" (fragmentação externa)

        for (MemoryBlock block : blocks) {
            if (block.isFree()) {
                freeSpace += block.getSize();
                freeBlocks++;
            } else {
                usedSpace += block.getSize();
                internalFragmentation += block.getInternalFragmentation();
            }
        }

        double usagePercentage = (usedSpace * 100.0) / totalSize;

        System.out.println("\n== Estatísticas ==");
        System.out.println("Tamanho total: " + totalSize + " bytes");
        System.out.println("Ocupado: " + usedSpace + " bytes | Livre: " + freeSpace + " bytes");
        System.out.println("Buracos (fragmentação externa): " + freeBlocks);
        System.out.println("Fragmentação interna: " + internalFragmentation + " bytes");
        System.out.printf("Uso efetivo: %.2f%%\n", usagePercentage);
    }
}
