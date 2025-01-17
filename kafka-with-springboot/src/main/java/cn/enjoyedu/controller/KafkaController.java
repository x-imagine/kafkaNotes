package cn.enjoyedu.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mark老师   享学课堂 https://enjoy.ke.qq.com
 * 往期课程咨询芊芊老师  QQ：2130753077 VIP课程咨询 依娜老师  QQ：2133576719
 * 类说明：
 */
@RestController
@RequestMapping("/kafka")
public class KafkaController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @RequestMapping(value = "/send")
    public String sendKafka(@RequestParam(required = false) String key,
                            @RequestParam(required = false) String value) {
        try {
            logger.info("kafka的消息={}", value);
            kafkaTemplate.send("test", key, value);
            return "发送kafka成功";
        } catch (Exception e) {
            logger.error("发送kafka异常：", e);
            return "发送kafka失败";
        }
    }

    @RequestMapping(value = "/sendAck")
    public String sendKafkaAck(@RequestParam(required = false) String key, @RequestParam(required = false) String value) {
        try {
            logger.info("kafka的消息={}", value);
            kafkaTemplate.send("testAck", key, value);
            return "发送kafka成功";
        } catch (Exception e) {
            logger.error("发送kafka异常：", e);
            return "发送kafka失败";
        }
    }
}
