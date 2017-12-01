package com.herscher.cribbage.activity

import android.app.Activity
import android.support.annotation.StringRes
import com.herscher.cribbage.fragment.SimpleMessageDialogFragment

open class BaseActivity : Activity(), SimpleMessageDialogFragment.Callback {
    fun showMessage(@StringRes title: Int, @StringRes message: Int) {
        showMessage(getString(title), getString(message))
    }

    fun showMessage(title: String, message: String) {
        val dialogFragment = SimpleMessageDialogFragment.newInstance(title, message, true)
        dialogFragment.show(fragmentManager, SimpleMessageDialogFragment.TAG)
    }

    fun hideMessage() {
        val fragment = fragmentManager
                .findFragmentByTag(SimpleMessageDialogFragment.TAG) as SimpleMessageDialogFragment
        fragment.dismiss()
    }

    override fun onPositiveChoice(fragment: SimpleMessageDialogFragment) {
        // intended for override
    }
}