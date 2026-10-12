-- Atualização do banco da Etapa 4 para o modelo refatorado da Etapa 5.
-- Execute DEPOIS de cereja_no_bolo.sql (script original do sistema desktop).
USE cereja_no_bolo;

-- 1) Categorias duplicadas (ids 4, 5 e 6 repetiam 1, 2 e 3) e unicidade por nome
DELETE FROM categoria
 WHERE id IN (4, 5, 6)
   AND id NOT IN (SELECT categoria_id FROM produto WHERE categoria_id IS NOT NULL);
ALTER TABLE categoria ADD CONSTRAINT uq_categoria_nome UNIQUE (nome);

-- 2) Estoque passa a existir no produto + regras também garantidas no banco
ALTER TABLE produto
  ADD COLUMN estoque INT NOT NULL DEFAULT 0,
  MODIFY categoria_id INT NOT NULL,
  ADD CONSTRAINT ck_produto_estoque CHECK (estoque >= 0),
  ADD CONSTRAINT ck_produto_preco CHECK (preco > 0);

-- 3) Movimentação: campos obrigatórios e quantidade positiva
ALTER TABLE movimentacao
  MODIFY produto_id INT NOT NULL,
  MODIFY quantidade INT NOT NULL,
  MODIFY tipo VARCHAR(20) NOT NULL,
  ADD CONSTRAINT ck_mov_quantidade CHECK (quantidade > 0);

-- 4) Usuários (a tela de login do desktop não autenticava ninguém)
CREATE TABLE IF NOT EXISTS usuario (
  id INT NOT NULL AUTO_INCREMENT,
  login VARCHAR(50) NOT NULL,
  senha_hash VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uq_usuario_login (login)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
