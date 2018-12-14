package cn.ehai.rpc.email.service.impl;

import cn.ehai.common.core.ApolloBaseConfig;
import cn.ehai.common.core.ResultCode;
import cn.ehai.common.core.ServiceException;
import cn.ehai.email.domain.EmailModel;
import cn.ehai.email.domain.EmailResult;
import cn.ehai.email.service.EmailService;
import cn.ehai.rpc.email.api.EmailApi;
import feign.Feign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:EmailServiceImpl
 * @author:方典典
 * @time:2018/11/23 10:22
 */
@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private Feign.Builder builder;

    /**
     * 发送Email
     *
     * @param emailModel
     * @return void
     * @exception:
     * @author: 方典典
     * @time:2018/11/23 10:23
     */
    @Override
    public List<EmailResult> send(EmailModel emailModel) {
        if (emailModel == null) {
            throw new ServiceException(ResultCode.BAD_REQUEST, "邮件发送失败，参数为空！");
        }
        List<EmailModel> list = new ArrayList<>();
        list.add(emailModel);
        return builder.target(EmailApi.class, ApolloBaseConfig.aesDecrypt("emailUrl", "")).sendEmail(list);
    }
}
