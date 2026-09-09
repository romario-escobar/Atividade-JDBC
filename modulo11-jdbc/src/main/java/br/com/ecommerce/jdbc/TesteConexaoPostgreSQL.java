package br.com.ecommerce.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public class TesteConexaoPostgreSQL {

    public static void main(String[] args) {
        Connection conexao = null;

        try {
            conexao = FabricaConexao.obterConexao();

            System.out.println("Conexão estabelecida com sucesso!");

            // Aqui provamos que java.sql.Connection é uma INTERFACE,
            // e o objeto real devolvido é uma implementação concreta
            // fornecida pelo driver do PostgreSQL (polimorfismo).
            System.out.println("Classe concreta retornada: " + conexao.getClass().getName());

        } catch (ClassNotFoundException e) {
            // Ocorre quando o driver não foi encontrado no classpath
            // (ex: esqueceu de adicionar a dependência no pom.xml).
            System.out.println("Driver do PostgreSQL não encontrado no classpath: " + e.getMessage());

        } catch (SQLException e) {
            // Ocorre por falha de rede, banco fora do ar, ou credenciais incorretas.
            System.out.println("Falha ao conectar ao banco de dados: " + e.getMessage());

        } finally {
            // Garante o fechamento da conexão mesmo se algo falhar acima.
            if (conexao != null) {
                try {
                    conexao.close();
                    System.out.println("Conexão encerrada.");
                } catch (SQLException e) {
                    System.out.println("Erro ao fechar a conexão: " + e.getMessage());
                }
            }
        }
    }
}