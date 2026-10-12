package service;

import exception.RecursoNaoEncontradoException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import model.Movimentacao;
import model.Produto;
import model.TipoMovimentacao;
import repository.MovimentacaoRepository;
import repository.ProdutoRepository;

/**
 * Regras de estoque (RF04, RF05): não permite estoque negativo e toda alteração
 * de estoque gera um registro de movimentação. O Clock é injetado para que a
 * data seja controlável nos testes.
 */
public class EstoqueService {

    private final ProdutoRepository produtos;
    private final MovimentacaoRepository movimentacoes;
    private final Clock relogio;

    public EstoqueService(ProdutoRepository produtos, MovimentacaoRepository movimentacoes, Clock relogio) {
        this.produtos = produtos;
        this.movimentacoes = movimentacoes;
        this.relogio = relogio;
    }

    public Movimentacao registrar(int produtoId, TipoMovimentacao tipo, int quantidade) {
        Validador.maiorQueZero(quantidade, "Quantidade");
        Produto produto = produtos.buscarPorId(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", produtoId));

        int novoEstoque = tipo.aplicar(produto.getEstoque(), quantidade); // pode lançar "estoque insuficiente"
        produto.setEstoque(novoEstoque);
        produtos.atualizar(produto);

        return movimentacoes.salvar(
                new Movimentacao(0, produtoId, tipo, quantidade, LocalDateTime.now(relogio)));
    }

    public List<Movimentacao> historico(int produtoId) {
        return movimentacoes.listarPorProduto(produtoId);
    }
}
