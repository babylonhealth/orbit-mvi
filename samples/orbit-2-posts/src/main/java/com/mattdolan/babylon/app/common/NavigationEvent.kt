package com.mattdolan.babylon.app.common

import android.content.Context

interface NavigationEvent {
    fun navigate(context: Context)
}
