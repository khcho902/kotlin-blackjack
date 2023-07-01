package blackjack.domain.shuffle

import blackjack.domain.card.Card

class CardCustomShuffler(
    private val shuffleDelegator: (List<Card>) -> List<Card>
) : Shuffler<Card> {

    override fun shuffled(list: List<Card>): List<Card> {
        return shuffleDelegator(list)
    }
}