package com.mtjk.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.mtjk.utils.log

/**
 * chenliang
 * email:chenliangj2ee@163.com
 * 2021-03-13
 */
open class MyBaseAdapter<D : MyBaseBean>(
    context: Context, layoutIds: HashMap<Int, Int>,
    func: (d: D) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var loading = false;
    var con = context;
    open var data = ArrayList<D>()
    var layoutIds = layoutIds;
    var viewHolders = HashMap<Int, MyViewHolder>()
    var func = func
    var loadFun: (() -> Unit?)? = null
    var position = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var type = viewType
        if (layoutIds.size == 1) {
            type = 31415926
        }

        val inflater: LayoutInflater = LayoutInflater.from(con)
        val layoutId = layoutIds[type]!!
        val binding: ViewDataBinding = DataBindingUtil.inflate(inflater, layoutId, parent, false)
        viewHolders[type] = MyViewHolder(binding.root)
        return viewHolders[type]!!
    }

    fun finishLoading() {
        loading = false
    }

    fun autoLoadMore() {
        loading = true
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        this.position = position
        if (position == data.size - 10 && !loading && data.size > 20) {
//            log("自动加载....$position")
//            loading = true
//            if (loadFun != null) {
//                loadFun!!()
//            }
        }

        val binding: ViewDataBinding = DataBindingUtil.getBinding(holder.itemView)!!
        if (binding != null) {
            data[position].itemPosition = position
            data[position].binding = binding
            onBindViewHolder(data[position])
            binding.executePendingBindings()
        }
    }

    private fun onBindViewHolder(bean: D) {
        func(bean)
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].itemType
    }


    override fun getItemCount(): Int {
        return data!!.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}

