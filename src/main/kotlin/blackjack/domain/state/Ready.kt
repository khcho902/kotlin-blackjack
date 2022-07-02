package blackjack.domain.state

import blackjack.domain.card.Card
import blackjack.domain.card.PlayerDeck

class Ready(private val playerDeck: PlayerDeck) : State {
    override fun currentCard(): PlayerDeck = playerDeck

    override fun isFinish(): Boolean = false

    override fun draw(card: Card): State {
        playerDeck.addCard(card)

        when {
            playerDeck.cards.size < BASE_CARD_SIZE -> return Ready(playerDeck)
            playerDeck.cards.size == BASE_CARD_SIZE && score(playerDeck) == BLACKJACK_NUMBER -> return BlackJack(playerDeck)
        }

        return Hit(playerDeck)
    }

    companion object {
        private const val BASE_CARD_SIZE = 2
    }
}