package org.kafka.origin.rebalance;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类说明：再均衡监听器：由于在均衡之后，分区和消费和都是重新分配的。所以监听器在分区再均衡之前，把所有的偏移量记录到数据库中，当分区在均衡结束以后
 * 每个消费者再从数据库中读取分区的偏移量。然后调用seek方法，继续读取消息。
 * 这种方式处理比较稳，除了断电时候还有点毛病，任何服务器突然断电都有点毛病。
 *
 *
 * 只要遇到断电情况，都会对部分消息漏提交，断电情况才会出现。最终导致重复消费
 */
public class HandlerRebalance implements ConsumerRebalanceListener {

    /*模拟一个保存分区偏移量的数据库表*/
    public final static ConcurrentHashMap<TopicPartition,Long> partitionOffsetMap = new ConcurrentHashMap<TopicPartition,Long>();

    private final Map<TopicPartition, OffsetAndMetadata> currOffsets;
    private final KafkaConsumer<String,String> consumer;
    //private final Transaction  tr事务类的实例

    public HandlerRebalance(Map<TopicPartition, OffsetAndMetadata> currOffsets,
                            KafkaConsumer<String, String> consumer) {
        this.currOffsets = currOffsets;
        this.consumer = consumer;
    }

    //分区再均衡之前
    public void onPartitionsRevoked(
            Collection<TopicPartition> partitions) {
        final String id = Thread.currentThread().getId()+"";
        System.out.println(id+"-onPartitionsRevoked参数值为："+partitions);
        System.out.println(id+"-服务器准备分区再均衡，提交偏移量。当前偏移量为："
                +currOffsets);
        //我们可以不使用consumer.commitSync(currOffsets);
        //提交偏移量到kafka,由我们自己维护*/
        //开始事务

        //偏移量写入数据库
        System.out.println("分区偏移量表中："+partitionOffsetMap);
        for(TopicPartition topicPartition:partitions){
            partitionOffsetMap.put(topicPartition,currOffsets.get(topicPartition).offset());
        }
        consumer.commitSync(currOffsets);
        //提交业务数和偏移量入库  tr.commit
    }

    //分区再均衡完成以后
    public void onPartitionsAssigned(
            Collection<TopicPartition> partitions) {
        final String id = Thread.currentThread().getId()+"";
        System.out.println(id+"-再均衡完成，onPartitionsAssigned参数值为："+partitions);
        System.out.println("分区偏移量表中："+partitionOffsetMap);
        for(TopicPartition topicPartition:partitions){
            System.out.println(id+"-topicPartition"+topicPartition);
            //模拟从数据库中取得上次的偏移量
            Long offset = partitionOffsetMap.get(topicPartition);
            if(offset==null) continue;
            consumer.seek(topicPartition,partitionOffsetMap.get(topicPartition));
        }

    }
}
