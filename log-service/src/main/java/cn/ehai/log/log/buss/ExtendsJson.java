package cn.ehai.log.log.buss;

/**
 * TODO
 *
 * @author lixiao
 * @date 2019-02-15 14:41
 */
public class ExtendsJson<T> {

    private T extens;

    public ExtendsJson(T obj){
        extens= obj;
    }

    public T getExtens() {
        return extens;
    }

    public void setExtens(T extens) {
        this.extens = extens;
    }
}
