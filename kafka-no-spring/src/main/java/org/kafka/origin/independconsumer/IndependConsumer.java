package org.kafka.origin.independconsumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.kafka.origin.config.KafkaConst;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Mark老师   享学课堂 https://enjoy.ke.qq.com
 * 往期课程咨询芊芊老师  QQ：2130753077 VIP课程咨询 依娜老师  QQ：2133576719
 * 类说明：
 */
public class IndependConsumer {

    private static KafkaConsumer<String,String> consumer = null;
    public static final String SINGLE_CONSUMER_TOPIC = "single-consumer";

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                KafkaConst.LOCAL_BROKER);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());

        /*独立消息消费者*/
        consumer= new KafkaConsumer<String, String>(properties);
        List<TopicPartition> topicPartitionList = new ArrayList<TopicPartition>();
        List<PartitionInfo> partitionInfos
                = consumer.partitionsFor(SINGLE_CONSUMER_TOPIC);
        if(null!=partitionInfos){
            //将一个主题下所有的分区打包到list中
            for(PartitionInfo partitionInfo:partitionInfos){
                topicPartitionList.add(new TopicPartition(partitionInfo.topic(),
                        partitionInfo.partition()));
            }
        }
        //消费者读取分区list下的所有消息
        consumer.assign(topicPartitionList);
        try {

            while(true){
                ConsumerRecords<String, String> records
                        = consumer.poll(1000);
                for(ConsumerRecord<String, String> record:records){
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
