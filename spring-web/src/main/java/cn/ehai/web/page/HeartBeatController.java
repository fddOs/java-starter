package cn.ehai.web.page;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Description:运维心跳页面
 * @author:lixiao
 * @time:2018年3月13日 下午5:34:48
 */
@Controller
public class HeartBeatController {

    @GetMapping("/heartbeat")
    public String index(ModelMap model) {
        LocalDateTime now = LocalDateTime.now();
        model.addAttribute("timestamp", now.toString());
        return "heartbeat";
    }
}
