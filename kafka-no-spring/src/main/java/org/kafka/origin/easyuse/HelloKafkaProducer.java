package org.kafka.origin.easyuse;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.kafka.origin.config.BusiConst;

import java.util.Properties;


/**
 * 开发步骤：
 * 1、配置属性对象
 * 2、创建生产者
 * 3、创建消息
 * 4、发送消息
 * 5、关闭资源
 * <p>
 * 生产者实例不要随便创建，生产者实际上占用了一定的资源，并且生产者实例是并发安全的，可以多线程下共用一个实例。
 */
public class HelloKafkaProducer {

    public static void main(String[] args) {
        //1、配置属性对象
        Properties properties = new Properties();
        //设置kafka地址，官方推荐至少写两个,不用把kafka所有的节点写出来，kafka会智能的连接上集群
        properties.put("bootstrap.servers", "127.0.0.1:9092");
        //消息在发送之前都要序列化，要指定k-v使用的什么类型的序列化器
        //设置key为string类型序列化器》编码
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        //设置value为string类型序列化器》编码
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        //2、创建生产者
        KafkaProducer producer = new KafkaProducer(properties);

        try {
            //3、创建消息
            ProducerRecord<String, String> record;
            //key用于kafka分区器判断将消息放到哪个分区，如果key为null，kafka会通过算法均匀的每个分区上
            record = new ProducerRecord<>(BusiConst.HELLO_TOPIC, "abc", "hi");
            //4、发送消息
            //发送消息时，正常情况下不是一条一条发送的，达到了某种条件才会发送消息，例如消息大小达到一定的值，或者时间到了
            //在没有发送之前，消息都是存放在生产者的缓存中，缓存使用的是双端队列数据结构。有新消息都会插入到尾部
            producer.send(record);
            System.out.println("already send msg");
        } catch (Exception e) {

        } finally {
            //5、关闭资源
            producer.close();
        }

    }
}