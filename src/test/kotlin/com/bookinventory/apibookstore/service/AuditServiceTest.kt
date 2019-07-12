package com.bookinventory.apibookstore.service

import com.bookinventory.apibookstore.model.Book
import com.bookinventory.apibookstore.model.ImageUrl
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.junit.After
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.slf4j.LoggerFactory
//import org.junit.platform.commons.logging.LoggerFactory

import org.springframework.kafka.test.hamcrest.KafkaMatchers.hasValue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.KafkaMessageListenerContainer
import org.springframework.kafka.listener.MessageListener
import org.springframework.kafka.test.rule.EmbeddedKafkaRule
import org.springframework.kafka.test.utils.ContainerTestUtils
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

@RunWith(SpringRunner::class)
@SpringBootTest
@DirtiesContext
class AuditServiceTest {

    companion object {
        val logger = LoggerFactory.getLogger(AuditServiceTest::class.java)
        val topic="booksAudit"

        @ClassRule
        @JvmField
        val embeddedKafkaRule = EmbeddedKafkaRule(1,true, topic)
    }

    @Autowired lateinit var auditService : AuditService
    lateinit var container : KafkaMessageListenerContainer<String, String>
    lateinit var records :BlockingQueue<ConsumerRecord<String,String>>



    @Before
    fun setUp() {
        val consumerProperties = KafkaTestUtils.consumerProps("sender",
                "false", embeddedKafkaRule.embeddedKafka)

        val consumerFactory = DefaultKafkaConsumerFactory<String, String>(consumerProperties)
        val containerProperties = ContainerProperties(topic)

        container = KafkaMessageListenerContainer(consumerFactory,containerProperties)
        records = LinkedBlockingQueue()

        container.setupMessageListener(MessageListener<String, String> { record ->
                    logger.debug("test-listener received message='{}'", record.toString())
                    records!!.add(record)
                })

        container.start()

        ContainerTestUtils.waitForAssignment(container,
                embeddedKafkaRule.embeddedKafka.partitionsPerTopic)
    }

    @After
    fun tearDown(){
        container.stop()
    }

    @Test
    fun `test sendAddMesasge`(){
        val imageUrl = ImageUrl("xyz.com","abc.com")
        val book = Book("123", "abc", listOf("xyz","abc"), "desc", 1.00, 10, imageUrl)

        auditService.sendAddMessage(book)

         records.poll(10,TimeUnit.SECONDS)
        val auditLog = AuditService.auditLogs

        assert(auditLog.last().contains("abc"))
        assert(auditLog.last().contains("ADD"))

    }

    @Test
    fun `test sendEditMesasge`(){
        val imageUrl = ImageUrl("xyz.com","abc.com")
        val book = Book("123", "abc", listOf("xyz","abc"), "desc", 1.00, 10, imageUrl)

        auditService.sendEditMessage(book)

        records.poll(10,TimeUnit.SECONDS)
        val auditLog = AuditService.auditLogs
        assert(auditLog.last().contains("abc"))
        assert(auditLog.last().contains("EDIT"))

    }

    @Test
    fun `test sendDeleteMesasge`(){
        val imageUrl = ImageUrl("xyz.com","abc.com")
        val book = Book("123", "abc", listOf("xyz","abc"), "desc", 1.00, 10, imageUrl)

        auditService.sendDeleteMessage(book)

        records.poll(10,TimeUnit.SECONDS)
        val auditLog = AuditService.auditLogs
        assert(auditLog.last().contains("abc"))
        assert(auditLog.last().contains("DELETE"))

    }




}
