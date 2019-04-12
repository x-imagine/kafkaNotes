一、概述
查看《kafka相关概念.txt》





二、kafka安装
参考《kafka安装和配置》






三、相关命令工具
参考《kafka管理工具命令.txt》




四、kafka配置文件server.properties重要的属性
参考《kafka配置文件.md》


五、影响kafka性能的因素
磁盘大小：如果分区片段设置的比较大，则需要更大的磁盘
磁盘吞吐量（IOPS）：吞吐量高对生产者有利，当生产者向kafka发送消息后等待kafka的回应。如果kafka吞吐量很差，或者集群数量比较多，将大大影响生产者收到回应的时间
内存大小：内存主要影响了消费者消费。kafka会把最新未被读取的消息存放在内存中，因此如果内存越大会让消费者消费的效率提升很多。
带宽大小：kafka在网络中传输数据的数据量很大需要带宽
cpu：如果传递消息开启了解压缩功能就需要好一些的cpu。





六、如何计算broker数量
考虑要保存多少数据，每个broker上有多少空间可用
例如
10TB的数据，每个broker可以保持2TB，则需要5个broker
同样的条件如果启用了复制功能，就需要10个broker
因为每个broker上存放的真实数据只有1G，还有1G是备份数据。



七、kafka api基本使用

消费者：org.kafka.origin.easyuse.HelloKafkaConsumer
生产者：org.kafka.origin.easyuse.HelloKafkaProducer(发送消息后不做处理)


八、生产者分析：
（1）生产者发送消息后的3中数据里方式
①、生产者：org.kafka.origin.easyuse.HelloKafkaProducer(发送消息后不做处理)
②、生产者：org.kafka.origin.easyuse.HelloKafkaProducer2(发送消息后用同步非阻塞的方式接收应答信息)
③、生产者：org.kafka.origin.easyuse.HelloKafkaProducer3(发送消息后用异步的方式接收应答信息)一般来说异步的方式更为常用一些
（2）多线程使用同一个生产者实例
参考：org.kafka.origin.concurrent.KafKaConProducer

（3）生产者参数配置：
参考：org.apache.kafka.clients.producer.ProducerConfig
大部分参数都有合理的默认值，有几个参数对性能、内存、可靠性有很大影响，需要手动设置
具体参考《kafka生产者参数.md》


九、生产者发送消息的顺序保证


Kafka 可以保证同一个分区里的消息是有序的。也就是说，如果生产者一定的顺序发送消息， broker 就会按照这个顺序把它们写入分区，消费者也会按照同样的顺序读取它们。在某些情况下， 顺序是非常重要的。
反例，如果往一个账户存入100 元再取出来，这个与先取钱再存钱是截然不同的！不过，有些场景对顺序不是很敏感。
如果把retires设为非零整数，同时把max.in.flight.request.per.connection设为比1 大的数，那么，如果第一个批次消息写入失败，而第二个批次写入成功， broker 会重试写入第一个批次。如果此时第一个批次也写入成功，那么两个批次的顺序就反过来了。
一般来说，如果某些场景要求消息是有序的，那么消息是否写入成功也是很关键的，所以不建议把retires设为0 。可以把max.in.flight.request.per.connection 设为1，这样在生产者尝试发送第一批消息时，就不会有其他的消息发送给broker 。不过这样会严重影响生产者的吞吐量，所以只有在对消息的顺序有严格要求的情况下才能这么做。

保证顺序简单来说：（场景一定是对消息顺序有严格要求下使用）
①retires>0
②max.in.flight.request.per.connection=1  #表示上一个消息成功以后才能发下一个消息
③只有一个分区



十、消息序列化和反序列化
kafka有一些基本的序列化类和反序列化类
1、自定义序列化类/反序列化类：
序列化器：org.kafka.origin.selfserial.SelfSerializer #实现Serializer接口的serialize方法
反序列化器：org.kafka.origin.selfserial.SelfDeserializer #实现Deserializer接口的deserialize方法
序列化器测试类参考：org.kafka.origin.selfserial.SelfSerialProducer
反序列化器测试类参考：org.kafka.origin.selfserial.SelfSerialConsumer
ps：
①、少用自定义的方式，多使用第三方序列化方式。从性能，和扩展角度来看，第三方序列化方式更为灵活，性能也更好一些，
②、至于传输过程是一个对象，把对象转化为json字符串在序列化，这种方式性能可能不够好。
2、常用的第三方序列化/反序列化 框架有：messagepack、kyro、protobuf、avro
互联网应用优先使用avro、protobuf
①avro原理：传输数据同时传输一个描述文本（描述文本用于解析数据），当然不能每次都传输描述文本，太浪费带宽了，因此引入sckema注册表机制，生产者会将描述文本注册到注册表中心，消费者从生产者中心取描述文本。不是每次取，当描述文本有变化时会通知消费者来取。
具体使用参考：https://cloud.tencent.com/developer/article/1336568
ps：序列化是中小公司还是使用json字符串序列化方式


十一、分区器
1、kafka提供的分区器：自定hash算法计算消息的key后映射到分区上。
2、kafka的分区只能增加不能减少，使用kafka时，创建了一个主题，要设计好分区数量，不要随意增加分区。
3、自定义分区器：满足一些特定的业务
参考org.kafka.origin.selfpartition.SelfPartitioner  实现Partitioner接口的partition方法
生产者使用自定义分区器发送消息：org.kafka.origin.selfpartition.SelfPartitionProducer



十二、消费者分析
kafka里生产者的生产速度要远比消费者消费消息的速度高很多。
消费者必须标记属于某一个群组，不同的群组可以重复消费同一个topic，类似广播。
一个分区只能被同一个组的一个消费者消费。
同一组下的消费可以消费同一个topic下的多个分区。
换句话说，不同组的消费者可以同时消费同一个主题的同一个分区。

为了提高消费消息的效率只能通过增加分区的方式，
或者创建多个消费者组消费同一个topic消息（但是这种方式不好，重复消费的问题很严重。）
假设场景：一个主题有4个分区。第一个消费者连接，则第一个消费者会被设定为groupId消费者组的群主。该消费者消费四个分区上的消息。
此时又有一个消费者连接进来，这时kafka会进入分区再均衡的阶段，这个阶段内所有的消费者不能消费消息，分区再均衡就是让消费者组中的群主重新分配分区给消费者。分区方式是随机且均衡的方式。
群主可能把两个分区分配给第二个消费者消费。
以此类推到第三个消费者加入组，kafka进入分区再均衡（1，2，1分配）；第四个消费者加入组，kafka进入分区再均衡（1、1、1、1分配）。
后续又加入了几个消费者，此时不再进行分区再均衡（如果前四个消费者没有死亡的话），多余的消费者只能傻等着。
因此可以看出，消费者数量尽量和分区数量一致，才能达到效率最高。
具体参考：org.kafka.origin.consumergroup #consumer-group-test有两个分区，有A(三个消费者)、B(两个消费者)、C（一个消费者），生产者发送50条消息
结果：A1（收到25条消息）、A2（收到25条消息）、A3（收到0条消息）、B1（收到25条消息）、B2、C（收到50条消息）.

总结：使用过程中增加分区数量和变更消费者数量都会导致分区再均衡，分区再均衡时消费者不能消费。严重影响整体性能。其中变更消费者数量的行为包括消费者上线，断线，如果在线消费者和分区相等，其他消费者的断线和在线也不影响了。
分区再均衡机制是kafka高性能的表现。
消费者组的群主控制着kafka句群中的对该组对topic的控制器，没有消费者（包括群主）和控制器保持着心跳通信。通过心跳反馈一个消费者组的成员变化和群主选举和是否进行分区再均衡。
ps：早期心跳检查机制是放在消费者的poll方法中，但是如果拉取消息后处理业务的时间过长，将导致下一次poll被推迟，此时没有发送心跳报文，将导致控制器认为这个消费者死了。就会触发不必要的分区再均衡。因此在0.10.00版本以后心跳机制被独立的一个线程上发送了。

十三、同一个消费者可以消费多个topic
public void subscribe(Collection<String> topics) #主题集合订阅
public void subscribe(Pattern pattern, ConsumerRebalanceListener listener)#正则方式订阅
public void unsubscribe()#取消所有订阅

十四、消费者poll方式
主要作用：
①连接kafka后加入消费者组（可能很多个组）
②拉取要处理的分区号


十五、多线程下的消费者是不安全的
原因是当多线程下调用poll时会引起多个线程拉取到同一个主题下同一个分区的消息。也就是重复消费问题
因此正确做法时一个线程一个消费者。可以使用threadLocal实例。类似数据库连接的做法，一个线程一个消费者实例。
参考：org.kafka.origin.concurrent.KafkaConConsumer 


十六、影响消费者性能的属性

消费者有很多属性可以设置，大部分都有合理的默认值，无需调整。有些参数可能对内存使用，性能和可靠性方面有较大影响。可以参考org.apache.kafka.clients.consumer包下ConsumerConfig类。
①、fetch.min.bytes（缺省为1个字节）
    每次fetch请求时，server应该返回的最小字节数。如果没有足够的数据返回，请求会等待，直到足够的数据才会返回。。多消费者下，可以设大这个值，以降低broker的工作负载
②fetch.wait.max.ms	（缺省为500个毫秒）
    如果没有足够的数据能够满足fetch.min.bytes，则此项配置是指在应答fetch请求之前，server会阻塞的最大时间。。
    和fetch.min.bytes结合起来，要么满足数据的大小，要么满足时间，就看哪个条件先满足。
③max.partition.fetch.bytes
    指定了服务器从每个分区里返回给消费者的最大字节数，默认1MB。
    假设一个主题有20个分区和5个消费者，那么每个消费者至少要有4MB的可用内存来接收记录，而且一旦有消费者崩溃，这个内存还需更大。注意，这个参数要比服务器的message.max.bytes更大，否则消费者可能无法读取消息。
④session.timeout.ms
    如果consumer在这段时间内没有发送心跳信息，则它会被认为挂掉了。默认3秒。
⑤auto.offset.reset	（默认值是latest）
    消费者在读取一个没有偏移量的分区或者偏移量无效的情况下，如何处理。
    latest，从最新的记录开始读取
    earliest，表示消费者从起始位置读取分区的记录。
注意：默认值是latest，意思是说，在偏移量无效的情况下，消费者将从最新的记录开始读取数据（在消费者启动之后生成的记录），
可以先启动生产者，再启动消费者，观察到这种情况。观察代码，在模块kafka-no-spring下包hellokafka中。
⑥enable.auto.commit	（默认值true）
    表明消费者是否自动提交偏移。为了尽量避免重复数据和数据丢失，可以改为false，自行控制何时提交。
⑦partition.assignment.strategy（默认为Range）
    分区分配给消费者的策略。系统提供两种策略。默认为Range。允许自定义策略。
    Range
    把主题的连续分区分配给消费者。例如，有主题T1和T2，各有3个分区，消费者C1和C2，则可能的分配形式为：
    C1: T1(0，1),T2(0,，1)
    C2: T1(2),T2(2)
    RoundRobin
    把主题的分区循环分配给消费者。例如，有主题T1和T2，各有3个分区，消费者C1和C2，则可能的分配形式为：
    C1: T1(0，2),T2(1)
    C2: T1(1),T2(0，2)
    自定义策略
    extends 类AbstractPartitionAssignor，然后在消费者端增加参数：
    properties.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, 类.class.getName());即可。
⑧client.id	
    当向server发出请求时，这个字符串会发送给server。目的是能够追踪请求源头，以此来允许ip/port许可列表之外的一些应用可以发送信息。这项应用可以设置任意字符串，因为没有任何功能性的目的，除了记录和跟踪。
⑨max.poll.records
    控制每次poll方法返回的的记录数量。
⑩receive.buffer.bytes和send.buffer.bytes
    指定TCP socket接受和发送数据包的缓存区大小。如果它们被设置为-1，则使用操作系统的默认值。如果生产者或消费者处在不同的数据中心，那么可以适当增大这些值，因为跨数据中心的网络一般都有比较高的延迟和比较低的带宽。




十七、消费者提交
1、在消费者自动提交偏移量的情况下可能出现的两个错误现象：
①如果某个消费者处理了几个消息没有提交就死掉了，kafka再分区再均衡后，另一个消费者从未提交的地方开始拉取消息。这样就会出现消息重复消费。 
②如果某个消费者处理了几个消息没有提交就死掉了，并提交的偏移量大于消费者正在处理的消息的偏移量，kafka再分区再均衡后，另一个消费者从未提交的地方开始拉取消息。这样就会出现部分消息没有被处理过。 


2、消费者提交方式：
①自动提交:
设置enable.auto.comnit被设为 true（默认是true），消费者会自动把从poll()方法接收到的最大偏移量提交上去。
auto.commit.interval.ms 提交时间间隔，默认值是5s。自动提交是在轮询里进行的，每5s提交一次，消费者每次在进行轮询时会检査是否该提交偏移量了，如果是，那么就会提交从上一次轮询poll()返回的偏移量。
不过,在使用这种简便的方式之前,需要知道它将会带来怎样的结果。
ps：如果把auto.commit.interval.ms 的值修改的很小，那很稍微避免以上提到的两种错误情况，但是无法真正解决，反而导致kafka性能下降。

手动提交：
②手动提交》同步提交 参考cn.enjoyedu.commit.CommitSync
    properties.put("enable.auto.commit",false); #设置手动提交
    consumer.commitSync(); #设置手动同步提交，再每次poll拉取一批消息以后，这批消息经过业务处理完之后，调用commitSync方法。
ps：commitSync方法提交之前不会再拉取更多的消息。如果提交失败时会不断的重试提交。
③手动提交》异步提交 参考cn.enjoyedu.commit.CommitAsync
    properties.put("enable.auto.commit",false); #设置手动提交
    consumer.commitAsync(); #异步提交
ps：异步提交存在失败的情况，失败以后不会重试。
    public void commitAsync(OffsetCommitCallback callback) 在异步提交时，增加回调。当失败时通过回调方法处理。
③手动提交》同步提交+异步提交 （开发过程中推荐的方式）参考cn.enjoyedu.commit.SyncAndAsync
        properties.put("enable.auto.commit",false); #设置手动提交
        try {
            //接收消息
                consumer.commitAsync();
        } catch (CommitFailedException e) {
        } finally {
                consumer.commitSync();
        }
④手动提交》特定提交 参考cn.enjoyedu.commit.CommitSpecial
        properties.put("enable.auto.commit",false); #设置手动提交
        Map<TopicPartition, OffsetAndMetadata> currOffsets = new HashMap<TopicPartition, OffsetAndMetadata>();
        currOffsets.put(new TopicPartition(record.topic(),record.partition()),
                            new OffsetAndMetadata(record.offset()+1,"no meta"));//没消费一个消息，offset偏移量要加一。
                    if(count%10==0){
                        consumer.commitAsync(currOffsets,null);//每处理完10条消息提交一次，通过currOffsets对象进行指定偏移量提交
                    }
ps：
1、采用什么方式消费根据消息的重要性来决定。
2、特定提交可以每处理一条消息就提交一次。
3、消费方一般都是使用线程池处理消息的。
4、手动提交情况下使用同步提交+异步提交的组合方式提交正常情况下都会提交成功，除了消费者断电了。可能会引起消息重复消费。因此。。。


5、当消费者消费过程异常时如何处理？
解决方案就是：每当处理一条消息并且成功以后向mysql/redis记录一下offset,或者处理多条或者是一批消息时，只要处理成功了，就向mysql/redis记录一下offset。使用了这种方案以后向kafka提交offset有点多余，但是为了保证kafka数据的完整性还是要提交一下。
由于向mysql/redis记录offset操作和消费消息的操作绑定在一个事务中，因此即使异常了也不怕。也不会出现重复消费的现象。
但是仅仅记录了offset还是不够的。当分区再均衡时，所有的分区和消费者都是重新搭配的。因此使用了以上方案有一个问题还要解决
6、分区再均衡以后，消费者怎么知道当前分区的上一个消费者消费到哪个偏移量offset?
因此需要使用在均衡监听器,它的作用就是在分区再均衡之前向mysql/redis保存一下offset，分区再均衡之后向mysql/redis读取offset.


十八、自定义再均衡监听器 参考org.kafka.origin.rebalance.HandlerRebalance
1、在发生分区再均衡之前之后调用监听器中的方法：
    在分区再均衡之前调用onPartitionsRevoked #记录每个消费者的每个分区的的偏移量。并把这个偏移量记录到mysql/redis中，
    分区在均衡之后调用onPartitionsAssigned #kafka为每个消费者分配了分区之后，从mysql/redis中拉取偏移量，再调用seed（偏移量），从偏移量的位置开始读取消息。

2、如何从特定的偏移量读取记录：seek(offset) 从offset位置开始读取。
其他方法：
    seekToBeginning 从分区的起始点读取
    seekToEnd 从最新的位置开始读取

3、监听器的使用:参考：org.kafka.origin.rebalance.ConsumerWorker
//添加监听器
 consumer.subscribe(Collections.singletonList(BusiConst.REBALANCE_TOPIC),
                new HandlerRebalance(currOffsets,consumer));
//每次消费完一个消息时需要重新设置offset。
currOffsets.put(topicPartition,new OffsetAndMetadata(offset,
                            "no"));

ps：
由于kafka消费者读取消息的方式只有从分区下标为0的地方开始读取和从消费者连接的那一个时刻开始读取，很明显这两种方式都是不能满足我们的需求，
我们希望消费者在断线重连或者分区再分配以后能够在我们指定offset位置开始消费消息，因此我们需要在消费者消费了一匹消息时，把offset保存到mysql/redis。 记录offset和消费消息处于同一个事务中，因此系统异常了也不会造成重复消费或者消息丢失。

总结十七、十八，只有结合把消费后把offset记录到redis/mysql和再均衡监听器结合在一起才能保证消息的正确消费。


十九、消费者优雅退出
异常时调用consumer.wakeup();//从whlie循环中的poll退出，之后抛出异常
抛出异常之后，向kafka提交offest，再调用consumer.close()。
close方法会通知kafka集群触发分区再均衡，如何暴力的退出可能发生一些意想不到的结果



二十、独立消费者 参考org.kafka.origin.independconsumer.IndependConsumer
一个消费者读取所有的分区，不需要调用subscribe方法。
consumer.assign(topicPartitionList);//根据分区列表读取消息
ps：因此我们想要读取某个特定分区的消息时可以调用assign方法。



二十一、spring继承kafka
参考《spring-kafka开发步骤.md》
spring上的配置套路：连接配置，工厂配置，模板配置。如果spring需要配置些什么内容需要参考原生API的一些内容。
































