package backendacademy.analyzer;

import backendacademy.analyzer.reportClasses.ReportFormatter;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReportFormatterTest {
    private ReportFormatter formatter;
    private String mdString;
    private String adocString;

    void setUpCorrectArraysSizes() {
        String[][] numbers = {{"one","two"}, {"three", "four"}};
        String[][] ordinalNumbers = {{"first","second"}, {"third", "fourth"}};
        String[][] numbersRus = {{"один","два"}, {"три", "четыре"}};
        String[][] ordinalNumbersRus = {{"первый","второй"}, {"третий", "четвёртый"}};
        formatter = new ReportFormatter(numbers, ordinalNumbers, numbersRus, ordinalNumbersRus);
        mdString = "#### Общая информация\n\n" +
            "| Метрика | Значение |\n" +
            "|:-------:|:--------:|\n" +
            "| one     | two      |\n" +
            "| three   | four     |\n\n" +
            "#### Запрашиваемые ресурсы\n\n" +
            "| Ресурс | Количество |\n" +
            "|:------:|:----------:|\n" +
            "| first  | second     |\n" +
            "| third  | fourth     |\n\n" +
            "#### Коды ответа\n\n" +
            "| Код  | Количество |\n" +
            "|:----:|:----------:|\n" +
            "| один | два        |\n" +
            "| три  | четыре     |\n\n" +
            "#### IP‑адреса источников запросов\n\n" +
            "| IP-адрес | Количество |\n" +
            "|:--------:|:----------:|\n" +
            "| первый   | второй     |\n" +
            "| третий   | четвёртый  |\n\n";
        adocString = "==== Общая информация\n\n" +
            "|====\n" +
            "| Метрика | Значение \n\n" +
            "| one\n" +
            "| two\n\n" +
            "| three\n" +
            "| four\n" +
            "|====\n\n" +
            "==== Запрашиваемые ресурсы\n\n" +
            "|====\n" +
            "| Ресурс | Количество \n\n" +
            "| first\n" +
            "| second\n\n" +
            "| third\n" +
            "| fourth\n" +
            "|====\n\n" +
            "==== Коды ответа\n\n" +
            "|====\n" +
            "| Код | Количество \n\n" +
            "| один\n" +
            "| два\n\n" +
            "| три\n" +
            "| четыре\n" +
            "|====\n\n" +
            "==== IP‑адреса источников запросов\n\n" +
            "|====\n" +
            "| IP-адрес | Количество \n\n" +
            "| первый\n" +
            "| второй\n\n" +
            "| третий\n" +
            "| четвёртый\n" +
            "|====\n\n";
    }

    void setUpIncorrectArraysSizes() {
        String[][] numbers = {{"one","two"}, {"three", "four"}};
        String[][] ordinalNumbers = {{"first"}, {"third", "fourth"}};
        String[][] numbersRus = {{"один","два"}, {}};
        String[][] ordinalNumbersRus = {{"первый","второй"}, {"третий", "четвёртый", "пятый"}};
        formatter = new ReportFormatter(numbers, ordinalNumbers, numbersRus, ordinalNumbersRus);
        mdString = "#### Общая информация\n\n" +
            "| Метрика | Значение |\n" +
            "|:-------:|:--------:|\n" +
            "| one     | two      |\n" +
            "| three   | four     |\n\n" +
            "Ошибка: количество заголовков столбцов не соответствует количеству столбцов данных.\n\n" +
            "Ошибка: количество заголовков столбцов не соответствует количеству столбцов данных.\n\n" +
            "Ошибка: количество заголовков столбцов не соответствует количеству столбцов данных.\n\n";
        adocString = "==== Общая информация\n\n" +
            "|====\n" +
            "| Метрика | Значение \n\n" +
            "| one\n" +
            "| two\n\n" +
            "| three\n" +
            "| four\n" +
            "|====\n\n" +
            "Ошибка: количество заголовков столбцов не соответствует количеству столбцов данных.\n\n" +
            "Ошибка: количество заголовков столбцов не соответствует количеству столбцов данных.\n\n" +
            "Ошибка: количество заголовков столбцов не соответствует количеству столбцов данных.\n\n";
    }

    @Test
    void testFormReportCorrectArraysSizes() {
        setUpCorrectArraysSizes();
        assertEquals(mdString, formatter.formReport("markdown"));
        assertEquals(adocString, formatter.formReport("adoc"));
        assertEquals(mdString, formatter.formReport("wrong input"));
        assertEquals(mdString, formatter.formReport(""));
    }

    @Test
    void testFormReportIncorrectArraysSizes() {
        setUpIncorrectArraysSizes();
        assertEquals(mdString, formatter.formReport("markdown"));
        assertEquals(adocString, formatter.formReport("adoc"));
        assertEquals(mdString, formatter.formReport("wrong input"));
        assertEquals(mdString, formatter.formReport(""));
    }

    @Test
    void testMdFormatCorrectArraysSizes() {
        setUpCorrectArraysSizes();
        assertEquals(mdString, formatter.mdFormat());
    }

    @Test
    void testMdFormatIncorrectArraysSizes() {
        setUpIncorrectArraysSizes();
        assertEquals(mdString, formatter.mdFormat());
    }

    @Test
    void testAdocFormatCorrectArraysSizes() {
        setUpCorrectArraysSizes();
        assertEquals(adocString, formatter.adocFormat());
    }

    @Test
    void testAdocFormatIncorrectArraysSizes() {
        setUpIncorrectArraysSizes();
        assertEquals(adocString, formatter.adocFormat());
    }
}
