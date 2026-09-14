package br.com.teste;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class Principal {

    private static final BigDecimal SALARIO_MINIMO = new BigDecimal("1212.00");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DecimalFormat MOEDA = criarFormatadorMoeda();

    public static void main(String[] args) {
        List<Funcionario> funcionarios = new ArrayList<>();

        inserirFuncionarios(funcionarios);          // 3.1
        removerJoao(funcionarios);                  // 3.2
        imprimirFuncionarios("3.3 — Funcionários", funcionarios);

        aplicarAumento(funcionarios, new BigDecimal("0.10")); // 3.4
        imprimirFuncionarios("3.4 — Após aumento de 10%", funcionarios);

        Map<String, List<Funcionario>> porFuncao = agruparPorFuncao(funcionarios); // 3.5
        imprimirAgrupadosPorFuncao(porFuncao);      // 3.6

        imprimirAniversariantesMeses(funcionarios, 10, 12); // 3.8
        imprimirMaisVelho(funcionarios);            // 3.9
        imprimirOrdemAlfabetica(funcionarios);      // 3.10
        imprimirTotalSalarios(funcionarios);        // 3.11
        imprimirSalariosMinimos(funcionarios);      // 3.12
    }

    /** 3.1 — Insere os funcionários na ordem da tabela. */
    private static void inserirFuncionarios(List<Funcionario> funcionarios) {
        funcionarios.add(new Funcionario("Maria", LocalDate.of(2000, 10, 18), bd("2009.44"), "Operador"));
        funcionarios.add(new Funcionario("João", LocalDate.of(1990, 5, 12), bd("2284.38"), "Operador"));
        funcionarios.add(new Funcionario("Caio", LocalDate.of(1961, 5, 23), bd("9836.14"), "Coordenador"));
        funcionarios.add(new Funcionario("Miguel", LocalDate.of(1988, 10, 14), bd("19119.88"), "Diretor"));
        funcionarios.add(new Funcionario("Alice", LocalDate.of(1995, 1, 1), bd("2234.68"), "Recepcionista"));
        funcionarios.add(new Funcionario("Heitor", LocalDate.of(1999, 11, 2), bd("1582.72"), "Operador"));
        funcionarios.add(new Funcionario("Arthur", LocalDate.of(1993, 3, 31), bd("4071.84"), "Contador"));
        funcionarios.add(new Funcionario("Laura", LocalDate.of(1994, 7, 8), bd("3017.45"), "Gerente"));
        funcionarios.add(new Funcionario("Heloísa", LocalDate.of(2003, 5, 24), bd("1606.85"), "Eletricista"));
        funcionarios.add(new Funcionario("Helena", LocalDate.of(1996, 9, 2), bd("2799.93"), "Gerente"));
    }

    /** 3.2 — Remove o funcionário João. */
    private static void removerJoao(List<Funcionario> funcionarios) {
        funcionarios.removeIf(funcionario -> funcionario.getNome().equalsIgnoreCase("João"));
    }

    /** 3.4 — Aplica aumento percentual em todos os salários. */
    private static void aplicarAumento(List<Funcionario> funcionarios, BigDecimal percentual) {
        BigDecimal fator = BigDecimal.ONE.add(percentual);
        funcionarios.forEach(funcionario ->
                funcionario.setSalario(funcionario.getSalario().multiply(fator).setScale(2, RoundingMode.HALF_UP)));
    }

    /** 3.5 — Agrupa por função (chave = função, valor = lista). */
    private static Map<String, List<Funcionario>> agruparPorFuncao(List<Funcionario> funcionarios) {
        return funcionarios.stream()
                .collect(Collectors.groupingBy(Funcionario::getFuncao, LinkedHashMap::new, Collectors.toList()));
    }

    /** 3.6 — Imprime o mapa agrupado por função. */
    private static void imprimirAgrupadosPorFuncao(Map<String, List<Funcionario>> porFuncao) {
        titulo("3.6 — Funcionários agrupados por função");
        porFuncao.forEach((funcao, lista) -> {
            System.out.println(funcao + ":");
            lista.forEach(funcionario -> System.out.println("  - " + formatar(funcionario)));
        });
        System.out.println();
    }

    /** 3.8 — Aniversariantes dos meses informados. */
    private static void imprimirAniversariantesMeses(List<Funcionario> funcionarios, int... meses) {
        titulo("3.8 — Aniversariantes dos meses 10 e 12");
        funcionarios.stream()
                .filter(funcionario -> {
                    int mes = funcionario.getDataNascimento().getMonthValue();
                    for (int m : meses) {
                        if (mes == m) {
                            return true;
                        }
                    }
                    return false;
                })
                .forEach(funcionario -> System.out.println(formatar(funcionario)));
        System.out.println();
    }

    /** 3.9 — Funcionário com maior idade (nome e idade). */
    private static void imprimirMaisVelho(List<Funcionario> funcionarios) {
        titulo("3.9 — Funcionário com maior idade");
        funcionarios.stream()
                .min(Comparator.comparing(Pessoa::getDataNascimento))
                .ifPresent(funcionario -> {
                    int idade = Period.between(funcionario.getDataNascimento(), LocalDate.now()).getYears();
                    System.out.printf("%s — %d anos%n%n", funcionario.getNome(), idade);
                });
    }

    /** 3.10 — Lista em ordem alfabética. */
    private static void imprimirOrdemAlfabetica(List<Funcionario> funcionarios) {
        titulo("3.10 — Funcionários em ordem alfabética");
        funcionarios.stream()
                .sorted(Comparator.comparing(Pessoa::getNome, String.CASE_INSENSITIVE_ORDER))
                .forEach(funcionario -> System.out.println(formatar(funcionario)));
        System.out.println();
    }

    /** 3.11 — Soma dos salários. */
    private static void imprimirTotalSalarios(List<Funcionario> funcionarios) {
        titulo("3.11 — Total dos salários");
        BigDecimal total = funcionarios.stream()
                .map(Funcionario::getSalario)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println(MOEDA.format(total));
        System.out.println();
    }

    /** 3.12 — Quantos salários mínimos cada um ganha (R$ 1.212,00). */
    private static void imprimirSalariosMinimos(List<Funcionario> funcionarios) {
        titulo("3.12 — Salários mínimos por funcionário");
        funcionarios.forEach(funcionario -> {
            BigDecimal qtd = funcionario.getSalario().divide(SALARIO_MINIMO, 2, RoundingMode.HALF_UP);
            System.out.printf("%s — %s salários mínimos%n", funcionario.getNome(), MOEDA.format(qtd));
        });
        System.out.println();
    }

    private static void imprimirFuncionarios(String titulo, List<Funcionario> funcionarios) {
        titulo(titulo);
        funcionarios.forEach(funcionario -> System.out.println(formatar(funcionario)));
        System.out.println();
    }

    private static String formatar(Funcionario funcionario) {
        return String.format("%s | %s | %s | %s",
                funcionario.getNome(),
                funcionario.getDataNascimento().format(DATA),
                MOEDA.format(funcionario.getSalario()),
                funcionario.getFuncao());
    }

    private static void titulo(String texto) {
        System.out.println("==== " + texto + " ====");
    }

    private static BigDecimal bd(String valor) {
        return new BigDecimal(valor);
    }

    private static DecimalFormat criarFormatadorMoeda() {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(new Locale("pt", "BR"));
        simbolos.setGroupingSeparator('.');
        simbolos.setDecimalSeparator(',');
        DecimalFormat formato = new DecimalFormat("#,##0.00", simbolos);
        formato.setParseBigDecimal(true);
        return formato;
    }
}
