package com.proven.pdks.repositories;

import com.proven.pdks.entities.SubWorklog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubWorklogRepository extends JpaRepository<SubWorklog, Long> {
    List<SubWorklog> findByWorklogId(Long worklogId);
}
