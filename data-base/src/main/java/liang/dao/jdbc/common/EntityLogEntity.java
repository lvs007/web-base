/*
 * 【注意】此类系代码生成器自动生成，请尽量不要修改此类的内容，因为
 * 重新生成的时候，会被覆盖。
 */
package liang.dao.jdbc.common;

import liang.dao.jdbc.annotation.Id;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 记录审计日志的表，此表不记录审计，否则就死循环了
 *
 * @author
 */
public class EntityLogEntity implements Serializable {

    @Id(name = "id")
    private Long id;

    private String entityName;
    private Long entityId;
    private String operation;
    private String title;
    private String fields;
    private String oldContent;
    private String newContent;
    private String changedContent;
    private boolean success;
    private Timestamp createTime;
    private String operator;
    private Long operatorId;
    private String operatorNickname;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Long getEntityId() {
        return this.entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getOperation() {
        return this.operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOldContent() {
        return this.oldContent;
    }

    public void setOldContent(String oldContent) {
        this.oldContent = oldContent;
    }

    public String getNewContent() {
        return this.newContent;
    }

    public void setNewContent(String newContent) {
        this.newContent = newContent;
    }

    public String getChangedContent() {
        return this.changedContent;
    }

    public void setChangedContent(String changedContent) {
        this.changedContent = changedContent;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Timestamp getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getOperator() {
        return this.operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorNickname() {
        return operatorNickname;
    }

    public void setOperatorNickname(String operatorNickname) {
        this.operatorNickname = operatorNickname;
    }
}
