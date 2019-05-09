package cn.seed.common.utils;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
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
    public static <T extends BaseRowModel> OutputStream exportDefaultSheet(OutputStream out, ExcelTypeEnum type,
                                                                           List<T> data, Class c) {
        return exportSheet(out, type, data, c, 1, "sheet");
    }

    /**
     * Excel导出
     *
     * @param out
     * @param type
     * @param data
     * @param c
     * @param sheetNum
     * @param sheetName
     * @return java.io.OutputStream
     * @author 方典典
     * @time 2019/5/8 16:04
     */
    public static <T extends BaseRowModel> OutputStream exportSheet(OutputStream out, ExcelTypeEnum type, List<T>
            data, Class c, int sheetNum, String sheetName) {
        ExcelWriter writer = new ExcelWriter(out, type);
        //写第一个sheet, sheet1  数据全是List<String> 无模型映射关系
        Sheet sheet1 = new Sheet(sheetNum, 0, c, sheetName, new ArrayList<>());
        writer.write(data, sheet1);
        writer.finish();
        try {
            out.close();
        } catch (IOException e) {
            LoggerUtils.error(ExcelUtils.class, new Object[]{out, type, data, c}, e);
        }
        return out;
    }
}
