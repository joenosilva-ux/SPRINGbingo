# Gerador de cartelas de bingo

Aplicação web em Java 21 e Spring Boot MVC para criar e imprimir lotes de cartelas de bingo tradicional. A página permite aplicar a arte do evento, repetir uma cartela para até seis prêmios ou distribuir cartelas diferentes em folhas A4 paisagem.

Não há banco de dados, cadastro, login ou armazenamento de imagens. Cada lote fica temporariamente na memória do servidor por 30 minutos, enquanto a arte selecionada permanece somente no navegador.

## O que o sistema faz

### Uma cartela para vários prêmios

O usuário informa:

- quantas cartelas diferentes precisa, entre 1 e 200;
- quantos prêmios existem em cada bingo, entre 1 e 6;
- opcionalmente, o nome de cada prêmio e a arte do evento.

Cada cartela diferente ocupa uma folha A4. Dentro dessa folha, a mesma combinação é repetida uma vez para cada prêmio.

Exemplo: 50 cartelas com 6 prêmios geram 50 folhas A4 e 300 quadros de cartela. Os seis quadros da primeira folha usam a `Cartela 001`, os seis da segunda usam a `Cartela 002` e assim por diante.

### Cartelas para os jogadores

O usuário informa o total de cartelas diferentes e escolhe 2, 4 ou 6 por folha. A quantidade de páginas é calculada automaticamente, e a última folha recebe apenas as cartelas restantes.

Exemplo: 50 cartelas, com 6 por folha, geram 9 folhas A4; a última contém duas cartelas.

## Regras

- Bingo tradicional de 75 números.
- Colunas B 1–15, I 16–30, N 31–45, G 46–60 e O 61–75.
- Centro da coluna N livre.
- Números sem repetição dentro da coluna.
- Cartelas sem duplicidade dentro do mesmo lote.
- Identificação automática: `Cartela 001`, `Cartela 002` etc.
- Até 200 cartelas diferentes por geração.
- Arte em PNG, JPEG ou WebP, com até 8 MB.

## Prévia paginada

O servidor gera o lote uma vez e devolve um identificador temporário. A tela carrega somente a folha que o usuário está visualizando, mesmo quando o lote possui 50 ou 200 páginas. Os botões **Anterior** e **Próxima** buscam uma folha por vez.

Todas as folhas são transformadas em HTML somente depois que o usuário clica em **Imprimir**. Esse conteúdo temporário é removido do navegador ao finalizar a impressão.

Os lotes:

- expiram automaticamente depois de 30 minutos;
- são removidos quando uma nova prévia substitui a anterior;
- não sobrevivem à reinicialização do servidor;
- não dependem de banco de dados.

## Arte e composição da folha

A impressão usa uma grade A4 paisagem de três colunas por duas linhas.

- Com até cinco tabelas, a arte ocupa uma posição da grade, preferencialmente o centro superior.
- Com cinco tabelas, a arte completa exatamente a sexta posição.
- Com seis tabelas, a arte passa a ser uma marca-d’água suave atrás de toda a grade.
- Sem arte, as tabelas são centralizadas automaticamente.
- A mesma regra é aplicada às páginas incompletas do modo para jogadores.

## Tecnologias

- Java 21
- Spring Boot 4.1.0
- Spring MVC
- Thymeleaf
- HTML, CSS e JavaScript sem framework de frontend
- JUnit 6 e Spring Test

## Executar

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Se o wrapper não iniciar no seu ambiente e houver Maven instalado:

```powershell
mvn spring-boot:run
```

Abra `http://localhost:8080`. O endereço `/` redireciona para `/bingo`.

## Testar

```powershell
.\mvnw.cmd test
```

Ou, com Maven instalado:

```powershell
mvn test
```

A suíte cobre regras numéricas, imutabilidade, igualdade de cartelas, lotes de 50 folhas, repetição por prêmio, paginação para jogadores, entradas inválidas, controlador MVC e inicialização do Spring.

## Gerar o JAR

```powershell
.\mvnw.cmd clean package
java -jar target\bingo-0.0.1-SNAPSHOT.jar
```

## Estrutura principal

```text
src/main/java/br/com/bingo/
├── controller/
│   ├── BingoController.java
│   ├── LoteController.java
│   └── LoteCriadoResponse.java
├── model/
│   ├── Cartela.java
│   ├── ConfiguracaoImpressao.java
│   ├── ItemCartelaImpressao.java
│   ├── LoteGerado.java
│   ├── LoteImpressao.java
│   ├── ModoImpressao.java
│   └── PaginaBingo.java
├── repository/
│   ├── LoteRepository.java
│   └── LoteRepositoryEmMemoria.java
└── service/
    ├── BingoService.java
    ├── LoteGeradoService.java
    └── LoteImpressaoService.java

src/main/resources/
├── static/
│   ├── css/bingo.css
│   └── js/bingo.js
└── templates/
    ├── bingo.html
    └── fragments/
        ├── folha.html
        └── impressao.html
```

## Impressão e privacidade

O botão **Imprimir** prepara o lote completo e abre o diálogo do navegador. Nele, o usuário pode selecionar uma impressora ou usar **Salvar como PDF**. Os controles são ocultados automaticamente e cada `PaginaBingo` ocupa uma folha A4 paisagem.

A arte do evento é lida com `FileReader` e aplicada à prévia localmente. Ela não é enviada ao servidor e deixa de existir quando a página é fechada ou recarregada.

## Hospedagem

`application.properties` usa:

```properties
server.port=${PORT:8080}
```

Assim, a aplicação usa a porta 8080 localmente e aceita a porta fornecida por serviços como Railway.
