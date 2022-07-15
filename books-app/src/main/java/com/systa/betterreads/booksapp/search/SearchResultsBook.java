package com.systa.betterreads.booksapp.search;

import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class SearchResultsBook {
    
    private String key;
    private String title;

    @JsonAlias("author_name")
    private List<String> authorName;

    @JsonAlias("cover_i")
    private String coverId = "/images/no-image.png";

    @JsonAlias("first_publish_year")
    private int publishyear;

    public void setKey(String key){
        this.key = key.replace("/works/", "");
    }

    public void setCoverId(String coverId){
        if(StringUtils.hasText(coverId)){
            this.coverId = "http://covers.openlibrary.org/b/id/"+coverId+"-M.jpg";
        }
        else{
            this.coverId = "/images/no-image.png";
        }
    }
}
