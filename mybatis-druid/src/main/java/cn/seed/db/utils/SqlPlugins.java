package cn.seed.db.utils;

import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.plugin.Interceptor;

/**
 * sql插件
 * @author lixiao
 * @date 2019-04-16 21:46
 */
public enum SqlPlugins {
    INSTANCE;
    private List<Interceptor> interceptorList = new ArrayList<>(5);

    public void addSqlIntercepter(Interceptor interceptor){
        if(interceptor!=null){
            interceptorList.add(interceptor);
        }
    };
    public List<Interceptor> getSqlIntercepter(){
       return interceptorList;
    };
}
