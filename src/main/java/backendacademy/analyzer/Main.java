package backendacademy.analyzer;

import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Optional;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Main {
    private static final PrintStream OUTPUT = System.out;

    public static void main(String[] args) {
        if (args.length < 1) {
            OUTPUT.println("Правильное использование: java LogAnalyzer --path <путь к лог-файлам> [--from <дата>] "
                + "[--to <дата>] [--format <markdown|adoc>] \nДата from включена в диапазон, дата to - нет");
            return;
        }
        Optional<String> optPath = Optional.empty();
        Optional<LocalDate> optFromDate = Optional.empty();
        Optional<LocalDate> optToDate = Optional.empty();
        String format = "markdown";
        //CHECKSTYLE:OFF
        for (int i = 0; i < args.length; ++i) {
            switch (args[i]) {
                case "--path":
                    optPath = Optional.of(args[++i]);
                    break;
                case "--from":
                    if (checkDateInput(args[++i])) {
                        optFromDate = Optional.of(LocalDate.parse(args[i]));
                    }
                    break;
                case "--to":
                    if (checkDateInput(args[++i])) {
                        optToDate = Optional.of(LocalDate.parse(args[i]));
                    }
                    break;
                case "--format":
                    format = args[++i];
                    break;
                default:
                    OUTPUT.println("Найдены неизвестные параметры");
            }
        }
        //CHECKSTYLE:ON

        if (optPath.isEmpty()) {
            OUTPUT.println("Среди параметров отсутствует путь, программа будет завершена");
        } else {
            LogAnalyzer analyzer = new LogAnalyzer(optPath.get(), optFromDate, optToDate);
            if (!analyzer.analyze()) {
                OUTPUT.println("Анализ невозможен, программа будет завершена");
            } else {
                OUTPUT.println(analyzer.report(format));
            }
        }
    }

    public static boolean checkDateInput(String input) {
        try {
            LocalDate.parse(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
