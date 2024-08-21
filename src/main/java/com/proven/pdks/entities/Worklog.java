package com.proven.pdks.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "worklogs")
@Builder
public class Worklog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tckn", nullable = false)
    private User user;

    private LocalDate date;

    @Column(name = "start_time" )
    private LocalTime from;

    @Column(name = "end_time")
    private LocalTime to;
}
