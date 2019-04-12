package org.kafka.origin.concurrent;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.kafka.origin.config.BusiConst;
import org.kafka.origin.config.KafkaConst;
import org.kafka.origin.vo.DemoUser;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KafKaConProducer {

    //发送消息的个数
    private static final int MSG_SIZE = 1000;
    //负责发送消息的线程池
    private static ExecutorService executorService = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors());
    private static CountDownLatch countDownLatch
            = new CountDownLatch(MSG_SIZE);

    private static DemoUser makeUser(int id){
        DemoUser demoUser = new DemoUser(id);
        String userName = "xiangxue_"+id;
        demoUser.setName(userName);
        return demoUser;
    }

    /*发送消息的任务*/
    private static class ProduceWorker implements Runnable{

        private ProducerRecord<String,String> record;
        private KafkaProducer<String,String> producer;

        public ProduceWorker(ProducerRecord<String, String> record,
                             KafkaProducer<String, String> producer) {
            this.record = record;
            this.producer = producer;
        }

        public void run() {
            final String id = Thread.currentThread().getId()
                    +"-"+System.identityHashCode(producer);
            try {
                producer.send(record, new Callback() {
                    public void onCompletion(RecordMetadata metadata,
                                             Exception exception) {
                        if(null!=exception){
                            exception.printStackTrace();
                        }
                        if(null!=metadata){
                            System.out.println(id+"|"
                                    +String.format("偏移量：%s,分区：%s",
                                    metadata.offset(),metadata.partition()));
                        }
                    }
                });
                System.out.println(id+":数据["+record+"]已发送。");
                countDownLatch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        KafkaProducer<String,String> producer
                = new KafkaProducer<String, String>(
                KafkaConst.producerConfig(StringSerializer.class,
                        StringSerializer.class));
        try {
            for(int i=0;i<MSG_SIZE;i++){
                DemoUser demoUser = makeUser(i);
                ProducerRecord<String,String> record
                        = new ProducerRecord<String,String>(
                        BusiConst.CONCURRENT_USER_INFO_TOPIC,null,
                        System.currentTimeMillis(),
                        demoUser.getId()+"", demoUser.toString());
                //投递任务时，每个任务的消息是不同势力，但是生产者只有一个实例
                executorService.submit(new ProduceWorker(record,producer));
            }
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.close();
            executorService.shutdown();
        }
    }

}
