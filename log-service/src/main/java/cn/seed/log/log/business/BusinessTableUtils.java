package cn.seed.log.log.business;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

/**
 * 保存业务日志需要记录的数据库表名
 *
 * @author lixiao
 * @date 2019-03-08 13:27
 */
public class BusinessTableUtils {

     private static ArrayList<String> tables = new ArrayList<>();

     public static void addBusiTables(String table){
         if(StringUtils.isEmpty(table)){
             return;
         }
         tables.add(table);
     }

    public static void addBusiTables(String[] strings){
        if(strings!=null&&strings.length>0){
            for (String s :strings){
                if(!tables.contains(s)){
                    tables.add(s);
                }
            }
        }
    }

    public static ArrayList<String> getBusiTables() {
        return tables;
    }
}
