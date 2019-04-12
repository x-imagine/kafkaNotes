package org.kafka.origin.easyuse;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.kafka.origin.config.BusiConst;

import java.util.Collections;
import java.util.Properties;

/**
 * 开发步骤：
 * 1、配置属性对象
 * 2、创建消费者
 * 3、消费者订阅
 * 4、主动拉取信息
 * 5、关闭资源
 *
 */
public class HelloKafkaConsumer {

    public static void main(String[] args) {
        //1、配置属性对象
        Properties properties =new Properties();
        //设置kafka地址，官方推荐至少写两个,不用把kafka所有的节点写出来，kafka会智能的连接上集群
        properties.put("bootstrap.servers","127.0.0.1:9092");
        //设置key为string类型解码器
        properties.put("key.deserializer",StringDeserializer.class);
        //设置value为string类型解码器
        properties.put("value.deserializer",StringDeserializer.class);
        //必须设置groupId
        properties.put(ConsumerConfig.GROUP_ID_CONFIG,"test");
        //2、创建消费者
        KafkaConsumer<String,String> consumer =new KafkaConsumer<String, String>(properties);

        try {
            //3、消费者订阅
            consumer.subscribe(Collections.singleton(BusiConst.HELLO_TOPIC));
            while (true){
                //4、主动拉取信息，kafka中没有推送的概念
                ConsumerRecords<String, String> records = consumer.poll(500);
                for (ConsumerRecord<String,String> record:records) {
                    System.out.println(String.format("topic:%s,分区：%d,偏移量：%d," +
                                    "key:%s,value:%s",record.topic(),record.partition(),
                            record.offset(),record.key(),record.value()));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //5、关闭资源
            consumer.close();
        }

    }
}
