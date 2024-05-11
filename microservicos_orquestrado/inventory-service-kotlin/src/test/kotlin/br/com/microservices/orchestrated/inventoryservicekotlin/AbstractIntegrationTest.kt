package br.com.microservices.orchestrated.inventoryservicekotlin

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
@SpringBootTest
abstract class AbstractIntegrationTest {

    companion object {
        private val network: Network = Network.newNetwork()

        @Container
        private val postgresContainer = PostgreSQLContainer(DockerImageName.parse("postgres:latest"))
            .apply {
                withDatabaseName("inventory-db")
                withUsername("postgres")
                withPassword("postgres")
                withNetwork(network)
                withNetworkAliases("orchestrator-saga")
            }

        @Container
        protected val kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))
            .apply {
                withNetwork(network)
                withNetworkAliases("orchestrator-saga")
                dependsOn(postgresContainer)
            }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
            registry.add("spring.datasource.password", postgresContainer::getPassword)
            registry.add("spring.datasource.username", postgresContainer::getUsername)

            registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers)
        }
    }
}