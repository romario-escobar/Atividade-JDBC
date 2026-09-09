package br.com.ecommerce.jdbc;

/**
 * Exceção de regra de negócio, lançada deliberadamente quando não há
 * estoque suficiente para atender a uma venda.
 */
public class EstoqueInsuficienteException extends Exception {
    public EstoqueInsuficienteException(String mensagem) {
        super(mensagem);
    }
}