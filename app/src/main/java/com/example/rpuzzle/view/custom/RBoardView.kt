package com.example.rpuzzle.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.rpuzzle.game.Puzzle
import kotlin.math.min


class RBoardView(context: Context, attributeSet: AttributeSet): View(context, attributeSet) {

    private val colorArr = arrayOf(Color.parseColor("#FFFFFF"), Color.parseColor("#FD3402"), Color.parseColor("#0143FA"))
    private var boardArr = IntArray(Puzzle.size*Puzzle.size)
    private val size = 3

    private var cellSizePixels = 0F

    private val thickLinePaint = Paint().apply{
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 4F
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizePixels = min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(sizePixels, sizePixels)
    }

    override fun onDraw(canvas: Canvas) {
        cellSizePixels = (min(width, height) /size).toFloat()
        drawLines(canvas)
        fillCells(canvas)
    }

    private fun drawLines(canvas: Canvas) {
        val squareSize = cellSizePixels * size
        canvas.drawRect(0F, 0F, squareSize, squareSize, thickLinePaint)

        for (i in 1 until size) {
            canvas.drawLine(i*cellSizePixels, 0F, i*cellSizePixels, squareSize, thickLinePaint)
            canvas.drawLine(0F, i*cellSizePixels, squareSize, i*cellSizePixels, thickLinePaint)
        }

    }

    private fun fillCells(canvas: Canvas) {
        for(r in 0 until size){
            for (c in 0 until size){
                fillCell(canvas, r, c)
            }
        }

    }

    private fun fillCell(canvas: Canvas, row: Int, col: Int) {
        canvas.drawRect(col*cellSizePixels + 1,
            row*cellSizePixels + 1,
            (col+1)*cellSizePixels - 1,
            (row+1)*cellSizePixels - 1,
            Paint().apply{
                style = Paint.Style.FILL_AND_STROKE
                color = colorArr[boardArr[row*size + col]]
            })
    }

    fun updateBoardString(boardArr: IntArray) {
        for (i in boardArr.indices) {
            this.boardArr[i] = boardArr[i]
        }
        invalidate()
    }
}