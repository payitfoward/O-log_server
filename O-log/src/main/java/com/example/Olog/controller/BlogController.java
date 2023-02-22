package com.example.Olog.controller;

import com.example.Olog.dto.PostUrlReq;
import com.example.Olog.dto.PostUrlRes;
import com.example.Olog.service.BlogService;
import com.example.Olog.util.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.catalina.connector.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value = "/blogs")
public class BlogController {
    private final BlogService blogService;

    public BlogController(BlogService blogService){ this.blogService = blogService; }

    @ResponseBody
    @PostMapping("/url-input")
    public BaseResponse<PostUrlRes> postBlog(PostUrlReq postUrlReq) throws IOException {
        PostUrlRes postUrlRes = blogService.postUrl(postUrlReq);
        return new BaseResponse<>(postUrlRes);
    }
}
