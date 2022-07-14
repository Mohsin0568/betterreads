package com.systa.betterreads.booksapp.book;

import java.util.Optional;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class BooksController {
    
    @Autowired
    BookRepository bookRepository;

    private final String BOOK_COVER_URL = "http://covers.openlibrary.org/b/id/";

    @GetMapping(value = "/book/{id}")
    public String getBookById(@PathVariable("id") String id, Model model){
        Optional<Book> optionalBook = bookRepository.findById(id);
        if(optionalBook.isPresent()){
            Book book = optionalBook.get();
            String bookCoverUrl = "/images/no-image.png";
            if(book.getCoverIds() != null && book.getCoverIds().size() > 0){
                bookCoverUrl = BOOK_COVER_URL + book.getCoverIds().get(0)+"-L.jpg";                
            }
            model.addAttribute("coverImage", bookCoverUrl);
            model.addAttribute("book", book);
            return "book";
        }
        return "book-not-found";
    }
}
