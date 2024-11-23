package backendacademy.analyzer.reportClasses;

import backendacademy.analyzer.LogAnalyze;
import backendacademy.analyzer.fileParserClasses.LogParser;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import static org.junit.jupiter.api.Assertions.*;

class LogReportTest {
    LogAnalyze analyze = new LogAnalyze(null, null);
    String md;
    String adoc;

    void setUp() throws IOException {
        String logRecords = "54.84.255.104 - - [20/May/2010:07:05:24 +0000] \"GET /downloads/product_1 HTTP/1.1\" 100 333 \"-\" \"Debian APT-HTTP/1.3 (1.0.1ubuntu2)\"\n" +
            "172.29.141.101 - - [20/May/2012:07:05:54 +0000] \"GET /downloads/product_2 HTTP/1.1\" 404 444 \"-\" \"Debian APT-HTTP/1.3 (0.9.7.9)\"\n" +
            "78.108.122.1 - - [20/May/2015:08:05:41 +0000] \"GET /downloads/product_2 HTTP/1.1\" 200 555 \"-\" \"Chef Knife/11.10.4 (ruby-1.9.3-p484; ohai-6.20.0; x86_64-linux; +http://opscode.com)\"\n" +
            "184.73.132.8 - - [20/May/2017:08:05:30 +0000] \"GET /downloads/product_1 HTTP/1.1\" 300 666 \"-\" \"Chef Client/11.4.4 (ruby-1.9.3-p286; ohai-6.16.0; x86_64-linux; +http://opscode.com)\"\n" +
            "148.251.2.47 - - [20/May/2019:08:05:15 +0000] \"GET /downloads/product_2 HTTP/1.1\" 200 777 \"-\" \"Wget/1.13.4 (linux-gnu)\"";
        LogParser.addRecordsToAnalyze(new BufferedReader(new StringReader(logRecords)), analyze);
        analyze.finishAnalyze();
        md = "#### Общая информация\n\n" +
            "| Метрика               | Значение |\n" +
            "|:---------------------:|:--------:|\n" +
            "| Файл(-ы)              | path     |\n" +
            "| Начальная дата        | -        |\n" +
            "| Конечная дата         | -        |\n" +
            "| Количество запросов   | 5        |\n" +
            "| Средний размер ответа | 555b     |\n" +
            "| 50p размера ответа    | 555b     |\n" +
            "| 95p размера ответа    | 777b     |\n" +
            "\n" +
            "#### Запрашиваемые ресурсы\n" +
            "\n" +
            "| Ресурс               | Количество |\n" +
            "|:--------------------:|:----------:|\n" +
            "| /downloads/product_2 | 3          |\n" +
            "| /downloads/product_1 | 2          |\n" +
            "\n" +
            "#### Коды ответа\n" +
            "\n" +
            "| Код | Количество |\n" +
            "|:---:|:----------:|\n" +
            "| 200 | 2          |\n" +
            "| 100 | 1          |\n" +
            "| 404 | 1          |\n" +
            "\n" +
            "#### IP‑адреса источников запросов\n" +
            "\n" +
            "| IP-адрес       | Количество |\n" +
            "|:--------------:|:----------:|\n" +
            "| 54.84.255.104  | 1          |\n" +
            "| 172.29.141.101 | 1          |\n" +
            "| 78.108.122.1   | 1          |\n\n";
        adoc = "==== Общая информация\n\n" +
            "|====\n" +
            "| Метрика | Значение \n" +
            "\n" +
            "| Файл(-ы)\n" +
            "| path\n" +
            "\n" +
            "| Начальная дата\n" +
            "| -\n" +
            "\n" +
            "| Конечная дата\n" +
            "| -\n" +
            "\n" +
            "| Количество запросов\n" +
            "| 5\n" +
            "\n" +
            "| Средний размер ответа\n" +
            "| 555b\n" +
            "\n" +
            "| 50p размера ответа\n" +
            "| 555b\n" +
            "\n" +
            "| 95p размера ответа\n" +
            "| 777b\n" +
            "|====\n" +
            "\n" +
            "==== Запрашиваемые ресурсы\n" +
            "\n" +
            "|====\n" +
            "| Ресурс | Количество \n" +
            "\n" +
            "| /downloads/product_2\n" +
            "| 3\n" +
            "\n" +
            "| /downloads/product_1\n" +
            "| 2\n" +
            "|====\n" +
            "\n" +
            "==== Коды ответа\n" +
            "\n" +
            "|====\n" +
            "| Код | Количество \n" +
            "\n" +
            "| 200\n" +
            "| 2\n" +
            "\n" +
            "| 100\n" +
            "| 1\n" +
            "\n" +
            "| 404\n" +
            "| 1\n" +
            "|====\n" +
            "\n" +
            "==== IP‑адреса источников запросов\n" +
            "\n" +
            "|====\n" +
            "| IP-адрес | Количество \n" +
            "\n" +
            "| 54.84.255.104\n" +
            "| 1\n" +
            "\n" +
            "| 172.29.141.101\n" +
            "| 1\n" +
            "\n" +
            "| 78.108.122.1\n" +
            "| 1\n" +
            "|====\n\n";
    }

    @Test
    public void testReport_withoutAnalysis() {
        assertEquals("Нет данных для составления отчёта.", LogReport.report(analyze, "path", "format"));
    }

    @Test
    public void testReport_withAnalysis() throws IOException {
        setUp();
        String mdReport = LogReport.report(analyze, "path", "markdown");
        String adocReport = LogReport.report(analyze, "path", "adoc");
        assertEquals(md, mdReport);
        assertEquals(adoc, adocReport);
    }

}
