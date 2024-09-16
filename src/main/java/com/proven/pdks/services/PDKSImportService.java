package com.proven.pdks.services;

import com.proven.pdks.common.SimpleRows;
import com.proven.pdks.entities.Worklog;
import com.proven.pdks.repositories.UserRepository;
import com.proven.pdks.repositories.WorklogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PDKSImportService {
    @Autowired
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final WorklogRepository worklogRepository;

    public PDKSImportService(JdbcTemplate jdbcTemplate, UserRepository userRepository, WorklogRepository worklogRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
        this.worklogRepository = worklogRepository;
    }

    private Map<String, LocalDate> userLastImportMap(){
        Map<String, LocalDate> map = new HashMap<>();
        String sql = "SELECT users.tckn, wg.lastImport FROM users LEFT JOIN ( SELECT tckn, MAX(date) AS lastImport FROM worklogs GROUP BY tckn ) wg ON users.tckn = wg.tckn";
        jdbcTemplate.query(sql, (row) -> {
            String tckn = row.getString(1);
            Date date = row.getDate(2);
            map.put(tckn, date != null ? date.toLocalDate() : null);
        });
        return map;
    }


    public void importPDKS(List<SimpleRows> rows){
        Map<String, LocalDate> importMap = userLastImportMap();
        LocalDate now = LocalDate.now();
        LocalDate max = rows.stream().max(Comparator.comparing(SimpleRows::getDate)).map(SimpleRows::getDate).orElse(LocalDate.now());
        boolean bypassMaxControl = !max.getMonth().equals(now.getMonth());
        System.err.println("Raw data count: " + rows.size());
        rows = rows.stream().filter(row -> importMap.containsKey(row.getTckn()) &&
                (row.getDate().isBefore(max) || bypassMaxControl) && (importMap.get(row.getTckn()) == null || row.getDate().isAfter(importMap.get(row.getTckn())))).toList();
        System.err.println("Filtered data count: " + rows.size());

        Map<String, Map<LocalDate, Worklog>> result = new HashMap<>();
        for (SimpleRows row : rows) {
            if (!result.containsKey(row.getTckn())) {
                result.put(row.getTckn(), new HashMap<>());
            }

            Map<LocalDate, Worklog> dateLogs = result.get(row.getTckn());
            if (!dateLogs.containsKey(row.getDate())) {
                Worklog temp = new Worklog();
                temp.setUser(userRepository.findByTckn(row.getTckn()));
                temp.setDate(row.getDate());
                dateLogs.put(row.getDate(), temp);
            }

            Worklog worklog = dateLogs.get(row.getDate());
            if (row.isFrom()) {
                if (worklog.getFrom() == null || row.getTime().isBefore(worklog.getFrom())) {
                    worklog.setFrom(row.getTime());
                }
            } else {
                if (worklog.getTo() == null || row.getTime().isAfter(worklog.getFrom())) {
                    worklog.setTo(row.getTime());
                }
            }
        }
        List<Worklog> worklogs = result.values().stream().flatMap(q -> q.values().stream()).toList();
        worklogRepository.saveAllAndFlush(worklogs);
        System.err.println("Imported worklog count: " + worklogs.size());
    }

}
