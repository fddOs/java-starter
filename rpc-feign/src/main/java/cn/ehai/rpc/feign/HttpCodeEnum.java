package cn.ehai.rpc.feign;

/**
 * @Description:HttpCodeEnum
 * @author:方典典
 * @time:2018/11/26 9:40
 */
public enum HttpCodeEnum {
    CODE_516(516), CODE_517(517), CODE_518(518), CODE_200(200);
    private int code;

    HttpCodeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
