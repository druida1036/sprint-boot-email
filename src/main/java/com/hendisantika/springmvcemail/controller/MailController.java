package com.hendisantika.springmvcemail.controller;

import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.hendisantika.springmvcemail.SpringMvcEmailApplication;
import com.hendisantika.springmvcemail.dto.MailObject;
import com.hendisantika.springmvcemail.service.EmailServiceImpl;
import com.sun.management.OperatingSystemMXBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by IntelliJ IDEA. User: hendisantika Email: hendisantika@gmail.com Telegram : @hendisantika34 Date: 8/15/17
 * Time: 7:31 AM To change this template use File | Settings | File Templates.
 */

@Controller
@RequestMapping("/mail")
public class MailController {

    private Logger logger = LogManager.getLogger(MailController.class);

    @Autowired
    private ConfigurableApplicationContext context;


    @Autowired
    public EmailServiceImpl emailService;

    @Value("${attachment.invoice}")
    private String attachmentPath;

    @Autowired
    public SimpleMailMessage template;


    private static final Map<String, Map<String, String>> labels;

    static {
        labels = new HashMap<>();

        //Simple email
        Map<String, String> props = new HashMap<>();
        props.put("headerText", "Send Simple Email");
        props.put("messageLabel", "Message");
        props.put("additionalInfo", "");
        props.put("action", "send");
        labels.put("send", props);

        //Email with template
        props = new HashMap<>();
        props.put("headerText", "Send Email Using Template");
        props.put("messageLabel", "Template Parameter");
        props.put("additionalInfo",
            "The parameter value will be added to the following message template:<br>" +
                "<b>This is the test email template for your email:<br>'Template Parameter'</b>"
        );
        props.put("action", "sendTemplate");
        labels.put("sendTemplate", props);

        //Email with attachment
        props = new HashMap<>();
        props.put("headerText", "Send Email With Attachment");
        props.put("messageLabel", "Message");
        props.put("additionalInfo",
            "To make sure that you send an attachment with this email, change the value for the 'attachment.invoice' "
                + "in the application.properties file to the path to the attachment.");
        props.put("action", "sendAttachment");
        labels.put("sendAttachment", props);
    }

    @GetMapping(value = {"/send", "/sendTemplate", "/sendAttachment"})
    public String createMail(Model model,
        HttpServletRequest request) {
        String action = request.getRequestURL().substring(
            request.getRequestURL().lastIndexOf("/") + 1
        );
        Map<String, String> props = labels.get(action);
        Set<String> keys = props.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            model.addAttribute(key, props.get(key));
        }

        model.addAttribute("mailObject", new MailObject());
        model.addAttribute("labels", labels);
        logger.info("props : {}" + props);
        logger.info("labels : {}" + labels);
        return "mail/send";
    }

    @PostMapping("/send")
    public String createMail(Model model,
        @ModelAttribute("mailObject") @Valid MailObject mailObject,
        Errors errors) {
        logger.info("================= send simple Email ===============");
        if (errors.hasErrors()) {
            logger.info("mailObject : {}", mailObject);
            logger.info("Error: {}", errors.getAllErrors());
            return "mail/send";
        }
        emailService.sendSimpleMessage(mailObject.getTo(),
            mailObject.getSubject(), mailObject.getText());

        logger.info("mailObject : {}", mailObject);

        return "redirect:/home";

    }

    @PostMapping("/sendTemplate")
    public String createMailWithTemplate(Model model,
        @ModelAttribute("mailObject") @Valid MailObject mailObject,
        Errors errors) {
        if (errors.hasErrors()) {
            logger.info("================= send with Template ===============");
            logger.info("mailObject : {}" + mailObject.toString());
            logger.info("Error: " + errors.getAllErrors());
            return "mail/send";
        }
        emailService.sendSimpleMessageUsingTemplate(mailObject.getTo(),
            mailObject.getSubject(),
            template,
            mailObject.getText());
        logger.info("mailObject : {}" + mailObject.toString());
        return "redirect:/home";
    }

    @PostMapping(value = "/sendAttachment")
    public String createMailWithAttachment(Model model,
        @ModelAttribute("mailObject") @Valid MailObject mailObject,
        Errors errors) {
        if (errors.hasErrors()) {
            logger.info("================= send with Attachment ===============");
            logger.info("mailObject : {}" + mailObject.toString());
            logger.info("Error: " + errors.getAllErrors());
            return "mail/send";
        }
        emailService.sendMessageWithAttachment(
            mailObject.getTo(),
            mailObject.getSubject(),
            mailObject.getText(),
            attachmentPath
        );

        logger.info("mailObject : {}" + mailObject.toString());
        logger.info("attachmentPath : {}" + attachmentPath);
        return "redirect:/home";
    }

//    @Scheduled(initialDelay = 120000, fixedRate = 120000)
    public void checkCpuLoad() {
        emailService.sendSimpleMessage("jmartinez01581@gmail.com",
            "New api email", "Hello Dear User TESTBI\n"
                + "\n"
                + "You win 0.00000008 BTC, 0 free lottery tickets and 104 reward points!\n"
                + "\n"
                + "08/22/2021 15:58:57:437 => VERIFYING Active bonus\n"
                + "08/22/2021 15:58:57:851 => BTC-EXTRA - Active bonus 100% FREE BTC bonus ends in 04h:47m:31s\n"
                + "08/22/2021 15:58:58:040 => REWARDS - Active bonus 100 extra reward points ends in 04h:47m:19s\n"
                + "08/22/2021 15:58:58:614 => NEXT ROLL TIME - 30000\n"
                + "08/22/2021 15:59:32:817 => TOTAL BTC - 0.01556584\n"
                + "System IP Address : 10.142.0.3\n"
                + "Public IP Address: 35.211.159.77\n"
                + "{\n"
                + "    \"zip\": \"\",\n"
                + "    \"country\": \"United States\",\n"
                + "    \"city\": \"North Charleston\",\n"
                + "    \"org\": \"Google Cloud (us-east1)\",\n"
                + "    \"timezone\": \"America/New_York\",\n"
                + "    \"regionName\": \"South Carolina\",\n"
                + "    \"isp\": \"Google LLC\",\n"
                + "    \"query\": \"35.211.159.77\",\n"
                + "    \"lon\": -80.013,\n"
                + "    \"as\": \"AS15169 Google LLC\",\n"
                + "    \"countryCode\": \"US\",\n"
                + "    \"region\": \"SC\",\n"
                + "    \"lat\": 32.8771,\n"
                + "    \"status\": \"success\"\n"
                + "}\n"
                + "\n"
                + "{\n"
                + "  \"git.branch\" : \"develop\",\n"
                + "  \"git.build.host\" : \"instance-3-bot\",\n"
                + "  \"git.build.time\" : \"2021-08-22T20:57:36+0000\",\n"
                + "  \"git.build.user.email\" : \"\",\n"
                + "  \"git.build.user.name\" : \"\",\n"
                + "  \"git.build.version\" : \"1.0\",\n"
                + "  \"git.closest.tag.commit.count\" : \"\",\n"
                + "  \"git.closest.tag.name\" : \"\",\n"
                + "  \"git.commit.id\" : \"06934c9d83956063a333b9548711477518f79483\",\n"
                + "  \"git.commit.id.abbrev\" : \"06934c9\",\n"
                + "  \"git.commit.id.describe\" : \"06934c9\",\n"
                + "  \"git.commit.id.describe-short\" : \"06934c9\",\n"
                + "  \"git.commit.message.full\" : \"Fix Login Issue.\",\n"
                + "  \"git.commit.message.short\" : \"Fix Login Issue.\",\n"
                + "  \"git.commit.time\" : \"2021-06-28T19:09:06+0000\",\n"
                + "  \"git.commit.user.email\" : \"jmartinez@trustarc.com\",\n"
                + "  \"git.commit.user.name\" : \"Jorge Martinez\",\n"
                + "  \"git.dirty\" : \"false\",\n"
                + "  \"git.local.branch.ahead\" : \"0\",\n"
                + "  \"git.local.branch.behind\" : \"0\",\n"
                + "  \"git.remote.origin.url\" : \"git@gitlab.com:druida1036/freebitcoin.git\",\n"
                + "  \"git.tags\" : \"\",\n"
                + "  \"git.total.commit.count\" : \"115\"\n"
                + "}");

        logger.info("mailObject : {}", "sent email");

        OperatingSystemMXBean operatingSystemMXBean =
            (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        logger.info(operatingSystemMXBean.getProcessCpuLoad());
        logger.info(operatingSystemMXBean.getSystemCpuLoad());
        logger.info(operatingSystemMXBean.getFreePhysicalMemorySize());
        logger.info(operatingSystemMXBean.getTotalPhysicalMemorySize());

        logger.info("CPU load = {}", ThreadLocalRandom.current().nextFloat());
        restart();


    }

    public void restart() {
        ApplicationArguments args = context.getBean(ApplicationArguments.class);

        Thread thread = new Thread(() -> {
            context.close();
            context = SpringApplication.run(SpringMvcEmailApplication.class, args.getSourceArgs());
        });

        thread.setDaemon(false);
        thread.start();
        logger.info("Restarted Finished");
    }
}
