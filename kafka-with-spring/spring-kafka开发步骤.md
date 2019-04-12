一、pom引入kafka相关依赖
二、配置生产者相关配置
    <!-- 1、定义producer的参数 -->
    <bean id="producerProperties" class="java.util.HashMap">
        <constructor-arg>
            <map>
                <entry key="bootstrap.servers" value="${bootstrap.servers}" />
                <entry key="key.serializer"
                       value="org.apache.kafka.common.serialization.StringSerializer" />
                <entry key="value.serializer"
                       value="org.apache.kafka.common.serialization.StringSerializer" />
            </map>
        </constructor-arg>
    </bean>
    <!-- 2、创建kafkatemplate需要使用的producerfactory bean -->
    <bean id="producerFactory"
          class="org.springframework.kafka.core.DefaultKafkaProducerFactory">
        <constructor-arg>
            <ref bean="producerProperties"/>
        </constructor-arg>
    </bean>
    <!-- 发送监听器bean -->
    <bean id="sendListener" class="cn.enjoyedu.service.SendListener" />
    <!-- 3、创建kafkatemplate bean，使用的时候，只需要注入这个bean，
    即可使用template的send消息方法 -->
    <bean id="kafkaTemplate" class="org.springframework.kafka.core.KafkaTemplate">
        <constructor-arg ref="producerFactory" />
        <constructor-arg name="autoFlush" value="true" />
        <!-- 配置发送监听器bean -->
        <property name="producerListener" ref="sendListener"></property>
    </bean>
    
    
三、配置消费者
    <!-- 1.定义consumer的参数 -->
    <bean id="consumerProperties" class="java.util.HashMap">
        <constructor-arg>
            <map>
                <entry key="bootstrap.servers" value="${bootstrap.servers}" />
                <entry key="group.id" value="spring-kafka-group" />
                <entry key="key.deserializer"
                       value="org.apache.kafka.common.serialization.StringDeserializer" />
                <entry key="value.deserializer"
                       value="org.apache.kafka.common.serialization.StringDeserializer" />
            </map>
        </constructor-arg>
    </bean>
    <!-- 2.创建consumerFactory bean -->
    <bean id="consumerFactory"
          class="org.springframework.kafka.core.DefaultKafkaConsumerFactory" >
        <constructor-arg>
            <ref bean="consumerProperties" />
        </constructor-arg>
    </bean>
    <!-- 3.定义消费实现类 -->
    <bean id="kafkaConsumerService" class="cn.enjoyedu.service.KafkaConsumer" />
    <!-- 4.消费者容器配置信息 -->
    <bean id="containerProperties"
          class="org.springframework.kafka.listener.config.ContainerProperties">
        <constructor-arg name="topics">
            <list>
                <value>kafka-spring-topic</value>
            </list>
        </constructor-arg>
        <property name="messageListener" ref="kafkaConsumerService"></property>
    </bean>
    <!-- 5.消费者并发消息监听容器，执行doStart()方法 -->
    <bean id="messageListenerContainer"
          class="org.springframework.kafka.listener.ConcurrentMessageListenerContainer"
          init-method="doStart" >
        <constructor-arg ref="consumerFactory" />
        <constructor-arg ref="containerProperties" />
        <property name="concurrency" value="${concurrency}" />
    </bean>
    
    
四、如果消费者需要手动确认
    <!-- 消费者自行确认-1.定义consumer的参数 -->
    <bean id="consumerPropertiesAck" class="java.util.HashMap">
        <constructor-arg>
            <map>
                <entry key="bootstrap.servers" value="${bootstrap.servers}" />
                <entry key="group.id" value="spring-kafka-group-ack" />
                <entry key="key.deserializer"
                       value="org.apache.kafka.common.serialization.StringDeserializer" />
                <entry key="value.deserializer"
                       value="org.apache.kafka.common.serialization.StringDeserializer" />
                <entry key="enable.auto.commit" value="false"/>
            </map>
        </constructor-arg>
    </bean>
    <!-- 消费者自行确认-2.创建consumerFactory bean -->
    <bean id="consumerFactoryAck"
          class="org.springframework.kafka.core.DefaultKafkaConsumerFactory" >
        <constructor-arg>
            <ref bean="consumerPropertiesAck" />
        </constructor-arg>
    </bean>
    <!-- 消费者自行确认-3.定义消费实现类 -->
    <bean id="kafkaConsumerServiceAck" class="cn.enjoyedu.service.KafkaConsumerAck" />
    <!-- 消费者自行确认-4.消费者容器配置信息 -->
    <bean id="containerPropertiesAck"
          class="org.springframework.kafka.listener.config.ContainerProperties">
        <!-- topic -->
        <constructor-arg name="topics">
            <list>
                <value>kafka-spring-topic-b</value>
            </list>
        </constructor-arg>
        <property name="messageListener" ref="kafkaConsumerServiceAck" />
        <!-- 消费者自行确认模式 -->
        <property name="ackMode" value="MANUAL_IMMEDIATE"></property>
    </bean>
    <!-- 消费者自行确认-5.消费者并发消息监听容器，执行doStart()方法 -->
    <bean id="messageListenerContainerAck"
          class="org.springframework.kafka.listener.ConcurrentMessageListenerContainer"
          init-method="doStart" >
        <constructor-arg ref="consumerFactoryAck" />
        <constructor-arg ref="containerPropertiesAck" />
        <property name="concurrency" value="${concurrency}" />
    </bean>
