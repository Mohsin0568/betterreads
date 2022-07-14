package com.systa.betterreads.booksapp.connection;

import java.io.File;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;


@ConfigurationProperties(prefix = "datastax.astra")
@Getter
@Setter
public class DataStaxAstraProperties {
    
    private File secureConnectBundle;

}
