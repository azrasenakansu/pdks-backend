package com.proven.pdks.services;

import com.proven.pdks.entities.ExternalWorklog;
import com.proven.pdks.entities.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface ExternalWorklogService {
    List<ExternalWorklog> getWorklogs(String tckn);

    ExternalWorklog createWorklog(ExternalWorklog worklog);

    ExternalWorklog updateWorklog(Long id, ExternalWorklog worklogDetails);

    void deleteWorklog(Long id);

    ExternalWorklog approveWorklog(Long id, Optional<Boolean> state);

    List<ExternalWorklog> getAllPendingWorklogs();

}

