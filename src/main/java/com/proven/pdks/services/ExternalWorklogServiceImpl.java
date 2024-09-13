package com.proven.pdks.services;

import com.proven.pdks.entities.ExternalWorklog;
import com.proven.pdks.exceptionHandling.WillfullException;
import com.proven.pdks.repositories.ExternalWorklogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExternalWorklogServiceImpl implements ExternalWorklogService {
    private final ExternalWorklogRepository externalWorklogRepository;

    @Autowired
    public ExternalWorklogServiceImpl(ExternalWorklogRepository externalWorklogRepository) {
        this.externalWorklogRepository = externalWorklogRepository;
    }

    @Override
    public Page<ExternalWorklog> getWorklogs(String tckn, int page, int size) {
        return externalWorklogRepository.findByUser_TcknOrderByDateDesc(tckn, PageRequest.of(page,size));
    }

    public ExternalWorklog createWorklog(ExternalWorklog worklog) {
        worklog.setIsApproved(null); //henüz bakılmadı
        return externalWorklogRepository.save(worklog);
    }

    public ExternalWorklog updateWorklog(Long id, ExternalWorklog worklogDetails) {
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(authority -> "ADMIN".equals(authority.getAuthority()));
        Optional<ExternalWorklog> worklogOptional = externalWorklogRepository.findById(id);
        if (worklogOptional.isEmpty()) {
            throw new WillfullException("Worklog not found with ID: " + id);
        }
        ExternalWorklog worklog = worklogOptional.get();
        if (worklog.getIsApproved() == null || isAdmin) {
            worklog.setDate(worklogDetails.getDate());
            worklog.setFrom(worklogDetails.getFrom());
            worklog.setTo(worklogDetails.getTo());
            worklog.setType(worklogDetails.getType());
            worklog.setDescription(worklogDetails.getDescription());
        }
        return externalWorklogRepository.save(worklog);
    }

    public void deleteWorklog(Long id) {
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(authority -> "ADMIN".equals(authority.getAuthority()));
        Optional<ExternalWorklog> worklogOptional = externalWorklogRepository.findById(id);
        if (worklogOptional.isEmpty()) {
            throw new WillfullException("Worklog not found with ID: " + id);
        }
        ExternalWorklog worklog = worklogOptional.get();
        if (worklog.getIsApproved() == null || isAdmin) {
            externalWorklogRepository.delete(worklog);
        }
    }

    public ExternalWorklog approveWorklog(Long id, Optional<Boolean> state) {
        Optional<ExternalWorklog> worklogOptional = externalWorklogRepository.findById(id);
        if (worklogOptional.isPresent()) {
            ExternalWorklog worklog = worklogOptional.get();
            worklog.setIsApproved(state.orElse(null));
            return externalWorklogRepository.save(worklog);
        } else {
            throw new WillfullException("Worklog not found with ID: " + id);
        }
    }

    public Page<ExternalWorklog> getAllPendingWorklogs(int page, int size) {
        return externalWorklogRepository.findByIsApprovedNullOrderByDateDesc(PageRequest.of(page,size));
    }

    public Page<ExternalWorklog> getAllRejectedWorklogs(int page, int size) {
        return externalWorklogRepository.findByIsApprovedFalseOrderByDateDesc(PageRequest.of(page,size));
    }

    public Page<ExternalWorklog> getAllApprovedWorklogs(int page, int size) {
        return externalWorklogRepository.findByIsApprovedTrueOrderByDateDesc(PageRequest.of(page,size));
    }

}
