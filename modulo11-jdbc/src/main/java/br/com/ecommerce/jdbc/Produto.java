package br.com.ecommerce.jdbc;

import java.math.BigDecimal;

/**
 * Representa a entidade Produto, mapeando diretamente a tabela
 * "produto" do banco de dados.
 */
public class Produto {

    private String codigo;
    private String nome;
    private BigDecimal preco;
    private int quantidadeEstoque;

    public Produto(String codigo, String nome, BigDecimal preco, int quantidadeEstoque) {
        this.codigo = codigo;
        this.nome = nome;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    @Override
    public String toString() {
        return codigo + " - " + nome + " (R$ " + preco + ") | Estoque: " + quantidadeEstoque;
    }
}