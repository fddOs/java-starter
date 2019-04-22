package cn.seed.log.log.business;

/**
 * TODO
 *
 * @author lixiao
 * @date 2019-02-15 14:41
 */
public class ExtendsJson<T> {

    private T extend;

    public ExtendsJson(T obj){
        extend= obj;
    }

    public T getExtend() {
        return extend;
    }

    public void setExtend(T extend) {
        this.extend = extend;
    }
}
