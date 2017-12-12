package com.herscher.cribbage.fragment

import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import com.herscher.cribbage.R
import com.herscher.cribbage.core.CribbageApplication
import com.herscher.cribbage.game.GameCenter
import com.herscher.cribbage.view.RoundScoreView
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_round_score_dialog.*

class RoundScoreDialogFragment : DialogFragment() {
    @Inject lateinit var gameCenter: GameCenter

    companion object {
        const val TAG = "RoundScoreDialogFragment"

        fun newInstance(): RoundScoreDialogFragment {
            val frag = RoundScoreDialogFragment()
            val bundle = Bundle()
            //bundle.putString(MESSAGE_KEY, message)
            //bundle.putString(TITLE_KEY, title)
            //bundle.putBoolean(CALLBACK_ENABLED, callbackEnabled)
            frag.arguments = bundle
            return frag
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_round_score_dialog, container, false)
        val scoreView = view?.findViewById<RoundScoreView>(R.id.score_view)
        scoreView?.scoreUnits = gameCenter.lastRoundScores?.get(0)?.scores
        scoreView?.playerName = "Testing"
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        return dialog
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context.applicationContext as CribbageApplication).component.inject(this)
    }
}
