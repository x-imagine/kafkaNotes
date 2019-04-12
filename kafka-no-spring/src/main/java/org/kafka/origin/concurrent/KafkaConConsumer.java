package org.kafka.origin.concurrent;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.kafka.origin.config.BusiConst;
import org.kafka.origin.config.KafkaConst;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Mark老师   享学课堂 https://enjoy.ke.qq.com
 * 往期课程咨询芊芊老师  QQ：2130753077 VIP课程咨询 依娜老师  QQ：2133576719
 * 类说明：多线程下正确的使用消费者，需要记住，一个线程一个消费者
 */
public class KafkaConConsumer {

    private static ExecutorService executorService
            = Executors.newFixedThreadPool(
                    BusiConst.CONCURRENT_PARTITIONS_COUNT);

    private static class ConsumerWorker implements Runnable{

        private ThreadLocal<KafkaConsumer<String, String>> consumerThreadLocal =new ThreadLocal<KafkaConsumer<String, String>>(){
            @Override
            protected KafkaConsumer<String, String> initialValue() {
                Map<String, Object> properties
                        = KafkaConst.consumerConfigMap("concurrent",
                        StringDeserializer.class,
                        StringDeserializer.class);
                return new KafkaConsumer<String, String>(properties);
            }
        };


        public ConsumerWorker( String topic) {
            consumerThreadLocal.get().subscribe(Collections.singletonList(topic));
        }

        public void run() {
            final String id = Thread.currentThread().getId()
                    +"-"+System.identityHashCode(consumerThreadLocal.get());
            try {
                while(true){
                    ConsumerRecords<String, String> records
                            = consumerThreadLocal.get().poll(500);
                    for(ConsumerRecord<String, String> record:records){
                        System.out.println(id+"|"+String.format(
                                "主题：%s，分区：%d，偏移量：%d，" +
                                        "key：%s，value：%s",
                                record.topic(),record.partition(),
                                record.offset(),record.key(),record.value()));
                        //do our work
                    }
                }
            } finally {
                consumerThreadLocal.get().close();
            }
        }
    }

    public static void main(String[] args) {
        /*消费配置的实例*/
        Map<String, Object> config
                = KafkaConst.consumerConfigMap("concurrent",
                StringDeserializer.class,
                StringDeserializer.class);
        for(int i = 0; i<BusiConst.CONCURRENT_PARTITIONS_COUNT; i++){
            executorService.submit(new ConsumerWorker(BusiConst.CONCURRENT_USER_INFO_TOPIC));
        }
    }




}
