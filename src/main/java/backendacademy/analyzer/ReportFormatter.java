package backendacademy.analyzer;

import java.io.PrintStream;
//CHECKSTYLE:OFF
public class ReportFormatter {
    private final String[][] generalInfo;
    private final String[][] requestedResources;
    private final String[][] responseCodes;
    private final String[][] addressesRequestSources;
    private final String titleGeneralInfo = "Общая информация";
    private final String titleRequestedResources = "Запрашиваемые ресурсы";
    private final String titleResponseCodes = "Коды ответа";
    private final String titleAddressesRequestSources = "IP‑адреса источников запросов";
    private final String[][] tableTitlesGeneralInfo = {{"Метрика", "Значение"}};
    private final String[][] tableTitlesRequestedResources = {{"Ресурс", "Количество"}};
    private final String[][] tableTitlesResponseCodes = {{"Код", "Количество"}};
    private final String[][] tableTitlesAddressesRequestSources = {{"IP-адрес", "Количество"}};
    private final PrintStream output = System.out;

    public ReportFormatter(
        String[][] generalInfo,
        String[][] requestedResources,
        String[][] responseCodes,
        String[][] addressesRequestSources
    ) {
        this.generalInfo = generalInfo;
        this.requestedResources = requestedResources;
        this.responseCodes = responseCodes;
        this.addressesRequestSources = addressesRequestSources;
    }

    public String formReport(String format) {
        if (!format.equals("markdown") && !format.equals("adoc")) {
            output.println("Ошибка в названии формата отчёта. Отчёт будет сформирован в формате markdown.");
            return mdFormat();
        } else if (format.equals("markdown")) {
            return mdFormat();
        } else {
            return adocFormat();
        }
    }

    private String mdFormat() {
        return mdSection(titleGeneralInfo, tableTitlesGeneralInfo, generalInfo)
            + mdSection(titleRequestedResources, tableTitlesRequestedResources, requestedResources)
            + mdSection(titleResponseCodes, tableTitlesResponseCodes, responseCodes)
            + mdSection(titleAddressesRequestSources, tableTitlesAddressesRequestSources, addressesRequestSources);
    }

    private String adocFormat() {
        return adocSection(titleGeneralInfo, tableTitlesGeneralInfo, generalInfo)
            + adocSection(titleRequestedResources, tableTitlesRequestedResources, requestedResources)
            + adocSection(titleResponseCodes, tableTitlesResponseCodes, responseCodes)
            + adocSection(titleAddressesRequestSources, tableTitlesAddressesRequestSources, addressesRequestSources);
    }

    private String mdSection(String title, String[][] tableTitles, String[][] data) {
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

    private String adocSection(String title, String[][] tableTitles, String[][] data) {
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
}
