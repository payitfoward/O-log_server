package com.example.Olog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
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
    @JsonProperty("store_type")
    private String storeType;
    @JsonProperty("city")
    private String city;
    @JsonProperty("review_list")
    private List<String> reviewList;
    @JsonProperty("image_list")
    private List<String> imageList;
}
