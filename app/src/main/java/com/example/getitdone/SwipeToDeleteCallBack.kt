package com.example.getitdone

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


abstract class SwipeToDeleteCallBack(context: Context):ItemTouchHelper
.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {


    private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete)
    private val editIcon = ContextCompat.getDrawable(context, R.drawable.ic_edit)
    private val intrinsicWidth = deleteIcon?.intrinsicWidth
    private val intrinsicHeight = deleteIcon?.intrinsicHeight
    private val background = ColorDrawable()
    private val backgroundColorEdit = Color.parseColor("#5763CF")
    private val backgroundColorDelete = Color.parseColor("#E14E65")
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }


    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val swipeFlag = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(0,swipeFlag)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
       return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(c, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }


        if (dX > 0){
            background.color = backgroundColorEdit
            background.setBounds(itemView.left + dX.toInt(), itemView.top, itemView.left, itemView.bottom)
            background.draw(c)

            val iconTop = itemView.top + (itemHeight - intrinsicHeight!!) / 2
            val iconMargin = (itemHeight - intrinsicHeight) / 2
            val iconLeft = itemView.left - iconMargin - intrinsicWidth!!
            val iconRight = itemView.left - iconMargin
            val iconBottom = iconTop + intrinsicHeight

            editIcon?.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            editIcon?.draw(c)
        }
        if (dX < 0){
            background.color = backgroundColorDelete
            background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
            background.draw(c)

            val iconTop = itemView.top + (itemHeight - intrinsicHeight!!) / 2
            val iconMargin = (itemHeight - intrinsicHeight) / 2
            val iconLeft = itemView.right - iconMargin - intrinsicWidth!!
            val iconRight = itemView.right - iconMargin
            val iconBottom = iconTop + intrinsicHeight

            deleteIcon?.setBounds(iconLeft, iconTop, iconRight , iconBottom)
            deleteIcon?.draw(c)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }


}