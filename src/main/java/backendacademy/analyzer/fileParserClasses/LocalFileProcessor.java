package backendacademy.analyzer.fileParserClasses;

import backendacademy.analyzer.LogRecord;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LocalFileProcessor {
    private LocalFileProcessor() {}

    public static List<LogRecord> read(String pathString) throws IOException {
        LocalFileProcessor lfp = new LocalFileProcessor();
        return lfp.readFromLocalFile(pathString);
    }

    private List<LogRecord> readFromLocalFile(String pathString) throws IOException {
        if (pathString.contains("*")) {         // путь - шаблон
            return readMask(pathString);
        }
        Path path = Paths.get(pathString);
        if (Files.isRegularFile(path)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
                return LogParser.makeRecordList(reader);
            }
        } else if (Files.isDirectory(path)) {      // путь - директория
            List<LogRecord> recordList = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for (Path entry : stream) {
                    if (Files.isRegularFile(entry)) {
                        try (BufferedReader reader = new BufferedReader(new FileReader(entry.toFile()))) {
                            recordList.addAll(LogParser.makeRecordList(reader));
                        } catch (IOException e) { }
                    }
                }
            }
            return recordList;
        }
        throw new IOException();
    }

    private List<LogRecord> readMask(String pathString) throws IOException {
        List<LogRecord> recordList = new ArrayList<>();
        String rootDir = pathString.contains("**") ? pathString.substring(0, pathString.indexOf("**"))
            : pathString.substring(0, pathString.lastIndexOf('/')); // корневая директория
        String filePattern = pathString.substring(pathString.lastIndexOf('/') + 1);
        Files.walk(Paths.get(rootDir))
            .filter(path -> {
                if (Files.isRegularFile(path)) {
                    if (pathString.contains("**")) {
                        return path.getFileName().toString().matches(convertGlobToRegex(filePattern));
                    } else {
                        return path.getParent().equals(Paths.get(rootDir))
                            && path.getFileName().toString().matches(convertGlobToRegex(filePattern));
                    }
                }
                return false;
            })
            .forEach(path -> {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(path.toFile()));
                    recordList.addAll(LogParser.makeRecordList(reader));
                } catch (IOException e) { }
            });
        return recordList;
    }

    private String convertGlobToRegex(String glob) {
        String regex = glob.replace("**", ".*")
            .replace("*", "[^/]*")
            .replace(".", "\\.")
            .replace("?", ".");
        return "^" + regex + "$";
    }
}
