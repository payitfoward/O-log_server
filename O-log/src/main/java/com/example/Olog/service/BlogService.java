package com.example.Olog.service;

import com.example.Olog.dto.PostClientRes;
import com.example.Olog.dto.PostTextRes;
import com.example.Olog.dto.PostUrlReq;
import com.example.Olog.dto.PostUrlRes;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
public class BlogService {
    private WebDriver driver;
    private String baseUrl = "https://map.naver.com/v5/search/%EC%88%A0%EC%9D%B8/place/1046678640?placePath=%3Fentry=pll%26from=nx%26fromNxList=true&c=15,0,0,0,dh";
//    @Value("${flask.flaskUrl}")
    private String flaskUrl = "";

//    public BlogService (String flaskUrl){
//        this.flaskUrl = flaskUrl;
//    }
    public PostClientRes requestToFastAPI(PostUrlRes postUrlRes) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        // Header set
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        // Body set **
        Map<String, Object> body = new HashMap<>();
        body.put("name", postUrlRes.getName());
        body.put("place", postUrlRes.getCity());
        body.put("store_type", postUrlRes.getStoreType());
        body.put("review_list", postUrlRes.getReviewList());
        body.put("image_list", postUrlRes.getImageList());
        log.info(body.toString());
        // Message
        HttpEntity<Map<String, Object>> requestMessage = new HttpEntity<>(body, httpHeaders);
        // Request
        HttpEntity<String> response = restTemplate.postForEntity(flaskUrl, requestMessage, String.class);

        // Response 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        PostTextRes dto = objectMapper.readValue(response.getBody(), PostTextRes.class);

        PostClientRes postClientRes = new PostClientRes();
        for(int i = 0; i < dto.getText().size(); i++){
            log.info(dto.getText().get(i));
        }
        postClientRes.setTitle(dto.getText().get(0));
        List<String> textList = new ArrayList<>();
        for(int i = 1; i < dto.getText().size(); i++) {
            textList.add(dto.getText().get(i));
        }
        postClientRes.setDesc(textList);
        postClientRes.setText(dto.getText());
        postClientRes.setStore(postUrlRes.getName());
        postClientRes.setCategory(postUrlRes.getStoreType());
        postClientRes.setCity(postUrlRes.getCity());

        Random ran = new Random();
        List<String> imageList = new ArrayList<>();
        int[] image = new int[2];
        for(int i = 0; i < 2; i++) {
            if(postUrlRes.getImageList().size() > 0) {
                image[i] = ran.nextInt(postUrlRes.getImageList().size() - 1);
            }
            if(image[1] == image[0]) {
                i--;
            }
        }
        for(int i = 0; i < image.length; i++){
            imageList.add(postUrlRes.getImageList().get(i));
        }
        postClientRes.setImg(imageList);
        return postClientRes;
    }
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

        Thread.sleep(1000);

        WebElement store = driver.findElement(By.cssSelector("#_title > span.Fc1rA"));
        postUrlRes.setName(store.getText());
        System.out.println("가게명: " + store.getText());

        WebElement storeType = driver.findElement(By.cssSelector("#_title > span.DJJvD"));
        postUrlRes.setStoreType(storeType.getText());
        System.out.println("업종: " + storeType.getText());

        WebElement place = driver.findElement(By.xpath("/html/body/div[3]/div/div/div/div[6]/div/div[2]/div/div/div[1]/div/a/span[1]"));
        String city = place.getText().split(" ")[1].split("구")[0];
        postUrlRes.setCity(city);
        System.out.println("주소: " + place.getText());
        System.out.println("지역구: " + city);
        postUrlRes.setPlace(place.getText());

        List<WebElement> buttons = driver.findElements(By.xpath("//*[@id=\"app-root\"]/div/div/div/div[5]/div/div/div/div/a"));
        WebElement reviewButton;
        if (buttons.size() == 4)
        {
            reviewButton = driver.findElement(By.cssSelector("#app-root > div > div > div > div.place_fixed_maintab > div > div > div > div > a:nth-child(3) > span"));
        }
        else if (buttons.size() == 5){
            reviewButton = driver.findElement(By.cssSelector("#app-root > div > div > div > div.place_fixed_maintab > div > div > div > div > a:nth-child(4) > span"));
        }
        else {
            reviewButton = driver.findElement(By.cssSelector("#app-root > div > div > div > div.place_fixed_maintab > div > div > div > div > a:nth-child(5) > span"));
        }
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
                try{
                    // 더보기 X
                    element = driver.findElement(By.xpath("/html/body/div[3]/div/div/div/div[6]/div[3]/div[3]/div[1]/ul/li["+i+"]/div[2]/a/span[1]"));
                } catch (NoSuchElementException n){
                    // 더보기 O
                    element = driver.findElement(By.xpath("/html/body/div[3]/div/div/div/div[7]/div[3]/div[3]/div[1]/ul/li["+i+"]/div[2]/a/span"));
                }
            } catch (NoSuchElementException e) {
                // 그림 리뷰
                element = driver.findElement(By.xpath("/html/body/div[3]/div/div/div/div[7]/div[3]/div[3]/div[1]/ul/li["+i+"]/div[3]/a/span[1]"));
            }
            if (!element.getText().equals("개의 리뷰가 더 있습니다")) {
                reviews.add(element.getText());
            }
        }
        postUrlRes.setReviewList(reviews);
        System.out.println(reviews);

        /*
        사진 리뷰
         */
        List<String> photoList = new ArrayList<>();
        System.out.println("사진 개수 : " + driver.findElements(By.xpath("//*[@id=\"app-root\"]/div/div/div/div[7]/div[3]/div[3]/div[1]/ul/li[7]/div[2]/div/div/div/div[1]/a/div")).size());
        for(int i = 1; i <= reviewLen; i++){
            try {
                String photo = driver.findElement(By.xpath("//*[@id=\"app-root\"]/div/div/div/div[7]/div[3]/div[3]/div[1]/ul/li["+i+"]/div[2]/div/div/div/div[1]/a/div")).getCssValue("background-image").split("\"")[1].split("\"")[0];
                photoList.add(photo);
            } catch (NoSuchElementException e){
                String photo = "";
                photoList.add(photo);
            }
        }
        postUrlRes.setImageList(photoList);
        System.out.println("사진 리뷰 : " + photoList);
        driver.close();    //탭 닫기
        driver.quit();    //브라우저 닫기
        return postUrlRes;
    }
}
