package br.ufc.dc.so;

/**
 * Enum que representa os algoritmos de alocação de memória.
 *
 * - FIRST_FIT: Seleciona o primeiro bloco livre que seja suficientemente grande
 * - BEST_FIT: Seleciona o menor bloco livre que seja suficientemente grande
 * - WORST_FIT: Seleciona o maior bloco livre disponível
 */
public enum FitAlgorithm {
    FIRST_FIT,
    BEST_FIT,
    WORST_FIT
}
