package com.unica.bxhbaihatapi.main.songonline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unica.bxhbaihatapi.databinding.ItemBinding
import com.unica.bxhbaihatapi.databinding.ItemSearchBinding
import com.unica.bxhbaihatapi.db.entity.SongSearch
import com.unica.bxhbaihatapi.model.song.Song
import com.unica.bxhbaihatapi.ui.base.BaseAdapter

class SongAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private val inter: ISongSearch

    constructor(inter: ISongSearch) {
        this.inter = inter
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            SongViewHolder(
                ItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
            )
        } else {
            SongSearchViewHolder(
                ItemSearchBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
            )
        }
    }

    override fun getItemCount() = inter.getCount()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is SongViewHolder){
            holder.binding.data = inter.getData(position) as Song
        }
        else{
            (holder as SongSearchViewHolder).binding.data = inter.getData(position) as SongSearch
            holder.binding.btnDownload.setOnClickListener{
                inter.onItemClickDownload(position)
            }
        }
        holder.itemView.setOnClickListener{
            inter.onItemClick(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(inter.getData(position) is Song) 1 else 2
    }

    interface ISongSearch : BaseAdapter.IBaseAdapter<Any> {
        fun onItemClickDownload(position: Int)
    }

    class SongViewHolder(binding: ItemBinding) :
        BaseAdapter.BaseViewHolder<ItemBinding>(binding)

    class SongSearchViewHolder(binding: ItemSearchBinding) :
        BaseAdapter.BaseViewHolder<ItemSearchBinding>(binding)


}
