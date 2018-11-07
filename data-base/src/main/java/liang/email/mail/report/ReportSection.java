/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package liang.email.mail.report;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 报表的一个分段
 *
 * @author
 */
public class ReportSection {

    private String title;
    private final List<ReportRow> headRowList = new ArrayList<>();//标题的头
    private List<ReportRow> contentRowList = new ArrayList<>();//内容的行;
    private final List<ReportRow> summaryRowList = new ArrayList<>();//概要的一行，一般放在最下面

    public void addRow(ReportRow row) {
        contentRowList.add(row);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<h3>").append(title).append("</h3>\n");
        sb.append("<table class=\"dat\" cellspacing=\"0\">\n");
        sb.append("<thead>\n");
        sb.append(buildRowList(headRowList, null));
        sb.append("</thead>\n");
        sb.append("<tbody>\n");
        sb.append(buildRowList(summaryRowList, "all"));
        sb.append(buildRowList(contentRowList, null));
        sb.append(buildRowList(summaryRowList, "all"));
        sb.append("</tbody>\n");
        sb.append("</table>\n");
        return sb.toString();
    }

    private String buildRowList(List<ReportRow> list, String rowClassName) {
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        // 处理跨行或跨列的情况
        processSpan(list);

        StringBuilder sb = new StringBuilder();
        for (ReportRow row : list) {
            if (StringUtils.isNotBlank(rowClassName)) {
                row.addClassName(rowClassName);
            }
            sb.append(row).append("\n");
        }
        return sb.toString();
    }

    private void processSpan(List<ReportRow> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        // 先展开colspan
        for (ReportRow row : list) {
            if (CollectionUtils.isEmpty(row.getColumnList())) {
                continue;
            }
            int index = 0;
            while (true) {
                if (index >= row.getColumnList().size()) {
                    break;
                }
                ReportColumn col = row.getColumnList().get(index++);
                if (col.getColSpan() > 1) {
                    for (int colIndex = 0; colIndex < col.getColSpan() - 1; colIndex++) {
                        row.getColumnList().add(index + colIndex, new ReportColumn(true));
                    }
                }
            }
        }
        //再展开row span
        for (int rowIndex = 0; rowIndex < list.size(); rowIndex++) {
            ReportRow row = list.get(rowIndex);
            if (CollectionUtils.isEmpty(row.getColumnList())) {
                continue;
            }
            for (int colIndex = 0; colIndex < row.getColumnList().size(); colIndex++) {
                ReportColumn col = row.getColumnList().get(colIndex);
                if (col.getRowSpan() > 1) {
                    for (int i = 0; i < col.getRowSpan() - 1; i++) {
                        list.get(rowIndex + 1 + i).getColumnList().add(colIndex, new ReportColumn(true));
                    }
                }
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ReportRow> getHeadRowList() {
        return headRowList;
    }

    public void addHeadRow(ReportRow headRow) {
        this.headRowList.add(headRow);
    }

    public List<ReportRow> getContentRowList() {
        return contentRowList;
    }

    public void setContentRowList(List<ReportRow> contentRowList) {
        this.contentRowList = contentRowList;
    }

    public List<ReportRow> getSummaryRowList() {
        return summaryRowList;
    }

    public void addSummaryRow(ReportRow summaryRow) {
        this.summaryRowList.add(summaryRow);
    }

}
