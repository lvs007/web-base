/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.liang.email.mail.report;

import org.apache.commons.lang3.StringUtils;

/**
 * 报表里面的列
 *
 * @author
 */
public class ReportColumn {

    private String text;
    private int colSpan;//跨列，大于1才有效，其他都无效
    private int rowSpan;//跨行，大于1才有效
    private String className;
    private String style;
    private boolean virtual;

    public ReportColumn() {
    }

    public ReportColumn(boolean virtual) {
        this.virtual = virtual;
    }

    public ReportColumn(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        // 虚拟的不输出任何东西,只负责占位
        if (isVirtual()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<td");
        if (colSpan > 1) {
            sb.append(" colspan=").append(colSpan);
        }
        if (rowSpan > 1) {
            sb.append(" rowspan=").append(rowSpan);
        }
        if (StringUtils.isNotBlank(className)) {
            sb.append(" class=\"").append(className).append("\"");
        }
        if (StringUtils.isNotBlank(style)) {
            sb.append(" style=\"").append(style).append("\"");
        }
        sb.append(">");
        sb.append(text);
        sb.append("</td>");
        return sb.toString();
    }

    public boolean isVirtual() {
        return virtual;
    }

    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getColSpan() {
        return colSpan;
    }

    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

    public int getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

}
