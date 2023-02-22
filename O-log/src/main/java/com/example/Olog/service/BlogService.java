package com.example.Olog.service;

import com.example.Olog.dto.PostUrlReq;
import com.example.Olog.dto.PostUrlRes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BlogService {
    public PostUrlRes postUrl(PostUrlReq postUrlReq) throws IOException {
        try {
            String url = postUrlReq.getUrl();
            Document doc = Jsoup.connect("https://map.naver.com/v5/search/%EC%88%A0%EC%9D%B8/place/1046678640?placePath=%3Fentry=pll%26from=nx%26fromNxList=true&c=15,0,0,0,dh").get();
            return postUrl(doc);
        } catch (IOException ignored) {
        }
        return null;
    }
    public PostUrlRes postUrl(Document doc){
    }
}
