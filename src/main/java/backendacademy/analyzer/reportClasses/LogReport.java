package backendacademy.analyzer.reportClasses;

import backendacademy.analyzer.LogAnalyze;
import java.util.List;
import java.util.Map;

public class LogReport {
    private LogAnalyze analyze;
    private static final int MAX_TOP = 3;

    private LogReport() {}

    public static String report(LogAnalyze analyze, String path, String format) {
        LogReport logReport = new LogReport();
        return logReport.makeReport(analyze, path, format);
    }

    private String makeReport(LogAnalyze analyze, String path, String format) {
        this.analyze = analyze;
        if (analyze.getRequestsCount() == 0) {
            return "Нет данных для составления отчёта.";
        }
        try {
            LogReport.StringArrayMaker maker = new LogReport.StringArrayMaker();
            String[][] generalInfo = maker.generalInfo(path);
            String[][] requestedResources = maker.requestedResources();
            String[][] responseCodes = maker.responseCodes();
            String[][] addressesRequestSources = maker.addressesRequestSources();
            ReportFormatter formatter = new ReportFormatter(generalInfo, requestedResources, responseCodes,
                addressesRequestSources);
            return formatter.formReport(format);
        } catch (IllegalAccessError e) {
            return "Недостижимо при вызове report, ошибку эти фукции могут бросить только при вызове внутреннего "
                + "класса отдельно.";
        }
    }

    public class StringArrayMaker {
        public String[][] generalInfo(String path) {
            if (analyze.getRequestsCount() == 0) {
                throw new IllegalStateException();
            }
            String from = (analyze.getFrom() != null) ? analyze.getFrom().toString() : "-";
            String to = (analyze.getTo() != null) ? analyze.getTo().toString() : "-";
            return new String[][] {
                {"Файл(-ы)", path},
                {"Начальная дата", from},
                {"Конечная дата", to},
                {"Количество запросов", String.valueOf(analyze.getRequestsCount())},
                {"Средний размер ответа", analyze.getAverageResponseSize() + "b"},
                {"50p размера ответа", analyze.getPercentile50() + "b"},
                {"95p размера ответа", analyze.getPercentile95() + "b"}
            };
        }

        public String[][] requestedResources() {
            if (analyze.getRequestsCount() == 0) {
                throw new IllegalStateException();
            }
            List<Map.Entry<String, Long>> top = analyze.getResourcesCount().entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(MAX_TOP)
                .toList();
            return top.stream()
                .map(entry -> new String[] {entry.getKey(), entry.getValue().toString()})
                .toArray(String[][]::new);
        }

        public String[][] responseCodes() {
            if (analyze.getRequestsCount() == 0) {
                throw new IllegalStateException();
            }
            List<Map.Entry<Integer, Long>> top = analyze.getResponseCodesCount().entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(MAX_TOP)
                .toList();
            return top.stream()
                .map(entry -> new String[] {entry.getKey().toString(), entry.getValue().toString()})
                .toArray(String[][]::new);
        }

        public String[][] addressesRequestSources() {
            if (analyze.getRequestsCount() == 0) {
                throw new IllegalStateException();
            }
            List<Map.Entry<String, Long>> top = analyze.getRemoteAddrCount().entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(MAX_TOP)
                .toList();
            return top.stream()
                .map(entry -> new String[] {entry.getKey(), entry.getValue().toString()})
                .toArray(String[][]::new);
        }
    }
}
