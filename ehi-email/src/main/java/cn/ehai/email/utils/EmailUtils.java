/**
 *
 */
package cn.ehai.email.utils;

import cn.ehai.email.domain.EmailKeyValue;
import cn.ehai.email.domain.EmailModel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

/**
 * @Description:邮件发送工具
 * @author:方典典
 * @time:2018年4月21日 下午4:32:07
 */
public class EmailUtils {

    /**
     * 生成EmailModel
     *
     * @param edmId
     * @param emailTo
     * @param params
     * @return cn.ehai.email.domain.EmailModel
     * @exception:
     * @author: 方典典
     * @time:2018/11/23 11:05
     */
    public static EmailModel generateEmail(int edmId, String emailTo, List<EmailKeyValue> params) {
        EmailModel emailModel = new EmailModel();
        emailModel.setAttachmentPath("");
        emailModel.setClientKey("E9FFBFD19B43");
        emailModel.setEdmId(edmId);
        emailModel.setEmailTo(emailTo);
        emailModel.setEmailBcc("");
        emailModel.setEmailCc("");
        emailModel.setOprNo("27894");
        emailModel.setConfirmation_no("");
        emailModel.setKeyValues(params);
        return emailModel;
    }

    private EmailUtils() {
    }
}
