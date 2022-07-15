package com.systa.betterreads.booksapp.search;

import java.util.List;

import lombok.Data;

@Data
public class SearchResults {
    
    private int numFound;

    private List<SearchResultsBook> docs;
}
