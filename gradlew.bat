package com.uuuuk.blur_test

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.Message
import android.os.ParcelUuid
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import org.greenrobot.eventbus.EventBus
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.util.*

object BluetoothUtils {


    private var mSocket: BluetoothSocket? = null
    private var mReadThread: ReadThread? = null
    private var mServerSocket: BluetoothServerSocket? = null
    private var mClientThread: ClientThread? = null
    @SuppressLint("StaticFieldLeak")
    private var mServerThread: ServerThread? = null

    const val HANDLER_RECEIVE_MESSAGE = 101

    /**
     * 獲取藍牙適配器
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getBluetoothAdapter(context: Context): BluetoothAdapter {
        var mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        val mBluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager//獲取藍牙管理器
        mBluetoothAdapter = mBluetoothManager.adapter //獲取藍牙適配器

        return mBluetoothAdapter
    }

    /**
     * 檢查藍牙是否打開(是否可用)
     */
    fun checkBleDevice(context: Context): Boolean {
        return BluetoothUtils.getBluetoothAdapter(context).isEnabled
    }

    /**
     * 嘗試打開藍牙
     */
    fun openBluetooth(context: Context) {
        openBluetooth(context, 3600)//3600爲藍牙設備可見時間
    }

    /**
     * 嘗試打開藍牙
     */
    fun openBluetooth(context: Context, time: Int) {
        BluetoothUtils.getBluetoothAdapter(context).enable()
        val enable = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        enable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, time)
        context.startActivity(enable)
    }

    /**
     * 關�