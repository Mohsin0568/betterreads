package com.systa.betterreads.dataloader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

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
import com.systa.betterreads.dataloader.connection.DataStaxAstraProperties;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class DataLoaderApplication {

    @Autowired
    AuthorRepository authorRepository;

    @Value("${datadump.location.author}")
    private String authorsDumpPath;

    @Value("${datadump.location.works}")
    private String worksDumpPath;

    public static void main(String[] args) {
		SpringApplication.run(DataLoaderApplication.class, args);
	}

    private void initAuthors(){
        Path path = Paths.get(authorsDumpPath);
        try(Stream<String> lines = Files.lines(path)){
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
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void initWorks(){

    }

    @PostConstruct
    public void start(){
        initAuthors();
        initWorks();
    } 

    @Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
        Path bundle = astraProperties.getSecureConnectBundle().toPath();
        return builder -> builder.withCloudSecureConnectBundle(bundle);
    }

}
