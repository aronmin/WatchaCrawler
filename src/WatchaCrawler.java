import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import db.DBInsert;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class WatchaCrawler {
    private final WebDriver driver;
    private final List<String> links;
    private final List<String> titles;
    private final List<String> genres;
    private final List<String> directors;
    private final List<String> actors;
    private final List<String> descriptions;
    private final List<String> imgs;

    public WatchaCrawler(WebDriver driver) {
        this.driver = driver;
        this.links = new ArrayList<>();
        titles = new ArrayList<>();
        genres = new ArrayList<>();
        directors = new ArrayList<>();
        actors = new ArrayList<>();
        descriptions = new ArrayList<>();
        imgs = new ArrayList<>();
    }

    private DBInsert dbInsert = new DBInsert();
    int cnt = 0;

    public void crawl(String startUrl) throws InterruptedException, IOException {

        driver.get(startUrl);
        WebElement item = driver.findElement(By.cssSelector(".custom-uvitkv-Cell.etpnybg0"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        while (true) {
            List<WebElement> elements = driver.findElements(By.cssSelector(".custom-uvitkv-Cell.etpnybg0"));
            js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            Thread.sleep(3000);
            List<WebElement> newElements = driver.findElements(By.cssSelector(".custom-uvitkv-Cell.etpnybg0"));
            if (newElements.size() == elements.size()) {
                break;
            }
        }

        Thread.sleep(3000);
        List<WebElement> elements = driver.findElements(By.cssSelector(".custom-uvitkv-Cell.etpnybg0"));
        for (WebElement element : elements) {
            String url = element.findElement(By.tagName("a")).getAttribute("href");
            titles.add(element.findElement(By.tagName("a")).getAttribute("aria-label"));
            links.add(url);
        }
        System.out.println(links.size());

        for (WebElement imgelement : elements){
            imgs.add(imgelement.findElement(By.tagName("img")).getAttribute("src"));
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("WatchaMovies.txt"))){
            for (String link : links) {
                boolean directorFound = false;
                driver.get(link);
                Thread.sleep(2000);
                WebElement moreinfo = driver.findElement(By.cssSelector(".custom-13n4b9j.e1pzg7yt0"));
                moreinfo.click();
                Thread.sleep(2000);

                List<WebElement> DirElements = driver.findElements(By.cssSelector(".custom-19skux8.epptgn96"));
                List<WebElement> GenDirActElements = driver.findElements(By.cssSelector(".epptgn94.custom-1k42ak8.e1tadl9z0"));
                StringBuilder actorText = new StringBuilder();
                for (WebElement DirElement : DirElements) {
                    if (DirElement.getText().equals("감독")) {
                        directorFound = true;
                        directors.add(GenDirActElements.get(1).getText());
                        for (int i = 2; i < GenDirActElements.size(); i++) {
                            actorText.append(GenDirActElements.get(i).getText());
                            if (i < GenDirActElements.size() - 1) {
                                actorText.append(", ");
                            }
                        }
                        break;
                    }
                }
                if (!directorFound) {
                    directors.add("정보없음");
                    for (int i = 1; i < GenDirActElements.size(); i++) {
                        actorText.append(GenDirActElements.get(i).getText());
                        if (i < GenDirActElements.size() - 1) {
                            actorText.append(", ");
                        }
                    }
                }
                actors.add(actorText.toString().trim());
                genres.add(GenDirActElements.get(0).getText());

                WebElement descriptionElement = driver.findElement(By.cssSelector(".custom-121evwe.epptgn93"));
                descriptions.add(descriptionElement.getText());

                System.out.println((cnt+1) + "번째 영화");
                System.out.println("title : " + titles.get(cnt));
                System.out.println("genre : " + genres.get(cnt));
                System.out.println("description : " + descriptions.get(cnt));
                System.out.println("director : " + directors.get(cnt));
                System.out.println("actor : " + actors.get(cnt));
                System.out.println("img : " + imgs.get(cnt));
                System.out.println("url : " + links.get(cnt));
                System.out.println("----------------------------------------------------");

                writer.write((cnt + 1) + "번째 영화\n");
                writer.write("Title : " + titles.get(cnt) + "\n");
                writer.write("Genre : " + genres.get(cnt) + "\n");
                writer.write("Description : " + descriptions.get(cnt) + "\n");
                writer.write("Director : " + directors.get(cnt) + "\n");
                writer.write("Actor : " + actors.get(cnt) + "\n");
                writer.write("Image : " + imgs.get(cnt) + "\n");
                writer.write("URL : " + links.get(cnt) + "\n");
                writer.write("----------------------------------------------------\n");

                try {
                    if (!dbInsert.isDataExists(titles.get(cnt), directors.get(cnt))) {
                        Integer genreId = dbInsert.searchGenreID(genres.get(cnt));
                        if (genreId != null && genreId != -1) {
                            dbInsert.contentInsert(titles.get(cnt), imgs.get(cnt), descriptions.get(cnt).replace("\n", "") + "\n", directors.get(cnt), actors.get(cnt), links.get(cnt), "watcha");
                            dbInsert.genreInsert(dbInsert.searchMovieID("watcha", titles.get(cnt)), genreId, "watcha_genre");
                        }
                    } else {
                        System.out.println("이미 데이터가 존재합니다: " + titles.get(cnt) + ", " + directors.get(cnt));
                    }
                } catch (SQLException e) {
                    System.err.println("DB 삽입 중 오류가 발생했습니다: " + e.getMessage());
                }
                cnt++;
            }
        }
    }
}