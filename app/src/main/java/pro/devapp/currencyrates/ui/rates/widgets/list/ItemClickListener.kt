package pro.devapp.currencyrates.ui.rates.widgets.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView


interface OnItemClickListener {
    fun onItemClicked(position: Int)
}

fun RecyclerView.addOnItemClickListener(onClickListener: OnItemClickListener) {
    this.addOnChildAttachStateChangeListener(object :
        RecyclerView.OnChildAttachStateChangeListener {
        override fun onChildViewDetachedFromWindow(view: View) {
            view.setOnClickListener(null)
        }

        override fun onChildViewAttachedToWindow(view: View) {
            view.setOnClickListener {
                val holder = getChildViewHolder(view)
                onClickListener.onItemClicked(holder.adapterPosition)
            }
        }
    })
}