package com.example.rpuzzle.game

import androidx.lifecycle.MutableLiveData
import java.util.*
import kotlin.collections.HashMap

class Puzzle {

    private var initBoardArr = IntArray(size*size)
    private var targetBoard = IntArray(size*size)

    private var currentBoard = RBoardAdvanced()
    private var tempBoard = RBoardAdvanced()

    private val solStack = Stack<IntArray>()
    private var solRevealed = false

    var targetBoardArrLiveData = MutableLiveData(
        IntArray(
            size * size
        )
    )
    var currentBoardArrLiveData = MutableLiveData(
        IntArray(
            size * size
        )
    )

    var solutionStringLiveData = MutableLiveData("view solution")

    var solvedLiveData = MutableLiveData(false)

    var puzzleType = TORUS
    var bestMoves = 0
    var playerMoves = 0

    private val movesStack = Stack<Int>()

    fun generateRandomPuzzle() {
        generateRandomBoard()
        bestMoves = solve()
        playerMoves = 0
        currentBoardArrLiveData.postValue(currentBoard.getBoard())
        targetBoardArrLiveData.postValue(targetBoard)
        solutionStringLiveData.postValue("view solution")
        solRevealed = false
        if(deepEquals(currentBoard.getBoard(), targetBoard)){
            solvedLiveData.postValue(true)
        }else{
            solvedLiveData.postValue(false)
        }


        movesStack.clear()

    }

    private fun generateRandomBoard(){
        val rand = Random()
        // making the board blank
        for (i in 0 until size * size) {
            currentBoard.getBoard()[i] = 0
        }
        // numOfColoredCells = 4 or 5 or 9 with probability 22% each (these are easy and interesting)
        // numOfColoredCells = 3 or 6 with probability 12% each (these are of moderate interests)
        // numOfColoredCells = 7 or 8 with probability 5% each (these kind of puzzles are difficult to keep track of in ones head)
        // doing this keeps the app more interesting than just random puzzles
        val numOfColoredCells: Int
        var p = rand.nextFloat()
        numOfColoredCells =
            if (p < .22) 4 else if (p < .44) 5 else if (p < .66) 9 else if (p < .78) 3 else if (p < .90) 6 else if (p < .95) 7 else 8
        val coloredCells: ArrayList<Int> = ArrayList()
        for (i in 0 until numOfColoredCells) {
            var cell = rand.nextInt(9)
            while (coloredCells.contains(cell)) {
                cell = rand.nextInt(9)
            }
            coloredCells.add(cell)
        }

        for (i in coloredCells) {
            p = rand.nextFloat()
            currentBoard.getBoard()[i] = if (p < .5) 1 else 2
        }

        deepCopy(currentBoard.getBoard(), initBoardArr)

        val rMoves = (Math.random() * RANDOM_MOVES).toInt()
        var m: Int
        for (i in 0 until rMoves) {
            m = rand.nextInt(6) + 1
            currentBoard.makeMove(m)
        }
        deepCopy(currentBoard.getBoard(), targetBoard)
        currentBoard.setBoard(initBoardArr)
    }

    private fun solve(): Int {

        val frontier = LinkedList<IntArray>()
        val parentMap = HashMap<String, IntArray?>()
        val adjacencyList = LinkedList<IntArray>()

        var boardArr: IntArray?
        var boardString = getBoardString(currentBoard.getBoard())

        frontier.offer(currentBoard.getBoard())
        parentMap[boardString] = null

        while (frontier.isNotEmpty()) {
            boardArr = frontier.poll()

            if (deepEquals(boardArr, targetBoard)) {
                solStack.clear()
                while (boardArr != null) {
                    solStack.push(boardArr)
                    boardString = getBoardString(boardArr)
                    boardArr = parentMap[boardString]
                }
                return solStack.size-1
            }
            for (i in 1 until 7){
                tempBoard.setBoard(boardArr)
                tempBoard.makeMove(i)
                boardString = getBoardString(tempBoard.getBoard())
                if ((!deepEquals(tempBoard.getBoard(), initBoardArr)) && (parentMap[boardString] == null)){
                    parentMap[boardString]= boardArr
                    val tempArr = IntArray(size* size)
                    deepCopy(tempBoard.getBoard(), tempArr)
                    adjacencyList.add(tempArr)
                }
            }

            for (adj in adjacencyList) {
                frontier.add(adj)
            }
            adjacencyList.clear()
        }
        return 0
    }

    fun updatePuzzleType(puzzleType: Int) {
        this.puzzleType = puzzleType
        currentBoard.puzzleType = puzzleType
        tempBoard.puzzleType = puzzleType


        generateRandomPuzzle()
    }

    fun undo() {
        if (!movesStack.empty()) {

            currentBoard.makeMove(currentBoard.undoMap[movesStack.pop()]!!)
            currentBoardArrLiveData.postValue(currentBoard.getBoard())

            playerMoves -= 1
        }
    }

    fun reset() {
        playerMoves = 0
        movesStack.clear()
        currentBoard.setBoard(initBoardArr)
        currentBoardArrLiveData.postValue(currentBoard.getBoard())

    }

    fun makeMove(move: Int) {

        currentBoard.makeMove(move)

        currentBoardArrLiveData.postValue(currentBoard.getBoard())

        movesStack.push(move)

        playerMoves += 1

        if (deepEquals(currentBoard.getBoard(), targetBoard)) solvedLiveData.postValue(true)

    }

    private fun deepCopy(from: IntArray, to: IntArray) {
        for (i in from.indices) {
            to[i] = from[i]
        }
    }

    private fun deepEquals(a:IntArray, b:IntArray): Boolean{
        for(i in a.indices){
            if(a[i] != b[i])
                return false
        }
        return true
    }

    private fun getBoardString(board: IntArray): String{
        val sb = StringBuilder()
        for(i in board.indices)
            sb.append(board[i])
        return sb.toString()
    }

    fun buildSolutionText(){
        if(!solRevealed){
            if (solStack.isNotEmpty()){
                val sb = StringBuilder()
                tempBoard.setBoard(solStack.pop())
                while(solStack.isNotEmpty()){
                    val solStep = solStack.pop()
                    for(i in 1 until 7){
                        tempBoard.makeMove(i)
                        if(deepEquals(tempBoard.getBoard(), solStep)){
                            sb.append(movesDictionary[i])
                            if(solStack.isNotEmpty()) sb.append(" ")
                            break
                        }
                        tempBoard.makeMove(tempBoard.undoMap[i]!!)
                    }
                }
                solRevealed = true
                solutionStringLiveData.postValue(sb.toString())
            }
        }
    }

    companion object {
        const val RANDOM_MOVES = 50
        const val TORUS = 0
        const val KB = 1
        const val RPP = 2

        const val size = 3
        const val UP = 1
        const val DOWN = 2
        const val LEFT= 3
        const val RIGHT = 4
        const val CW = 5
        const val CCW = 6
        val movesDictionary = hashMapOf(1 to "UP", 2 to "DOWN", 3 to "LEFT", 4 to "RIGHT", 5 to "CW", 6 to "CCW")
    }
}