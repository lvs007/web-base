/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.liang.email.mail.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 报表数据
 *
 * @author
 */
public class ReportData {

    private String title;
    private List<ReportSection> sectionList = new ArrayList<>();
    private String styleText;//自定义的style，可以自定义css样式

    public String getStyleText() {
        return styleText;
    }

    public void setStyleText(String styleText) {
        this.styleText = styleText;
    }

    public void addSection(ReportSection section) {
        sectionList.add(section);
    }

    public String process(String template) {
        if (StringUtils.isNotBlank(styleText)) {
            template = template.replace("</head>", styleText + "\n</head>");
        }
        template = template.replace("${title}", String.valueOf(title));
        StringBuilder sb = new StringBuilder();
        if (CollectionUtils.isNotEmpty(sectionList)) {
            for (ReportSection section : sectionList) {
                sb.append(section).append("\n");
            }
        }
        template = template.replace("${report}", sb.toString());
        return template;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ReportSection> getSectionList() {
        return sectionList;
    }

    public void setSectionList(List<ReportSection> sectionList) {
        this.sectionList = sectionList;
    }

}
