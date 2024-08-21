package com.proven.pdks.services;

import com.proven.pdks.entities.Worklog;

import java.util.List;

public interface WorklogService {
    List<Worklog> getWorklogs(String tckn);

}
