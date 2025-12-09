package br.ufc.dc.so;

/**
 * Classe que representa um bloco de memória.
 *
 * Estrutura de dados utilizada: Classe encapsulada com atributos privados
 * para garantir integridade dos dados.
 *
 * Cada bloco possui:
 * - id: Identificador único do bloco (0 para blocos livres)
 * - startAddress: Endereço inicial do bloco na memória
 * - size: Tamanho total do bloco
 * - usedSize: Tamanho efetivamente usado (para cálculo de fragmentação interna)
 * - isFree: Indica se o bloco está livre ou alocado
 */
public class MemoryBlock {
    private int id;
    private int startAddress;
    private int size;
    private int usedSize;
    private boolean isFree;

    /**
     * Construtor para criar um bloco de memória.
     *
     * @param id Identificador do bloco (0 para livre)
     * @param startAddress Endereço inicial
     * @param size Tamanho total do bloco
     * @param usedSize Tamanho usado (igual a size para blocos livres)
     * @param isFree True se o bloco está livre
     */
    public MemoryBlock(int id, int startAddress, int size, int usedSize, boolean isFree) {
        this.id = id;
        this.startAddress = startAddress;
        this.size = size;
        this.usedSize = usedSize;
        this.isFree = isFree;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getStartAddress() {
        return startAddress;
    }

    public int getSize() {
        return size;
    }

    public int getUsedSize() {
        return usedSize;
    }

    public boolean isFree() {
        return isFree;
    }

    public int getEndAddress() {
        return startAddress + size - 1;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setStartAddress(int startAddress) {
        this.startAddress = startAddress;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setUsedSize(int usedSize) {
        this.usedSize = usedSize;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    /**
     * Retorna a fragmentação interna do bloco.
     * Fragmentação interna = espaço alocado mas não utilizado.
     *
     * @return Bytes desperdiçados no bloco
     */
    public int getInternalFragmentation() {
        if (isFree) {
            return 0;
        }
        return size - usedSize;
    }

    @Override
    public String toString() {
        if (isFree) {
            return String.format("Livre @%d +%dB", startAddress, size);
        }
        return String.format("[id=%d] @%d +%dB (usado=%dB)", id, startAddress, size, usedSize);
    }
}
