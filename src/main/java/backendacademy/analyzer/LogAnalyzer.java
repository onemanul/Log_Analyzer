package backendacademy.analyzer;

import backendacademy.analyzer.reportClasses.ReportFormatter;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LogAnalyzer {
    private LocalDate fromDate;
    private LocalDate toDate;
    private long requestsCount;                                 // общее количество запросов
    private Map<String, Long> resourcesCount;                   // частота запрашиваемых ресурсов
    private Map<Integer, Long> responseCodesCount;              // частота встречающихся кодов ответа
    private long averageResponseSize;                           // средний размер ответа сервера
    private long percentile50;                                  // 50% перцентиль размера ответа сервера
    private long percentile95;                                  // 95% перцентиль размера ответа сервера
    private Map<String, Long> remoteAddrCount;                  // частота запросов отдельных IP-адресов
    private boolean hasBeenAnalyzed = false;
    private static final int MAX_TOP = 3;
    private static final double PER50 = 0.50;
    private static final double PER95 = 0.95;

    private final PrintStream output = System.out;

    public LogAnalyzer() {}

    public boolean analyze(List<LogRecord> recordList) {
        if (recordList.isEmpty()) {
            output.println("Нет данных, соответствующих запросу.");
            return false;
        } else {
            makeAnalyze(recordList);
            hasBeenAnalyzed = true;
            return true;
        }
    }

    public String report(String path, LocalDate from, LocalDate to, String format) {
        if (!hasBeenAnalyzed) {
            return "Анализ данных не был проведён.";
        }
        this.fromDate = from;
        this.toDate = to;
        try {
            StringArrayMaker maker = new StringArrayMaker();
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

    private void makeAnalyze(List<LogRecord> recordList) {
        requestsCount = recordList.size();
        resourcesCount = recordList.stream()
            .collect(Collectors.groupingBy(LogRecord::requestPath, Collectors.counting()));
        responseCodesCount = recordList.stream()
            .collect(Collectors.groupingBy(LogRecord::status, Collectors.counting()));
        averageResponseSize = recordList.stream().mapToLong(LogRecord::bodyBytesSent).sum() / requestsCount;
        percentile50 = getPercentile(PER50, recordList);
        percentile95 = getPercentile(PER95, recordList);
        remoteAddrCount = recordList.stream()
            .collect(Collectors.groupingBy(LogRecord::remoteAddr, Collectors.counting()));
    }

    public long getPercentile(double per, List<LogRecord> recordList) {
        if (per < 0 || per > 1 || recordList.isEmpty()) {
            return 0;
        }
        List<LogRecord> sortByResponseSize = recordList.stream()
            .sorted(Comparator.comparing(LogRecord::bodyBytesSent)).toList();
        int index = (per == 0) ? 0 : (int) Math.ceil(per * sortByResponseSize.size()) - 1;
        return sortByResponseSize.get(index).bodyBytesSent();
    }

    public class StringArrayMaker {
        public String[][] generalInfo(String path) {
            if (!hasBeenAnalyzed) {
                throw new IllegalStateException();
            }
            String from = (fromDate != null) ? fromDate.toString() : "-";
            String to = (toDate != null) ? toDate.toString() : "-";
            return new String[][] {
                {"Файл(-ы)", path},
                {"Начальная дата", from},
                {"Конечная дата", to},
                {"Количество запросов", String.valueOf(requestsCount)},
                {"Средний размер ответа", averageResponseSize + "b"},
                {"50p размера ответа", percentile50 + "b"},
                {"95p размера ответа", percentile95 + "b"}
            };
        }

        public String[][] requestedResources() {
            if (!hasBeenAnalyzed) {
                throw new IllegalStateException();
            }
            List<Map.Entry<String, Long>> top = resourcesCount.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(MAX_TOP)
                .toList();
            return top.stream()
                .map(entry -> new String[] {entry.getKey(), entry.getValue().toString()})
                .toArray(String[][]::new);
        }

        public String[][] responseCodes() {
            if (!hasBeenAnalyzed) {
                throw new IllegalStateException();
            }
            List<Map.Entry<Integer, Long>> top = responseCodesCount.entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(MAX_TOP)
                .toList();
            return top.stream()
                .map(entry -> new String[] {entry.getKey().toString(), entry.getValue().toString()})
                .toArray(String[][]::new);
        }

        public String[][] addressesRequestSources() {
            if (!hasBeenAnalyzed) {
                throw new IllegalStateException();
            }
            List<Map.Entry<String, Long>> top = remoteAddrCount.entrySet()
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
