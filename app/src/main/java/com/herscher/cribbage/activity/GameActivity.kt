package com.herscher.cribbage.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import com.herscher.cribbage.R
import com.herscher.cribbage.core.CribbageApplication
import com.herscher.cribbage.fragment.RoundScoreDialogFragment
import com.herscher.cribbage.fragment.SimpleMessageDialogFragment
import com.herscher.cribbage.game.GameCenter
import com.herscher.cribbage.model.Card
import com.herscher.cribbage.model.GameState
import com.herscher.cribbage.model.Player
import com.herscher.cribbage.model.PlayerScoring
import com.herscher.cribbage.view.HandView
import kotlinx.android.synthetic.main.activity_game.*
import org.parceler.Parcel
import org.parceler.Parcels
import javax.inject.Inject

class GameActivity : BaseActivity() {
    @Inject lateinit var gameCenter: GameCenter
    private val gameListener = GameListener()
    private val playerSlots = HashMap<Player, PlayerSlot>()
    private var instanceData: InstanceData = InstanceData()
    private var isHotseatPlay: Boolean = false

    @Parcel
    private class InstanceData {
        companion object {
            const val TAG = "InstanceDataTag"
        }

        var hideCards: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as CribbageApplication).component.inject(this)
        setContentView(R.layout.activity_game)

        if (savedInstanceState != null) {
            instanceData = Parcels.unwrap(savedInstanceState.getParcelable(InstanceData.TAG))
        }

        var localPlayerCount = 0
        for (p in gameCenter.allPlayers) {
            if (gameCenter.getPlayerInfo(p).isLocal) {
                localPlayerCount++
            }
        }

        isHotseatPlay = localPlayerCount > 1
        played_cards_view.isFacedown = false

        gameCenter.resumeGame() // tODO why both here?
        initGame()
    }

    override fun onResume() {
        super.onResume()
        gameCenter.addListener(gameListener)
        gameCenter.resumeGame()
    }

    override fun onPause() {
        super.onPause()
        gameCenter.removeListener(gameListener)
    }

    override fun onPositiveChoice(fragment: SimpleMessageDialogFragment) {
        if (instanceData.hideCards) {
            showActivePlayerHand()
            instanceData.hideCards = false
        }
    }

    private fun initGame() {
        playerSlots.clear()

        when (gameCenter.allPlayers.size) {
            3 -> {
                val player1 = gameCenter.allPlayers.get(0)
                val player2 = gameCenter.allPlayers.get(1)
                val player3 = gameCenter.allPlayers.get(2)
                playerSlots.put(player1, PlayerSlot(player1, south_hand_view, south_name_text))
                playerSlots.put(player2, PlayerSlot(player2, east_hand_view, east_name_text))
                playerSlots.put(player3, PlayerSlot(player3, north_hand_view, north_name_text))

                east_hand_view.visibility = View.GONE
                east_name_text.visibility = View.GONE
            }
            4 -> {
                val player1 = gameCenter.allPlayers.get(0)
                val player2 = gameCenter.allPlayers.get(1)
                val player3 = gameCenter.allPlayers.get(2)
                val player4 = gameCenter.allPlayers.get(3)
                playerSlots.put(player1, PlayerSlot(player1, south_hand_view, south_name_text))
                playerSlots.put(player2, PlayerSlot(player2, west_hand_view, west_name_text))
                playerSlots.put(player3, PlayerSlot(player3, north_hand_view, north_name_text))
                playerSlots.put(player4, PlayerSlot(player4, east_hand_view, east_name_text))
            }
            else -> {
                val player1 = gameCenter.allPlayers.get(0)
                val player2 = gameCenter.allPlayers.get(1)
                playerSlots.put(player1, PlayerSlot(player1, south_hand_view, south_name_text))
                playerSlots.put(player2, PlayerSlot(player2, north_hand_view, north_name_text))

                east_hand_view.visibility = View.GONE
                east_name_text.visibility = View.GONE
                west_hand_view.visibility = View.GONE
                west_name_text.visibility = View.GONE
            }
        }

        // Show all players' data
        /*for (slot in playerSlots.values) {
            slot.handView.removeAllCards()
            slot.handView.listener = HandViewListener(slot.player)
            for (card in slot.player.hand) {
                slot.handView.addCard(card)
            }
            updateScore(slot.player)
        }*/

        showActivePlayer()
        updatePlayCount()
        //attemptToShowActivePlayerHand()
    }

    // TODO MARK: restore leadCard ability

    private fun showActivePlayer() {
        // TODO: show status/what is being waited on
        val showPlayerAsActive: Boolean

        when (gameCenter.gameState) {
            GameState.ROUND_START -> {
                // TODO: how to handle hotseat discards?
                showPlayerAsActive = false
            }
            GameState.LEAD -> {
                showPlayerAsActive = true
            }
            GameState.PLAY -> {
                showPlayerAsActive = true
            }
            else -> {
                showPlayerAsActive = false
            }
        }

        // Show the active player
        for (slot in playerSlots.values) {
            if (showPlayerAsActive && slot.player == gameCenter.activePlayer) {
                slot.nameView.setTextColor(
                        ContextCompat.getColor(this, R.color.activePlayerText))
            } else {
                slot.nameView.setTextColor(
                        ContextCompat.getColor(this, R.color.inactivePlayerText))
            }
        }
    }

    private fun updateScore(player: Player) {
        val slot = playerSlots[player]
        if (slot != null) {
            slot.nameView.text = String.format("%s (%d)",
                    gameCenter.getPlayerInfo(player).name, player.score)
        }
    }

    private fun updatePlayCount() {
        play_count_text.text = String.format("Count: %d", gameCenter.playTotal)
    }

    private fun hideAllPlayerHands() {
        for (slot in playerSlots.values) {
            slot.handView.isFacedown = true
        }
    }

    private fun showActivePlayerHand() {
        for (slot in playerSlots.values) {
            if (slot.player == gameCenter.activePlayer) {
                slot.handView.isFacedown = false
            }
        }
    }

    private fun showInstructions(text: String) {
        instructions_text.text = text
    }

    private fun attemptToShowActivePlayerHand() {
        val playerInfo = gameCenter.getPlayerInfo(gameCenter.activePlayer)
        hideAllPlayerHands()

        // If the current player's cards are hidden and we're in hot seat play: don't show yet
        if (playerInfo.isLocal) {
            if (playerSlots[gameCenter.activePlayer]?.handView?.isFacedown == true && isHotseatPlay) {
                if (!instanceData.hideCards) {
                    instanceData.hideCards = true
                    showMessage("Next Player",
                            String.format("Pass to %s and hit OK when ready.", playerInfo.name))
                }
            } else {
                showActivePlayerHand()
            }
        }
    }

    private inner class GameListener : GameCenter.Listener {
        override fun onCardsDiscarded(player: Player, cards: Array<Card>) {
            for (c in cards) {
                playerSlots[player]?.handView?.removeCard(c)
            }
        }

        override fun onCardLead(player: Player, card: Card, scores: PlayerScoring) {
            playerSlots[player]?.handView?.removeCard(card)
            played_cards_view.addCard(card)
            updateScore(player)
            updatePlayCount()
        }

        override fun onCardPlayed(player: Player, card: Card, scores: PlayerScoring) {
            playerSlots[player]?.handView?.removeCard(card)
            played_cards_view.addCard(card)
            updateScore(player)
            updatePlayCount()

            if (scores.scores.isNotEmpty()) {
                Snackbar.make(played_cards_view, "Points were scored", Snackbar.LENGTH_SHORT).show()
            }
        }

        override fun onRoundStart() {
            if (gameCenter.lastRoundScores != null &&
                    fragmentManager.findFragmentByTag(RoundScoreDialogFragment.TAG) == null) {
                RoundScoreDialogFragment.newInstance().show(fragmentManager, RoundScoreDialogFragment.TAG)
            }

            played_cards_view.removeAllCards()
            showInstructions("New round started; please discard cards")

            for (slot in playerSlots.values) {
                slot.handView.removeAllCards()
                slot.handView.listener = HandViewListener(slot.player)
                for (card in slot.player.hand) {
                    slot.handView.addCard(card)
                }
                updateScore(slot.player)
            }

            // Show all cards for now TODO temporary until I can figure out how to discard hot seat
            for (slot in playerSlots.values) {
                slot.handView.isFacedown = !gameCenter.getPlayerInfo(slot.player).isLocal
            }
        }

        override fun onLeadRequired(player: Player) {
            played_cards_view.removeAllCards()
            showInstructions(String.format("Waiting for %s to lead a card",
                    gameCenter.getPlayerInfo(player).name))
            attemptToShowActivePlayerHand()
        }

        override fun onPlayRequired(player: Player) {
            showInstructions(String.format("Waiting for %s to play a card",
                    gameCenter.getPlayerInfo(player).name))
            attemptToShowActivePlayerHand()
        }

        override fun onGameComplete() {

        }
    }

    private inner class HandViewListener(val player: Player) : HandView.Listener {
        override fun onAttemptedCardHighlight(card: Card): Boolean {
            when (gameCenter.gameState) {
                GameState.ROUND_START -> {
                    return gameCenter.getRemainingDiscardCount(player) > 0
                }
                GameState.LEAD -> {
                    return player == gameCenter.activePlayer
                }
                GameState.PLAY -> {
                    val isValid = gameCenter.isCardValidToPlay(player, card)
                    if (!isValid) {
                        Snackbar.make(play_count_text, "Card is not valid to play",
                                Snackbar.LENGTH_LONG).show()
                    }
                    return isValid
                }
                else -> {
                    // ignore
                    return false
                }
            }
        }

        override fun onCardPlayed(card: Card) {
            when (gameCenter.gameState) {
                GameState.ROUND_START -> {
                    if (gameCenter.getRemainingDiscardCount(player) > 0) {
                        gameCenter.discardCards(player, Array(1, { card }))
                    }
                }
                GameState.LEAD -> {
                    gameCenter.leadCard(player, card)
                }
                GameState.PLAY -> {
                    if (gameCenter.isCardValidToPlay(player, card)) {
                        gameCenter.playCard(player, card, true)
                    }
                }
                else -> {
                    // ignore
                }
            }
        }
    }

    private class PlayerSlot(val player: Player, val handView: HandView, val nameView: TextView)
}