package com.dockey.todocska.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Table(name = "todo")
@Entity
@Getter
@Setter
@ToString

public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "megnevezes", nullable = false)
    private String megnevezes;

    @Column(name = "hatarido", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate hatarido;

    @Column(name = "kesz", nullable = false)
    private Boolean kesz;

    @Column(name = "gid", nullable = false)
    private Long gid;

}