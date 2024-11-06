package backendacademy.analyzer.reportClasses;

public class SectionFormat {
    private SectionFormat() {}

    //CHECKSTYLE:OFF
    public static String md(String title, String[][] tableTitles, String[][] data) {
        if (!correctSizesOfTitlesAndData(tableTitles, data)) {
            return "Ошибка: количество заголовков столбцов не соответствует количеству столбцов данных.\n\n";
        }
        StringBuilder section = new StringBuilder();
        section.append("#### ").append(title).append("\n\n");
        int[] columnWidths = new int[tableTitles[0].length];
        for (String[] row : data) {                             // Определение ширины столбцов
            for (int i = 0; i < row.length; i++) {
                columnWidths[i] = Math.max(columnWidths[i], tableTitles[0][i].length());
                columnWidths[i] = Math.max(columnWidths[i], row[i].length());
            }
        }
        for (int i = 0; i < tableTitles[0].length; i++) {       // Заголовки таблицы
            section.append(String.format("| %-" + columnWidths[i] + "s ", tableTitles[0][i]));
        }
        section.append("|\n");
        for (int width : columnWidths) {                        // Добавление разделителя
            section.append("|:").append("-".repeat(width)).append(":");
        }
        section.append("|\n");
        for (String[] datum : data) {                 // Данные
            for (int j = 0; j < datum.length; j++) {
                section.append(String.format("| %-" + columnWidths[j] + "s ", datum[j]));
            }
            section.append("|\n");
        }
        section.append("\n");
        return section.toString();
    }

    public static String adoc(String title, String[][] tableTitles, String[][] data) {
        if (!correctSizesOfTitlesAndData(tableTitles, data)) {
            return "Ошибка: количество заголовков столбцов не соответствует количеству столбцов данных.\n\n";
        }
        StringBuilder section = new StringBuilder();
        section.append("==== ").append(title).append("\n\n");   // Название таблицы
        section.append("|====\n");                              // "Открытие" таблицы
        for (int i = 0; i < tableTitles[0].length; i++) {       // Заголовки таблицы
            section.append("| ").append(tableTitles[0][i]).append(" ");
        }
        section.append("\n");
        for (String[] datum : data) {                 // Данные
            for (String s : datum) {
                section.append("\n| ").append(s);
            }
            section.append("\n");
        }
        section.append("|====\n\n");    // "закрытие" таблицы
        return section.toString();
    }
    //CHECKSTYLE:ON

    private static boolean correctSizesOfTitlesAndData(String[][] tableTitles, String[][] data) {
        for (String[] row : data) {
            if (row.length != tableTitles[0].length || row.length == 0) {
                return false;
            }
        }
        return true;
    }
}
