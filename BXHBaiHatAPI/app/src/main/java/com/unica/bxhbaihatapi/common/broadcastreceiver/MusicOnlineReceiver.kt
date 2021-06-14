package com.unica.bxhbaihatapi.common.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.unica.bxhbaihatapi.common.MyApp
import com.unica.bxhbaihatapi.common.service.MusicOnlineService
import com.unica.bxhbaihatapi.main.songonline.PlayerActivity

class MusicOnlineReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val actionName = intent?.action
        val serviceOnlineIntent  = Intent(context, MusicOnlineService::class.java)
        val positionOnline = PlayerActivity.position
        if(actionName != null){
            when(actionName){
                MyApp.ACTION_PLAY ->
                    serviceOnlineIntent.putExtra("ActionName","playPause")
                MyApp.ACTION_NEXT ->
                    serviceOnlineIntent.putExtra("ActionName","next")
                MyApp.ACTION_PREVIOUS ->
                    serviceOnlineIntent.putExtra("ActionName","previous")
            }
            serviceOnlineIntent.putExtra("servicePosition",positionOnline)
            context?.startService(serviceOnlineIntent)
        }
    }
}