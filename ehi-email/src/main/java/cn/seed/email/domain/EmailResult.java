package cn.seed.email.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @Description:EmailResult
 * @author:方典典
 * @time:2018/11/23 10:54
 */
public class EmailResult {
    @JsonProperty("ErrorMessage")
    private String errorMessage;
    @JsonProperty("SendStatus")
    private Integer sendStatus;
    @JsonProperty("Success")
    private Boolean success;
    @JsonProperty("SuccessId")
    private Integer successId;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(Integer sendStatus) {
        this.sendStatus = sendStatus;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getSuccessId() {
        return successId;
    }

    public void setSuccessId(Integer successId) {
        this.successId = successId;
    }
}
