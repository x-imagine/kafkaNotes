package org.kafka.origin.selfpartition;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;

import java.util.List;
import java.util.Map;

/**
 * @author Mark老师   享学课堂 https://enjoy.ke.qq.com
 * 往期课程咨询芊芊老师  QQ：2130753077 VIP课程咨询 依娜老师  QQ：2133576719
 * 类说明：自定义分区器，以value值进行分区
 */
public class SelfPartitioner implements Partitioner {


    public int partition(String topic, Object key, byte[] keyBytes,
                         Object value, byte[] valueBytes, Cluster cluster) {
        //1、获取集群分区
        List<PartitionInfo> partitionInfos = cluster.partitionsForTopic(topic);
        int num = partitionInfos.size();
        //2、分区算法根据业务进行设计
        int parId = ((String)value).hashCode()%num;
        return parId;
    }

    public void close() {
        //do nothing
    }

    public void configure(Map<String, ?> configs) {
        //do nothing
    }

}
