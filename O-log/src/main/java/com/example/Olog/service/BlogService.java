package com.example.Olog.service;

import com.example.Olog.dto.PostUrlReq;
import com.example.Olog.dto.PostUrlRes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BlogService {
    private WebDriver driver;
    private String baseUrl = "https://map.naver.com/v5/search/%EC%88%A0%EC%9D%B8/place/1046678640?placePath=%3Fentry=pll%26from=nx%26fromNxList=true&c=15,0,0,0,dh";

    public PostUrlRes getBlogList(PostUrlReq postUrlReq) throws InterruptedException {
        String url = postUrlReq.getUrl();
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\codus\\Desktop\\O-log_server\\O-log\\src\\main\\java\\com\\example\\Olog\\util\\chromedriver\\chromedriver.exe");
        //크롬 드라이버 셋팅 (드라이버 설치한 경로 입력)
        driver = new ChromeDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        PostUrlRes postUrlRes = new PostUrlRes();
        driver.get(url);
        Thread.sleep(1000);
        driver.switchTo().frame("entryIframe");

        WebElement store = driver.findElement(By.xpath("/html/body/div[3]/div/div/div/div[2]/div[1]/div[1]/span[2]"));
        postUrlRes.setName(store.getText());
        System.out.println("가게명: " + store.getText());

        WebElement storeType = driver.findElement(By.xpath("/html/body/div[3]/div/div/div/div[2]/div[1]/div[1]/span[3]"));
        postUrlRes.setStore_type(storeType.getText());
        System.out.println("업종: " + storeType.getText());

        WebElement place = driver.findElement(By.xpath("/html/body/div[3]/div/div/div/div[6]/div/div[2]/div/div/div[1]/div/a/span[1]"));
        postUrlRes.setPlace(place.getText());
        String city = place.getText().split(" ")[1].split("구")[0];
        System.out.println("주소: " + place.getText());
        System.out.println("위치: " + city);
        // postUrlRes.setCity(city);

        WebElement reviewButton = driver.findElement(By.xpath("/html/body/div[3]/div/div/div/div[5]/div/div/div/div/a[4]/span"));
        reviewButton.click();

        // 더보기 버튼
        while (true) {
            try {
                driver.findElement(By.tagName("body")).sendKeys(Keys.END);
                Thread.sleep(3000);
                driver.findElement(By.cssSelector(".lfH3O")).click();
                Thread.sleep(3000);
                driver.findElement(By.tagName("body")).sendKeys(Keys.END);
            } catch (NoSuchElementException e) {
                System.out.println("---더보기 버튼 클릭 완료---");
                break;
            }
        }

        /*
         글 리뷰
         */
        List<WebElement> reviewList = driver.findElements(By.xpath("//*[@id=\"app-root\"]/div/div/div/div[7]/div[3]/div[3]/div/ul/li"));
        int reviewLen = reviewList.size();
        // 더보기 없는 글 리뷰
        List<String> reviews = new ArrayList<>();
        for (int i = 1; i <= reviewLen; i++) {
            WebElement element;
            // 단일 리뷰
            try {
                element = driver.findElement(By.xpath("//*[@id=\"app-root\"]/div/div/div/div[7]/div[3]/div[3]/div[1]/ul/li[" + i + "]/div[3]/a/span[1]"));
            } catch (NoSuchElementException e) {
                element = driver.findElement(By.xpath("//*[@id=\"app-root\"]/div/div/div/div[7]/div[3]/div[3]/div[1]/ul/li[" + i + "]/div[2]/a/span"));
            }
            if (!element.getText().equals("개의 리뷰가 더 있습니다")) {
                reviews.add(element.getText());
            }
        }
        postUrlRes.setReview_list(reviews);
        System.out.println(reviews);

        /*
        사진 리뷰
         */
        List<String> photoList = new ArrayList<>();
        System.out.println("사진 개수 : " + driver.findElements(By.xpath("//*[@id=\"app-root\"]/div/div/div/div[7]/div[3]/div[3]/div[1]/ul/li[7]/div[2]/div/div/div/div[1]/a/div")).size());
        for(int i = 0; i < reviewLen; i++){
            try {
                    String photo = driver.findElement(By.xpath("//*[@id=\"app-root\"]/div/div/div/div[7]/div[3]/div[3]/div[1]/ul/li["+i+"]/div[2]/div/div/div/div[1]/a/div")).getCssValue("background-image").split("\"")[1].split("\"")[0];
                    photoList.add(photo);
                } catch (NoSuchElementException e){
                    String photo = "";
                    photoList.add(photo);
                }
            }
        postUrlRes.setImage_list(photoList);
        System.out.println("사진 리뷰 : " + photoList);
        driver.close();    //탭 닫기
        driver.quit();    //브라우저 닫기
        return postUrlRes;
    }
}
