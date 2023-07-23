package com.example.whoami.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "index_number")
    private int indexNumber;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "link", unique = true)
    private String link;

    @Column(name = "create_date")
    private LocalDate createDate;
}
