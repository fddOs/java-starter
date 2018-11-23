package cn.ehai.rpc.feign;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.ehai.common.core.ApolloBaseConfig;
import cn.ehai.common.core.ResultCode;
import cn.ehai.common.core.ServiceException;
import cn.ehai.common.utils.*;
import cn.ehai.rpc.elk.EHILogstashMarker;
import cn.ehai.rpc.elk.RequestLog;
import cn.ehai.rpc.elk.ResponseLog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.exception.ExceptionUtils;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * okhtt工具类
 */
@Configuration
@EnableConfigurationProperties(FeignProperties.class)
public class EhiOkHttpClient {
    private static Logger LOGGER = LoggerFactory.getLogger(EhiOkHttpClient.class);
    private SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

    @Autowired
    private FeignProperties feignProperties;

    @Bean
    public OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new Interceptor() {
            public Response intercept(Chain chain) {
                Request builderRequest = handleRequest(chain.request());
                Response response;
                String exceptionMsg = "";
                String requestTime = simpleFormat.format(new Date());
                String responseTime;
                Response resultResponse = null;
                try {
                    resultResponse = chain.proceed(builderRequest);
                } catch (IOException e) {
                    exceptionMsg = ExceptionUtils.getStackTrace(e);
                    LoggerUtils.error(getClass(), ExceptionUtils.getStackTrace(e));
                } finally {
                    responseTime = simpleFormat.format(new Date());
                    response = sendHttpLog(builderRequest, handleResponse(resultResponse), exceptionMsg, requestTime,
                            responseTime);
                }
                return response;
            }
        }).writeTimeout(feignProperties.getConnectTimeoutMillis(), TimeUnit.MILLISECONDS).pingInterval(1, TimeUnit
                .SECONDS).retryOnConnectionFailure(true);
        return builder.build();
    }

    /**
     * 处理发出的请求
     *
     * @param request
     * @return
     */
    private Request handleRequest(Request request) {
        RequestBody requestBody = request.body();
        RequestBody requestBodyNew;
        /**
         * 处理body里面的参数
         */
        String bodyParams = "";
        if (requestBody != null && requestBody.contentType() != null
                && !requestBody.contentType().type().equals(MediaType.parse("multipart/form-data").type())) {
            try {
                Buffer bufferedSink = new Buffer();
                requestBody.writeTo(bufferedSink);
                bodyParams = bufferedSink.readString(Charset.forName("utf-8"));
                requestBodyNew = RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), bodyParams);
            } catch (Exception e) {
                requestBodyNew = requestBody;
            }
        } else {
            requestBodyNew = requestBody;
        }
        /**
         * 处理请求的url
         */
        return request.newBuilder().addHeader("Content-MD5", SignUtils.sign(request.url().query(), bodyParams))
                .addHeader("Content-Type", "application/json").url(request.url())
                .method(request.method(), requestBodyNew).build();
    }

    /*
     * 处理返回参数
     */
    private Response handleResponse(Response res) {
        ResponseBody responseBody = res.body();
        ResponseBody responseBodyNew = null;
        String md5 = res.header("Content-MD5");
        if (responseBody != null) {
            try {
                // TODO 一次性将数据加载到内存中需要优化
                String temp = responseBody.string();
                if (md5 != null) {
                    if (temp != null) {
                        String resMd5 = SignUtils.signResponse("", temp);
                        if (!md5.equalsIgnoreCase(resMd5)) {
                            throw new ServiceException(ResultCode.UNAUTHORIZED, "接口签名失败");
                        }
                        responseBodyNew = ResponseBody.create(MediaType.parse("application/json; charset=UTF-8"), temp);
                    }
                } else {
                    responseBodyNew = ResponseBody.create(MediaType.parse("application/json; charset=UTF-8"), temp);
                }
            } catch (Exception e) {
                LoggerUtils.error(EhiOkHttpClient.class, "接口处理异常", e);
            } finally {
                responseBody.close();
            }
        }
//        if (res.code() != 200) {
//            return res.newBuilder().body(responseBodyNew).code(500000 + res.code()).build();
//        }
        return res.newBuilder().body(responseBodyNew).build();
    }

    /**
     * @param request
     * @param response
     * @param exceptionMsg
     * @param requestTime
     * @param responseTime
     * @return okhttp3.Response
     * @Description:发送http的ELK日志
     * @exception:
     * @author: 方典典
     * @time:2018/11/7 17:56
     */
    private Response sendHttpLog(Request request, Response response, String exceptionMsg, String requestTime, String
            responseTime) {
        boolean isClose = "none".equalsIgnoreCase(ApolloBaseConfig.getLogSwitch());
        boolean isBool = !"ALL".equalsIgnoreCase(ApolloBaseConfig.getLogSwitch()) && "GET".equalsIgnoreCase(request
                .method());
        if (isClose || isBool) {
            return response;
        }
        long totalTime = 0l;
        try {
            totalTime = simpleFormat.parse(responseTime).getTime() - simpleFormat.parse(requestTime).getTime();
        } catch (Exception e) {
            // ignore
            LoggerUtils.error(getClass(), ExceptionUtils.getStackTrace(e));
        }
        String requestUrl = request.url().encodedPath();
        RequestBody requestBody = request.body();
        ResponseBody responseBody = response.body();
        String requestUrlQuery = request.url().encodedQuery();
        Headers requestHeaders = request.headers();
        Map<String, String> requestHeaderMap = new HashMap<>();
        for (String headerName : requestHeaders.names()) {
            try {
                requestHeaderMap.put(headerName, new String(request.header(headerName).getBytes("iso-8859-1"),
                        "utf-8"));
            } catch (UnsupportedEncodingException e) {
                requestHeaderMap.put(headerName, request.header(headerName));
            }
        }
        Headers responseHeaders = response.headers();
        Map<String, String> responseHeaderMap = new HashMap<>();
        for (String headerName : responseHeaders.names()) {
            try {
                responseHeaderMap.put(headerName, new String(response.header(headerName).getBytes("iso-8859-1"),
                        "utf-8"));
            } catch (UnsupportedEncodingException e) {
                responseHeaderMap.put(headerName, response.header(headerName));
            }
            responseHeaderMap.put("response.code", String.valueOf(response.code() == 200 ? 200 : 507));
        }
        if (requestUrlQuery != null) {
            try {
                requestUrlQuery = URLDecoder.decode(request.url().encodedQuery(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                LoggerUtils.error(getClass(), "decode请求url参数失败", e);
            }
        } else {
            requestUrlQuery = "";
        }
        String bodyParams = null;
        if (requestBody != null) {
            try {
                Buffer bufferedSink = new Buffer();
                requestBody.writeTo(bufferedSink);
                bodyParams = bufferedSink.readString(Charset.forName("UTF-8"));
            } catch (Exception e) {
                LoggerUtils.error(getClass(), "获取请求Body参数失败", e);
            }
        }
        JSON requestBodyJSON = JSON.parseObject(bodyParams);
        if (requestBodyJSON == null) {
            requestBodyJSON = new JSONObject();
        }
        JSON responseBodyJSON;
        try {
            responseBodyJSON = JSON.parseObject(responseBody.string());
            response = response.newBuilder().body(ResponseBody.create(MediaType.parse("application/json; " +
                    "charset=UTF-8"), responseBodyJSON.toJSONString())).build();
        } catch (IOException e) {
            responseBodyJSON = JSON.parseObject("{\"unknown\":\"" + ExceptionUtils.getStackTrace(e) + "\"}");
        } finally {
            responseBody.close();
        }
        RequestLog requestLog = new RequestLog(UuidUtils.getRandomUUID(), requestTime, false, ProjectInfoUtils
                .getProjectContext(), requestUrl + "?" + requestUrlQuery, requestBodyJSON, request.method(),
                requestHeaderMap);
        int httpStatus = response.code();
        ResponseLog responseLog = new ResponseLog(responseTime, httpStatus, exceptionMsg, totalTime,
                responseBodyJSON, responseHeaderMap);
        // 发送日志信息
        LOGGER.info(new EHILogstashMarker(requestLog, responseLog), null);
        return response;
    }
}
