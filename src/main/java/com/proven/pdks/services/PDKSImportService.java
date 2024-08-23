package com.proven.pdks.services;

import com.proven.pdks.common.SimpleRows;
import com.proven.pdks.entities.User;
import com.proven.pdks.entities.Worklog;
import com.proven.pdks.repositories.UserRepository;
import com.proven.pdks.repositories.WorklogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PDKSImportService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final WorklogRepository worklogRepository;

    public PDKSImportService(UserRepository userRepository, WorklogRepository worklogRepository) {
        this.userRepository = userRepository;
        this.worklogRepository = worklogRepository;
    }

    public void importPDKS(List<SimpleRows> rows) {
        LocalDate lastImportDate = worklogRepository.findTopByOrderByDateDesc().map(Worklog::getDate).orElse(null);
        LocalDate now = LocalDate.now();
        rows = rows.stream().filter(row -> row.getDate().isBefore(now) && (lastImportDate == null || row.getDate().isAfter(lastImportDate))).toList();

        Map<String, Map<LocalDate, Worklog>> result = new HashMap<>();

        for (SimpleRows row : rows) {
            User user = userRepository.findByTckn(row.getTckn());

            if (user == null) {
                continue;
            }

            if (!result.containsKey(row.getTckn())) {
                result.put(row.getTckn(), new HashMap<>());
            }

            Map<LocalDate, Worklog> dateLogs = result.get(row.getTckn());
            if (!dateLogs.containsKey(row.getDate())) {
                Worklog temp = new Worklog();
                temp.setUser(user);
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
    }
}
