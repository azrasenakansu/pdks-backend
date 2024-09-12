package com.proven.pdks.repositories;

import com.proven.pdks.dtos.WorklogReportDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class ReportRepositoryCustomImpl implements ReportRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReportRepositoryCustomImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<WorklogReportDTO> getReport(LocalDate startDate, LocalDate endDate, List<String> tckns) {
        String sql = "SELECT users.name,report.* FROM users " +
                "INNER JOIN (" +
                "SELECT COALESCE(wrk.tckn,ext.tckn) as tckn, COALESCE(wrk.date,ext.date) as date, wrk.start_time, wrk.end_time, ext.ext_hours, ext.ext_descriptions FROM worklogs AS wrk " +
                "FULL JOIN " +
                "(SELECT date,tckn, SUM(end_time-start_time) as ext_hours , STRING_AGG(description,'-$-') as ext_descriptions FROM external_worklogs " +
                "WHERE is_approved = TRUE " +
                "GROUP BY (date,tckn)) " +
                "AS ext " +
                "ON ext.tckn = wrk.tckn AND ext.date = wrk.date) AS report " +
                "ON report.tckn = users.tckn " +
                "WHERE report.date BETWEEN ? AND ? " ;
        if (!tckns.isEmpty()) {
            sql += " AND report.tckn IN (" + String.join(",", tckns.stream().map(t -> "?").toArray(String[]::new)) + ") ";
        }
        sql += "ORDER BY report.tckn, report.date";

        Object[] params = new Object[2 + tckns.size()];
        params[0] = startDate;
        params[1] = endDate;
        for (int i = 0; i < tckns.size(); i++) {
            params[2 + i] = tckns.get(i);
        }

        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(WorklogReportDTO.class));
    }

}
