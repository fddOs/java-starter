package cn.ehai.rpc.feign;

/**
 * @Description:HttpCodeEnum
 * @author:方典典
 * @time:2018/11/26 9:40
 */
public enum HttpCodeEnum {
    /*
     *  本项目发生的程序异常
     */

    CODE_516(516),
    /*
     *  调用第三方服务异常
     */
    CODE_517(517),
    /*
     *  调用第三方服务引起本项目异常
     */
    CODE_518(518),
    /*
     *  服务响应成功
     */
    CODE_200(200);
    private int code;

    HttpCodeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
