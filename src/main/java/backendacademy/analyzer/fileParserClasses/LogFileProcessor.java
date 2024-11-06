package backendacademy.analyzer.fileParserClasses;

import backendacademy.analyzer.LogRecord;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class LogFileProcessor {
    private LogFileProcessor() {}

    private final PrintStream output = System.out;

    public static List<LogRecord> getLogRecords(String path) throws Exception {
        LogFileProcessor lfp = new LogFileProcessor();
        return lfp.get(path);
    }

    private List<LogRecord> get(String path) throws Exception {
        try {
            if (isValidURL(path)) {
                return readFromURL(path);
            } else {
                return LocalFileProcessor.read(path);
            }
        } catch (IOException e) {
            output.println("Ошибка с чтением файла (не найден или не может быть прочитан) по пути: " + path);
            throw e;
        }
    }

    private boolean isValidURL(String path) {
        try {
            URL url = new URL(path);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private List<LogRecord> readFromURL(String path) throws IOException {
        URL url = new URL(path);
        URLConnection connection = url.openConnection();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return LogParser.makeRecordList(reader);
        }
    }
}
