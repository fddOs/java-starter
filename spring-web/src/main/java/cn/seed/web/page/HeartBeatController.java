package cn.seed.web.page;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:运维心跳页面
 * @author:lixiao
 * @time:2018年3月13日 下午5:34:48
 */
@RestController
public class HeartBeatController {

    @GetMapping("/heartbeat")
    public String index() {
        LocalDateTime now = LocalDateTime.now();
        return now.toString();
    }
}
