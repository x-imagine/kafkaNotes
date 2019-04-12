kafka配置文件server.properties重要的属性


1、broker.id： 
    每个节点配置不同的id，如果更换了服务器，只要把原来服务器的broker.id移植到新的服务器即可；从伸缩性来说，这一点方便很多
    
2、listeners：
    <!-- listeners=PLAINTEXT://:9092 默认端口9092 -->
    listeners=PLAINTEXT://:9093 如果想要在同一台机器上配置两个节点，则配置一个listeners端口为9093
    
3、zookeeper.connect：
    zookeeper.connect=localhost:2181/kafak 配置zookeeper集群 使用逗号分隔;记得加上后缀

4、log.dirs：
log.dirs=/tmp/kafka-logs #存放数据的目录
或者log.dirs=/tmp/kafka-logs-1,/tmp/kafka-logs-2,/tmp/kafka-logs-3 #指定3个目录
ps:可以指定多个存储目录来存储数据，kafka可以并行读写多个文件
   kafka会把数据均匀的分布在每个文件夹下。例如有一个主题有6个分区，则每个文件夹下存放2个分区


5、num.recovery.threads.per.data.dir
num.recovery.threads.per.data.dir=8 #表示kafka启动恢复数据时为每一个数据目录启动的线程数量，启动恢复结束后，将销毁这些线程，因此可以设置的大一些
例如log.dirs=/tmp/kafka-logs-1,/tmp/kafka-logs-2,/tmp/kafka-logs-3 配置了3个目录
则kafka会为每个目录启动8个线程，一共是24个线程，在启动完成后就会关闭这些线程。

6、auto.create.topics.enable
auto.create.topics.enable=true（默认值为true）  #允许在生产者和消费者在kafka自动创建主题；



7、num.partitions
num.partitions=1 分区数量为1
例如：
./kafka-topics.sh --zookeeper localhost:2181/kafka --create --replication-factor 1 --partitions 8 --topic my-topic #创建my-topic主题设置了8个分区
如何计算一个主题应该定义多少分区：如果每秒要处理1G的数据，而一个消费者每秒处理50M，则需要20个分区。原因是由于一个分区一个消息只能被同组消费者的一个消费者消费掉。
    
8、log.retention.hours
log.retention.hours=168 #数据的保存时间168小时，也就是7天

9、log.retention.bytes
log.retention.byte=-1 #一个分区的最大文件大小 -1表示没有限制。
log.retention.byte= 2 #设置为2M

log.retention.bytes和log.retention.minutes任意一个达到要求，都会执行删除


10、log.segment.bytes
log.segment.bytes=1073741824 #将分区数据分段保存 1G
需要注意的是每个片段在写满指定大小以后才开始计算
例子：
一个分区大小是1G，如果一个topic每天写入100M的数据在一个分区，那么这个分区将会在17天以后被删除。


11、log.segment.ms
设置片段的有效时间，默认情况下不开启，也不常用。


12、message.max.bytes
设置kafka能够处理消息最大字节数。默认值900kb，如果生产者发送的消息大小大于这个值，将被kafka拒绝。

