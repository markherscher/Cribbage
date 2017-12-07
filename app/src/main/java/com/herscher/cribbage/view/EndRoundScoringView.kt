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
import kotlinx.android.synthetic.main.view_end_round_scoring.view.*

class EndRoundScoringView : ConstraintLayout {
    private val adapter = RecyclerViewAdapter()

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        View.inflate(context, R.layout.view_end_round_scoring, this)

        score_recycler.adapter = adapter
        score_recycler.layoutManager = LinearLayoutManager(context)
    }

    var scoreUnits: List<ScoreUnit>?
        set(value) {
            adapter.scoreUnits = value
            adapter.notifyDataSetChanged()
        }
        get() = adapter.scoreUnits

    private inner class ScoreUnitViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                    R.layout.view_score_unit, parent, false)) {
        var scoreUnit: ScoreUnit?
            set(value) {
                if (itemView is ScoreUnitView) {
                    itemView.scoreUnit = value
                }
            }
            get() {
                return if (itemView is ScoreUnitView) {
                    itemView.scoreUnit
                } else {
                    null
                }
            }
    }

    private inner class RecyclerViewAdapter : RecyclerView.Adapter<ScoreUnitViewHolder>() {
        var scoreUnits: List<ScoreUnit>? = null

        override fun onBindViewHolder(holder: ScoreUnitViewHolder?, position: Int) {
            holder?.scoreUnit = scoreUnits?.get(position)
        }

        override fun getItemCount(): Int = scoreUnits?.size ?: 0

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ScoreUnitViewHolder = ScoreUnitViewHolder(parent!!)

    }
}