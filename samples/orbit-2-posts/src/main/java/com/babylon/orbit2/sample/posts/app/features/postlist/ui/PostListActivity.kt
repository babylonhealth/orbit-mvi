package com.babylon.orbit2.sample.posts.app.features.postlist.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.babylon.orbit2.sample.posts.R
import kotlinx.android.synthetic.main.post_list_activity.*

class PostListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_list_activity)

        setSupportActionBar(toolbar)

        setTitle(R.string.post_list_title)
    }
}
