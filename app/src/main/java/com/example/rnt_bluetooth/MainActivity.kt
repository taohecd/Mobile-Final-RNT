package com.example.rnt_bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {
    private var myBluetooth: BluetoothAdapter? = null
    private var pairedDevices: Set<BluetoothDevice>? = null
    var devicelist: ListView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnPaired: Button
        btnPaired = findViewById<View>(R.id.button) as Button
        devicelist = findViewById<View>(R.id.listView) as ListView
        myBluetooth = BluetoothAdapter.getDefaultAdapter()

        if (myBluetooth == null) {
            Toast.makeText(
                applicationContext,
                "Bluetooth device not available",
                Toast.LENGTH_LONG
            ).show()
            finish()
        } else if (!myBluetooth!!.isEnabled) {
            val turnBTon = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(turnBTon, 1)
        }
        btnPaired.setOnClickListener { pairedDevicesList() }
    }

    private fun pairedDevicesList() {
        pairedDevices = myBluetooth!!.bondedDevices
        val list: ArrayList<Any> = ArrayList<Any>()
        if (pairedDevices?.size!! > 0) {
            for (bt in pairedDevices!!) {
                list.add(bt.name.toString() + "\n" + bt.address.toString())
            }
        } else {
            Toast.makeText(
                applicationContext,
                "No Paired Bluetooth Devices Found.",
                Toast.LENGTH_LONG
            ).show()
        }
        val adapter: ArrayAdapter<*> =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        devicelist!!.adapter = adapter
        devicelist!!.onItemClickListener = myListClickListener
    }

    private val myListClickListener =
        OnItemClickListener { parent, view, position, id ->
            val info = (view as TextView).text.toString()
            val address = info.substring(info.length - 17)
            val i = Intent(this@MainActivity, LedControl::class.java)
            i.putExtra(EXTRA_ADDRESS, address)
            startActivity(i)
        }

    companion object {
        var EXTRA_ADDRESS = "device_address"
    }
}