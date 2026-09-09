package br.com.ecommerce.jdbc;

import java.math.BigDecimal;

public class TesteProdutoDAO {

    public static void main(String[] args) {
        ProdutoDAO dao = new ProdutoDAO();

        // Testando inserir()
        Produto novoProduto = new Produto("COD001", "Teclado Mecânico", new BigDecimal("250.00"), 10);
        boolean inserido = dao.inserir(novoProduto);
        System.out.println("Produto inserido? " + inserido);

        // Testando atualizarPreco()
        boolean atualizado = dao.atualizarPreco("COD001", new BigDecimal("199.90"));
        System.out.println("Preço atualizado? " + atualizado);

        // Testando excluir() com um código que NÃO existe, pra ver o retorno false
        boolean excluidoInexistente = dao.excluir("CODIGO_INEXISTENTE");
        System.out.println("Excluiu código inexistente? " + excluidoInexistente);

        // Testando excluir() com o código que realmente existe
        boolean excluido = dao.excluir("COD001");
        System.out.println("Produto excluído? " + excluido);
    }
}
