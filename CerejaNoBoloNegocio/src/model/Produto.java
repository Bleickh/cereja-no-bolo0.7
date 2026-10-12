package model;

import java.math.BigDecimal;

/**
 * Produto da confeitaria. O preço usa BigDecimal (dinheiro nunca em double)
 * e o estoque faz parte do produto, conforme o documento de requisitos.
 */
public class Produto {

    private int id;
    private String nome;
    private BigDecimal preco;
    private int categoriaId;
    private int estoque;

    public Produto() {
    }

    public Produto(int id, String nome, BigDecimal preco, int categoriaId, int estoque) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.categoriaId = categoriaId;
        this.estoque = estoque;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

    @Override
    public String toString() {
        return nome + " (R$ " + preco + ", estoque " + estoque + ")";
    }
}
