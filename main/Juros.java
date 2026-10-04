import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Juros {

    public record ResultadoCalculo(
            BigDecimal valorOriginal,
            LocalDate dataVencimento,
            LocalDate dataCalculo,
            long diasAtraso,
            BigDecimal valorJuros,
            BigDecimal valorTotal
    ) {}

    public static ResultadoCalculo calcularJuros(BigDecimal valor, LocalDate vencimento) {
        LocalDate hoje = LocalDate.now();

        if (!hoje.isAfter(vencimento)) {
            return new ResultadoCalculo(valor, vencimento, hoje, 0, BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), valor);
        }

        long diasAtraso = ChronoUnit.DAYS.between(vencimento, hoje);

        BigDecimal taxaDiaria = new BigDecimal("0.025");
        BigDecimal valorJuros = valor.multiply(taxaDiaria)
                .multiply(BigDecimal.valueOf(diasAtraso))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal valorTotal = valor.add(valorJuros);

        return new ResultadoCalculo(valor, vencimento, hoje, diasAtraso, valorJuros, valorTotal);
    }

    public static void main(String[] args) {
        BigDecimal valorConta = new BigDecimal("1000.00");
        LocalDate vencimento = LocalDate.now().minusDays(10); // Exemplo de vencimento há 10 dias

        ResultadoCalculo resultado = calcularJuros(valorConta, vencimento);

        System.out.println("--- CÁLCULO DE JUROS ---");
        System.out.println("Valor Original: R$ " + resultado.valorOriginal());
        System.out.println("Data de Vencimento: " + resultado.dataVencimento());
        System.out.println("Data Atual: " + resultado.dataCalculo());
        System.out.println("Dias de Atraso: " + resultado.diasAtraso());
        System.out.println("Juros Acumulados (2,5%/dia): R$ " + resultado.valorJuros());
        System.out.println("Valor Total: R$ " + resultado.valorTotal());
    }
}