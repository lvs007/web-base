/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.liang.email.mail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.core.io.InputStreamSource;

/**
 * 带附件的邮件
 *
 * @author
 */
public class MultipartEmail extends SimpleEmail {

    private final Map<String, InputStreamSource> attachments = new LinkedHashMap<>();

    public void addAttachment(String name, final byte[] data) {
        attachments.put(name, new InputStreamSource() {

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(data);
            }
        });
    }

    public void addAttachment(String name, final InputStream is) {
        attachments.put(name, new InputStreamSource() {

            @Override
            public InputStream getInputStream() throws IOException {
                return is;
            }
        });
    }

    public void addAttachment(String name, final File file) {
        attachments.put(name, new InputStreamSource() {

            @Override
            public InputStream getInputStream() throws IOException {
                return new FileInputStream(file);
            }
        });
    }

    public Map<String, InputStreamSource> getAttachments() {
        return attachments;
    }

}
