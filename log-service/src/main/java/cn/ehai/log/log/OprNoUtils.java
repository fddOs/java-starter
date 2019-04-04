package cn.ehai.log.log;

/**
 * 操作人处理类
 *
 * @author lixiao
 * @date 2019-03-18 18:40
 */
public class OprNoUtils {

    /**
     * 操作人取5位
     * @param oprNo
     * @return java.lang.String
     * @author lixiao
     * @date 2019-03-18 18:42
     */
    public static String  handlerOprNo(String oprNo){
        if(oprNo!=null&&oprNo.length()>5){
            oprNo = oprNo.substring(0,5);
        }
        return oprNo==null?"":oprNo;
    }

}
