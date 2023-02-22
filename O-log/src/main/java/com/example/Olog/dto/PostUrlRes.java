package com.example.Olog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostUrlRes {
    private String store;
    private String place;
    private String storeType;
    private List<String> reviewList;
    private List<String> photoList;
}
