package com.herscher.cribbage.fragment

import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.herscher.cribbage.R

const val MESSAGE_KEY = "message-key"
const val TITLE_KEY = "title-key"
const val CALLBACK_ENABLED = "callback-enabled"

class SimpleMessageDialogFragment : DialogFragment() {
    private var mMessage: String? = null
    private var mTitle: String? = null
    private var mCallbackEnabled: Boolean = false

    private var mCallback: Callback? = null

    interface Callback {
        fun onPositiveChoice(fragment: SimpleMessageDialogFragment)
    }

    companion object {
        const val TAG = "SimpleMessageDialogFragment"
        fun newInstance(message: String): SimpleMessageDialogFragment {
            return newInstance(null, message)
        }

        fun newInstance(title: String?, message: String): SimpleMessageDialogFragment {
            val frag = SimpleMessageDialogFragment()
            val bundle = Bundle()
            bundle.putString(MESSAGE_KEY, message)
            bundle.putString(TITLE_KEY, title)
            frag.arguments = bundle
            return frag
        }

        fun newInstance(title: String, message: String, callbackEnabled: Boolean): SimpleMessageDialogFragment {
            val frag = SimpleMessageDialogFragment()
            val bundle = Bundle()
            bundle.putString(MESSAGE_KEY, message)
            bundle.putString(TITLE_KEY, title)
            bundle.putBoolean(CALLBACK_ENABLED, callbackEnabled)
            frag.arguments = bundle
            return frag
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false

        mMessage = arguments.getString(MESSAGE_KEY)
        mTitle = arguments.getString(TITLE_KEY)
        mCallbackEnabled = arguments.getBoolean(CALLBACK_ENABLED)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(MESSAGE_KEY, mMessage)
        outState.putString(TITLE_KEY, mTitle)
        outState.putBoolean(CALLBACK_ENABLED, mCallbackEnabled)
        super.onSaveInstanceState(outState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is SimpleMessageDialogFragment.Callback) {
            mCallback = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        mCallback = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)

        if (mTitle != null) {
            builder.setTitle(mTitle)
        }

        return builder.setMessage(mMessage)
                .setPositiveButton(R.string.ok, DialogInterface.OnClickListener { dialog, i ->
                    dialog.cancel()
                    if (mCallback != null) {
                        mCallback!!.onPositiveChoice(this@SimpleMessageDialogFragment)
                    }
                })
                .create()
    }
}