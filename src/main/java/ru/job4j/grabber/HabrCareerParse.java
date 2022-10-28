package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private static final int NUM_PAGE = 5;

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private static String retrieveDescription(String link) throws IOException {
        return Jsoup.connect(link).get()
                .select(".collapsible-description")
                .select(".style-ugc")
                .first()
                .text();
    }

    public static void main(String[] args) throws IOException {
        new HabrCareerParse(new HabrCareerDateTimeParser()).list(PAGE_LINK);
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> resultList = new ArrayList<>();
        for (int i = 1; i <= NUM_PAGE; i++) {
            String pageNumber = String.format("%s?page=%s", link, i);
            Document document = Jsoup.connect(pageNumber).get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element dateElement = row.select(".vacancy-card__date").first().child(0);
                String vacancyName = titleElement.text();
                String links = String.format("%s%s", SOURCE_LINK, titleElement.child(0).attr("href"));
                String time = String.format("%s", dateElement.attr("datetime"));
                try {
                   resultList.add(new Post(vacancyName, links, retrieveDescription(links), dateTimeParser.parse(time)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
            return resultList;
    }
}