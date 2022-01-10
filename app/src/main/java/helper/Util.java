package helper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Util {

    public static String shareChatUrlExtractor(Document document) {

        Document scDocument = null;
        String videoUrl = document.select("meta[property=\"og:video:secure_url\"]")
                .last().attr("content");
        return videoUrl;

    }

    public static void main(String[] args) {

    }
}


