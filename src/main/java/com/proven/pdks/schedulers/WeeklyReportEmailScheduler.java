package com.proven.pdks.schedulers;

import com.proven.pdks.entities.User;
import com.proven.pdks.repositories.UserRepository;
import com.proven.pdks.services.EmailingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class WeeklyReportEmailScheduler {

    @Autowired
    private final EmailingService emailingService;

    @Autowired
    private final UserRepository userRepository;

    public WeeklyReportEmailScheduler(EmailingService emailingService, UserRepository userRepository){
        this.emailingService = emailingService;
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 0 9 * * MON")
    public void schedule(){
        LocalDate date = LocalDate.now();
        List<User> users = this.userRepository.findAll();
        if(users.isEmpty()){
            return;
        }
        for(User user : users){
            emailingService.sendReportMail(user, date);
        }
    }
}
