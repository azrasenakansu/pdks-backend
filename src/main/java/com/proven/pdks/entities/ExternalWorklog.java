package com.proven.pdks.entities;

import com.proven.pdks.common.ExternalWorklogType;
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
@Table(name = "external_worklogs")
@Builder
public class ExternalWorklog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tckn", nullable = false)
    private User user;

    private LocalDate date;

    @Column(name = "start_time")
    private LocalTime from;

    @Column(name = "end_time")
    private LocalTime to;

    @Column(name = "is_approved")
    private Boolean isApproved;

    @Enumerated(EnumType.ORDINAL)
    private ExternalWorklogType type;

    @Column(name = "description")
    private String description;

}


