# Cereja no Bolo – Núcleo reutilizável (Etapa 5 do PI)

Sistema de confeitaria: estoque e movimentações de produtos. Esta etapa extrai do
sistema desktop Java Swing (Etapa 4) as **regras de negócio**, deixando-as
independentes de interface, para reaproveitamento no sistema web.

## Tecnologias
Java 17+ · JDBC · MySQL 8 · NetBeans (Ant)

## Estrutura (`src/`)
| Pacote | Responsabilidade |
|---|---|
| `model` | Entidades de domínio (`Produto`, `Categoria`, `Movimentacao`, `Usuario`, `TipoMovimentacao`) |
| `service` | Regras de negócio (`ProdutoService`, `EstoqueService`, `CategoriaService`, `AutenticacaoService`) |
| `repository` | Interfaces de persistência (abstrações) |
| `repository.jdbc` | Implementações MySQL/JDBC |
| `repository.memory` | Implementações em memória (testes) |
| `config` | Configuração do banco e raiz de composição (`FabricaDeServicos`) |
| `exception` | Exceções de negócio e de persistência |

## Como executar
1. Abrir a pasta `CerejaNoBoloNegocio` no NetBeans e executar `Main` (testes em memória, sem banco).
2. Teste com MySQL (opcional): rodar `cereja_no_bolo.sql` e depois `sql/cereja_no_bolo_v2.sql`,
   copiar `config/database.properties.example` para `config/database.properties`,
   adicionar o driver `mysql-connector-j` ao projeto e executar `Main` com o argumento `--mysql`.

> A senha do banco **não** fica no código: vem de `config/database.properties` (ignorado pelo Git)
> ou das variáveis de ambiente `CEREJA_DB_URL`, `CEREJA_DB_USER`, `CEREJA_DB_PASSWORD`.
