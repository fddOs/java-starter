package cn.seed.db;

import cn.seed.db.generator.CodeGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JavaDbApplicationTests {

    @Test
    public void contextLoads() {
        CodeGenerator.genCode("action_item","ActionItem","操作类型日志表",
            "yd_message_center","app_yd_message_center","dra2kugci9t*a37QPcB#","test","方典典");

    }

}
