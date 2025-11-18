package edu.temple.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import java.util.logging.Handler

class MainActivity : AppCompatActivity() {

    lateinit var timerTextView : TextView
    lateinit var timerBinder : TimerService.TimerBinder
    var isConnected = false

    val timerHandler = android.os.Handler(Looper.getMainLooper()){
        timerTextView.text = it.what.toString()
        true
    }
    val serviceConnection = object : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            timerBinder = service as TimerService.TimerBinder
            timerBinder.setHandler(timerHandler)
            isConnected = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isConnected = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        when(item.itemId){
            R.id.start -> {
                if(isConnected && !timerBinder.isRunning){
                    timerBinder.start(100)
                    item.setTitle("Pause")
                }else if(isConnected && timerBinder.isRunning && !timerBinder.paused){
                    timerBinder.pause()
                    item.setTitle("Start")
                }else if(isConnected && timerBinder.isRunning && timerBinder.paused){
                    timerBinder.pause()
                    item.setTitle("Pause")
                }
            }
            R.id.stop -> {
                if(isConnected && timerBinder.isRunning){
                    timerBinder.stop()
                    timerTextView.text = 0.toString()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindService(
            Intent(this, TimerService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )

        timerTextView = findViewById<TextView>(R.id.textView)
    }
}