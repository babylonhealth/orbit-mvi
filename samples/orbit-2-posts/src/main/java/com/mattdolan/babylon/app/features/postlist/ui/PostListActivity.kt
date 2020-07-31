package com.mattdolan.babylon.app.features.postlist.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mattdolan.babylon.R
import kotlinx.android.synthetic.main.post_list_activity.*

class PostListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_list_activity)

        setSupportActionBar(toolbar)

        setTitle(R.string.post_list_title)
    }
}
