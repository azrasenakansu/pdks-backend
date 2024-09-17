package com.proven.pdks.services;

import com.proven.pdks.common.SimpleRows;
import com.proven.pdks.entities.SubWorklog;
import com.proven.pdks.entities.Worklog;
import com.proven.pdks.repositories.UserRepository;
import com.proven.pdks.repositories.WorklogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

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

    private String toKey(Worklog worklog){
        return worklog.getUser().getTckn() + "_" + worklog.getDate().toString();
    }

    private String toKey(SimpleRows row){
        return row.getTckn() + "_" + row.getDate().toString();
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

        Map<String, Worklog> worklogs = new HashMap<>();
        Map<String, Stack<SubWorklog>> subWorklogs = new HashMap<>();
        for (SimpleRows row : rows) {
            String key = toKey(row);
            if(!worklogs.containsKey(key)){
                Worklog temp = new Worklog();
                temp.setUser(userRepository.findByTckn(row.getTckn()));
                temp.setDate(row.getDate());
                worklogs.put(key, temp);
                subWorklogs.put(key, new Stack<>());
            }

            Worklog worklog = worklogs.get(key);
            Stack<SubWorklog> stack = subWorklogs.get(key);
            if (row.isFrom()) {
                if (worklog.getFrom() == null || row.getTime().isBefore(worklog.getFrom())) {
                    worklog.setFrom(row.getTime());
                }
                SubWorklog sub = new SubWorklog();
                sub.setFrom(row.getTime());
                stack.push(sub);
            } else {
                if (worklog.getTo() == null || row.getTime().isAfter(worklog.getFrom())) {
                    worklog.setTo(row.getTime());
                }
                if(!stack.empty() && stack.peek().getTo() == null){
                    stack.peek().setTo(row.getTime());
                }
                else{
                    SubWorklog sub = new SubWorklog();
                    sub.setTo(row.getTime());
                    stack.push(sub);
                }
            }
        }
        List<Worklog> result = worklogs.values().stream()
                .peek(w -> w.setParts(subWorklogs.get(toKey(w)).stream().toList()))
                .sorted(Comparator.comparing(Worklog::getDate))
                .toList();
        worklogRepository.saveAllAndFlush(result);
        System.err.println("Imported worklog count: " + worklogs.size());
    }

}
