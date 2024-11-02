package backendacademy.analyzer;

import java.time.LocalDate;

public class LogRecord {
    private final String remoteAddr;
    private final LocalDate date;
    private final String requestPath;
    private final int status;
    private final long bodyBytesSent;

    public LogRecord(String remoteAddr, LocalDate date, String requestPath, int status, long bodyBytesSent) {
        this.remoteAddr = remoteAddr;
        this.date = date;
        this.requestPath = requestPath;
        this.status = status;
        this.bodyBytesSent = bodyBytesSent;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public int getStatus() {
        return status;
    }

    public long getBodyBytesSent() {
        return bodyBytesSent;
    }
}
