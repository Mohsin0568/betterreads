package com.systa.betterreads.booksapp.home;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.systa.betterreads.booksapp.user.BooksByUser;
import com.systa.betterreads.booksapp.user.BooksByUserRepository;

@Controller
public class HomeController {
    
    @Autowired
    BooksByUserRepository booksByUserRepository;

    @GetMapping(value = "/")
    public String home(Model model, @AuthenticationPrincipal OAuth2User user){

        if(user == null || user.getAttribute("login") == null){
            return "index";
        }

        String loginId = user.getAttribute("login");
        Slice<BooksByUser> booksSlice = booksByUserRepository.findAllById(loginId, CassandraPageRequest.of(0, 50));
        List<BooksByUser> booksList = booksSlice.getContent();

        booksList.stream().forEach(userBook -> {
            String coverBaseUrl = "http://covers.openlibrary.org/b/id/";
            String bookCoverUrl = "/images/no-image.png";
            if(userBook.getCoverIds() != null && userBook.getCoverIds().size() > 0){
                bookCoverUrl = coverBaseUrl + userBook.getCoverIds().get(0)+"-M.jpg";                
            }
            userBook.setCoverUrl(bookCoverUrl);
        });
        

        model.addAttribute("userBooks", booksList);
        return "home";
    }
}
