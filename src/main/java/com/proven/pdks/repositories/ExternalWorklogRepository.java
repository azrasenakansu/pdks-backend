package com.proven.pdks.repositories;

import com.proven.pdks.entities.ExternalWorklog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExternalWorklogRepository extends JpaRepository<ExternalWorklog, Long> {
    List<ExternalWorklog> findByIsApprovedNull();
    List<ExternalWorklog> findByUser_Tckn(String tckn);
}