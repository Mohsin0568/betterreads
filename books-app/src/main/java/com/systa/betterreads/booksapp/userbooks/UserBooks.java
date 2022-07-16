package com.systa.betterreads.booksapp.userbooks;

import java.time.LocalDate;

import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.CassandraType.Name;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Table(value = "books_by_user_by_bookid")
@Getter
@Setter
public class UserBooks {
    
    @PrimaryKey
    private UserBooksPrimaryKey key;

    @Column("started_date")
    @CassandraType(type = Name.DATE)
    private LocalDate startedDate;

    @Column("completion_date")
    @CassandraType(type = Name.DATE)
    private LocalDate completionDate;

    @Column("reading_status")
    @CassandraType(type = Name.TEXT)
    private String readingStatus;

    @Column("ratings")
    @CassandraType(type = Name.INT)
    private int ratings;
}
