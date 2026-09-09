package br.com.ecommerce.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitária centralizada para carregar o driver JDBC do
 * PostgreSQL e fornecer conexões ativas com o banco de dados.
 */
public class FabricaConexao {

    private static final String URL = "jdbc:postgresql://localhost:5432/bdecommerce?escapeSyntaxCallMode=callIfNoReturn";
    private static final String USUARIO = "postgres";
    private static final String SENHA = "4405";

    public static Connection obterConexao() throws ClassNotFoundException, SQLException {
        // Solicita ao ClassLoader da JVM a carga do bytecode do driver.
        // Isso dispara o bloco static interno da classe Driver, que se
        // registra automaticamente no DriverManager.
        Class.forName("org.postgresql.Driver");

        // O DriverManager percorre os drivers registrados e devolve
        // uma conexão do driver que reconhecer o prefixo da URL.
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}