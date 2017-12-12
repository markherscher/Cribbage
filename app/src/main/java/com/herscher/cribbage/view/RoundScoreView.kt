package com.herscher.cribbage.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.herscher.cribbage.R
import com.herscher.cribbage.model.ScoreUnit
import kotlinx.android.synthetic.main.view_round_score.view.*

class RoundScoreView : ConstraintLayout {
    private val adapter = RecyclerViewAdapter()

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        View.inflate(context, R.layout.view_round_score, this)

        score_recycler.adapter = adapter
        score_recycler.layoutManager = LinearLayoutManager(context)
    }

    var playerName: String = ""
        set(value) {
            field = value
            title.text = value
        }

    var scoreUnits: List<ScoreUnit>?
        set(value) {
            adapter.scoreUnits = value
            adapter.notifyDataSetChanged()
        }
        get() = adapter.scoreUnits

    private inner class ScoreUnitViewHolder(val scoreUnitView: ScoreUnitView) : RecyclerView.ViewHolder(scoreUnitView) {
        var scoreUnit: ScoreUnit?
            set(value) {
                scoreUnitView.scoreUnit = value
            }
            get() = scoreUnitView.scoreUnit
    }

    private inner class RecyclerViewAdapter : RecyclerView.Adapter<ScoreUnitViewHolder>() {
        var scoreUnits: List<ScoreUnit>? = null

        override fun onBindViewHolder(holder: ScoreUnitViewHolder?, position: Int) {
            holder?.scoreUnit = scoreUnits?.get(position)
        }

        override fun getItemCount(): Int = scoreUnits?.size ?: 0

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ScoreUnitViewHolder {
            val context = parent!!.context
            val view = ScoreUnitView(context)

            // FUCKING PIECE OF SHIT RECYCLERVIEW MUST BE TOLD TO USE THE CORRECT WIDTH BECAUSE
            // IT'S FUCKING BUGGY AS SHIT
            view.layoutParams = RecyclerView.LayoutParams((parent as RecyclerView).layoutManager.width,
                    RecyclerView.LayoutParams.WRAP_CONTENT)

            return ScoreUnitViewHolder(view)
        }

    }
}