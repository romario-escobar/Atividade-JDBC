package br.com.ecommerce.jdbc;

import java.math.BigDecimal;

public class TesteRecursosAvancados {

    public static void main(String[] args) {
        ProdutoDAO dao = new ProdutoDAO();
        RecursosAvancadosDAO recursos = new RecursosAvancadosDAO();

        // Garantindo alguns produtos com preços variados para o teste do cursor rolável
        dao.excluir("COD001");
        dao.excluir("COD002");
        dao.excluir("COD003");
        dao.inserir(new Produto("COD001", "Mouse", new BigDecimal("50.00"), 20));
        dao.inserir(new Produto("COD002", "Teclado", new BigDecimal("150.00"), 15));
        dao.inserir(new Produto("COD003", "Monitor", new BigDecimal("900.00"), 5));

        System.out.println("=== Cursor Rolável (TYPE_SCROLL_INSENSITIVE) ===");
        recursos.demonstrarCursorRolavel();

        System.out.println("\n=== Stored Procedure (CallableStatement) ===");
        recursos.executarProcedureSaldo("COD003");
    }
}