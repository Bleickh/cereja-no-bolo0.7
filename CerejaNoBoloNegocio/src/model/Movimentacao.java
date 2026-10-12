package model;

import java.time.LocalDateTime;

/** Registro histórico de uma entrada ou saída de estoque. */
public class Movimentacao {

    private int id;
    private int produtoId;
    private TipoMovimentacao tipo;
    private int quantidade;
    private LocalDateTime data;

    public Movimentacao() {
    }

    public Movimentacao(int id, int produtoId, TipoMovimentacao tipo, int quantidade, LocalDateTime data) {
        this.id = id;
        this.produtoId = produtoId;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(int produtoId) {
        this.produtoId = produtoId;
    }

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }
}
