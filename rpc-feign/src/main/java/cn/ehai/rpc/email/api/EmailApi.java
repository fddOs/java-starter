package cn.ehai.rpc.email.api;

import java.util.List;

import cn.ehai.email.domain.EmailModel;

import cn.ehai.email.domain.EmailResult;
import feign.RequestLine;

/**
 * @Description:邮件接口API
 * @author:方典典
 * @time:2017年12月25日 上午10:59:46
 */
public interface EmailApi {

    /**
     * 发送邮件接口
     *
     * @param models
     * @return java.util.List<java.lang.Object>
     * @exception:
     * @author: 方典典
     * @time:2018/11/23 10:42
     */
    @RequestLine("POST /Email")
    List<EmailResult> sendEmail(List<EmailModel> models);

}
