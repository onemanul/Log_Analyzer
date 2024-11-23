package backendacademy.analyzer.fileParserClasses;

import backendacademy.analyzer.LogAnalyze;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;

public class LogFileProcessor {
    private LogFileProcessor() {}

    private final PrintStream output = System.out;

    public static LogAnalyze getLogRecords(String path, LocalDate from, LocalDate to) throws Exception {
        LogFileProcessor lfp = new LogFileProcessor();
        return lfp.get(path, from, to);
    }

    private LogAnalyze get(String path, LocalDate from, LocalDate to) throws Exception {
        try {
            if (isValidURL(path)) {
                return readFromURL(path, from, to);
            } else {
                return readFromLocalFile(path, from, to);
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

    private LogAnalyze readFromURL(String path, LocalDate from, LocalDate to) throws IOException {
        LogAnalyze analyze = new LogAnalyze(from, to);
        URL url = new URL(path);
        URLConnection connection = url.openConnection();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            LogParser.addRecordsToAnalyze(reader, analyze);
            return analyze;
        }
    }

    private LogAnalyze readFromLocalFile(String pathString, LocalDate from, LocalDate to) throws IOException {
        LogAnalyze analyze = new LogAnalyze(from, to);
        AtomicBoolean pathExists = new AtomicBoolean(false);
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pathString);
        Files.walk(Paths.get(""))
            .filter(Files::isRegularFile)
            .filter(matcher::matches)
            .forEach(path -> {
                pathExists.set(true);
                try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
                    LogParser.addRecordsToAnalyze(reader, analyze);
                } catch (IOException e) { }
            });
        if (pathExists.get()) {
            return analyze;
        } else {
            throw new IOException();
        }
    }
}
