package com.android.batya.tictactoe.presentation.offline

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toRect
import com.android.batya.tictactoe.R
import com.android.batya.tictactoe.domain.model.Cell
import com.android.batya.tictactoe.domain.model.Field
import com.android.batya.tictactoe.domain.model.OnFieldChangedListener
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates


typealias OnCellClickedListener = (row: Int, column: Int, field: Field) -> Unit

enum class TURN {
    PLAYER_1,
    PLAYER_2
}

class TicTacToeViewOffline(
    context: Context,
    attributeSet: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : View(context, attributeSet, defStyleAttr, defStyleRes) {
    var field: Field? = null
        set(value) {
            this.field?.listeners?.remove(listener)
            field = value
            this.field?.listeners?.add(listener)
            drawLastCell = false
            lastMovePlayer1.clear()
            lastMovePlayer2.clear()
            currentTurn = TURN.PLAYER_1
            drawWinLine = false
            // Если еще и влияет на размер view (например, когда размер wrap_content, и он может поменяться), то вызываем и requestLayout()
            invalidate()
            updateViewSizes()
        }

    var actionListener: OnCellClickedListener? = null

    private var isLightTheme by Delegates.notNull<Boolean>()
    private var areCrossesFirst by Delegates.notNull<Boolean>()

    private var gridColor by Delegates.notNull<Int>()
    private var winColor by Delegates.notNull<Int>()


    private val fieldRect = RectF()
    private var cellSize = 0f
    private var cellPadding = 0f


    private lateinit var gridPaint: Paint
    private lateinit var lastCellPaint: Paint
    private lateinit var winPaint: Paint

    private var player1 = ResourcesCompat.getDrawable(resources, R.drawable.player1, null)
    private var player2 = ResourcesCompat.getDrawable(resources, R.drawable.player2, null)
    private var player2dark = ResourcesCompat.getDrawable(resources, R.drawable.player2dark, null)



    private val cellRect = RectF()
    private var drawLastCell = false
    var lastMovePlayer1: MutableList<Pair<Int, Int>> = mutableListOf()
    var lastMovePlayer2: MutableList<Pair<Int, Int>> = mutableListOf()
    var currentTurn = TURN.PLAYER_1

    var drawWinLine = false
        set(value) {
            field = value
            invalidate()
            Log.d("TAG", "drawWinLine: true")
        }
    private var cellRectStart = RectF()
    private var cellRectEnd = RectF()
    private val winPath = Path()

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : this(context, attributeSet, defStyleAttr, R.style.DefaultTicTacToeFieldStyle)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, R.attr.ticTaToeFieldStyle)
    constructor(context: Context) : this(context, null)

    init {
        if (attributeSet != null) {
            initAttributes(attributeSet, defStyleAttr, defStyleRes)
        } else {
            initDefaultColors()
        }
        initPaints()
        if (isInEditMode) {
            field = Field(8, 6)
            field?.setCell(4, 2, Cell.PLAYER_1)
            field?.setCell(4, 3, Cell.PLAYER_2)
            field?.setCell(5, 3, Cell.PLAYER_1)
            lastMovePlayer1 = mutableListOf(Pair(5,3))
        }
        isFocusable = true
        isClickable = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        field?.listeners?.add(listener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        field?.listeners?.remove(listener)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        updateViewSizes()
    }

    private fun initPaints() {
        gridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        gridPaint.color = gridColor
        gridPaint.style = Paint.Style.STROKE
        gridPaint.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics)

        lastCellPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        lastCellPaint.color = gridColor
        lastCellPaint.style = Paint.Style.STROKE
        lastCellPaint.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics)

        winPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        winPaint.color = winColor
        winPaint.style = Paint.Style.STROKE
        winPaint.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7f, resources.displayMetrics)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val minHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        val desiredCellSizeInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DESIRED_CELL_SIZE, resources.displayMetrics).toInt()
        val rows = field?.rows ?: 0
        val columns = field?.columns ?: 0

        val desiredWidth = max(minWidth, columns * desiredCellSizeInPixels + paddingLeft + paddingRight)
        val desiredHeight = max(minHeight,rows * desiredCellSizeInPixels + paddingTop + paddingBottom)

        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (field == null) return
        if (cellSize == 0f) return
        if (fieldRect.width() <= 0) return
        if (fieldRect.height() <= 0) return

        if (drawLastCell) drawLastCell(canvas)
        drawGrid(canvas)
        drawCells(canvas)
        if (drawWinLine) drawWinLine(canvas)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val field = this.field ?: return false
        when(event.action) {

            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                val row = getRow(event)
                val column = getColumn(event)
                if (row >= 0 && column >= 0 && row < field.rows && column < field.columns) {
                    actionListener?.invoke(row, column, field)
                    return true
                }
                return false
            }
        }
        return false
    }

    private fun getRow(event: MotionEvent): Int {
        return ((event.y - fieldRect.top) / cellSize).toInt()
    }

    private fun getColumn(event: MotionEvent): Int {
        return ((event.x - fieldRect.left) / cellSize).toInt()

    }
    fun rollbackLastMove() {
        if (currentTurn == TURN.PLAYER_1 && lastMovePlayer2.isNotEmpty()) {
            field?.setCell(lastMovePlayer2.last().first, lastMovePlayer2.last().second, Cell.EMPTY)
            lastMovePlayer2 -= lastMovePlayer2.last()
            currentTurn = TURN.PLAYER_2
        } else if (currentTurn == TURN.PLAYER_2 && lastMovePlayer1.isNotEmpty()) {
            field?.setCell(lastMovePlayer1.last().first, lastMovePlayer1.last().second, Cell.EMPTY)
            lastMovePlayer1 -= lastMovePlayer1.last()
            currentTurn = TURN.PLAYER_1
        }

        invalidate()
    }
    private fun drawGrid(canvas: Canvas) {
        val field = this.field ?: return
        val xStart = fieldRect.left
        val xEnd = fieldRect.right
        for (i in 0..field.rows) {
            val y = fieldRect.top + cellSize*i
            canvas.drawLine(xStart, y, xEnd, y, gridPaint)
        }

        val yStart = fieldRect.top
        val yEnd = fieldRect.bottom
        for (i in 0..field.columns) {
            val x = fieldRect.left + cellSize*i
            canvas.drawLine(x, yStart, x, yEnd, gridPaint)
        }
    }
    private fun drawWinLine(canvas: Canvas) {
        val field = this.field ?: return

        with(field) {
            Log.d("TAG", "drawWinLine: $winStartRow -> $winEndRow, $winStartColumn -> $winEndColumn")

            val (startX, startY) = getWinCenter(winStartRow, winStartColumn)
            val (endX, endY) = getWinCenter(winEndRow, winEndColumn)

            winPath.reset()
            winPath.moveTo(startX , startY)
            winPath.lineTo(endX, endY)

            //Log.d("TAG", "cellRectStart: $cellRectStart")
            //Log.d("TAG", "cellRectEnd: $cellRectEnd")
        }

        canvas.drawPath(winPath, winPaint)
    }

    private fun drawLastCell(canvas: Canvas) {
        if (currentTurn == TURN.PLAYER_1 && lastMovePlayer2.isNotEmpty()) {
            canvas.drawRect(getLastCellRect(lastMovePlayer2.last().first, lastMovePlayer2.last().second), lastCellPaint)
        } else if (currentTurn == TURN.PLAYER_2 && lastMovePlayer1.isNotEmpty()) {
            canvas.drawRect(getLastCellRect(lastMovePlayer1.last().first, lastMovePlayer1.last().second), lastCellPaint)
        }
    }
    private fun drawCells(canvas: Canvas) {
        val field = this.field ?: return
        for (row in 0 until field.rows) {
            for (column in 0 until field.columns) {
                val cell = field.getCell(row, column)
                if (cell == Cell.PLAYER_1) {
                    if (areCrossesFirst) drawPlayer1(canvas, row, column)
                    else drawPlayer2(canvas, row, column)
                } else if (cell == Cell.PLAYER_2) {
                    if (areCrossesFirst) drawPlayer2(canvas, row, column)
                    else drawPlayer1(canvas, row, column)
                }
            }
        }
    }

    private fun drawPlayer1(canvas: Canvas, row: Int, column: Int) {
        val cellRect = getCellRect(row, column)

        player1?.bounds = cellRect.toRect()
        player1?.draw(canvas)


    }
    private fun drawPlayer2(canvas: Canvas, row: Int, column: Int) {
        val cellRect = getCellRect(row, column)

        if (isLightTheme) {
            player2?.bounds = cellRect.toRect()
            player2?.draw(canvas)
        } else {
            player2dark?.bounds = cellRect.toRect()
            player2dark?.draw(canvas)
        }

    }

    private fun getCellRect(row: Int, column: Int): RectF {
        cellRect.left = fieldRect.left + column * cellSize + cellPadding
        cellRect.top = fieldRect.top + row * cellSize + cellPadding
        cellRect.right = cellRect.left + cellSize - cellPadding*2
        cellRect.bottom = cellRect.top + cellSize - cellPadding*2
        return cellRect
    }
    private fun getLastCellRect(row: Int, column: Int): RectF {
        cellRect.left = fieldRect.left + column * cellSize
        cellRect.top = fieldRect.top + row * cellSize
        cellRect.right = cellRect.left + cellSize
        cellRect.bottom = cellRect.top + cellSize
        return cellRect
    }

    private fun getWinCenter(row: Int, column: Int): Pair<Float, Float> {
        cellRect.left = fieldRect.left + column * cellSize
        cellRect.top = fieldRect.top + row * cellSize
        cellRect.right = cellRect.left + cellSize
        cellRect.bottom = cellRect.top + cellSize
        return Pair(cellRect.centerX(), cellRect.centerY())
    }


    private fun initAttributes(attributeSet: AttributeSet, defStyleAttr: Int, defStyleRes: Int) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.TicTacToeView, defStyleAttr, defStyleRes)
        isLightTheme = typedArray.getBoolean(R.styleable.TicTacToeView_isLightTheme, true)
        gridColor = typedArray.getColor(R.styleable.TicTacToeView_gridColor, GRID_DEFAULT_COLOR)
        winColor = typedArray.getColor(R.styleable.TicTacToeView_winColor, WIN_DEFAULT_COLOR)

        typedArray.recycle()
    }

    private fun initDefaultColors() {
        isLightTheme = true
        gridColor = GRID_DEFAULT_COLOR
        winColor = WIN_DEFAULT_COLOR
    }

    private fun updateViewSizes() {
        val field = this.field ?: return

        val safeWidth = width - paddingLeft - paddingRight
        val safeHeight = height - paddingTop - paddingBottom

        val cellWidth = safeWidth / field.columns.toFloat()
        val cellHeight = safeHeight / field.rows.toFloat()

        cellSize = min(cellWidth, cellHeight)
        cellPadding = cellSize*0.19f

        val fieldWidth = cellSize * field.columns
        val fieldHeight = cellSize * field.rows

        fieldRect.left = paddingLeft + (safeWidth - fieldWidth) / 2
        fieldRect.top = paddingTop + (safeHeight - fieldHeight) / 2
        fieldRect.right = fieldRect.left + fieldWidth
        fieldRect.bottom = fieldRect.top + fieldHeight
    }


    private val listener: OnFieldChangedListener = {
        drawLastCell = true
        invalidate()
    }

    fun clearActionListeners() {
        actionListener = null
    }
    fun setTheme(isLightTheme: Boolean) {
        this.isLightTheme = isLightTheme
        invalidate()
    }

    fun setFirstTurn(areCrossesFirst: Boolean) {
        this.areCrossesFirst = areCrossesFirst
        invalidate()
    }

    companion object {
        const val GRID_DEFAULT_COLOR = Color.GRAY
        const val WIN_DEFAULT_COLOR = Color.GREEN

        const val DESIRED_CELL_SIZE = 50f
    }
}