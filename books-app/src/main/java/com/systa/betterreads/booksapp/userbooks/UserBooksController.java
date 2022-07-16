package com.systa.betterreads.booksapp.userbooks;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import com.systa.betterreads.booksapp.book.Book;
import com.systa.betterreads.booksapp.book.BookRepository;
import com.systa.betterreads.booksapp.user.BooksByUser;
import com.systa.betterreads.booksapp.user.BooksByUserRepository;

@Controller
public class UserBooksController {

    @Autowired
    UserBooksRepository userBooksRepository;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    BooksByUserRepository booksByUserRepository;
    
    @PostMapping("/addUserBook")
    public ModelAndView addUserBook(@AuthenticationPrincipal OAuth2User user, 
        @RequestBody MultiValueMap<String, String> inputData){
            
            if(user == null || user.getAttribute("login") == null ){
                return null;
            }

            String bookId = inputData.getFirst("bookId");
            Optional<Book> bookOptional = bookRepository.findById(bookId);
            if(!bookOptional.isPresent()){
                return new ModelAndView("redirect:/");
            }

            Book book = bookOptional.get();

            UserBooksPrimaryKey userBooksPrimaryKey = new UserBooksPrimaryKey();
            userBooksPrimaryKey.setBookId(bookId);
            userBooksPrimaryKey.setUserId(user.getAttribute("login"));
            
            UserBooks userBooks = new UserBooks();
            userBooks.setKey(userBooksPrimaryKey);

            userBooks.setCompletionDate(LocalDate.parse(inputData.getFirst("completedDate")));
            userBooks.setStartedDate(LocalDate.parse(inputData.getFirst("startDate")));
            userBooks.setRatings(Integer.parseInt(inputData.getFirst("ratings")));
            userBooks.setReadingStatus(inputData.getFirst("readingStatus"));

            userBooksRepository.save(userBooks);

            BooksByUser booksByUser = new BooksByUser();
            booksByUser.setId(user.getAttribute("login"));
            booksByUser.setBookId(bookId);
            booksByUser.setBookName(book.getName());
            booksByUser.setCoverIds(book.getCoverIds());
            booksByUser.setAuthorNames(book.getAuthorNames());
            booksByUser.setReadingStatus(inputData.getFirst("readingStatus"));
            booksByUser.setRating(Integer.parseInt(inputData.getFirst("ratings")));
            booksByUserRepository.save(booksByUser);

            return new ModelAndView("redirect:/book/" + bookId);
    }
}
