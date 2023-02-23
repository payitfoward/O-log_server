package com.example.Olog.controller;

import com.example.Olog.dto.PostClientRes;
import com.example.Olog.dto.PostUrlReq;
import com.example.Olog.dto.PostUrlRes;
import com.example.Olog.service.BlogService;
import com.example.Olog.util.BaseException;
import com.example.Olog.util.BaseResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
public class BlogController {
    private final BlogService blogService;

    public BlogController(BlogService blogService){ this.blogService = blogService; }

    @ResponseBody
    @PostMapping("/bloggpt")
    public BaseResponse<PostClientRes> postBlog(@RequestBody PostUrlReq postUrlReq) throws Exception {
        try {
            PostUrlRes postUrlRes = blogService.getBlogList(postUrlReq); // 크롤링 파싱 데이터
            PostClientRes postClientRes = blogService.requestToFastAPI(postUrlRes); // 스프링 - 플라스크 서버 연결
            return new BaseResponse<>(postClientRes);
        } catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }
}
