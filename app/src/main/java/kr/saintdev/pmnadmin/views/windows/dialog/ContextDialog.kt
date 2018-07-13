package kr.saintdev.pmnadmin.views.windows.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import kr.saintdev.pmnadmin.R

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 * @Date 2018-07-13
 */
class ContextDialog(
        context: Context,
        private val dialogTitle: String,
        private val dialogItems: Array<String>,
        private val listener: OnContextItemClickListener? = null) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_context_view)

        val titleView = findViewById<TextView>(R.id.dialog_context_title)
        val listView = findViewById<ListView>(R.id.dialog_context_list)

        titleView.text = dialogTitle
        listView.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, dialogItems)
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            listener?.onItemClick(position)
        }
    }
}

interface OnContextItemClickListener {
    fun onItemClick(position: Int)
}