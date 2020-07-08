package com.example.rabbitmqtest.ui.main

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.rabbitmqtest.R
import java.lang.StringBuilder


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var textView : TextView

    private lateinit var viewModel: MainViewModel




    private val massagesObserver = Observer<String> {
        textView.text = it
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val root =  inflater.inflate(R.layout.main_fragment, container, false)

        val btnSubscribe  = root.findViewById<Button>(R.id.button_subscribe)
        btnSubscribe.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                viewModel.subscribeRabbitMQ()
            }
        }

        val btnUnSubscribe  = root.findViewById<Button>(R.id.button_unsubscribe)
        btnUnSubscribe.setOnClickListener(View.OnClickListener {
            disconnectRabbitMQ()
        })

        textView = root.findViewById(R.id.message)
        return root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
//        viewModel.massages.observe(viewLifecycleOwner, massagesObserver)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            viewModel.subscribeRabbitMQ()
//        }
//
//    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.massages.observe(viewLifecycleOwner, massagesObserver)
    }


    private fun disconnectRabbitMQ() {
        viewModel.disconnectRabbitMQ()
    }
}