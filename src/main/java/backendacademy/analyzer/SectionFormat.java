package backendacademy.analyzer;

public class SectionFormat {
    private SectionFormat() {}

    //CHECKSTYLE:OFF
    public static String md(String title, String[][] tableTitles, String[][] data) {
        if (!correctSizesOfTitlesAndData(tableTitles, data)) {
            return "Ошибка: количество заголовков столбцов не соответствует количеству столбцов данных.\n\n";
        }
        StringBuilder section = new StringBuilder();
        section.append("#### ").append(title).append("\n\n");
        // Определяем ширину столбцов
        int[] columnWidths = new int[tableTitles[0].length];
        for (String[] row : data) {
            for (int i = 0; i < row.length; i++) {
                columnWidths[i] = Math.max(columnWidths[i], tableTitles[0][i].length());
                columnWidths[i] = Math.max(columnWidths[i], row[i].length());
            }
        }
        // Добавляем заголовок таблицы
        for (int i = 0; i < tableTitles[0].length; i++) {
            section.append(String.format("| %-" + columnWidths[i] + "s ", tableTitles[0][i]));
        }
        section.append("|\n");
        // Добавляем разделитель
        for (int width : columnWidths) {
            section.append("|:").append("-".repeat(width)).append(":");
        }
        section.append("|\n");
        // Добавляем данные
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                section.append(String.format("| %-" + columnWidths[j] + "s ", data[i][j]));
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
        section.append("==== ").append(title).append("\n\n");
        // "Открывание" таблицы
        section.append("|====\n");
        // Добавляем заголовок таблицы
        for (int i = 0; i < tableTitles[0].length; i++) {
            section.append("| ").append(tableTitles[0][i]).append(" ");
        }
        section.append("\n");
        // Добавляем данные
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                section.append("\n| ").append(data[i][j]);
            }
            section.append("\n");
        }
        section.append("|====\n\n"); // "закрытие" таблицы
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
