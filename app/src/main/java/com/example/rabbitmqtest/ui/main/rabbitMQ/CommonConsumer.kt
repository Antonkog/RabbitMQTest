package com.example.trackandtrace.order.orderDetails.rabbitMq

import com.example.rabbitmqtest.ui.main.rabbitMQ.RabbitmqListener
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope

class CommonConsumer(
    channel: Channel, private val listener : RabbitmqListener?
) : DefaultConsumer(channel) {

    override fun handleDelivery(
        consumerTag: String?,
        envelope: Envelope?,
        properties: AMQP.BasicProperties?,
        body: ByteArray?
    ) {

        val value = body?.let { String(it) }
        println("-----Order value----- $value")
        value?.let {
            listener?.gotMessage(it)
        }
//        val orderUpdateRabbitMQ = Gson().fromJson(value, OrderUpdateRabbitMQ::class.java)
    }
}