package com.batya.tictactoe.domain.model

enum class Cell {
    PLAYER_1,
    PLAYER_2,
    EMPTY,
    OUT_OF_FIELD
}

typealias OnFieldChangedListener = (field: Field) -> Unit

class Field(val rows: Int, val columns: Int) {
    private val cells = Array(rows) { Array(columns) { Cell.EMPTY } }

    //val cells = Array(rows) { Array(columns) { Cell.EMPTY } }

    val listeners = mutableSetOf<OnFieldChangedListener>()
    var hasWinner = false

    var winStartRow: Int = - 1
        set(value) {
            if (!hasWinner) field = value
        }
    var winStartColumn: Int = -1
        set(value) {
            if (!hasWinner) field = value
        }
    var winEndRow: Int = - 1
        set(value) {
            if (!hasWinner) field = value
        }
    var winEndColumn: Int = -1
        set(value) {
            if (!hasWinner) field = value
        }

    fun getCell(row: Int, column: Int): Cell {
        if (row < 0 || column < 0 || row >= rows || column >= columns) return Cell.OUT_OF_FIELD
        return cells[row][column]
    }

    fun setCell(row: Int, column: Int, cell: Cell) {
        if (row < 0 || column < 0 || row >= rows || column >= columns) return

        if (cells[row][column] != cell) {
            cells[row][column] = cell
            listeners.forEach {  it?.invoke(this) }
            checkWinnerOnline(row, column, cell)
        }
    }


    fun checkWinnerOnline(row: Int, column: Int, cell: Cell): Boolean {
        val winners = mutableListOf<Cell>()

        winners += checkHorizontals(row, column, cell)
        winners += checkVerticals(row, column, cell)
        winners += checkDiagonal1(row, column, cell)
        winners += checkDiagonal2(row, column, cell)

        return winners.any { it != Cell.EMPTY }
    }

    fun checkWinnerOffline(row: Int, column: Int): Cell {
        val cell = getCell(row, column)
        val winners = mutableListOf<Cell>()

        winners += checkHorizontals(row, column, cell)
        winners += checkVerticals(row, column, cell)
        winners += checkDiagonal1(row, column, cell)
        winners += checkDiagonal2(row, column, cell)


        if (winners.any { it != Cell.EMPTY }) {
            return winners.filter { it != Cell.EMPTY }[0]
        } else {
            return Cell.EMPTY
        }
    }

    private fun checkHorizontals(row: Int, column: Int, cell: Cell): Cell {
        var count = 1
        var counter = 0
        var cColumn = column
        winStartRow = row
        winEndRow = row
        winStartColumn = cColumn

        while (true) {
            counter++
            if(cColumn > 0 && column - cColumn < 4) {
                cColumn -= 1
                if (getCell(row, cColumn) != cell) {
                    break
                }
                else {
                    winStartColumn = cColumn
                    count++
                }
            } else {
                break
            }
        }

        counter = 0
        cColumn = column

        while (true) {
            counter++
            winEndColumn = cColumn
            if(cColumn < columns - 1 && cColumn - column < 4) {
                cColumn += 1
                if (getCell(row, cColumn) != cell) {
                    break
                }
                else {
                    count++
                }
            } else {
                break
            }
        }
        if (count >= 5) {
            hasWinner = true
            return cell
        }
        return Cell.EMPTY
    }
    private fun checkVerticals(row: Int, column: Int, cell: Cell): Cell {
        var count = 1
        var counter = 0
        var cRow = row

        winStartColumn = column
        winEndColumn = column
        winStartRow = cRow

        while (true) {
            counter++
            if(cRow > 0 && row - cRow < 4) {
                cRow -= 1
                if (getCell(cRow, column) != cell) {
                    break
                }
                else {
                    winStartRow = cRow
                    count++
                }
            } else {
                break
            }
        }

        counter = 0
        cRow = row

        while (true) {
            counter++
            winEndRow = cRow

            if(cRow < rows - 1 && cRow - row < 4) {
                cRow += 1
                if (getCell(cRow, column) != cell) {
                    break
                }
                else {
                    count++
                }
            } else {
                break
            }
        }
        if (count >= 5) {
            hasWinner = true
            return cell
        }
        return Cell.EMPTY
    }
    private fun checkDiagonal1(row: Int, column: Int, cell: Cell): Cell {
        var count = 1
        var counter = 0
        var cRow = row
        var cColumn = column

        winStartColumn = cColumn
        winStartRow = cRow

        while (true) {
            counter++
            if(cRow > 0 && row - cRow < 4 && cColumn > 0 && column - cColumn < 4) {
                cRow -= 1
                cColumn -= 1
                if (getCell(cRow, cColumn) != cell) {
                    break
                }
                else {
                    winStartRow = cRow
                    winStartColumn = cColumn
                    count++
                }
            } else {
                break
            }
        }

        counter = 0
        cRow = row
        cColumn = column

        while (true) {
            counter++
            winEndRow = cRow
            winEndColumn = cColumn

            if(cRow < rows - 1 && cRow - row < 4 && cColumn < columns - 1 && cColumn - column < 4) {
                cRow += 1
                cColumn += 1
                if (getCell(cRow, cColumn) != cell) {
                    break
                }
                else {
                    count++
                }
            } else {
                break
            }
        }
        if (count >= 5) {
            hasWinner = true
            return cell
        }
        return Cell.EMPTY


    }

    private fun checkDiagonal2(row: Int, column: Int, cell: Cell): Cell {
        var count = 1
        var counter = 0
        var cRow = row
        var cColumn = column

        winStartColumn = cColumn
        winStartRow = cRow

        while (true) {
            counter++
            if(cRow > 0 && row - cRow < 4 && cColumn < columns - 1 && cColumn - column < 4) {
                cRow -= 1
                cColumn += 1
                if (getCell(cRow, cColumn) != cell) {
                    break
                }
                else {
                    winStartRow = cRow
                    winStartColumn = cColumn
                    count++
                }
            } else {
                break
            }
        }

        counter = 0
        cRow = row
        cColumn = column

        while (true) {
            counter++
            winEndRow = cRow
            winEndColumn = cColumn

            if(cRow < rows - 1 && cRow - row < 4 && cColumn > 0 && column - cColumn < 4) {
                cRow += 1
                cColumn -= 1
                if (getCell(cRow, cColumn) != cell) {
                    break
                }
                else {
                    count++
                }
            } else {
                break
            }
        }
        if (count >= 5) {
            hasWinner = true
            return cell
        }
        return Cell.EMPTY
    }

}