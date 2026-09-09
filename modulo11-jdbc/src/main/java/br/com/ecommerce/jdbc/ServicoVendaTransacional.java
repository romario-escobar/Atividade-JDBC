package br.com.ecommerce.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ServicoVendaTransacional {

    /**
     * Processa uma venda de forma ATÔMICA: debita o estoque e registra
     * o pedido em uma única transação. Se qualquer etapa falhar
     * (incluindo estoque insuficiente), nenhuma alteração é persistida.
     */
    public boolean processarVenda(String idPedido, String codigoProduto, int quantidadeComprada) {
        Connection conexao = null;

        try {
            conexao = FabricaConexao.obterConexao();

            // Desativa a efetivação automática: nada será gravado
            // permanentemente até chamarmos commit() explicitamente.
            conexao.setAutoCommit(false);

            // 1. Verifica a quantidade disponível em estoque
            int estoqueAtual = consultarEstoque(conexao, codigoProduto);

            // 2. Regra de negócio: interrompe o fluxo se não houver saldo
            if (estoqueAtual < quantidadeComprada) {
                throw new EstoqueInsuficienteException(
                        "Estoque insuficiente para o produto " + codigoProduto
                        + ". Disponível: " + estoqueAtual + ", solicitado: " + quantidadeComprada);
            }

            // 3. Debita o estoque
            debitarEstoque(conexao, codigoProduto, quantidadeComprada);

            // 4. Registra o pedido
            registrarPedido(conexao, idPedido, codigoProduto, quantidadeComprada);

            // 5. Só agora as duas alterações se tornam permanentes e visíveis
            conexao.commit();
            System.out.println("Venda processada com sucesso! Pedido " + idPedido + " confirmado.");
            return true;

        } catch (EstoqueInsuficienteException e) {
            desfazerTransacao(conexao);
            System.out.println("Venda cancelada (regra de negócio): " + e.getMessage());
            return false;

        } catch (ClassNotFoundException | SQLException e) {
            desfazerTransacao(conexao);
            System.out.println("Venda cancelada (erro técnico): " + e.getMessage());
            return false;

        } finally {
            if (conexao != null) {
                try {
                    // Restaura o comportamento padrão antes de devolver
                    // a conexão, evitando efeitos colaterais em usos futuros.
                    conexao.setAutoCommit(true);
                    conexao.close();
                } catch (SQLException e) {
                    System.out.println("Erro ao restaurar conexão: " + e.getMessage());
                }
            }
        }
    }

    private int consultarEstoque(Connection conexao, String codigoProduto) throws SQLException {
        String sql = "SELECT quantidade_estoque FROM produto WHERE codigo = ?";
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, codigoProduto);
            try (ResultSet rs = comando.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantidade_estoque");
                }
                return 0; // produto não encontrado -> trata como estoque zero
            }
        }
    }

    private void debitarEstoque(Connection conexao, String codigoProduto, int quantidade) throws SQLException {
        String sql = "UPDATE produto SET quantidade_estoque = quantidade_estoque - ? WHERE codigo = ?";
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, quantidade);
            comando.setString(2, codigoProduto);
            comando.executeUpdate();
        }
    }

    private void registrarPedido(Connection conexao, String idPedido, String codigoProduto, int quantidade) throws SQLException {
        String sql = "INSERT INTO pedido (id_pedido, codigo_produto, quantidade_comprada, data_pedido) VALUES (?, ?, ?, ?)";
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, idPedido);
            comando.setString(2, codigoProduto);
            comando.setInt(3, quantidade);
            comando.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            comando.executeUpdate();
        }
    }

    /**
     * Desfaz qualquer alteração parcial feita durante a transação.
     */
    private void desfazerTransacao(Connection conexao) {
        if (conexao != null) {
            try {
                conexao.rollback();
                System.out.println("Rollback executado: nenhuma alteração foi persistida.");
            } catch (SQLException ex) {
                System.out.println("Erro ao executar rollback: " + ex.getMessage());
            }
        }
    }
}