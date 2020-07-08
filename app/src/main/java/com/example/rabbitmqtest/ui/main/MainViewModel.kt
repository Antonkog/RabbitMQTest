package com.example.rabbitmqtest.ui.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rabbitmqtest.ui.main.rabbitMQ.SocketConstants
import com.example.trackandtrace.order.orderDetails.rabbitMq.CommonConsumer
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.concurrent.TimeoutException


class MainViewModel : ViewModel() {

    private lateinit var connection: Connection
    private lateinit var channel: Channel
    val massages  =  MutableLiveData<String>()


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun subscribeRabbitMQ() {
        viewModelScope.launch {
            getSubscribtion()
        }

    }

//    http://yms-test.abona-erp.com:50517/

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    suspend fun getSubscribtion()   = withContext(Dispatchers.Default){
            try {
                val factory = ConnectionFactory()
                factory.username = SocketConstants.userName
                factory.password = SocketConstants.password
                factory.virtualHost = SocketConstants.virtualHost
                factory.host = SocketConstants.hostName

//                factory.isAutomaticRecoveryEnabled = true

                connection = factory.newConnection()

                connection.let { channel = it.createChannel() }
                channel.let {
                    channel.exchangeDeclare(SocketConstants.exchange, SocketConstants.exchangeType)

                    val q = channel.queueDeclare("", true, false, true, null)

                    channel.queueBind(
                        q.getQueue(),
                        SocketConstants.exchange,
                        SocketConstants.routingKey,
                        null
                    )

                    println(" [*] Waiting for messages. To exit press CTRL+C")

                    val consumer =   CommonConsumer( channel)

                    channel.basicConsume(q.queue, true, consumer)


                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: TimeoutException) {
                e.printStackTrace()
            }
        }

    fun disconnectRabbitMQ() {
        viewModelScope.launch {
            unsubscribe()
        }
    }

    suspend fun unsubscribe() = withContext(Dispatchers.Default) {
        if (connection.isOpen) {
            connection.close()
        }
    }
}