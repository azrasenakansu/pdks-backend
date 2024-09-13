package com.proven.pdks.repositories;

import com.proven.pdks.entities.ExternalWorklog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalWorklogRepository extends JpaRepository<ExternalWorklog, Long> {
    Page<ExternalWorklog> findByIsApprovedNullOrderByDateDesc(Pageable pageable);

    Page<ExternalWorklog> findByUser_TcknOrderByDateDesc(String tckn, Pageable pageable);

    Page<ExternalWorklog> findByIsApprovedFalseOrderByDateDesc(Pageable pageable);

    Page<ExternalWorklog> findByIsApprovedTrueOrderByDateDesc(Pageable pageable);

}