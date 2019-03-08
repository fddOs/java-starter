package cn.ehai.log.log.business;

import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

/**
 * 保存业务日志需要记录的数据库表名
 *
 * @author lixiao
 * @date 2019-03-08 13:27
 */
public class BusinessTableUtils {

     private static ArrayList<String> busiTables = new ArrayList<>();

     public static void addBusiTables(String table){
         if(StringUtils.isEmpty(table)){
             return;
         }
         busiTables.add(table);
     }

    public static ArrayList<String> getBusiTables() {
        return busiTables;
    }
}
