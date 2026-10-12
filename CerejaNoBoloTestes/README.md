# CerejaNoBoloTestes – Testes unitários JUnit (Etapa 6 do PI)

Projeto NetBeans (Ant) com testes JUnit 4 do núcleo `CerejaNoBoloNegocio`.

## Como executar no NetBeans
1. Abrir `CerejaNoBoloNegocio` e fazer **Clean and Build** (gera `dist/CerejaNoBoloNegocio.jar`).
2. Abrir `CerejaNoBoloTestes`. Se o NetBeans acusar biblioteca ausente:
   *Propriedades do projeto → Bibliotecas → Compilar testes → Adicionar biblioteca → JUnit 4.x e Hamcrest*.
3. Clique direito no projeto → **Test** (Alt+F6).

## Conteúdo (`test/`)
| Classe de teste | O que verifica | Testes |
|---|---|---|
| `service.CalculadoraPrecoTest` | preço de venda, margem, lucro, regra "preço > custo", valor em estoque | 14 |
| `model.TipoMovimentacaoTest` | regra de saldo de entrada/saída, sem estoque negativo | 6 |
| `service.EstoqueServiceTest` | movimentação de estoque com repositórios em memória e relógio fixo | 6 |
| `service.ProdutoServiceTest` | cadastro, edição e exclusão de produtos | 6 |

Testes de acesso ao banco (JDBC) ficam fora da suíte unitária, conforme a atividade.
