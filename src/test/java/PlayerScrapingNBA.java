import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

class PlayerScrapingNBA {
    WebDriver driver = null;
    private static String gLink = "";
    private static int i = 0;
    private static ArrayList<String> links;

    static {
        try {
            links = getPlayersLinks();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    private void driverinicilization(){
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.get(links.get(i++));
    }

    @Test
    @RepeatedTest(17)
    void Test(){

        String text = "";
        int sum = 0, i = 0, pm3 = 0;
        double average = 0.0;

        for (i = 1; i <= 5; i++) {
            try{
                text = driver.findElement(By.xpath("//*[@id=\"__next\"]/div[2]/section/div[4]/section[2]/div/div/div/table/tbody/tr[" + i + "]/td[9]/a")).getText();
                pm3 = Integer.parseInt(text);
            }catch (NoSuchElementException e){
                pm3 = 0;
            }

            sum += pm3;

            //System.out.print(pm3 + " ");
        }
        //System.out.printf("\n");
        //System.out.println("Sestevek skupaj: "+sum);

        average = (double)sum / (i-1);

        System.out.println("Average (3PM): "+average);

        String name = driver.findElement(By.xpath("//*[@id=\"__next\"]/div[2]/section/div[1]/section[1]/div[2]/div/div[2]/div[1]")).getText();

        System.out.println(name);

        System.out.println(average < 1 ? "Failed" : "Passed");

        driver.quit();
        assertTrue("Previous (" + average + ") should be greater than 1", average >= 1);
    }

    // Returning NBA players links from sportsdata API
    private static ArrayList<String> getPlayersLinks() throws IOException {
        ArrayList<String> idLinks = new ArrayList();
        URL url = new URL("https://api.sportsdata.io/v3/nba/scores/json/Players/DAL?key=58957574730c4ee1b809da2f53525997");
        JSONArray json = new JSONArray(IOUtils.toString(url, Charset.forName("UTF-8")));

        for(int i = 0; i < json.length(); ++i) {
            JSONObject obj = json.getJSONObject(i);
            idLinks.add("https://www.nba.com/player/" + Long.toString(obj.getLong("NbaDotComPlayerID")));
        }
        return idLinks;
    }
}