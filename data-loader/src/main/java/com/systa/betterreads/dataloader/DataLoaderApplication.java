package com.systa.betterreads.dataloader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.systa.betterreads.dataloader.author.Author;
import com.systa.betterreads.dataloader.author.AuthorRepository;
import com.systa.betterreads.dataloader.book.Book;
import com.systa.betterreads.dataloader.book.BookRepository;
import com.systa.betterreads.dataloader.connection.DataStaxAstraProperties;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class DataLoaderApplication {

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    BookRepository bookRepository;

    @Value("${datadump.location.author}")
    private String authorsDumpPath;

    @Value("${datadump.location.works}")
    private String worksDumpPath;

    public static void main(String[] args) {
        SpringApplication.run(DataLoaderApplication.class, args);
    }

    private void initAuthors() {
        Path path = Paths.get(authorsDumpPath);
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(line -> {
                // read each line and extract only author json
                String jsonString = line.substring(line.indexOf("{"));

                // convert to json
                JSONObject json = new JSONObject(jsonString);

                // create
                Author author = new Author();
                author.setName(json.optString("name"));
                author.setPersonalName(json.optString("personal_name"));
                author.setId(json.optString("key").replace("/authors/", ""));

                authorRepository.save(author);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initWorks() {
        Path path = Paths.get(worksDumpPath);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        try (Stream<String> lines = Files.lines(path)) {

            lines.forEach(line -> {
                // read each line and extract only book json
                String jsonString = line.substring(line.indexOf("{"));

                // convert to json
                JSONObject json = new JSONObject(jsonString);
                try{
                    // create
                    Book book = new Book();
                    book.setId(json.optString("key").replace("/works/", ""));
                    book.setName(json.optString("title"));

                    JSONObject jsonDescription = json.optJSONObject("description");
                    if (jsonDescription != null) {
                        book.setDescription(jsonDescription.optString("value"));
                    }

                    JSONObject jsonPublished = json.optJSONObject("created");
                    if (jsonPublished != null) {
                        String publishString = jsonPublished.optString("value");
                        book.setPublishedDate(LocalDate.parse(publishString, dateFormat));
                    }

                    JSONArray coversjson = json.optJSONArray("covers");
                    if (coversjson != null) {
                        List<String> covers = new ArrayList<>();
                        IntStream.range(0, coversjson.length()).forEach(i -> covers.add(coversjson.get(i).toString()));
                        book.setCoverIds(covers);
                    }

                    JSONArray authorsjson = json.optJSONArray("authors");
                    if (authorsjson != null) {
                        List<String> authorIds = new ArrayList<>();
                        for (int i = 0; i < authorsjson.length(); i++) {
                            String authorId = authorsjson.getJSONObject(i)
                                    .getJSONObject("author")
                                    .optString("key")
                                    .replace("/authors/", "");

                            authorIds.add(authorId);
                        }
                        book.setAuthorIds(authorIds);

                        List<String> authorNames = authorIds.stream().map(id -> authorRepository.findById(id))
                                .map(optionalAuthor -> {
                                    if (!optionalAuthor.isPresent())
                                        return "Unknown Author";
                                    return optionalAuthor.get().getName();
                                }).collect(Collectors.toList());

                        book.setAuthorNames(authorNames);
                    }
                    System.out.println("Saving book with name " + book.getName());
                    bookRepository.save(book);
                }
                catch(Exception e){
                    System.out.println("One book is not saved " + e);
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void start() {
        //initAuthors();
        initWorks();
    }

    @Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
        Path bundle = astraProperties.getSecureConnectBundle().toPath();
        return builder -> builder.withCloudSecureConnectBundle(bundle);
    }

}
