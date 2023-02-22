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
    private String name;
    private String place;
    // private String city;
    private String store_type;
    private List<String> review_list;
    private List<String> image_list;
}
