package com.example.rabbitmqtest.ui.main.rabbitMQ

interface RabbitmqListener {
    fun gotMessage(msg: String)
}