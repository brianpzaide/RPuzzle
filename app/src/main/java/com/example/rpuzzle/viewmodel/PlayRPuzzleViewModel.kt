package com.example.rpuzzle.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpuzzle.game.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch


class PlayRPuzzleViewModel : ViewModel() {
    // puzzle contains the mutable live data: currentBoard, and targetBoard
    val puzzle = Puzzle()
    // this is used for the initial setup
    var initial = true
    // MainActivity sends messages on this channel, worker coroutine receives the messages on this channel and manages the game state
    private val channel = Channel<Pair<MessageType, Int>>(Channel.UNLIMITED)

    fun send(msg: Pair<MessageType, Int>) {
        channel.trySend(msg)
    }

    fun start() {
        initial = false
        viewModelScope.launch(Dispatchers.Default) {
            for (msg in channel) {
                when (msg.first) {
                    // generating a new puzzle
                    MessageType.NEW_PUZZLE -> puzzle.generateRandomPuzzle()
                    // when user click on board type in the radio button group it updates the puzzle type and generates a new puzzle
                    MessageType.UPDATE_PUZZLE_TYPE -> puzzle.updatePuzzleType(msg.second)
                    // undoes the user's move
                    MessageType.UNDO -> puzzle.undo()
                    // resets the targetBoard
                    MessageType.RESET -> puzzle.reset()
                    // makes the move the user selected
                    MessageType.MOVE -> puzzle.makeMove(msg.second)
                    // solves the puzzle using breadth first search(BFS) and builds the solution string
                    MessageType.SHOW_SOLUTION -> puzzle.buildSolutionText()
                }
            }
        }
    }

    enum class MessageType {
        NEW_PUZZLE, UPDATE_PUZZLE_TYPE, UNDO, RESET, MOVE, SHOW_SOLUTION
    }

    override fun onCleared() {
        super.onCleared()
        channel.close()
    }
}


















