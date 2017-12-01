package com.herscher.cribbage.core

import com.herscher.cribbage.game.GameCenter
import com.herscher.cribbage.game.StandardCardFactory
import com.herscher.cribbage.game.StandardRulesController
import com.herscher.cribbage.game.StandardScorer
import com.herscher.cribbage.model.Game
import com.herscher.cribbage.model.GameState
import com.herscher.cribbage.model.Options
import com.herscher.cribbage.model.Player
import dagger.Module
import dagger.Provides

@Module
class ApplicationModule(private val app: CribbageApplication) {
    var gameCenter: GameCenter

    init {
        val playerList = ArrayList<Player>()
        playerList.add(Player("1", 0))
        playerList.add(Player("2", 11))

        val game = Game(GameState.NEW,
                Options(),
                playerList)

        gameCenter = GameCenter(game, StandardRulesController(StandardScorer(), StandardCardFactory()))
    }

    @Provides
    fun providesApplication(): CribbageApplication {
        return app
    }

    @Provides
    fun providesGameCenter(): GameCenter {
        return gameCenter
    }
}