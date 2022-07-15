package com.systa.betterreads.booksapp.search;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Controller
public class SearchController {
    
    private final WebClient webClient;
    
    public SearchController(WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder
            .baseUrl("http://openlibrary.org/search.json")
            .build();
    }

    @GetMapping(value = "/search")
    public String getSearchResults(@RequestParam String query, 
        Model model){
        Mono<SearchResults> foo = this.webClient
            .get()
            .uri("?q={query}&limit=10", query)
            .retrieve()
            .bodyToMono(SearchResults.class);

        SearchResults results = foo.block();
        System.out.println("Data is " + results.getDocs());
        model.addAttribute("searchResults", results.getDocs());
        return "search";
    }
}
