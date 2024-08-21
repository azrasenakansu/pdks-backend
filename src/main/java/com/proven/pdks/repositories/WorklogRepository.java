package com.proven.pdks.repositories;

import com.proven.pdks.entities.Worklog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorklogRepository extends JpaRepository<Worklog, Long> {
    List<Worklog> findByUser_Tckn(String tckn);

}
