package com.proven.pdks.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SimpleRows {
    private String tckn;
    private String sicil;
    private String name;
    private LocalDate date;
    private LocalTime time;
    private String status;
    private String source;

    public boolean isFrom() {
        return this.status.equalsIgnoreCase("G");
    }
}
