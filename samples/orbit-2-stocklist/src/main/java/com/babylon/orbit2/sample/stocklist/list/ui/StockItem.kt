/*
 * Copyright 2020 Babylon Partners Limited
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.babylon.orbit2.sample.stocklist.list.ui

import android.view.View
import com.babylon.orbit2.sample.stocklist.R
import com.babylon.orbit2.sample.stocklist.databinding.ListItemBinding
import com.babylon.orbit2.sample.stocklist.list.business.ListViewModel
import com.babylon.orbit2.sample.stocklist.streaming.stock.Stock
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem

class StockItem(
    private val stock: Stock,
    private val listViewModel: ListViewModel
) : BindableItem<ListItemBinding>() {

    private val bidRef = JobHolder()
    private val askRef = JobHolder()

    override fun initializeViewBinding(view: View) = ListItemBinding.bind(view)

    override fun getLayout() = R.layout.list_item

    override fun isSameAs(other: Item<*>) = other is StockItem && stock.name == other.stock.name
    override fun hasSameContentAs(other: Item<*>) = other is StockItem && stock == other.stock

    override fun bind(viewBinding: ListItemBinding, position: Int) {
        if (viewBinding.name.text == stock.name) {
            animateChange(viewBinding.bid, viewBinding.bidTick, stock.bid, bidRef)
            animateChange(viewBinding.ask, viewBinding.askTick, stock.ask, askRef)
        } else {
            hideTicks(viewBinding)
        }

        viewBinding.name.text = stock.name
        viewBinding.bid.text = stock.bid
        viewBinding.ask.text = stock.ask
        viewBinding.timestamp.text = stock.timestamp

        viewBinding.root.setOnClickListener {
            listViewModel.viewMarket(stock.itemName)
        }
    }

    private fun hideTicks(viewBinding: ListItemBinding) {
        bidRef.job?.cancel()
        askRef.job?.cancel()
        viewBinding.bidTick.visibility = View.INVISIBLE
        viewBinding.askTick.visibility = View.INVISIBLE
    }
}
