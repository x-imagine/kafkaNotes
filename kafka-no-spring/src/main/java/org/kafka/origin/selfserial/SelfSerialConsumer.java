package org.kafka.origin.selfserial;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.kafka.origin.config.BusiConst;
import org.kafka.origin.config.KafkaConst;
import org.kafka.origin.vo.DemoUser;

import java.util.Collections;

/**
 * @author Mark老师   享学课堂 https://enjoy.ke.qq.com
 * 往期课程咨询芊芊老师  QQ：2130753077 VIP课程咨询 依娜老师  QQ：2133576719
 * 类说明：
 */
public class SelfSerialConsumer {

    private static KafkaConsumer<String,DemoUser> consumer = null;

    public static void main(String[] args) {

        /*消息消费者*/
        consumer = new KafkaConsumer<String, DemoUser>(
                KafkaConst.consumerConfig("selfserial",
                StringDeserializer.class,
                SelfDeserializer.class));
        try {
            consumer.subscribe(Collections.singletonList(BusiConst.SELF_SERIAL_TOPIC));
            while(true){
                ConsumerRecords<String, DemoUser> records
                        = consumer.poll(500);
                for(ConsumerRecord<String, DemoUser> record:records){
                    System.out.println(String.format(
                            "主题：%s，分区：%d，偏移量：%d，key：%s，value：%s",
                            record.topic(),record.partition(),record.offset(),
                            record.key(),record.value()));
                    //do our work
                }
            }
        } finally {
            consumer.close();
        }
    }




}
