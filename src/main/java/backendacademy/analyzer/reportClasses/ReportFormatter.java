package backendacademy.analyzer.reportClasses;

import java.io.PrintStream;

public class ReportFormatter {
    //CHECKSTYLE:OFF
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
    private static final String MARKDOWN = "markdown";
    private static final String ADOC = "adoc";
    //CHECKSTYLE:ON

    public ReportFormatter(
        String[][] generalInfo,
        String[][] requestedResources,
        String[][] responseCodes,
        String[][] addressesRequestSources
    )  {
        this.generalInfo = generalInfo;
        this.requestedResources = requestedResources;
        this.responseCodes = responseCodes;
        this.addressesRequestSources = addressesRequestSources;
    }

    public String formReport(String format) {
        if (!format.equals(MARKDOWN) && !format.equals(ADOC)) {
            output.println("Ошибка в названии формата отчёта. Отчёт будет сформирован в формате markdown.");
            return mdFormat();
        } else if (format.equals(MARKDOWN)) {
            return mdFormat();
        } else {
            return adocFormat();
        }
    }

    public String mdFormat() {
        return SectionFormat.md(titleGeneralInfo, tableTitlesGeneralInfo, generalInfo)
            + SectionFormat.md(titleRequestedResources, tableTitlesRequestedResources, requestedResources)
            + SectionFormat.md(titleResponseCodes, tableTitlesResponseCodes, responseCodes)
            + SectionFormat.md(titleAddressesRequestSources, tableTitlesAddressesRequestSources,
                                                                                            addressesRequestSources);
    }

    public String adocFormat() {
        return SectionFormat.adoc(titleGeneralInfo, tableTitlesGeneralInfo, generalInfo)
            + SectionFormat.adoc(titleRequestedResources, tableTitlesRequestedResources, requestedResources)
            + SectionFormat.adoc(titleResponseCodes, tableTitlesResponseCodes, responseCodes)
            + SectionFormat.adoc(titleAddressesRequestSources, tableTitlesAddressesRequestSources,
                                                                                            addressesRequestSources);
    }
}
