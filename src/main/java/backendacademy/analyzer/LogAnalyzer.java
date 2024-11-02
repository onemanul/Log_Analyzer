package backendacademy.analyzer;

import java.io.PrintStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class LogAnalyzer {
    private String path;
    private final Optional<LocalDate> optFromDate;
    private final Optional<LocalDate> optToDate;
    private List<LogRecord> recordList;
    private long requestsCount;                                 // общее количество запросов
    private Map<String, Long> resourcesCount;                   // частота запрашиваемых ресурсов
    private Map<Integer, Long> responseCodesCount;              // частота встречающихся кодов ответа
    private long averageResponseSize;                            // средний размер ответа сервера
    private long percentile50;                                   // 50% перцентиль размера ответа сервера
    private long percentile95;                                   // 95% перцентиль размера ответа сервера
    private Map<String, Long> remoteAddrCount;
    private boolean hasBeenAnalyzed = false;
    private static final int MAX_TOP = 3;
    private static final double PER50 = 0.50;
    private static final double PER95 = 0.95;

    private final PrintStream output = System.out;

    public LogAnalyzer(String path, Optional<LocalDate> optFromDate, Optional<LocalDate> optToDate) {
        this.path = path;
        this.optFromDate = optFromDate;
        this.optToDate = optToDate;
        this.recordList = new ArrayList<>();
    }

    public boolean analyze() {
        try {
            recordList = LogFileProcessor.getLogRecords(path);
        } catch (Exception e) {
            return hasBeenAnalyzed;
        }
        filterByDate();
        if (recordList.isEmpty()) {
            output.println("Нет данных, соответствующих запросу.");
            return hasBeenAnalyzed;
        } else {
            makeAnalyze();
            hasBeenAnalyzed = true;
            return true;
        }
    }

    public String report(String format) {
        if (!hasBeenAnalyzed) {
            return "Анализ данных не был проведён.";
        }
        String[][] generalInfo = makeGeneralInfo();
        String[][] requestedResources = makeRequestedResources();
        String[][] responseCodes = makeResponseCodes();
        String[][] addressesRequestSources = makeAddressesRequestSources();
        ReportFormatter formatter = new ReportFormatter(generalInfo, requestedResources,
                                                        responseCodes, addressesRequestSources);
        return formatter.formReport(format);
    }

    private String[][] makeGeneralInfo() {
        String fromDate = optFromDate.map(LocalDate::toString).orElse("-");
        String toDate = optToDate.map(LocalDate::toString).orElse("-");
        return new String[][] {
            {"Файл(-ы)", path},
            {"Начальная дата", fromDate},
            {"Конечная дата", toDate},
            {"Количество запросов", String.valueOf(requestsCount)},
            {"Средний размер ответа", averageResponseSize + "b"},
            {"50p размера ответа", percentile50 + "b"},
            {"95p размера ответа", percentile95 + "b"}
        };
    }

    private String[][] makeRequestedResources() {
        List<Map.Entry<String, Long>> top = resourcesCount.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(MAX_TOP)
            .toList();
        return top.stream()
            .map(entry -> new String[]{entry.getKey(), entry.getValue().toString()})
            .toArray(String[][]::new);
    }

    private String[][] makeResponseCodes() {
        List<Map.Entry<Integer, Long>> top = responseCodesCount.entrySet()
            .stream()
            .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
            .limit(MAX_TOP)
            .toList();
        return top.stream()
            .map(entry -> new String[]{entry.getKey().toString(), entry.getValue().toString()})
            .toArray(String[][]::new);
    }

    private String[][] makeAddressesRequestSources() {
        List<Map.Entry<String, Long>> top = remoteAddrCount.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(MAX_TOP)
            .toList();
        return top.stream()
            .map(entry -> new String[]{entry.getKey(), entry.getValue().toString()})
            .toArray(String[][]::new);
    }

    private void makeAnalyze() {
        requestsCount = recordList.size();
        resourcesCount = recordList.stream()
            .collect(Collectors.groupingBy(LogRecord::getRequestPath, Collectors.counting()));
        responseCodesCount = recordList.stream()
            .collect(Collectors.groupingBy(LogRecord::getStatus, Collectors.counting()));
        averageResponseSize = recordList.stream().mapToLong(LogRecord::getBodyBytesSent).sum() / requestsCount;
        percentile50 = getPercentile(PER50);
        percentile95 = getPercentile(PER95);
        remoteAddrCount = recordList.stream()
            .collect(Collectors.groupingBy(LogRecord::getRemoteAddr, Collectors.counting()));
    }

    private long getPercentile(double per) {
        if (per < 0 || per > 1) {
            return 0;
        }
        List<LogRecord> sortByResponseSize = recordList.stream()
            .sorted(Comparator.comparing(LogRecord::getBodyBytesSent)).toList();
        int index = (int) Math.ceil(per * sortByResponseSize.size()) - 1;
        return sortByResponseSize.get(index).getBodyBytesSent();
    }

    private void filterByDate() {
        optFromDate.ifPresent(fromDate -> recordList = recordList.stream()
            .filter(obj -> obj.getDate().isAfter(fromDate) || obj.getDate().isEqual(fromDate)).toList());
        optToDate.ifPresent(toDate -> recordList = recordList.stream()
            .filter(obj -> obj.getDate().isBefore(toDate)).toList());
    }
}
