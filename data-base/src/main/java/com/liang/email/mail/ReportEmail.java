/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.liang.email.mail;

import com.liang.email.mail.report.ReportData;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author
 */
public class ReportEmail implements Email {

    private static final Logger LOG = LoggerFactory.getLogger(ReportEmail.class);
    private final SimpleEmail email = new SimpleEmail();
    private final ReportData data;

    public ReportEmail(ReportData data) {
        this.data = data;
    }

    @Override
    public String getFromName() {
        return email.getFromName();
    }

    public void setFromName(String fromName) {
        email.setFromName(fromName);
    }

    public void addCc(String cc) {
        email.addCc(cc);
    }

    public void addTo(String to) {
        email.addTo(to);
    }

    public void addBcc(String bcc) {
        email.addBcc(bcc);
    }

    public void setSubject(String subject) {
        email.setSubject(subject);
    }

    @Override
    public List<String> getToList() {
        return email.getToList();
    }

    @Override
    public List<String> getCcList() {
        return email.getCcList();
    }

    @Override
    public List<String> getBccList() {
        return email.getBccList();
    }

    @Override
    public String getSubject() {
        String subject = email.getSubject();
        if (StringUtils.isBlank(subject)) {
            subject = data.getTitle();
        }
        return subject;
    }

    @Override
    public String getContent() {
        try {
            try (InputStream is = getClass().getResourceAsStream("/mail.html")) {
                String template = IOUtils.toString(is, "UTF-8");
                template = data.process(template);
                return template;
            }
        } catch (Exception ex) {
            LOG.error(null, ex);
            return ex.toString();
        }
    }

}
