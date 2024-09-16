package com.proven.pdks.services;

import com.proven.pdks.dtos.WorklogReportDTO;
import com.proven.pdks.entities.User;
import com.proven.pdks.helpers.FormatterHelper;
import com.proven.pdks.models.EmailModel;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;


@Service
@RequiredArgsConstructor
public class EmailingService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final WorklogService worklogService;
    private final UserService userService;

    @Value("${spring.mail.username}")
    private String senderMail;

    @Async
    public void sendReportMail(User user, LocalDate date){
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);

        try {
            LocalDate from = LocalDate.of(date.getYear(), date.getMonth(), 1);
            LocalDate to = LocalDate.of(date.getYear(), date.getMonth(), Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));
            List<WorklogReportDTO> worklogs = worklogService.getReport(from, to, List.of(user.getTckn()));
            if(worklogs == null || worklogs.isEmpty()){
                return;
            }
            EmailModel emailDetails = new EmailModel();
            emailDetails.setWorklogs(worklogs);
            emailDetails.calculateTotal();
            emailDetails.setReceiver(user.getEmail());
            emailDetails.setName(user.getName());

            int startDay = worklogs.stream().min(Comparator.comparing(WorklogReportDTO::getDate)).map(WorklogReportDTO::getDate).orElse(from).getDayOfMonth();
            int endDay = worklogs.stream().max(Comparator.comparing(WorklogReportDTO::getDate)).map(WorklogReportDTO::getDate).orElse(to).getDayOfMonth();
            String betweenText = startDay + "-" + endDay + " " + FormatterHelper.textifyMonth(date.getMonth());
            emailDetails.setSubject(betweenText + " Çalışma Saatleri hk.");

            messageHelper.setFrom(senderMail);
            messageHelper.setTo(emailDetails.getReceiver());
            messageHelper.setSubject(emailDetails.getSubject());

            Context context = new Context();
            context.setVariable("worklogs", emailDetails.getWorklogs());
            context.setVariable("name", emailDetails.getName().toUpperCase(FormatterHelper.locale));
            context.setVariable("tckn",user.getTckn());
            context.setVariable("betweenText",betweenText);

            long hours = emailDetails.getTotalTime().toHours();
            int minutes = emailDetails.getTotalTime().toMinutesPart();

            String totalTimeText = FormatterHelper.formatter.format(hours) + ":" + FormatterHelper.formatter.format(minutes);
            context.setVariable("totalTime", totalTimeText);
            String processedString = templateEngine.process("weeklyMailTemplate", context);

            messageHelper.setText(processedString,true);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
