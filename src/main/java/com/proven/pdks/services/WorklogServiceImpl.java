package com.proven.pdks.services;

import com.proven.pdks.entities.Worklog;
import com.proven.pdks.repositories.WorklogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorklogServiceImpl implements WorklogService {
    private final WorklogRepository worklogRepository;

    @Autowired
    public WorklogServiceImpl(WorklogRepository worklogRepository) {
        this.worklogRepository = worklogRepository;
    }

    @Override
    public List<Worklog> getWorklogs(String tckn) {
        return worklogRepository.findByUser_Tckn(tckn);
    }
}
