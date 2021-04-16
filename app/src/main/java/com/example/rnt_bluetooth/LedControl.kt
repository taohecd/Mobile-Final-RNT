package com.example.rnt_bluetooth

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*


class LedControl : AppCompatActivity() {

    var setThresholdButton: Button? = null
    var btnDis: Button? = null
    var thresholdInput: EditText? = null
    var address: String? = null
    var tempView: TextView? = null
    private var progress: ProgressDialog? = null
    var myBluetooth: BluetoothAdapter? = null
    var btSocket: BluetoothSocket? = null
    private var isBtConnected = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_led_control2)
        tempView = findViewById(R.id.tempView)
        val intent = intent
        address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS)
        thresholdInput = findViewById(R.id.threasholdSet)
        setThresholdButton = findViewById(R.id.setThresholdButton)
        //For additional actions to be performed
        btnDis = findViewById(R.id.disconnect_button)
        ConnectBT().execute()

        setThresholdButton?.setOnClickListener(View.OnClickListener {
            val msg = thresholdInput?.getText().toString()
            sendSignal(msg)
        })
        btnDis?.setOnClickListener(View.OnClickListener { Disconnect() })
        thread1.start()
    }

    var thread1 = Thread(Runnable {
        val buffer = ByteArray(1024)
        var bytes: Int
        while (true) {
            if (btSocket != null && isBtConnected) {
                try {

                    var tmpIn: InputStream? = null
                    tmpIn = btSocket!!.inputStream
                    val mmInStream = DataInputStream(tmpIn)
                    bytes = mmInStream.read(buffer)
                    val readMessage = String(buffer, 0, bytes)
                    if (readMessage.length > 0) {
                        setText(tempView, readMessage)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    break
                }
            }
        }
    })


    @SuppressLint("SetTextI18n")
    private fun setText(text: TextView?, value: String) {
        runOnUiThread {
            if (!value.contains("\n") && !value.contains("\r")) {
                text!!.text = "${text.text.toString()}${value}"
            }
            if (text!!.text.toString().contains("!")) {
                text.setText("${resources.getString(R.string.temperature_humidity_view)} ")

            }
        }
    }

    private fun sendSignal(number: String) {
        if (btSocket != null) {
            try {
                btSocket!!.outputStream.write(number.toByteArray())
            } catch (e: IOException) {
                msg(resources.getString(R.string.error_msg))
            }
        }
    }

    private fun Disconnect() {
        if (btSocket != null) {
            try {
                btSocket!!.close()
            } catch (e: IOException) {
                msg(resources.getString(R.string.error_msg))
            }
        }
        finish()
    }

    private fun msg(s: String) {
        Toast.makeText(applicationContext, s, Toast.LENGTH_LONG).show()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class ConnectBT : AsyncTask<Void?, Void?, Void?>() {
        private var ConnectSuccess = true
        override fun onPreExecute() {
            progress = ProgressDialog.show(this@LedControl, resources.getString(R.string.connecting), resources.getString(R.string.please_wait))
        }

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter()
                    val bluetoothDevice = myBluetooth!!.getRemoteDevice(address)
                    btSocket =
                        bluetoothDevice.createInsecureRfcommSocketToServiceRecord(myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    btSocket!!.connect()
                }
            } catch (e: IOException) {
                ConnectSuccess = false
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            if (!ConnectSuccess) {
                msg(resources.getString(R.string.failed_to_connect))
                finish()
            } else {
                msg(resources.getString(R.string.connection_success))
                isBtConnected = true
            }
            progress!!.dismiss()
        }
    }

    companion object {
        val myUUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }
}
