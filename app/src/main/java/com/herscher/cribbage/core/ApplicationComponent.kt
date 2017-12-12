package com.herscher.cribbage.core

import com.herscher.cribbage.activity.BaseActivity
import com.herscher.cribbage.activity.GameActivity
import com.herscher.cribbage.fragment.RoundScoreDialogFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {
    fun inject(activity: BaseActivity)
    fun inject(activity: GameActivity)
    fun inject(fragment: RoundScoreDialogFragment)
}