import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Comissao {

    public record Venda(String vendedor, BigDecimal valor) {}

    public static void main(String[] args) {
        String jsonInput = """
        {
          "vendas": [
            { "vendedor": "João Silva", "valor": 1200.50 },
            { "vendedor": "João Silva", "valor": 950.75 },
            { "vendedor": "João Silva", "valor": 1800.00 },
            { "vendedor": "João Silva", "valor": 1400.30 },
            { "vendedor": "João Silva", "valor": 1100.90 },
            { "vendedor": "João Silva", "valor": 1550.00 },
            { "vendedor": "João Silva", "valor": 1700.80 },
            { "vendedor": "João Silva", "valor": 250.30 },
            { "vendedor": "João Silva", "valor": 480.75 },
            { "vendedor": "João Silva", "valor": 320.40 },
            { "vendedor": "Maria Souza", "valor": 2100.40 },
            { "vendedor": "Maria Souza", "valor": 1350.60 },
            { "vendedor": "Maria Souza", "valor": 950.20 },
            { "vendedor": "Maria Souza", "valor": 1600.75 },
            { "vendedor": "Maria Souza", "valor": 1750.00 },
            { "vendedor": "Maria Souza", "valor": 1450.90 },
            { "vendedor": "Maria Souza", "valor": 400.50 },
            { "vendedor": "Maria Souza", "valor": 180.20 },
            { "vendedor": "Maria Souza", "valor": 90.75 },
            { "vendedor": "Carlos Oliveira", "valor": 800.50 },
            { "vendedor": "Carlos Oliveira", "valor": 1200.00 },
            { "vendedor": "Carlos Oliveira", "valor": 1950.30 },
            { "vendedor": "Carlos Oliveira", "valor": 1750.80 },
            { "vendedor": "Carlos Oliveira", "valor": 1300.60 },
            { "vendedor": "Carlos Oliveira", "valor": 300.40 },
            { "vendedor": "Carlos Oliveira", "valor": 500.00 },
            { "vendedor": "Carlos Oliveira", "valor": 125.75 },
            { "vendedor": "Ana Lima", "valor": 1000.00 },
            { "vendedor": "Ana Lima", "valor": 1100.50 },
            { "vendedor": "Ana Lima", "valor": 1250.75 },
            { "vendedor": "Ana Lima", "valor": 1400.20 },
            { "vendedor": "Ana Lima", "valor": 1550.90 },
            { "vendedor": "Ana Lima", "valor": 1650.00 },
            { "vendedor": "Ana Lima", "valor": 75.30 },
            { "vendedor": "Ana Lima", "valor": 420.90 },
            { "vendedor": "Ana Lima", "valor": 315.40 }
          ]
        }
        """;

        Map<String, BigDecimal> comissoes = new HashMap<>();

        Pattern pattern = Pattern.compile("\"vendedor\":\\s*\"([^\"]+)\",\\s*\"valor\":\\s*([0-9.]+)");
        Matcher matcher = pattern.matcher(jsonInput);

        while (matcher.find()) {
            String vendedor = matcher.group(1);
            BigDecimal valor = new BigDecimal(matcher.group(2));
            BigDecimal comissaoVenda = calcularComissao(valor);

            comissoes.merge(vendedor, comissaoVenda, BigDecimal::add);
        }

        System.out.println("--- RELATÓRIO DE COMISSÕES ---");
        comissoes.forEach((vendedor, totalComissao) ->
                System.out.printf("%s: R$ %.2f%n", vendedor, totalComissao.setScale(2, RoundingMode.HALF_UP))
        );
    }

    private static BigDecimal calcularComissao(BigDecimal valor) {
        if (valor.compareTo(new BigDecimal("500.00")) >= 0) {
            return valor.multiply(new BigDecimal("0.05"));
        } else if (valor.compareTo(new BigDecimal("100.00")) >= 0) {
            return valor.multiply(new BigDecimal("0.01"));
        }
        return BigDecimal.ZERO;
    }
}