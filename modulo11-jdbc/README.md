# Módulo 11 — Persistência Relacional em Java com JDBC e PostgreSQL

Projeto Integrador desenvolvido para a disciplina de **Programação Orientada a Objetos**, implementando a camada de persistência do **Sistema de Gestão Logística e E-Commerce**, conectando o ecossistema Java ao SGBD relacional **PostgreSQL** via JDBC.

## 🗄️ Preparação do banco de dados

Antes de rodar o projeto, execute no PostgreSQL (via pgAdmin ou `psql`):

```sql
CREATE DATABASE bdecommerce;

-- Conecte-se ao banco bdecommerce antes de criar as tabelas abaixo

CREATE TABLE produto (
    codigo VARCHAR(10) PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    preco NUMERIC(10, 2) NOT NULL,
    quantidade_estoque INTEGER DEFAULT 0
);

CREATE TABLE pedido (
    id_pedido VARCHAR(20) PRIMARY KEY,
    codigo_produto VARCHAR(10) NOT NULL,
    quantidade_comprada INTEGER NOT NULL,
    data_pedido TIMESTAMP NOT NULL,
    CONSTRAINT fk_pedido_produto FOREIGN KEY (codigo_produto) REFERENCES produto (codigo)
);

CREATE OR REPLACE PROCEDURE sp_calcular_saldo_estoque(
    IN p_codigo VARCHAR,
    OUT p_quantidade INT,
    OUT p_total_reais NUMERIC
)
LANGUAGE plpgsql
AS $$
BEGIN
    SELECT quantidade_estoque, (quantidade_estoque * preco)
    INTO p_quantidade, p_total_reais
    FROM produto
    WHERE codigo = p_codigo;
END;
$$;
```

## ⚙️ Configuração da conexão

Em `FabricaConexao.java`, ajuste usuário e senha do PostgreSQL:

```java
private static final String USUARIO = "postgres";
private static final String SENHA = "SUA_SENHA_AQUI";
```

### Nota técnica sobre a URL JDBC

A URL de conexão inclui um parâmetro extra em relação ao padrão mais simples (`jdbc:postgresql://localhost:5432/bdecommerce`):

```
jdbc:postgresql://localhost:5432/bdecommerce?escapeSyntaxCallMode=callIfNoReturn
```

Isso é necessário porque o driver JDBC do PostgreSQL, por padrão, reescreve a sintaxe `{call ...}` como um comando `SELECT` — um comportamento mantido por compatibilidade histórica, de antes da versão 11 do PostgreSQL (quando só existiam `FUNCTION`s, sem `PROCEDURE`s reais). Como o Nível 5 deste projeto chama uma **procedure de verdade** (com `CALL`), essa configuração instrui o driver a usar a sintaxe nativa `CALL` sempre que não houver valor de retorno declarado — exatamente o caso da `sp_calcular_saldo_estoque`, que só possui parâmetros `OUT`.

## 📦 Estrutura do projeto

```
modulo11-jdbc/
├── pom.xml
└── src/main/java/br/com/ecommerce/jdbc/
    ├── FabricaConexao.java
    ├── TesteConexaoPostgreSQL.java
    ├── Produto.java
    ├── ProdutoDAO.java
    ├── TesteProdutoDAO.java
    ├── TesteConsultasProdutoDAO.java
    ├── EstoqueInsuficienteException.java
    ├── ServicoVendaTransacional.java
    ├── TesteVendaTransacional.java
    ├── RecursosAvancadosDAO.java
    └── TesteRecursosAvancados.java
```

## 🚀 Níveis implementados

### Nível 1 — Fundamentos de Arquitetura, Carga de Driver e Conexão
`FabricaConexao` carrega o driver via `Class.forName("org.postgresql.Driver")` e obtém conexões com `DriverManager.getConnection()`. `TesteConexaoPostgreSQL` testa a conexão tratando `ClassNotFoundException` e `SQLException` separadamente, exibe a classe concreta retornada por `conexao.getClass().getName()` (demonstrando o polimorfismo de `java.sql.Connection`) e fecha a conexão no `finally`.

**Conceitos:** `java.sql` · `DriverManager` · URL JDBC · Exceções verificadas · Polimorfismo

### Nível 2 — Operações DML Seguras e Prevenção a SQL Injection
`ProdutoDAO` implementa `inserir`, `atualizarPreco` e `excluir` usando `PreparedStatement` com marcadores posicionais (`?`), mapeando o preço com `setBigDecimal` (precisão monetária) e validando o resultado de `executeUpdate()`.

**Conceitos:** `PreparedStatement` · Marcadores posicionais · `BigDecimal` · `executeUpdate()`

### Nível 3 — Consultas, Navegação por Cursor e Mapeamento Objeto-Relacional
`listarTodos()` percorre o cursor com `while(rs.next())`, lendo colunas alternadamente por posição e por nome, populando uma `List<Produto>`. `buscarPorCodigo()` retorna o objeto populado ou `null`. Os recursos são fechados em cascata: `ResultSet` → `PreparedStatement` → `Connection`.

**Conceitos:** `ResultSet` · `executeQuery()` · Cursor sequencial · Mapeamento objeto-relacional

### Nível 4 — Controle de Transação Manual e Integridade ACID
`ServicoVendaTransacional.processarVenda(...)` desativa o auto-commit, verifica o saldo em estoque, debita a quantidade e registra o pedido como uma transação atômica. Em caso de estoque insuficiente ou erro, executa `rollback()`; em caso de sucesso, `commit()`. O `autoCommit` é restaurado no `finally`.

**Conceitos:** `setAutoCommit(false)` · `commit()` · `rollback()` · Atomicidade multi-tabela

### Nível 5 — Recursos Avançados (Cursores Roláveis e Stored Procedures)
`RecursosAvancadosDAO.demonstrarCursorRolavel()` usa `TYPE_SCROLL_INSENSITIVE` para navegação não linear (`last`, `previous`, `first`, `absolute(2)`). `executarProcedureSaldo()` invoca a stored procedure `sp_calcular_saldo_estoque` via `CallableStatement`, registrando os parâmetros de saída com `registerOutParameter`.

**Conceitos:** `TYPE_SCROLL_INSENSITIVE` · Navegação não linear · `CallableStatement` · `registerOutParameter`

## ▶️ Como executar

Cada nível tem sua própria classe de teste, executável de forma independente pelo VSCode (botão "Run" acima do `main`) ou via terminal:

```bash
mvn compile
mvn exec:java -Dexec.mainClass="br.com.ecommerce.jdbc.TesteConexaoPostgreSQL"
```

Repita trocando a classe pelo teste desejado (`TesteProdutoDAO`, `TesteConsultasProdutoDAO`, `TesteVendaTransacional`, `TesteRecursosAvancados`).

> 💡 Como os testes compartilham as mesmas tabelas, rodar um nível pode deixar dados de teste que interferem em execuções futuras (ex: um pedido registrado impedindo a exclusão do produto correspondente, por restrição de chave estrangeira). Se necessário, limpe o banco entre execuções:
> ```sql
> DELETE FROM pedido;
> DELETE FROM produto;
> ```

## 🛠️ Tecnologias

- Java 17 (JDK 21 compatível)
- Maven
- PostgreSQL
- Driver JDBC `org.postgresql:postgresql:42.7.13`

## 📚 Sobre

Projeto desenvolvido como prática do Módulo 11 — Java Database Connectivity (JDBC), aplicando os principais recursos do pacote `java.sql` na camada de persistência do sistema de e-commerce e logística.