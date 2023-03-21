package com.example.rpuzzle.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.rpuzzle.R
import com.example.rpuzzle.game.Puzzle.Companion.KB
import com.example.rpuzzle.game.Puzzle.Companion.RPP
import com.example.rpuzzle.game.Puzzle.Companion.TORUS
import com.example.rpuzzle.game.Puzzle.Companion.CCW
import com.example.rpuzzle.game.Puzzle.Companion.CW
import com.example.rpuzzle.game.Puzzle.Companion.DOWN
import com.example.rpuzzle.game.Puzzle.Companion.LEFT
import com.example.rpuzzle.game.Puzzle.Companion.RIGHT
import com.example.rpuzzle.game.Puzzle.Companion.UP
import com.example.rpuzzle.view.custom.RBoardView
import com.example.rpuzzle.viewmodel.PlayRPuzzleViewModel
import com.example.rpuzzle.viewmodel.PlayRPuzzleViewModel.MessageType


class MainActivity : AppCompatActivity() {
    private lateinit var viewmodel : PlayRPuzzleViewModel
    private lateinit var targetBoardView: RBoardView
    private lateinit var currentBoardView: RBoardView
    private lateinit var progressbar: ProgressBar
    private lateinit var solutionTextView:TextView

    private var puzzleTypeUpdated = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        targetBoardView = findViewById(R.id.target_rboard_view)
        currentBoardView = findViewById(R.id.current_rboard_view)


        viewmodel = ViewModelProvider(this)[PlayRPuzzleViewModel::class.java]

        progressbar = findViewById(R.id.rp_prgressbar)

        updatePuzzle()

        viewmodel.puzzle.targetBoardArrLiveData.observe(this, {
            updateTargetBoard(it)
        })

        viewmodel.puzzle.currentBoardArrLiveData.observe(this, {
            updateCurrentBoard(it)
        })

        viewmodel.puzzle.solutionStringLiveData.observe(this, {
            updateSolutionTextView(it)
        })


        val button1 = findViewById<Button>(R.id.rp_undo)
        button1.setOnClickListener { viewmodel.send(Pair(MessageType.UNDO, -1)) }

        val button2 = findViewById<Button>(R.id.rp_reset)
        button2.setOnClickListener { viewmodel.send(Pair(MessageType.RESET, -1)) }

        val button3 = findViewById<TextView>(R.id.rp_ccw)
        button3.setOnClickListener { viewmodel.send(Pair(MessageType.MOVE, CCW)) }

        val button4 = findViewById<TextView>(R.id.rp_up)
        button4.setOnClickListener { viewmodel.send(Pair(MessageType.MOVE, UP)) }

        val button5 = findViewById<TextView>(R.id.rp_cw)
        button5.setOnClickListener { viewmodel.send(Pair(MessageType.MOVE, CW)) }

        val button6 = findViewById<TextView>(R.id.rp_left)
        button6.setOnClickListener { viewmodel.send(Pair(MessageType.MOVE, LEFT)) }

        val button7 = findViewById<TextView>(R.id.rp_down)
        button7.setOnClickListener { viewmodel.send(Pair(MessageType.MOVE, DOWN)) }

        val button8 = findViewById<TextView>(R.id.rp_right)
        button8.setOnClickListener { viewmodel.send(Pair(MessageType.MOVE, RIGHT)) }

        val button9 = findViewById<Button>(R.id.next_puzzle)
        button9.setOnClickListener { viewmodel.send(Pair(MessageType.NEW_PUZZLE, -1)) }

        solutionTextView = findViewById(R.id.view_solution)
        solutionTextView.setOnClickListener{getSolutionString()}

        val puzzleType = findViewById<RadioGroup>(R.id.board_rg)!!
        when (viewmodel.puzzle.puzzleType) {
            KB -> puzzleType.check(R.id.kb)
            RPP -> puzzleType.check(R.id.rpp)
            else -> puzzleType.check(R.id.torus)
        }

        viewmodel.puzzle.solvedLiveData.observe(this, {
            button9.isEnabled = it
        })


        puzzleType.setOnCheckedChangeListener { _, checkedId ->

            val progressbar = findViewById<ProgressBar>(R.id.rp_prgressbar)

            progressbar.visibility = View.VISIBLE

            var ptype = TORUS
            if (checkedId == R.id.kb) ptype = KB
            else if (checkedId == R.id.rpp) ptype = RPP
            viewmodel.send(Pair(MessageType.UPDATE_PUZZLE_TYPE, ptype))

        }
    }

    private fun updateCurrentBoard(currentBoardArr: IntArray){
        //update the currentBoard
        currentBoardView.updateBoardString(currentBoardArr)

        //update the stats
        updateStats()

    }

    private fun updateTargetBoard(targetBoardArr: IntArray){
        //update the targetBoard
        targetBoardView.updateBoardString(targetBoardArr)
        puzzleTypeUpdated = true


        progressbar.visibility = View.GONE

    }

    private fun updateStats(){
        val stats = findViewById<TextView>(R.id.stats)
        val playerMoves = viewmodel.puzzle.playerMoves
        val bestMoves = viewmodel.puzzle.bestMoves
        stats.text =  "Best Solution: $bestMoves\nYour Moves: $playerMoves"
    }

    private fun updatePuzzle(){
        if (viewmodel.initial){
            progressbar.visibility = View.VISIBLE
            viewmodel.start()
            viewmodel.send(Pair(MessageType.NEW_PUZZLE, -1))

        }
    }

    private fun getSolutionString(){
        viewmodel.send(Pair(MessageType.SHOW_SOLUTION, -1))
    }

    private fun updateSolutionTextView(it: String?) {
        solutionTextView.text = it
    }
}