package com.example.rpuzzle.game

import com.example.rpuzzle.game.Puzzle.Companion.size
import com.example.rpuzzle.game.Puzzle.Companion.KB
import com.example.rpuzzle.game.Puzzle.Companion.RPP
import com.example.rpuzzle.game.Puzzle.Companion.TORUS
import com.example.rpuzzle.game.Puzzle.Companion.CCW
import com.example.rpuzzle.game.Puzzle.Companion.CW
import com.example.rpuzzle.game.Puzzle.Companion.DOWN
import com.example.rpuzzle.game.Puzzle.Companion.LEFT
import com.example.rpuzzle.game.Puzzle.Companion.RIGHT
import com.example.rpuzzle.game.Puzzle.Companion.UP

class RBoardAdvanced {
    private var board = IntArray(size * size)
    private var temp = IntArray(size * size)
    private val moves: HashMap<Int, HashMap<Int, HashMap<Int, Int>>> = HashMap()
    val undoMap =
        hashMapOf(UP to DOWN, DOWN to UP, LEFT to RIGHT, RIGHT to LEFT, CW to CCW, CCW to CW)
    var puzzleType = TORUS

    init {
        // this represents a hashmap of how each cell should move depending on the board type(TORUS, KB, RPP)
        // and the move(UP,DOWN,LEFT,RIGHT,CW,CCW)
        moves[TORUS] = hashMapOf(
            UP to hashMapOf(
                0 to 3,
                1 to 4,
                2 to 5,
                3 to 6,
                4 to 7,
                5 to 8,
                6 to 0,
                7 to 1,
                8 to 2
            ),
            DOWN to hashMapOf(
                0 to 6,
                1 to 7,
                2 to 8,
                3 to 0,
                4 to 1,
                5 to 2,
                6 to 3,
                7 to 4,
                8 to 5
            ),
            LEFT to hashMapOf(
                0 to 1,
                1 to 2,
                2 to 0,
                3 to 4,
                4 to 5,
                5 to 3,
                6 to 7,
                7 to 8,
                8 to 6
            ),
            RIGHT to hashMapOf(
                0 to 2,
                1 to 0,
                2 to 1,
                3 to 5,
                4 to 3,
                5 to 4,
                6 to 8,
                7 to 6,
                8 to 7
            ),
            CW to hashMapOf(
                0 to 3,
                1 to 0,
                2 to 1,
                3 to 6,
                4 to 4,
                5 to 2,
                6 to 7,
                7 to 8,
                8 to 5
            ),
            CCW to hashMapOf(
                0 to 1,
                1 to 2,
                2 to 5,
                3 to 0,
                4 to 4,
                5 to 8,
                6 to 3,
                7 to 6,
                8 to 7
            )
        )
        moves[KB] = hashMapOf(
            UP to hashMapOf(
                0 to 3,
                1 to 4,
                2 to 5,
                3 to 6,
                4 to 7,
                5 to 8,
                6 to 2,
                7 to 1,
                8 to 0
            ),
            DOWN to hashMapOf(
                0 to 8,
                1 to 7,
                2 to 6,
                3 to 0,
                4 to 1,
                5 to 2,
                6 to 3,
                7 to 4,
                8 to 5
            ),
            LEFT to hashMapOf(
                0 to 1,
                1 to 2,
                2 to 0,
                3 to 4,
                4 to 5,
                5 to 3,
                6 to 7,
                7 to 8,
                8 to 6
            ),
            RIGHT to hashMapOf(
                0 to 2,
                1 to 0,
                2 to 1,
                3 to 5,
                4 to 3,
                5 to 4,
                6 to 8,
                7 to 6,
                8 to 7
            ),
            CW to hashMapOf(
                0 to 3,
                1 to 0,
                2 to 1,
                3 to 6,
                4 to 4,
                5 to 2,
                6 to 7,
                7 to 8,
                8 to 5
            ),
            CCW to hashMapOf(
                0 to 1,
                1 to 2,
                2 to 5,
                3 to 0,
                4 to 4,
                5 to 8,
                6 to 3,
                7 to 6,
                8 to 7
            )
        )
        moves[RPP] = hashMapOf(
            UP to hashMapOf(
                0 to 3,
                1 to 4,
                2 to 5,
                3 to 6,
                4 to 7,
                5 to 8,
                6 to 2,
                7 to 1,
                8 to 0
            ),
            DOWN to hashMapOf(
                0 to 8,
                1 to 7,
                2 to 6,
                3 to 0,
                4 to 1,
                5 to 2,
                6 to 3,
                7 to 4,
                8 to 5
            ),
            LEFT to hashMapOf(
                0 to 1,
                1 to 2,
                2 to 6,
                3 to 4,
                4 to 5,
                5 to 3,
                6 to 7,
                7 to 8,
                8 to 0
            ),
            RIGHT to hashMapOf(
                0 to 8,
                1 to 0,
                2 to 1,
                3 to 5,
                4 to 3,
                5 to 4,
                6 to 2,
                7 to 6,
                8 to 7
            ),
            CW to hashMapOf(
                0 to 3,
                1 to 0,
                2 to 1,
                3 to 6,
                4 to 4,
                5 to 2,
                6 to 7,
                7 to 8,
                8 to 5
            ),
            CCW to hashMapOf(
                0 to 1,
                1 to 2,
                2 to 5,
                3 to 0,
                4 to 4,
                5 to 8,
                6 to 3,
                7 to 6,
                8 to 7
            )
        )
    }

    fun getBoard(): IntArray {
        return board
    }


    fun setBoard(boardArr: IntArray) {
        for (i in 0 until size * size) {
            this.board[i] = boardArr[i]
        }
    }

    fun makeMove(moveId: Int) {
        val moveMap = moves[puzzleType]!![moveId]!!

        for (i in 0 until size * size) {
            temp[i] = board[i]
        }

        for (i in 0 until size * size) {
            board[i] = temp[moveMap[i]!!]
        }
    }
}