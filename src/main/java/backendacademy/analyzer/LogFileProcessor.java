package backendacademy.analyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                return readFromFile(path);
            }
        } catch (IOException e) {
            output.println("Ошибка с чтением файла (не найден или не может быть прочитан) по пути: " + path);
        } catch (InvalidPathException e) {
            output.println("Путь указан неверно (строка \"" + path + "\" не может быть преобразована в путь).");
        }
        throw new Exception();
    }

    private boolean isValidURL(String path) {
        try {
            URL url = new URL(path);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private List<LogRecord> readFromFile(String pathString) throws IOException, InvalidPathException {
        if (pathString.contains("*")) {         // путь - шаблон
            return readTemplate(pathString);
        }
        Path path = Paths.get(pathString);
        if (Files.exists(path)) {
            if (Files.isDirectory(path)) {      // путь - директория
                List<LogRecord> recordList = new ArrayList<>();
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                    for (Path entry : stream) {
                        recordList.addAll(readFile(entry));
                    }
                }
                return recordList;
            } else  {                           // путь - конечный путь к файлу
                return readFile(path);
            }
        }
        throw new IOException();
    }

    private List<LogRecord> readTemplate(String pathString) throws IOException, InvalidPathException {
        Path path = Paths.get(pathString.replaceAll("([^\\/]*)\\*", ""));
        if (Files.exists(path)) {
            List<LogRecord> recordList = new ArrayList<>();
            String pattern = pathString.substring(pathString.lastIndexOf('/') + 1);
            Pattern regexPattern = Pattern.compile(pattern.replace("*", ".*"),
                                    Pattern.CASE_INSENSITIVE); // превращаем "огрызок" в регулярное выражение
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for (Path entry : stream) {
                    if (regexPattern.matcher(entry.getFileName().toString()).matches()) {
                        recordList.addAll(readFile(entry));
                    }
                }
            }
            return recordList;
        }
        throw new IOException();
    }

    private List<LogRecord> readFile(Path path) throws IOException {
        if (Files.isRegularFile(path)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
                return makeStreamOfRecords(reader);
            }
        }
        throw new IOException();
    }

    private List<LogRecord> readFromURL(String path) throws IOException {
        URL url = new URL(path);
        URLConnection connection = url.openConnection();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return makeStreamOfRecords(reader);
        }
    }

    private List<LogRecord> makeStreamOfRecords(BufferedReader reader) throws IOException {
        List<LogRecord> recordList = new ArrayList<>();
        String recordLine;
        Optional<LogRecord> optLogRecord;
        while ((recordLine = reader.readLine()) != null) {
            optLogRecord = parseLogLine(recordLine);
            optLogRecord.ifPresent(recordList::add);
        }
        return recordList;
    }

    private Optional<LogRecord> parseLogLine(String recordLine) {
        String logPattern = "^(?<remoteAddr>[^ ]+) - (?<remoteUser>[^ ]+) "
            + "\\[(?<dateLocal>[^:]+)[^\\]]+\\] \\\"(?<requestType>[A-Z]+) (?<requestPath>[^ ]+) "
            + "(?<requestProtocol>[^\\\"]+)\\\" (?<status>[1-5]\\d{2}) (?<bodyBytesSent>\\d+) "
            + "\\\"(?<httpReferer>[^\\\"]+)\\\" \\\"(?<httpUserAgent>[^\\\"]+)\\\"$";
        Pattern pattern = Pattern.compile(logPattern);
        Matcher matcher = pattern.matcher(recordLine);

        if (matcher.find()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy", Locale.ENGLISH);
            LocalDate dateLocal = LocalDate.parse(matcher.group("dateLocal"), formatter);
            return Optional.of(new LogRecord(matcher.group("remoteAddr"), dateLocal,
                matcher.group("requestPath"), Integer.parseInt(matcher.group("status")),
                Long.parseLong(matcher.group("bodyBytesSent"))));
        }
        return Optional.empty();
    }
}
