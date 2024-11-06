package backendacademy.analyzer.fileParserClasses;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

/*
root
├── LOG
│   ├── logs
│   │   └── logers_7.txt
│   ├── loges_3.txt
│   └── logs_5.txt
└── log_1.txt
 */

class LocalFileProcessorTest {
    @Test
    public void testGetLogRecords_LocalFile() throws Exception {
        String path = "log_1.txt";
        assertEquals(1, LocalFileProcessor.read(path).size());
        String wrongPath = "no.txt";                            // файл не существует
        assertThrows(IOException.class, () -> LocalFileProcessor.read(wrongPath));
    }

    @Test
    public void testGetLogRecords_LocalDirectory() throws Exception {
        String path = "LOG/";                       // в папке два файла
        assertEquals(3+5, LocalFileProcessor.read(path).size());
        path = "LOG/logs";                          // в папке один файл
        assertEquals(7, LocalFileProcessor.read(path).size());
        String wrongPath = "LOG/logs/loggers";      // папки не существует
        assertThrows(IOException.class, () -> LocalFileProcessor.read(wrongPath));
    }

    @Test
    public void testGetLogRecords_MaskOfFile() throws Exception {
        String path = "LOG/*3.txt";         // один файл подходит
        assertEquals(3, LocalFileProcessor.read(path).size());
        path = "LOG/*.txt";                 // два файла подходят
        assertEquals(3+5, LocalFileProcessor.read(path).size());
        path = "LOG/log*";                  // два файла подходят
        assertEquals(3+5, LocalFileProcessor.read(path).size());
        path = "LOG/*.pdf";                 // нет подходящих файлов
        assertEquals(0, LocalFileProcessor.read(path).size());
    }

    @Test
    public void testGetLogRecords_MaskOfDirectory() throws Exception {
        String path = "LOG/**/logers_7.txt";
        assertEquals(7, LocalFileProcessor.read(path).size());
        path = "LOG/**/logs_5.txt";
        assertEquals(5, LocalFileProcessor.read(path).size());
        path = "LOG/**/no.txt";                     // файл не существует
        assertEquals(0, LocalFileProcessor.read(path).size());
    }

    @Test
    public void testGetLogRecords_MaskOfFileAndDirectory() throws Exception {
        String path = "LOG/**/*.txt";
        assertEquals(3+5+7, LocalFileProcessor.read(path).size());
        path = "LOG/**/log*";
        assertEquals(3+5+7, LocalFileProcessor.read(path).size());
        path = "LOG/**/loge*.txt";
        assertEquals(3+7, LocalFileProcessor.read(path).size());
        path = "LOG/**/no.txt";                     // файл не существует
        assertEquals(0, LocalFileProcessor.read(path).size());
    }

    @Test
    public void testGetLogRecords_InvalidPath() throws Exception {
        String path = "path";
        assertThrows(IOException.class, () -> LocalFileProcessor.read(path));
    }
}
