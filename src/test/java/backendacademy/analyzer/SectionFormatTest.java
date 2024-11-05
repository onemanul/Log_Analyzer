package backendacademy.analyzer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SectionFormatTest {

    @Test
    public void testMdCorrectSizes() {
        String title = "title";
        String[][] tableTitles = {{"col1", "col2"}};
        String[][] data = {{"one","two"}, {"three", "four"}};
        String expected = "#### title\n\n" +
            "| col1  | col2 |\n" +
            "|:-----:|:----:|\n" +
            "| one   | two  |\n" +
            "| three | four |\n\n";
        assertEquals(expected, SectionFormat.md(title,tableTitles,data));
    }

    @Test
    public void testMdIncorrectSizes() {
        String title = "title";
        String[][] tableTitles = {{"col1", "col2"}};
        String[][] data = {{"one","two"}, {"three", "four", "five"}};
        assertEquals("Ошибка: количество заголовков столбцов не соответствует количеству столбцов данных.\n\n",
            SectionFormat.md(title,tableTitles,data));
    }

    @Test
    public void testMdEmptyTitles() {
        String title = "title";
        String[][] tableTitles = {{}};
        String[][] data = {{"one","two"}, {"three", "four", "five"}};
        assertEquals("Ошибка: количество заголовков столбцов не соответствует количеству столбцов данных.\n\n",
            SectionFormat.md(title,tableTitles,data));
    }

    @Test
    public void testMdEmptyData() {
        String title = "title";
        String[][] tableTitles = {{"col1", "col2"}};
        String[][] data = {{}, {"three", "four"}};
        assertEquals("Ошибка: количество заголовков столбцов не соответствует количеству столбцов данных.\n\n",
            SectionFormat.md(title,tableTitles,data));
    }

    @Test
    void testAdocCorrectSizes() {
        String title = "title";
        String[][] tableTitles = {{"col1", "col2"}};
        String[][] data = {{"one","two"}, {"three", "four"}};
        String expected = "==== title\n\n" +
            "|====\n" +
            "| col1 | col2 \n\n" +
            "| one\n" +
            "| two\n\n" +
            "| three\n" +
            "| four\n" +
            "|====\n\n";
        assertEquals(expected, SectionFormat.adoc(title,tableTitles,data));
    }

    @Test
    public void testAdocIncorrectSizes() {
        String title = "title";
        String[][] tableTitles = {{"col1", "col2"}};
        String[][] data = {{"one","two"}, {"three", "four", "five"}};
        assertEquals("Ошибка: количество заголовков столбцов не соответствует количеству столбцов данных.\n\n",
            SectionFormat.adoc(title,tableTitles,data));
    }

    @Test
    public void testAdocEmptyTitles() {
        String title = "title";
        String[][] tableTitles = {{}};
        String[][] data = {{"one","two"}, {"three", "four", "five"}};
        assertEquals("Ошибка: количество заголовков столбцов не соответствует количеству столбцов данных.\n\n",
            SectionFormat.adoc(title,tableTitles,data));
    }

    @Test
    public void testAdocEmptyData() {
        String title = "title";
        String[][] tableTitles = {{"col1", "col2"}};
        String[][] data = {{}, {"three", "four"}};
        assertEquals("Ошибка: количество заголовков столбцов не соответствует количеству столбцов данных.\n\n",
            SectionFormat.adoc(title,tableTitles,data));
    }
}
