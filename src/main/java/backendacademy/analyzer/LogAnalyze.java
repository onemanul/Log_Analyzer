package backendacademy.analyzer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class LogAnalyze {
    private final LocalDate from;
    private final LocalDate to;
    private long requestsCount = 0;                                         // общее количество запросов
    private HashMap<String, Long> resourcesCount = new HashMap<>();         // частота запрашиваемых ресурсов
    private HashMap<Integer, Long> responseCodesCount = new HashMap<>();    // частота встречающихся кодов ответа
    private HashMap<String, Long> remoteAddrCount = new HashMap<>();        // частота запросов отдельных IP-адресов
    private ArrayList<Long> responseSizes = new ArrayList<>();              // все размеры ответа сервера
    private long averageResponseSize = 0;                                   // средний размер ответа сервера
    private long percentile50 = 0;                                          // 50% перцентиль размера ответа сервера
    private long percentile95 = 0;                                          // 95% перцентиль размера ответа сервера
    private static final double PER50 = 0.50;
    private static final double PER95 = 0.95;

    public LogAnalyze(LocalDate from, LocalDate to) {
        this.from = from;
        this.to = to;
    }

    public void addRecord(LogRecord logRecord) {
        if (checkDate(logRecord)) {
            requestsCount++;
            resourcesCount
                .put(logRecord.requestPath(), resourcesCount.getOrDefault(logRecord.requestPath(), 0L) + 1);
            responseCodesCount
                .put(logRecord.status(), responseCodesCount.getOrDefault(logRecord.status(), 0L) + 1);
            remoteAddrCount
                .put(logRecord.remoteAddr(), remoteAddrCount.getOrDefault(logRecord.remoteAddr(), 0L) + 1);
            responseSizes.add(logRecord.bodyBytesSent());
        }
    }

    public boolean checkDate(LogRecord logRecord) {
        boolean isAfterFrom = (from == null || logRecord.date().isAfter(from) || logRecord.date().isEqual(from));
        boolean isBeforeTo = (to == null || logRecord.date().isBefore(to));
        return isAfterFrom && isBeforeTo;
    }

    public void finishAnalyze() {
        if (requestsCount != 0) {
            averageResponseSize = responseSizes.stream().mapToLong(Long::longValue).sum() / requestsCount;
            percentile50 = getPercentile(PER50);
            percentile95 = getPercentile(PER95);
        }
    }

    public long getPercentile(double per) {
        if (per < 0 || per > 1 || requestsCount == 0) {
            return 0;
        }
        Collections.sort(responseSizes);
        int index = (per == 0) ? 0 : (int) Math.ceil(per * responseSizes.size()) - 1;
        return responseSizes.get(index);
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    public long getRequestsCount() {
        return requestsCount;
    }

    public HashMap<String, Long> getResourcesCount() {
        return resourcesCount;
    }

    public HashMap<Integer, Long> getResponseCodesCount() {
        return responseCodesCount;
    }

    public HashMap<String, Long> getRemoteAddrCount() {
        return remoteAddrCount;
    }

    public long getAverageResponseSize() {
        return averageResponseSize;
    }

    public long getPercentile50() {
        return percentile50;
    }

    public long getPercentile95() {
        return percentile95;
    }
}
