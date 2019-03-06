/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.liang.email.mail.report;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 报表的某一行
 *
 * @author
 */
public class ReportRow {

    private String className;
    private String style;
    private List<ReportColumn> columnList = new ArrayList<>();

    public static ReportRow asRow(ReportColumn... list) {
        ReportRow row = new ReportRow();
        for (int i = 0; i < list.length; i++) {
            row.addColumn(list[i]);
        }
        return row;
    }

    public static ReportRow asRow(String... columns) {
        ReportColumn[] rs = asColumnArray(columns);
        return asRow(rs);
    }

    private static ReportColumn[] asColumnArray(String... columns) {
        ReportColumn[] rs = new ReportColumn[columns.length];
        for (int i = 0; i < rs.length; i++) {
            rs[i] = new ReportColumn(columns[i]);
        }
        return rs;
    }

    @Deprecated
    public static ReportRow summary(String... columns) {
        ReportRow row = new ReportRow();
        row.setClassName("all");
        List<ReportColumn> list = new ArrayList<>();
        for (int i = 0; i < columns.length; i++) {
            if (i == 0) {
                ReportColumn column = new ReportColumn();
                column.setText(columns[i]);
                column.setClassName("no-border");
                list.add(column);
            } else {
                list.add(new ReportColumn(columns[i]));
            }
        }
        row.setColumnList(list);
        return row;
    }

    @Deprecated
    public static ReportRow content(String... columns) {
        ReportRow row = new ReportRow();
        List<ReportColumn> list = new ArrayList<>();
        for (int i = 0; i < columns.length; i++) {
            if (i == 0) {
                ReportColumn column = new ReportColumn();
                column.setText(columns[i]);
                column.setClassName("no-border");
                list.add(column);
            } else {
                list.add(new ReportColumn(columns[i]));
            }
        }
        row.setColumnList(list);
        return row;
    }

    public void addColumn(ReportColumn column) {
        columnList.add(column);
    }

    public void addColumn(String text) {
        addColumn(new ReportColumn(text));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr");
        if (StringUtils.isNotBlank(className)) {
            sb.append(" class=\"").append(className).append("\"");
        }
        if (StringUtils.isNotBlank(style)) {
            sb.append(" style=\"").append(style).append("\"");
        }
        sb.append(">");
        if (CollectionUtils.isNotEmpty(columnList)) {
            int index = 0;
            for (ReportColumn column : columnList) {
                if (index++ == 0) {
                    column.setClassName("no-border");
                }
                sb.append("    ").append(column).append("\n");
            }
        }
        sb.append("</tr>");
        return sb.toString();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void addClassName(String className) {
        if (StringUtils.isBlank(this.className)) {
            this.className = className;
        } else {
            String[] ss = className.split("\\s+");
            boolean find = false;
            for (String s : ss) {
                if (StringUtils.equals(s, className)) {
                    find = true;
                    break;
                }
            }
            if (find == false) {
                this.className += " " + className;
            }
        }

    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public List<ReportColumn> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<ReportColumn> columnList) {
        this.columnList = columnList;
    }

}
