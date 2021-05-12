package org.kafka.origin.rebalance;


import org.kafka.origin.config.BusiConst;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 类说明：设置了再均衡监听器的消费者
 */
public class RebalanceConsumer {

    public static final String GROUP_ID = "rebalanceconsumer";

    private static ExecutorService executorService = Executors.newFixedThreadPool(BusiConst.CONCURRENT_PARTITIONS_COUNT);

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < BusiConst.CONCURRENT_PARTITIONS_COUNT; i++) {
            executorService.submit(new ConsumerWorker(false));
        }
        Thread.sleep(5000);
        //用来被停止，观察保持运行的消费者情况
        new Thread(new ConsumerWorker(true)).start();
    }
}
