# Simulador de Alocação de Memória

Trabalho prático da disciplina de Sistemas Operacionais - Universidade Federal do Ceará (UFC)

## Descrição

Este projeto implementa um simulador de gerência de memória que representa o funcionamento interno de um sistema operacional na alocação e liberação de blocos de memória. O simulador implementa e permite comparar três algoritmos clássicos de alocação:

- **First Fit**: Seleciona o primeiro bloco livre que seja suficientemente grande
- **Best Fit**: Seleciona o menor bloco livre que seja suficientemente grande
- **Worst Fit**: Seleciona o maior bloco livre disponível

## Características

- Interface de linha de comando (CLI) interativa
- Visualização em tempo real do mapa de memória
- Cálculo de fragmentação interna e externa
- Coalescência automática de blocos livres adjacentes
- Estatísticas detalhadas de uso de memória

## Estrutura do Projeto

```
memoryallocation/
├── src/
│   └── main/
│       └── java/
│           └── br/
│               └── ufc/
│                   └── dc/
│                       └── so/
│                           ├── FitAlgorithm.java      # Enum dos algoritmos
│                           ├── MemoryBlock.java       # Classe que representa um bloco
│                           ├── MemoryManager.java     # Gerenciador de memória
│                           └── MemorySimulator.java   # CLI principal
└── README.md
```

### Estruturas de Dados Utilizadas

1. **MemoryBlock**: Classe que encapsula as informações de cada bloco de memória:
   - `id`: Identificador único (0 para blocos livres)
   - `startAddress`: Endereço inicial do bloco
   - `size`: Tamanho total alocado
   - `usedSize`: Tamanho efetivamente usado
   - `isFree`: Estado do bloco (livre/ocupado)

2. **MemoryManager**: Utiliza:
   - `ArrayList<MemoryBlock>`: Lista dinâmica para gerenciar blocos
   - `byte[]`: Array que simula a memória física do sistema

### Decisões de Implementação

- **Coalescência automática**: Ao liberar um bloco, o sistema verifica e mescla automaticamente blocos livres adjacentes
- **Fragmentação interna**: Calculada como a diferença entre o tamanho alocado e o tamanho usado
- **Fragmentação externa**: Representada pelo número de "buracos" (blocos livres) na memória
- **Visualização**: Dois níveis de representação visual (física e lógica com IDs)

## Requisitos

- **Java 21** ou superior
- Sistema operacional: Windows, Linux ou macOS

## Como Compilar

### Opção 1: Usando javac diretamente

```bash
cd memoryallocation
javac -d . src/main/java/br/ufc/dc/so/*.java
```

### Opção 2: Compilar arquivos individualmente

```bash
cd memoryallocation
javac -d . src/main/java/br/ufc/dc/so/FitAlgorithm.java
javac -d . src/main/java/br/ufc/dc/so/MemoryBlock.java
javac -d . src/main/java/br/ufc/dc/so/MemoryManager.java
javac -d . src/main/java/br/ufc/dc/so/MemorySimulator.java
```

## Como Executar

Após compilar, execute o simulador com:

```bash
cd memoryallocation
java -cp . br.ufc.dc.so.MemorySimulator
```

## Comandos Disponíveis

| Comando | Descrição | Exemplo |
|---------|-----------|---------|
| `init <tamanho>` | Inicializa a memória com o tamanho especificado (em bytes) | `init 64` |
| `alloc <tamanho> <algoritmo>` | Aloca um bloco de memória usando o algoritmo especificado | `alloc 10 first` |
| `freeid <id>` | Libera o bloco com o ID especificado | `freeid 2` |
| `freeaddr <endereco>` | Libera o bloco no endereço especificado | `freeaddr 10` |
| `show` | Exibe o mapa visual da memória | `show` |
| `stats` | Exibe estatísticas de uso da memória | `stats` |
| `help` | Mostra a ajuda com todos os comandos | `help` |
| `exit` | Sai do programa | `exit` |

### Algoritmos disponíveis para alocação:
- `first` - First Fit
- `best` - Best Fit
- `worst` - Worst Fit

## Exemplo de Uso

```
> init 64
Memória inicializada com 64 bytes.

> alloc 10 first
Bloco 1 alocado: 10 bytes em @0 (algoritmo: FIRST_FIT)

> alloc 8 first
Bloco 2 alocado: 8 bytes em @10 (algoritmo: FIRST_FIT)

> freeid 2
Bloco 2 liberado.

> alloc 6 best
Bloco 3 alocado: 6 bytes em @10 (algoritmo: BEST_FIT)

> show

Mapa de Memória (64 bytes)
------------------------------------------------------------
[################................................................]
[1111111111333333................................................]
------------------------------------------------------------
Blocos ativos: [id=1] @0 +10B (usado=10B) | [id=3] @10 +6B (usado=6B)

> stats

== Estatísticas ==
Tamanho total: 64 bytes
Ocupado: 16 bytes | Livre: 48 bytes
Buracos (fragmentação externa): 1
Fragmentação interna: 0 bytes
Uso efetivo: 25.00%

> exit
Encerrando simulador...
```

## Interpretação do Mapa de Memória

O comando `show` exibe duas linhas:

1. **Linha física**: Mostra o uso da memória
   - `#` = byte ocupado
   - `.` = byte livre

2. **Linha de IDs**: Mostra qual bloco ocupa cada posição
   - Número = ID do bloco
   - `.` = espaço livre

## Métricas Calculadas

- **Tamanho total**: Capacidade total da memória
- **Ocupado/Livre**: Bytes alocados vs. disponíveis
- **Fragmentação externa**: Número de "buracos" (blocos livres não contíguos)
- **Fragmentação interna**: Bytes alocados mas não utilizados
- **Uso efetivo**: Percentual de memória efetivamente ocupada

## Testes Sugeridos

### Teste 1: Comparação de Algoritmos
```
init 100
alloc 20 first
alloc 15 first
alloc 10 first
freeid 2
alloc 12 first    # Testa First Fit
alloc 12 best     # Testa Best Fit
alloc 12 worst    # Testa Worst Fit
show
stats
```

### Teste 2: Coalescência
```
init 50
alloc 10 first
alloc 10 first
alloc 10 first
freeid 1
freeid 3
show              # Deve mostrar fragmentação
freeid 2
show              # Blocos devem ser mesclados
```

### Teste 3: Fragmentação
```
init 100
alloc 20 first
alloc 5 first
alloc 20 first
alloc 5 first
alloc 20 first
freeid 2
freeid 4
show
stats             # Observe a fragmentação externa
```

## Autor

Trabalho desenvolvido para a disciplina de Sistemas Operacionais - UFC

## Licença

Este projeto é de uso acadêmico.