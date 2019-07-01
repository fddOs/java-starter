package cn.seed.rpc.feign;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * 信任所有的证书
 *
 * @author 方典典
 * @return
 * @time 2019/7/1 14:52
 */
public class TrustAllCertsManager implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}