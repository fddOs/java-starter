package cn.ehai.common.utils;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Excel工具类
 *
 * @author:方典典
 * @time:2018/10/30 10:55
 */
public class ExcelUtils {
    /**
     * @param out
     * @param type
     * @param data
     * @param c
     * @return void
     * @Description:Excel导出
     * @exception:
     * @author: 方典典
     * @time:2018/10/30 11:06
     */
    public static <T extends BaseRowModel> void export(OutputStream out, ExcelTypeEnum type, List<T> data, Class c) {
        ExcelWriter writer = new ExcelWriter(out, type);
        //写第一个sheet, sheet1  数据全是List<String> 无模型映射关系
        Sheet sheet1 = new Sheet(1, 0, c);
        writer.write(data, sheet1);
        writer.finish();
        try {
            out.close();
        } catch (IOException e) {
            LoggerUtils.error(ExcelUtils.class, new EHIExceptionLogstashMarker(new EHIExceptionMsgWrapper
                    (ExcelUtils.class.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(), new
                            Object[]{out, type, data, c}, ExceptionUtils.getStackTrace(e))));
        }
    }
}
