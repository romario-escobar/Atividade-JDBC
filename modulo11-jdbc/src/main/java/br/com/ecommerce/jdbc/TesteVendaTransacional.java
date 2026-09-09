package br.com.ecommerce.jdbc;

import java.math.BigDecimal;

public class TesteVendaTransacional {

    public static void main(String[] args) {
        ProdutoDAO dao = new ProdutoDAO();
        ServicoVendaTransacional servico = new ServicoVendaTransacional();

        // Garantindo um produto com estoque conhecido para o teste
        dao.excluir("COD001"); // limpa se já existir de testes anteriores
        dao.inserir(new Produto("COD001", "Teclado Mecânico", new BigDecimal("250.00"), 10));

        System.out.println("=== Teste 1: Venda dentro do estoque disponível (deve dar COMMIT) ===");
        boolean sucesso = servico.processarVenda("PED-001", "COD001", 3);
        System.out.println("Resultado: " + sucesso);
        System.out.println("Produto após a venda: " + dao.buscarPorCodigo("COD001"));

        System.out.println("\n=== Teste 2: Venda maior que o estoque disponível (deve dar ROLLBACK) ===");
        boolean falhou = servico.processarVenda("PED-002", "COD001", 999);
        System.out.println("Resultado: " + falhou);
        System.out.println("Produto após a tentativa (estoque NÃO deve ter mudado): " + dao.buscarPorCodigo("COD001"));
    }
}