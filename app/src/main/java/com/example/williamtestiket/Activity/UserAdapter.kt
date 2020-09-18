package com.example.williamtestiket.Activity


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.williamtestiket.Model.GithubUserModel
import com.example.williamtestiket.R
import java.util.*


class UserAdapter(private val mContext: Context, private val messageList: ArrayList<GithubUserModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return this.messageList.size
    }

    private var loadingPos = -1

    private var parent: ViewGroup? = null


    private val VIEW_ITEM = 1
    private val VIEW_PROG = 0


    inner class UserHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imgProf : ImageView = itemView.findViewById(R.id.iv_profile)
        var textName : TextView = itemView.findViewById(R.id.tv_name)

    }
    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        this.parent = parent
        if (viewType == VIEW_ITEM) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user, parent, false)

            return UserHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_loading, parent, false)

            return LoadingViewHolder(itemView)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == loadingPos) {
            VIEW_PROG
        } else {
            VIEW_ITEM
        }
    }


    fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            messageList.add(GithubUserModel("",""))
            loadingPos = (messageList.size-1)
            notifyDataSetChanged()
        } else {
            messageList.removeAt(loadingPos)
            notifyItemRemoved(loadingPos)
            loadingPos = -1
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        if (holder is UserHolder) {
            holder.textName.text = message.name
            // Setting Bitmap to ImageView
            Glide.with(holder.imgProf)
                .load(message.profPicUrl)
                .into(holder.imgProf)
        }

    }



}
