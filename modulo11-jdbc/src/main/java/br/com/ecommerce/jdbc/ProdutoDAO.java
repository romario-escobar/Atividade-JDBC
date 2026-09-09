package br.com.ecommerce.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    /**
     * Insere um novo produto no banco usando PreparedStatement,
     * prevenindo SQL Injection através dos marcadores posicionais (?).
     */
    public boolean inserir(Produto produto) {
        String sql = "INSERT INTO produto (codigo, nome, preco, quantidade_estoque) VALUES (?, ?, ?, ?)";

        Connection conexao = null;
        PreparedStatement comando = null;

        try {
            conexao = FabricaConexao.obterConexao();
            comando = conexao.prepareStatement(sql);

            comando.setString(1, produto.getCodigo());
            comando.setString(2, produto.getNome());
            // BigDecimal garante precisão monetária, compatível com NUMERIC do PostgreSQL
            comando.setBigDecimal(3, produto.getPreco());
            comando.setInt(4, produto.getQuantidadeEstoque());

            int linhasAfetadas = comando.executeUpdate();
            return linhasAfetadas > 0;

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Erro ao inserir produto: " + e.getMessage());
            return false;
        } finally {
            fecharRecursos(conexao, comando);
        }
    }

    /**
     * Atualiza o preço de um produto existente, identificado pelo código.
     */
    public boolean atualizarPreco(String codigo, BigDecimal novoPreco) {
        String sql = "UPDATE produto SET preco = ? WHERE codigo = ?";

        Connection conexao = null;
        PreparedStatement comando = null;

        try {
            conexao = FabricaConexao.obterConexao();
            comando = conexao.prepareStatement(sql);

            comando.setBigDecimal(1, novoPreco);
            comando.setString(2, codigo);

            int linhasAfetadas = comando.executeUpdate();
            return linhasAfetadas > 0;

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Erro ao atualizar preço: " + e.getMessage());
            return false;
        } finally {
            fecharRecursos(conexao, comando);
        }
    }

    /**
     * Exclui um produto do banco, identificado pelo código.
     */
    public boolean excluir(String codigo) {
        String sql = "DELETE FROM produto WHERE codigo = ?";

        Connection conexao = null;
        PreparedStatement comando = null;

        try {
            conexao = FabricaConexao.obterConexao();
            comando = conexao.prepareStatement(sql);

            comando.setString(1, codigo);

            int linhasAfetadas = comando.executeUpdate();
            return linhasAfetadas > 0;

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Erro ao excluir produto: " + e.getMessage());
            return false;
        } finally {
            fecharRecursos(conexao, comando);
        }
    }

    /**
     * Consulta todos os produtos cadastrados, ordenados por nome,
     * e reconstrói cada linha como um objeto Produto em memória.
     */
    public List<Produto> listarTodos() {
        String sql = "SELECT codigo, nome, preco, quantidade_estoque FROM produto ORDER BY nome ASC";
        List<Produto> produtos = new ArrayList<>();

        Connection conexao = null;
        PreparedStatement comando = null;
        ResultSet rs = null;

        try {
            conexao = FabricaConexao.obterConexao();
            comando = conexao.prepareStatement(sql);
            rs = comando.executeQuery();

            // rs.next() avança o cursor uma linha por vez, devolvendo
            // false quando não há mais linhas.
            while (rs.next()) {
                // Acesso por POSIÇÃO (índice começa em 1, não em 0)
                String codigo = rs.getString(1);
                // Acesso por NOME de coluna
                String nome = rs.getString("nome");
                BigDecimal preco = rs.getBigDecimal("preco");
                int estoque = rs.getInt("quantidade_estoque");

                produtos.add(new Produto(codigo, nome, preco, estoque));
            }

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Erro ao listar produtos: " + e.getMessage());
        } finally {
            fecharRecursos(conexao, comando, rs);
        }

        return produtos;
    }

    /**
     * Busca um único produto pelo código. Retorna null se não existir
     * nenhum produto com aquele código.
     */
    public Produto buscarPorCodigo(String codigo) {
        String sql = "SELECT codigo, nome, preco, quantidade_estoque FROM produto WHERE codigo = ?";

        Connection conexao = null;
        PreparedStatement comando = null;
        ResultSet rs = null;

        try {
            conexao = FabricaConexao.obterConexao();
            comando = conexao.prepareStatement(sql);
            comando.setString(1, codigo);
            rs = comando.executeQuery();

            // Só pode haver 0 ou 1 resultado (codigo é chave primária),
            // por isso usamos "if" em vez de "while".
            if (rs.next()) {
                String nome = rs.getString("nome");
                BigDecimal preco = rs.getBigDecimal("preco");
                int estoque = rs.getInt("quantidade_estoque");
                return new Produto(codigo, nome, preco, estoque);
            } else {
                return null; // nenhum produto encontrado com esse código
            }

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Erro ao buscar produto: " + e.getMessage());
            return null;
        } finally {
            fecharRecursos(conexao, comando, rs);
        }
    }

    /**
     * Método auxiliar para fechar PreparedStatement e Connection,
     * evitando repetir esse bloco em cada método acima.
     */
    private void fecharRecursos(Connection conexao, PreparedStatement comando) {
        fecharRecursos(conexao, comando, null);
    }

    /**
     * Sobrecarga que também fecha o ResultSet, respeitando a ordem
     * em cascata: primeiro o recurso "mais interno" (ResultSet),
     * depois o PreparedStatement, e por último a Connection.
     */
    private void fecharRecursos(Connection conexao, PreparedStatement comando, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (comando != null) comando.close();
            if (conexao != null) conexao.close();
        } catch (SQLException e) {
            System.out.println("Erro ao fechar recursos: " + e.getMessage());
        }
    }
}