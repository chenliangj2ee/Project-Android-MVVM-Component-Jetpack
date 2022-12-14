package com.mtjk.debug

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.mtjk.base.R

internal class LogDialog : DialogFragment() {

    var root: View? = null
    var listview: ListView? = null
    var logs = ArrayList<BaseBeanLog>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (root == null) {
            root = inflater.inflate(R.layout.base_dialog_log_layout, null)
        }
        listview = root!!.findViewById(R.id.listview)
        listview!!.adapter = LogAdapter(context!!, logs)
        return root
    }

    fun setData(logs: ArrayList<BaseBeanLog>) {
        this.logs = logs
    }

    class LogAdapter(con: Context, logs: ArrayList<BaseBeanLog>) : BaseAdapter() {
        var con = con
        var logs = logs
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            var view = View.inflate(con, R.layout.base_log_item, null)
            var tag = view.findViewById<TextView>(R.id.tag)
            var url = view.findViewById<TextView>(R.id.url)
            var json = view.findViewById<TextView>(R.id.json)
            var bean = logs[position]
            tag.text = bean.tag
            url.text = bean.url
            json.text = bean.json
            return view;
        }

        override fun getItem(position: Int): Any {
            return logs[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return logs.size
        }

    }
}