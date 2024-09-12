package com.proven.pdks.services;

import com.proven.pdks.common.SimpleRows;
import com.proven.pdks.dtos.WorklogReportDTO;
import com.proven.pdks.entities.User;
import com.proven.pdks.helpers.FormatterHelper;
import com.proven.pdks.models.EmailModel;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


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
    public void sendWeeklyMail(String tckn) throws MessagingException{
        //JavaMailSenderImpl sender = new JavaMailSenderImpl();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);


        try {
            User user = userService.findByTCKN(tckn);
            LocalDate now = LocalDate.now();
            LocalDate from = LocalDate.of(now.getYear(), now.getMonth(), 1);
            LocalDate to = LocalDate.of(now.getYear(), now.getMonth(), Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));
            List<WorklogReportDTO> worklogs = worklogService.getReport(from, to, List.of(tckn));
            EmailModel emailDetails = new EmailModel();
            emailDetails.setWorklogs(worklogs);
            emailDetails.calculateTotal();
            emailDetails.setReceiver(user.getEmail());
            emailDetails.setName(user.getName());
            emailDetails.setSubject("Haftalık PDKS Raporunuz");

            messageHelper.setFrom(senderMail);
            messageHelper.setTo(emailDetails.getReceiver());
            messageHelper.setSubject(emailDetails.getSubject());

            Context context = new Context();
            context.setVariable("worklogs", emailDetails.getWorklogs());
            context.setVariable("name", emailDetails.getName().toUpperCase(FormatterHelper.locale));
            context.setVariable("tckn",user.getTckn());
            context.setVariable("startDate",worklogs.stream().min(Comparator.comparing(WorklogReportDTO::getDate)).map(WorklogReportDTO::getDate).orElse(from));
            context.setVariable("endDate",worklogs.stream().max(Comparator.comparing(WorklogReportDTO::getDate)).map(WorklogReportDTO::getDate).orElse(to));

            long hours = emailDetails.getTotalTime().toHours();
            int minutes = emailDetails.getTotalTime().toMinutesPart();

            String totalTimeText = FormatterHelper.formatter.format(hours) + ":" + FormatterHelper.formatter.format(minutes);
            context.setVariable("totalTime", totalTimeText);
            String processedString = templateEngine.process("weeklyMailTemplate", context);

            messageHelper.setText(processedString,true);
            mailSender.send(message);
        } catch (MessagingException | MailException e) {
            throw new RuntimeException(e);
        }
    }


}
