package com.babylon.orbit.sample.presentation.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(
    private val horizontalSpacing: Int = 0,
    private val verticalSpacing: Int = 0,
    private val isHorizontal: Boolean = false
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val size = parent.adapter?.itemCount

        val isFirstItem = position == 0
        val isLastItem = position + 1 == size

        if (position == -1) return

        getItemOffsetsDefaultBehaviour(outRect, isFirstItem, isLastItem)
    }

    private fun getItemOffsetsDefaultBehaviour(outRect: Rect, isFirstItem: Boolean, isLastItem: Boolean) {
        outRect.left = if (isHorizontal) {
            if (isFirstItem) horizontalSpacing else horizontalSpacing / 2
        } else {
            horizontalSpacing
        }

        outRect.right = if (isHorizontal) {
            if (isLastItem) horizontalSpacing else horizontalSpacing / 2
        } else {
            horizontalSpacing
        }

        outRect.top = if (!isHorizontal) {
            if (isFirstItem) verticalSpacing else verticalSpacing / 2
        } else {
            verticalSpacing
        }

        outRect.bottom = if (!isHorizontal) {
            if (isLastItem) verticalSpacing else verticalSpacing / 2
        } else {
            verticalSpacing
        }
    }
}
