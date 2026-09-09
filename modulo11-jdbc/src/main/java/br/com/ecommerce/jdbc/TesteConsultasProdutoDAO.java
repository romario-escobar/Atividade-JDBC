package br.com.ecommerce.jdbc;

import java.math.BigDecimal;
import java.util.List;

public class TesteConsultasProdutoDAO {

    public static void main(String[] args) {
        ProdutoDAO dao = new ProdutoDAO();

        // Cadastrando alguns produtos para termos o que consultar
        dao.inserir(new Produto("COD001", "Teclado Mecânico", new BigDecimal("250.00"), 10));
        dao.inserir(new Produto("COD002", "Mouse Sem Fio", new BigDecimal("90.00"), 25));
        dao.inserir(new Produto("COD003", "Monitor 24\"", new BigDecimal("899.90"), 5));

        System.out.println("=== listarTodos() ===");
        List<Produto> todos = dao.listarTodos();
        for (Produto p : todos) {
            System.out.println(p);
        }

        System.out.println("\n=== buscarPorCodigo(\"COD002\") ===");
        Produto encontrado = dao.buscarPorCodigo("COD002");
        System.out.println(encontrado != null ? encontrado : "Produto não encontrado.");

        System.out.println("\n=== buscarPorCodigo(\"CODIGO_INEXISTENTE\") ===");
        Produto naoEncontrado = dao.buscarPorCodigo("CODIGO_INEXISTENTE");
        System.out.println(naoEncontrado != null ? naoEncontrado : "Produto não encontrado.");
    }
}