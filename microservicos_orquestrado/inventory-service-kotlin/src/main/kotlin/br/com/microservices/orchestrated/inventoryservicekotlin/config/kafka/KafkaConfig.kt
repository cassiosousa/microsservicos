package br.com.microservices.orchestrated.inventoryservice.config.kafka;

import br.com.microservices.orchestrated.inventoryservice.config.kafka.KafkaConfig.Companion.PARTITION_COUNT
import br.com.microservices.orchestrated.inventoryservice.config.kafka.KafkaConfig.Companion.REPLICA_COUNT
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.*

@EnableKafka
@Configuration
class KafkaConfig(
    @Value("\${spring.kafka.bootstrap-servers}")
    val  bootstrapServers: String,
    @Value("\${spring.kafka.consumer.group-id}")
    val groupId: String,
    @Value("\${spring.kafka.consumer.auto-offset-reset}")
    val autoOffsetReset: String,

    @Value("\${spring.kafka.topic.orchestrator}")
    val orchestratorTopic: String,
    @Value("\${spring.kafka.topic.inventory-success}")
    val inventorySuccessTopic: String,
    @Value("\${spring.kafka.topic.inventory-fail}")
    val inventoryFailTopic: String

) {
    companion object {
        const val PARTITION_COUNT = 1;
        const val REPLICA_COUNT = 1;
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<String,String> = DefaultKafkaConsumerFactory(consumerProps())

    private fun consumerProps(): kotlin.collections.Map<String, Any> = mapOf<String,Any>(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
        ConsumerConfig.GROUP_ID_CONFIG to groupId,
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to autoOffsetReset)

    @Bean
    fun producerFactory(): ProducerFactory<String,String> = DefaultKafkaProducerFactory(producerProps())

    private fun producerProps(): kotlin.collections.Map<String, Any> = mapOf<String,Any>(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java)

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String,String>): KafkaTemplate<String,String> = KafkaTemplate(producerFactory)

    private fun buildTopic(name: String): NewTopic = TopicBuilder.name(name)
                .replicas(REPLICA_COUNT)
                .partitions(PARTITION_COUNT)
                .build()

    @Bean
    fun orchestratorTopic(): NewTopic = buildTopic(orchestratorTopic);

    @Bean
    fun inventorySuccessTopic(): NewTopic = buildTopic(inventorySuccessTopic);

    @Bean
    fun inventoryFailTopic(): NewTopic = buildTopic(inventoryFailTopic);
}
