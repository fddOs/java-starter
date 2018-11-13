package cn.ehai.web.jwt;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.ehai.javautils.core.Result;
import cn.ehai.javautils.core.ResultCode;
import cn.ehai.javautils.core.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;

public class JwtHandlerInterceptorAdapter extends HandlerInterceptorAdapter {

    private static final Logger log = LoggerFactory.getLogger(JwtHandlerInterceptorAdapter.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        log.info(request.getServletPath());
        if (JwtTokenAuthentication.getAuthentication(request)) {
            return true;
        } else {
            log.warn("jwt token 验证失败，请求接口：{}，请求参数：{}", request.getRequestURI(), JSON.toJSONString(request
					.getParameterMap()));
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
        }
    }
}
