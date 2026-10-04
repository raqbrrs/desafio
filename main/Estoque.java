import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Estoque {

    public enum TipoMovimentacao { ENTRADA, SAIDA }

    public record Produto(int codigo, String descricao, int quantidade) {}

    public record Movimentacao(
            String idUnico,
            int codigoProduto,
            String descricao,
            TipoMovimentacao tipo,
            int quantidade
    ) {}

    public static class Deposito {
        private final Map<Integer, Produto> inventario = new HashMap<>();

        public void carregarProduto(Produto p) {
            inventario.put(p.codigo(), p);
        }

        public synchronized int movimentar(Movimentacao mov) {
            Produto p = inventario.get(mov.codigoProduto());
            if (p == null) {
                throw new IllegalArgumentException("Erro: Produto " + mov.codigoProduto() + " não encontrado.");
            }

            int novaQtd = (mov.tipo() == TipoMovimentacao.ENTRADA)
                    ? p.quantidade() + mov.quantidade()
                    : p.quantidade() - mov.quantidade();

            if (novaQtd < 0) {
                throw new IllegalStateException("Erro: Saldo insuficiente em estoque para o produto " + p.descricao());
            }

            Produto produtoAtualizado = new Produto(p.codigo(), p.descricao(), novaQtd);
            inventario.put(p.codigo(), produtoAtualizado);

            System.out.printf("[ID: %s] %s | Produto: %s | Qtd Movimentada: %d | Qtd Final: %d%n",
                    mov.idUnico(), mov.descricao(), p.descricao(), mov.quantidade(), novaQtd);

            return novaQtd;
        }
    }

    public static void main(String[] args) {
        String jsonInput = """
        {
            "estoque": [
              { "codigoProduto": 101, "descricaoProduto": "Caneta Azul", "estoque": 150 },
              { "codigoProduto": 102, "descricaoProduto": "Caderno Universitário", "estoque": 75 },
              { "codigoProduto": 103, "descricaoProduto": "Borracha Branca", "estoque": 200 },
              { "codigoProduto": 104, "descricaoProduto": "Lápis Preto HB", "estoque": 320 },
              { "codigoProduto": 105, "descricaoProduto": "Marcador de Texto Amarelo", "estoque": 90 }
            ]
        }
        """;

        Deposito deposito = new Deposito();

        Pattern pattern = Pattern.compile("\"codigoProduto\":\\s*(\\d+),\\s*\"descricaoProduto\":\\s*\"([^\"]+)\",\\s*\"estoque\":\\s*(\\d+)");
        Matcher matcher = pattern.matcher(jsonInput);

        while (matcher.find()) {
            int codigo = Integer.parseInt(matcher.group(1));
            String desc = matcher.group(2);
            int qtd = Integer.parseInt(matcher.group(3));
            deposito.carregarProduto(new Produto(codigo, desc, qtd));
        }

        System.out.println("--- MOVIMENTAÇÕES DE ESTOQUE ---");

        Movimentacao m1 = new Movimentacao(
                UUID.randomUUID().toString(),
                101,
                "Entrada de fornecedor",
                TipoMovimentacao.ENTRADA,
                50
        );
        deposito.movimentar(m1);

        Movimentacao m2 = new Movimentacao(
                UUID.randomUUID().toString(),
                102,
                "Saída para venda",
                TipoMovimentacao.SAIDA,
                20
        );
        deposito.movimentar(m2);
    }
}