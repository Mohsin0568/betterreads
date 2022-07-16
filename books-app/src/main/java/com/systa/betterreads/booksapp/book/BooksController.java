package com.systa.betterreads.booksapp.book;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.systa.betterreads.booksapp.userbooks.UserBooks;
import com.systa.betterreads.booksapp.userbooks.UserBooksPrimaryKey;
import com.systa.betterreads.booksapp.userbooks.UserBooksRepository;

@Controller
public class BooksController {
    
    @Autowired
    BookRepository bookRepository;

    @Autowired
    UserBooksRepository userBooksRepository;

    private final String BOOK_COVER_URL = "http://covers.openlibrary.org/b/id/";

    @GetMapping(value = "/book/{id}")
    public String getBookById(@PathVariable("id") String id, Model model,
        @AuthenticationPrincipal OAuth2User user){
        Optional<Book> optionalBook = bookRepository.findById(id);
        if(optionalBook.isPresent()){
            Book book = optionalBook.get();
            String bookCoverUrl = "/images/no-image.png";
            if(book.getCoverIds() != null && book.getCoverIds().size() > 0){
                bookCoverUrl = BOOK_COVER_URL + book.getCoverIds().get(0)+"-L.jpg";                
            }
            model.addAttribute("coverImage", bookCoverUrl);
            model.addAttribute("book", book);

            if(user != null && user.getAttribute("login") != null){
                String loginId = user.getAttribute("login");
                model.addAttribute("loginId", loginId);

                UserBooksPrimaryKey key = new UserBooksPrimaryKey();
                key.setUserId(loginId);
                key.setBookId(book.getId());

                Optional<UserBooks> userBooks = userBooksRepository.findById(key);
                if(userBooks.isPresent()){
                    model.addAttribute("userBooks", userBooks.get());
                }
                else{
                    model.addAttribute("userBooks", new UserBooks());
                }

            }

            return "book";
        }
        return "book-not-found";
    
    }
}
