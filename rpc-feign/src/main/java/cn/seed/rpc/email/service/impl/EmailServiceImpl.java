package cn.seed.rpc.email.service.impl;

import cn.seed.common.core.ApolloBaseConfig;
import cn.seed.common.core.ResultCode;
import cn.seed.common.core.ServiceException;
import cn.seed.email.domain.EmailModel;
import cn.seed.email.domain.EmailResult;
import cn.seed.email.service.EmailService;
import cn.seed.rpc.email.api.EmailApi;
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
        return builder.target(EmailApi.class, ApolloBaseConfig.getMessageCenterUrl()).sendEmail(list);
    }
}
