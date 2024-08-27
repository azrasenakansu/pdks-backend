package com.proven.pdks.repositories;

import com.proven.pdks.dtos.WorklogReportDTO;
import org.apache.tomcat.util.buf.StringUtils;
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
        String sql = "SELECT users.name,report.* FROM users INNER JOIN (SELECT COALESCE(wrk.tckn,ext.tckn) as tckn, COALESCE(wrk.date,ext.date) as date, wrk.start_time, wrk.end_time, ext.ext_hours, ext.ext_descriptions FROM worklogs AS wrk FULL JOIN " +
                "(SELECT date,tckn, SUM(end_time-start_time) as ext_hours , STRING_AGG(description,'-$-') as ext_descriptions FROM external_worklogs GROUP BY (date,tckn)) AS ext " +
                "ON ext.tckn = wrk.tckn AND ext.date = wrk.date) AS report ON report.tckn = users.tckn WHERE report.date BETWEEN ? AND ? AND ( ? OR report.tckn IN (?) )";
        return jdbcTemplate.query(sql, new Object[]{startDate, endDate, tckns.isEmpty(), StringUtils.join(tckns, ',') }, new BeanPropertyRowMapper<>(WorklogReportDTO.class));
    }

}
