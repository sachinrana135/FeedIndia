package com.alfanse.feedindia.ui.needier

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alfanse.feedindia.R
import com.alfanse.feedindia.data.models.NeedieritemEntity
import kotlinx.android.synthetic.main.row_item_needier_item.view.*

class NeedierListAdapter(
    private val context: Context
) : PagedListAdapter<NeedieritemEntity, NeedierListAdapter.NeedierListViewHolder>(DiffUtilCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NeedierListViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        return NeedierListViewHolder(
            layoutInflater.inflate(
                R.layout.row_item_needier_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NeedierListViewHolder, position: Int) {
        holder.userName.text = getItem(position)?.name
        holder.status.text = getItem(position)?.name
        holder.needItems.text = getItem(position)?.needItems
        holder.locationAddress.text = getItem(position)?.address
    }

    class NeedierListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.tvUserName
        val status: TextView = view.status
        val needItems: TextView = view.tv_items_need
        val locationAddress: TextView = view.tv_address
    }

    class DiffUtilCallBack : DiffUtil.ItemCallback<NeedieritemEntity>() {
        override fun areItemsTheSame(
            oldItem: NeedieritemEntity,
            newItem: NeedieritemEntity
        ): Boolean {
            return oldItem.needierItemId == newItem.needierItemId
        }

        override fun areContentsTheSame(
            oldItem: NeedieritemEntity,
            newItem: NeedieritemEntity
        ): Boolean {
            return oldItem.needierItemId == newItem.needierItemId
                    && oldItem.mobile == newItem.mobile
                    && oldItem.address == newItem.address
                    && oldItem.name == newItem.name
        }

    }
}