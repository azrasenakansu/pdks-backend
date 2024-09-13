package com.proven.pdks.services;

import com.proven.pdks.entities.ExternalWorklog;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ExternalWorklogService {
    Page<ExternalWorklog> getWorklogs(String tckn, int page, int size);

    ExternalWorklog createWorklog(ExternalWorklog worklog);

    ExternalWorklog updateWorklog(Long id, ExternalWorklog worklogDetails);

    void deleteWorklog(Long id);

    ExternalWorklog approveWorklog(Long id, Optional<Boolean> state);

    Page<ExternalWorklog> getAllPendingWorklogs(int page, int size);

    Page<ExternalWorklog> getAllRejectedWorklogs(int page, int size);

    Page<ExternalWorklog> getAllApprovedWorklogs(int page, int size);
}

