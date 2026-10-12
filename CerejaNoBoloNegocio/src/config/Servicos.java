package config;

import service.AutenticacaoService;
import service.CategoriaService;
import service.EstoqueService;
import service.ProdutoService;

/** Agrupa os serviços prontos para uso por qualquer interface (Swing, web, testes). */
public record Servicos(
        CategoriaService categorias,
        ProdutoService produtos,
        EstoqueService estoque,
        AutenticacaoService autenticacao) {
}
