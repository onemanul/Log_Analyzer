package backendacademy.analyzer;

import backendacademy.analyzer.fileParserClasses.LogFileProcessor;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Main {
    private static final PrintStream OUTPUT = System.out;

    public static void main(String[] args) {
        /*String path = "LOG/lo*.txt";
        LogAnalyzer analyzer = new LogAnalyzer(path, Optional.empty(), Optional.empty());
        OUTPUT.println("Проверка: " + analyzer.analyze());
        OUTPUT.println(analyzer.report("markdown"));*/

        if (args.length < 1) {
            OUTPUT.println("Правила использования: --path <путь к лог-файлам> [--from <дата>] "
                + "[--to <дата>] [--format <markdown|adoc>] \nДата from включена в диапазон, дата to - нет");
            return;
        }
        Optional<String> optPath = Optional.empty();
        LocalDate fromDate = null;
        LocalDate toDate = null;
        String format = "markdown";

        //CHECKSTYLE:OFF
        for (int i = 0; i < args.length; ++i) {
            switch (args[i]) {
                case "--path":
                    optPath = Optional.of(args[++i]);
                    break;
                case "--from":
                    if (checkDateInput(args[++i])) {
                        fromDate = LocalDate.parse(args[i]);
                    }
                    break;
                case "--to":
                    if (checkDateInput(args[++i])) {
                        toDate = LocalDate.parse(args[i]);
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
            if (!analyzeLogs(optPath.get(), fromDate, toDate, format)) {
                OUTPUT.println("Анализ невозможен, программа будет завершена");
            }
        }
    }

    public static boolean analyzeLogs(String path, LocalDate fromDate, LocalDate toDate, String format) {
        List<LogRecord> recordList;
        try {
            recordList = LogFileProcessor.getLogRecords(path);
        } catch (Exception e) {
            return false;
        }
        LogAnalyzer analyzer = new LogAnalyzer();
        if (analyzer.analyze(analyzer.filterByDate(recordList, fromDate, toDate))) {
            OUTPUT.println(analyzer.report(format, path));
            return true;
        } else {
            return false;
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
