package org.kafka.origin.commit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.kafka.origin.config.BusiConst;
import org.kafka.origin.config.KafkaConst;

import java.util.Collections;
import java.util.Properties;

/**
 * @author Mark老师   享学课堂 https://enjoy.ke.qq.com
 * 往期课程咨询芊芊老师  QQ：2130753077 VIP课程咨询 依娜老师  QQ：2133576719
 * 类说明：特定提交
 */
public class CommitSpecial {
    public static void main(String[] args) {
        /*消息消费者*/
        Properties properties = KafkaConst.consumerConfig("CommitSync", StringDeserializer.class, StringDeserializer.class);
        /*取消自动提交*/
        properties.put("enable.auto.commit",false);
        KafkaConsumer<String,String> consumer = new KafkaConsumer<String, String>(properties);
        //TODO
        int count = 0;
        try {
            consumer.subscribe(Collections.singletonList(BusiConst.CONSUMER_COMMIT_TOPIC));
            while(true){
                ConsumerRecords<String, String> records = consumer.poll(500);
                for(ConsumerRecord<String, String> record:records){
                    System.out.println(String.format(
                            "主题：%s，分区：%d，偏移量：%d，key：%s，value：%s",
                            record.topic(),record.partition(),record.offset(),
                            record.key(),record.value()));
                    //TODO
                    count++;
                }
            }
        } finally {
            consumer.close();
        }
    }
}
