package cn.seed.rpc.feign;

import cn.seed.common.core.ApolloBaseConfig;
import cn.seed.common.core.HttpCodeEnum;
import cn.seed.common.core.ResultCode;
import cn.seed.common.core.ServiceException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.seed.common.utils.*;
import cn.seed.common.elk.SeedLogstashMarker;
import cn.seed.common.elk.RequestLog;
import cn.seed.common.elk.ResponseLog;
import com.alibaba.fastjson.JSONObject;

import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;

import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import okio.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * BaseOkHttpClient
 *
 * @author 方典典
 * @time 2019/4/30 10:17
 */
@Configuration
public class BaseOkHttpClient {
    private static Logger LOGGER = LoggerFactory.getLogger(BaseOkHttpClient.class);

    private final String HEADER_JWT_USER_ID = "jwt-user-id";

    @Bean
    public OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) {
                Request builderRequest = handleRequest(chain.request());
                Response response;
                String exceptionMsg = "";
                String requestTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String responseTime;
                Response resultResponse = null;
                Exception exception = null;
                try {
                    resultResponse = chain.proceed(builderRequest);
                } catch (IOException e) {
                    exception = e;
                    exceptionMsg = ExceptionUtils.getStackTrace(e);
                    LoggerUtils.error(getClass(), ExceptionUtils.getStackTrace(e));
                } finally {
                    if (resultResponse == null) {
                        throw new ServiceException(ResultCode.BAD_REQUEST, "请求异常" + builderRequest.url(), exception);
                    }
                    responseTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    response = sendHttpLog(builderRequest, handleResponse(resultResponse), exceptionMsg, requestTime,
                            responseTime);
                }
                return response;
            }
        }).writeTimeout(ProjectInfoUtils.PROJECT_FEIGN_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS).pingInterval(1,
                TimeUnit.SECONDS).retryOnConnectionFailure(false);
        if (ApolloBaseConfig.getOkHttpSSLEnable()) {
            builder.sslSocketFactory(createSSLSocketFactory(), new TrustAllCertsManager())
                    .hostnameVerifier((s, sslSession) -> true);
        }
        return builder.build();
    }

    /**
     * 处理发出的请求
     *
     * @param request
     * @return
     */
    private Request handleRequest(Request request) {
        String urlQuery = "?platform=" + ProjectInfoUtils.PROJECT_CONTEXT + "&oprUserNo=" + getUserCode();
        if (request.url().querySize() != 0) {
            urlQuery = "&platform=" + ProjectInfoUtils.PROJECT_CONTEXT + "&oprUserNo=" + getUserCode();
        }
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

        return request.newBuilder().addHeader("Content-MD5", SignUtils.sign(((null == request.url().query()) ? "" :
                request.url().query()) + urlQuery.replaceFirst("\\?", ""), bodyParams)).addHeader("Connection",
                "close").addHeader("Content-Type", "application/json").addHeader(HEADER_JWT_USER_ID, getUserCode())
                .url(HttpUrl.parse(request.url().url().toString() + urlQuery)).method(request.method(),
                        requestBodyNew).build();
    }


    /**
     * 从request中获取userCode
     *
     * @param
     * @return java.lang.String
     * @author 方典典
     * @time 2019/5/22 14:51
     */
    private String getUserCode() {
        HttpServletRequest request = null;
        try {
            request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        } catch (IllegalStateException e) {
            // IGNORE
        }
        if (request == null) {
            return "";
        }
        String userId = request.getHeader(HEADER_JWT_USER_ID);
        if (StringUtils.isEmpty(userId)) {
            userId = String.valueOf(request.getAttribute("oprNo"));
        }
        return userId == null ? "" : userId;
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
                        String resMd5 = SignUtils.signResponse(temp);
                        if (!md5.equalsIgnoreCase(resMd5)) {
                            throw new ServiceException(ResultCode.UNAUTHORIZED, "接口签名失败");
                        }
                        responseBodyNew = ResponseBody.create(MediaType.parse("application/json; charset=UTF-8"), temp);
                    }
                } else {
                    responseBodyNew = ResponseBody.create(MediaType.parse("application/json; charset=UTF-8"), temp);
                }
            } catch (Exception e) {
                LoggerUtils.error(getClass(), new Object[]{res}, e);
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
        if ((isClose || isBool) && StringUtils.isEmpty(exceptionMsg) && response.code() == HttpCodeEnum
                .CODE_200.getCode()) {
            return response;
        }
        long totalTime = 0L;
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            totalTime = simpleFormat.parse(responseTime).getTime() - simpleFormat.parse(requestTime).getTime();
        } catch (Exception e) {
            // ignore
            LoggerUtils.error(getClass(), new Object[]{response, request, exceptionMsg, requestTime, responseTime}, e);
        }
        String requestUrl = request.url().url().toString();
        RequestBody requestBody = request.body();
        ResponseBody responseBody = response.body();
        Headers requestHeaders = request.headers();
        int httpStatus = response.code() == HttpCodeEnum.CODE_200.getCode() ? HttpCodeEnum.CODE_200.getCode() :
                HttpCodeEnum.CODE_517.getCode();
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
            responseHeaderMap.put("response.code", String.valueOf(httpStatus));
        }
        String bodyParams = null;
        if (requestBody != null) {
            try {
                Buffer bufferedSink = new Buffer();
                requestBody.writeTo(bufferedSink);
                bodyParams = bufferedSink.readString(Charset.forName("UTF-8"));
            } catch (Exception e) {
                LoggerUtils.error(getClass(), new Object[]{response, request, exceptionMsg, requestTime,
                        responseTime}, e);
            }
        }
        Object requestBodyJSON = JsonUtils.parse(bodyParams);
        if (requestBodyJSON == null) {
            requestBodyJSON = new JSONObject();
        }
        Object responseBodyJSON = new JSONObject();
        String bodyString = "";
        try {
            bodyString = responseBody.string();
            responseBodyJSON = JsonUtils.parse(bodyString);
        } catch (Exception e) {
            exceptionMsg = "ResponseBody:" + bodyString + "\r\n Exception:" + exceptionMsg;
        } finally {
            response = response.newBuilder().body(ResponseBody.create(MediaType.parse("application/json; " +
                    "charset=UTF-8"), bodyString)).build();
            responseBody.close();
            responseBody = null;
        }
        RequestLog requestLog = new RequestLog(UuidUtils.getRandomUUID(), requestTime, false, requestUrl,
                requestBodyJSON, request.method(),
                requestHeaderMap);
        ResponseLog responseLog = new ResponseLog(responseTime, httpStatus, exceptionMsg, totalTime,
                responseBodyJSON, responseHeaderMap);
        // 发送日志信息
        LOGGER.info(new SeedLogstashMarker(requestLog, responseLog, ProjectInfoUtils.PROJECT_CONTEXT), null);
        return response;
    }


    /**
     * 生成SSL
     *
     * @return SSLSocketFactory
     * @author 方典典
     * @time 2019/7/1 14:55
     */
    private SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCertsManager()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            LoggerUtils.error(getClass(), ExceptionUtils.getStackTrace(e));
            LoggerUtils.error(getClass(), new Object[]{}, e);
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "请求失败，证书无效");
        }
        return ssfFactory;
    }
}
