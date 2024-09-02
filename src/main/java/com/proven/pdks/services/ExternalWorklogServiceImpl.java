package com.proven.pdks.services;

import com.proven.pdks.entities.ExternalWorklog;
import com.proven.pdks.exceptionHandling.WillfullException;
import com.proven.pdks.repositories.ExternalWorklogRepository;
import com.proven.pdks.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExternalWorklogServiceImpl implements ExternalWorklogService {
    private final ExternalWorklogRepository externalWorklogRepository;

    private final UserRepository userRepository;

    @Autowired
    public ExternalWorklogServiceImpl(ExternalWorklogRepository externalWorklogRepository, UserRepository userRepository) {
        this.externalWorklogRepository = externalWorklogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ExternalWorklog> getWorklogs(String tckn) {
        return externalWorklogRepository.findByUser_Tckn(tckn);
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

    public List<ExternalWorklog> getAllPendingWorklogs() {
        return externalWorklogRepository.findByIsApprovedNull();
    }

    public List<ExternalWorklog> getAllRejectedWorklogs() {
        return externalWorklogRepository.findByIsApprovedFalse();
    }

    public List<ExternalWorklog> getAllApprovedWorklogs() {
        return externalWorklogRepository.findByIsApprovedTrue();
    }

}
