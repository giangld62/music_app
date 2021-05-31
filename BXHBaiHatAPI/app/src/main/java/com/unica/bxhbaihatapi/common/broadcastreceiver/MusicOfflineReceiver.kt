package com.unica.bxhbaihatapi.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.unica.bxhbaihatapi.common.MyApp.Companion.ACTION_NEXT
import com.unica.bxhbaihatapi.common.MyApp.Companion.ACTION_PLAY
import com.unica.bxhbaihatapi.common.MyApp.Companion.ACTION_PREVIOUS
import com.unica.bxhbaihatapi.common.service.MusicOfflineService
import com.unica.bxhbaihatapi.main.songoffline.SongOfflinePlayerActivity

class MusicOfflineReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val actionName = intent?.action
        val serviceIntent  = Intent(context,MusicOfflineService::class.java)
        val position = SongOfflinePlayerActivity.position
        if(actionName != null){
            when(actionName){
                ACTION_PLAY -> serviceIntent.putExtra("ActionName","playPause")
                ACTION_NEXT -> serviceIntent.putExtra("ActionName","next")
                ACTION_PREVIOUS -> serviceIntent.putExtra("ActionName","previous")
            }
            serviceIntent.putExtra("servicePosition",position)
            context?.startService(serviceIntent)
        }
    }
}