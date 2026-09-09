package br.com.ecommerce.jdbc;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class RecursosAvancadosDAO {

    /**
     * Demonstra a navegação não linear de um cursor rolável
     * (TYPE_SCROLL_INSENSITIVE), útil para relatórios gerenciais
     * que precisam pular entre registros sem reprocessar tudo.
     */
    public void demonstrarCursorRolavel() {
        String sql = "SELECT codigo, nome, preco FROM produto ORDER BY preco ASC";

        try (Connection conexao = FabricaConexao.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(
                     sql,
                     ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = comando.executeQuery()) {

            // rs.last(): salta direto para o registro mais caro (último, já que ordenamos por preço ASC)
            if (rs.last()) {
                System.out.println("Registro mais caro (rs.last(), linha " + rs.getRow() + "): "
                        + rs.getString("nome") + " - R$ " + rs.getBigDecimal("preco"));
            }

            // rs.previous(): retrocede uma posição a partir de onde o cursor estava
            if (rs.previous()) {
                System.out.println("Retrocedendo uma posição (rs.previous(), linha " + rs.getRow() + "): "
                        + rs.getString("nome") + " - R$ " + rs.getBigDecimal("preco"));
            }

            // rs.first(): salta direto para o registro mais barato (primeiro)
            if (rs.first()) {
                System.out.println("Registro mais barato (rs.first(), linha " + rs.getRow() + "): "
                        + rs.getString("nome") + " - R$ " + rs.getBigDecimal("preco"));
            }

            // rs.absolute(2): posiciona o cursor diretamente na 2ª tupla do resultado
            if (rs.absolute(2)) {
                System.out.println("Posição absoluta 2 (rs.absolute(2)): "
                        + rs.getString("nome") + " - R$ " + rs.getBigDecimal("preco"));
            }

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Erro ao demonstrar cursor rolável: " + e.getMessage());
        }
    }

    /**
     * Executa a stored procedure sp_calcular_saldo_estoque, que roda
     * diretamente no servidor PostgreSQL, usando CallableStatement.
     */
    public void executarProcedureSaldo(String codigoProduto) {
        String chamada = "{call sp_calcular_saldo_estoque(?, ?, ?)}";

        try (Connection conexao = FabricaConexao.obterConexao();
             CallableStatement callStmt = conexao.prepareCall(chamada)) {

            // Parâmetro de ENTRADA (IN)
            callStmt.setString(1, codigoProduto);

            // Registrando os parâmetros de SAÍDA (OUT), "reservando o espaço"
            // para o tipo de dado que o banco vai devolver em cada posição.
            callStmt.registerOutParameter(2, Types.INTEGER);
            callStmt.registerOutParameter(3, Types.NUMERIC);

            callStmt.execute();

            // Após a execução, os parâmetros OUT já estão preenchidos
            int quantidade = callStmt.getInt(2);
            BigDecimal totalReais = callStmt.getBigDecimal(3);

            System.out.println("Produto " + codigoProduto + " -> Saldo em estoque: " + quantidade
                    + " unidades | Patrimônio total: R$ " + totalReais);

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Erro ao executar a stored procedure: " + e.getMessage());
        }
    }
}