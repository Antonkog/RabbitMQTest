package com.example.rabbitmqtest.ui.main

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rabbitmqtest.ui.main.rabbitMQ.RabbitmqListener
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


class MainViewModel : ViewModel(), RabbitmqListener {

   var connection: Connection? = null
    var channel: Channel? = null
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

                connection?.let { channel = it.createChannel() }
                channel?.let {
                    channel?.let {chan ->
                        chan.exchangeDeclare(SocketConstants.exchange, SocketConstants.exchangeType)

                        val q = chan.queueDeclare("", true, false, true, null)

                        chan.queueBind(
                            q.queue,
                            SocketConstants.exchange,
                            SocketConstants.routingKey,
                            null
                        )

                        println(" [*] Waiting for messages. To exit press CTRL+C")

                        val consumer = CommonConsumer(chan, this@MainViewModel)

                        chan.basicConsume(q.queue, true, consumer)
                    }


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
        if (connection?.isOpen == true) {
            Log.d(this.javaClass.canonicalName, "unsubscribe rabbit")
            connection?.close()
        }
    }

    override fun gotMessage(msg: String) {
        massages.postValue(msg)
    }
}