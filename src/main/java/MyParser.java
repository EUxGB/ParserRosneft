
import com.fasterxml.jackson.core.JsonToken;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyParser {
    public static void main(String[] args) throws IOException, GeneralSecurityException {

        List<Article> articles = getArticleList();
        articles = extracted(articles);
        addInSheet(articles);

    }

    private static void addInSheet(List<Article> articles) throws IOException, GeneralSecurityException {

        SheetsIntegration.sheetsService = SheetsIntegration.getSheetsService();

        for (int i = 0; i < articles.size(); i++) {


            ValueRange appendBody = new ValueRange()
                    .setValues(Arrays.asList(
                            Arrays.asList(articles.get(i).number
                                    , articles.get(i).name
                                    , articles.get(i).client
                                    , articles.get(i).okved
                                    , articles.get(i).dataStart
                                    , articles.get(i).dataEnd
                                    , articles.get(i).myUrl
                            )
                    ));

            AppendValuesResponse appendResult = SheetsIntegration.sheetsService.spreadsheets().values()
                    .append(SheetsIntegration.SPREDSHEET_ID, "tender", appendBody)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("INSERT_ROWS")
                    .setIncludeValuesInResponse(true)
                    .execute();
        }
    }

    //исключить имеющиеся значения
    private static List<Article> extracted(List<Article> articles) throws IOException, GeneralSecurityException {
        SheetsIntegration.sheetsService = SheetsIntegration.getSheetsService();
        String range = "tender!A1:A1000";

        ValueRange response = SheetsIntegration.sheetsService.spreadsheets().values()
                .get(SheetsIntegration.SPREDSHEET_ID, range)
                .execute();

        List<List<Object>> values = response.getValues();
        List<String> numberOfTendersSheets = new ArrayList<>();
        List<Article> newarticles = new ArrayList<>();

        if (values == null || values.isEmpty()) {
            System.out.println("No data found");

        } else {
            for (List row : values) {
                numberOfTendersSheets.add((String) row.get(0));
            }
            if (articles != null && articles.size() > 0) {
                for (int i = 0; i < articles.size(); i++) {
                    if (!numberOfTendersSheets.contains(articles.get(i).number)) {
                        newarticles.add(articles.get(i));
                    }
                }
            }
        }
        return newarticles;
    }

    private static List<Article> getArticleList() throws IOException {
        String date = LocalDate.now().plusDays(0).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        System.out.println(date);
        String urlRosneft =
                "https://www.tektorg.ru/rosneft/procedures?q=%D0%BA%D0%B0%D0%B1%D0%B5%D0%BB&defrom=" + date + "&lang=ru&sort=relevance&order=desc&limit=500";
        List<Article> articles = new ArrayList<>();

        Document document = Jsoup.connect(urlRosneft).get();

        Elements section_prcts = document.getElementsByAttributeValue("class", "section-procurement__item");

        Elements elementstest = document.getElementsByAttributeValue("class", "section-procurement__item-information");


        section_prcts.forEach(section_prct -> {

            Element a1Element = section_prct.child(0);
            String zakup = a1Element.child(0).text();
            String number = zakup.substring(28, 38);
            String name = zakup.substring(39);

            a1Element = section_prct.child(1);
            String client = a1Element.child(1).text();

            a1Element = section_prct.child(2);
            String okved = a1Element.child(1).text();


            a1Element = section_prct.child(4);
            String dataStart = a1Element.child(0).text().substring(27, 43);

            a1Element = section_prct.child(4);
            String dataEnd = a1Element.child(1).text().substring(57, 73);

            String myUrl = "www.tektorg.ru" + section_prct.getElementsByTag("a").get(0).attr("href");

            articles.add(new Article(number, name, client, okved, dataStart, dataEnd, myUrl));

        });
        articles.forEach(System.out::println);
        return articles;
    }
}
