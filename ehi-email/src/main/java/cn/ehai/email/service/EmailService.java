package cn.ehai.email.service;

import cn.ehai.email.domain.EmailModel;
import cn.ehai.email.domain.EmailResult;

import java.util.List;

public interface EmailService {

    /**
     * 发送Email
     *
     * @param emailModel
     * @return void
     * @exception:
     * @author: 方典典
     * @time:2018/11/23 10:20
     */
    List<EmailResult> send(EmailModel emailModel);

}
