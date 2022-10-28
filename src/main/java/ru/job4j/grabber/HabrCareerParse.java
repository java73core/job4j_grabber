package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class HabrCareerParse {

    private static  final int NUM_PAGE = 5;

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    private static String retrieveDescription(String link) throws IOException {
        return Jsoup.connect(link).get()
                .select(".collapsible-description")
                .select(".style-ugc")
                .first()
                .text();
    }

    public static void main(String[] args) throws IOException {
        for (int i = 1; i <= NUM_PAGE; i++) {
            String pageNumber = String.format("%s?page=%s", PAGE_LINK, i);
            Document document = Jsoup.connect(pageNumber).get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                Element dateElement = row.select(".vacancy-card__date").first().child(0);
                String vacancyName = titleElement.text();
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                String time = String.format("%s", dateElement.attr("datetime"));
                try {
                    System.out.printf("%s %s %s%n %s%n", vacancyName, link, time, retrieveDescription(link));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}