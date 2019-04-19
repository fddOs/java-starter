package cn.seed.email.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:邮件Model
 * @author:方典典
 * @time:2017年12月25日 上午10:39:12
 */
public class EmailModel {
    @JsonProperty("AttachmentPath")
    private String attachmentPath;
    @JsonProperty("ClientKey")
    private String clientKey;
    @JsonProperty("EdmId")
    private int edmId;
    @JsonProperty("EmailBcc")
    private String emailBcc;
    @JsonProperty("EmailCc")
    private String emailCc;
    @JsonProperty("EmailTo")
    private String emailTo;
    @JsonProperty("OprNo")
    private String oprNo;
    @JsonProperty("confirmation_no")
    private String confirmationNo;
    @JsonProperty("KeyValues")
    private List<EmailKeyValue> keyValues;

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public int getEdmId() {
        return edmId;
    }

    public void setEdmId(int edmId) {
        this.edmId = edmId;
    }

    public String getEmailBcc() {
        return emailBcc;
    }

    public void setEmailBcc(String emailBcc) {
        this.emailBcc = emailBcc;
    }

    public String getEmailCc() {
        return emailCc;
    }

    public void setEmailCc(String emailCc) {
        this.emailCc = emailCc;
    }

    public String getEmailTo() {
        return emailTo;
    }

    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    public String getOprNo() {
        return oprNo;
    }

    public void setOprNo(String oprNo) {
        this.oprNo = oprNo;
    }

    public String getConfirmationNo() {
        return confirmationNo;
    }

    public void setConfirmationNo(String confirmationNo) {
        this.confirmationNo = confirmationNo;
    }

    public List<EmailKeyValue> getKeyValues() {
        return this.keyValues == null ? null : new ArrayList<>(this.keyValues);
    }

    public void setKeyValues(List<EmailKeyValue> keyValues) {
        this.keyValues = (keyValues == null ? null : new ArrayList<>(keyValues));
    }
}
