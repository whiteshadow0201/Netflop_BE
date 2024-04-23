package com.example.filmStreaming.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "films")
public class Film {
    @Id
    @UuidGenerator
    @Column(name = "uuid", updatable = false, nullable = false)
    private UUID uuid;
    @Column(
            name = "filmName",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String filmName;
    @Column(
            name = "path",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String path;


    public Film(String filmName, String path) {
        this.filmName = filmName;
        this.path = path;
    }



}

