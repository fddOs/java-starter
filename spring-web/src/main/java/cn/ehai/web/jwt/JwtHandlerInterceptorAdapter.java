package cn.ehai.web.jwt;

import cn.ehai.common.utils.EHIExceptionLogstashMarker;
import cn.ehai.common.utils.EHIExceptionMsgWrapper;
import cn.ehai.common.utils.LoggerUtils;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.ehai.common.core.Result;
import cn.ehai.common.core.ResultCode;
import cn.ehai.common.core.ResultGenerator;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;

public class JwtHandlerInterceptorAdapter extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        if (JwtTokenAuthentication.getAuthentication(request)) {
            return true;
        } else {
            responseResult(response, ResultGenerator.genFailResult(ResultCode.UNAUTHORIZED, "jwt token 验证失败"));
            return false;
        }
    }

    private void responseResult(HttpServletResponse response, Result result) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.setStatus(200);
        try {
            response.getWriter().write(JSON.toJSONString(result));
        } catch (IOException ex) {
            LoggerUtils.error(getClass(), new EHIExceptionLogstashMarker(new EHIExceptionMsgWrapper
                    (getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(), new
                            Object[]{response, result}, ExceptionUtils.getStackTrace(ex))));
        }
    }
}
