package com.unica.bxhbaihatapi.main.songoffline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unica.bxhbaihatapi.databinding.ItemSongOfflineBinding
import com.unica.bxhbaihatapi.ui.base.BaseAdapter

class SongOfflineAdapter :RecyclerView.Adapter<SongOfflineAdapter.SongOfflineViewHolder>{
    private var inter : ISongOffline

    constructor(inter:ISongOffline){
        this.inter = inter
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongOfflineViewHolder {
        return SongOfflineViewHolder(ItemSongOfflineBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: SongOfflineViewHolder, position: Int) {
        holder.binding.data = inter.getData(position)
        holder.binding.root.setOnClickListener{
            inter.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return inter.getCount()
    }

    interface ISongOffline:BaseAdapter.IBaseAdapter<SongData>

    class SongOfflineViewHolder(binding:ItemSongOfflineBinding) : BaseAdapter.BaseViewHolder<ItemSongOfflineBinding>(binding)
}