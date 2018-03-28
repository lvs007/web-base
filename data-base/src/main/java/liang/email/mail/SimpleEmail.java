/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package liang.email.mail;

import java.util.ArrayList;
import java.util.List;

/**
 * 简单的邮件
 *
 * @author
 */
public class SimpleEmail implements Email {

    private final List<String> toList = new ArrayList<>();
    private final List<String> ccList = new ArrayList<>();
    private final List<String> bccList = new ArrayList<>();
    private String subject;
    private String content;
    private String fromName;

    public SimpleEmail() {
    }

    @Override
    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public void addCc(String cc) {
        ccList.add(cc);
    }

    public void addTo(String to) {
        toList.add(to);
    }

    public void addBcc(String bcc) {
        bccList.add(bcc);
    }

    @Override
    public List<String> getToList() {
        return toList;
    }

    @Override
    public List<String> getCcList() {
        return ccList;
    }

    @Override
    public List<String> getBccList() {
        return bccList;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
