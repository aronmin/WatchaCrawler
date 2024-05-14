import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "util/driver/chromedriver.exe");
        WebDriver driver = new ChromeDriver();

        WatchaCrawler crawler = new WatchaCrawler(driver);

        String startUrl = "https://watcha.com/staffmades/3608";

        try {
            crawler.crawl(startUrl);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            driver.quit();
        }
    }
}