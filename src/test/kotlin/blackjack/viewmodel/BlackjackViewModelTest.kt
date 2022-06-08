package blackjack.viewmodel

import blackjack.domain.CardDeck
import blackjack.domain.CardNumber
import blackjack.domain.Player
import blackjack.domain.PlayingCard
import blackjack.domain.PlayingCards
import blackjack.domain.Suit
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BlackjackViewModelTest {
    private val names = listOf("Spade", "Diamond")
    private val players: List<Player>
        get() = names.map { name ->
            Player.from(name)
        }
    private val viewModel: BlackjackViewModel
        get() = BlackjackViewModel.of(
            players = players,
            cardDeck = CardDeck.from(PlayingCard.all())
        )

    @Test
    fun `players는 현재 게임에 참가 중인 참가자들을 보관한다`() {
        assertThat(viewModel.players).allMatch { player ->
            player.name in names
        }
    }

    @Test
    fun `BlackJackViewModel을 생성할 때 각 플레이어들은 카드를 두 장씩 뽑는다`() {
        assertThat(viewModel.players).allMatch { player ->
            player.hands.cards.size == BlackjackViewModel.START_CARD_COUNT
        }
    }

    @Test
    fun `BlackJackViewModel을 생성할 때 카드를 뽑을 수 있는 첫 번째 사람이 턴을 얻게 된다`() {
        val viewModel = BlackjackViewModel.of(
            players = players,
            cardDeck = CardDeck.from(
                PlayingCards.from(
                    listOf(
                        PlayingCard.of(Suit.HEARTS, CardNumber.ACE),
                        PlayingCard.of(Suit.HEARTS, CardNumber.TWO),
                        PlayingCard.of(Suit.HEARTS, CardNumber.THREE),
                        PlayingCard.of(Suit.HEARTS, CardNumber.FOUR),
                        PlayingCard.of(Suit.HEARTS, CardNumber.FIVE),
                        PlayingCard.of(Suit.HEARTS, CardNumber.SIX)
                    )
                )
            )
        )

        assertThat(viewModel.currentTurn.value?.name).isEqualTo(names[0])
    }

    @Test
    fun `currentTurn을 observe해서 어떤 플레이어의 턴인지 확인할 수 있다`() {
        val viewModel = viewModel
        viewModel.currentTurn.observe { player ->
            assertThat(player).isIn(viewModel.players)
        }

        viewModel.currentTurn.value = viewModel.players[1]
    }

    @Test
    fun `hit를 호출하여 현재 턴인 플레이어가 카드를 받을 수 있다`() {
        val viewModel = viewModel
        viewModel.hit()

        assertThat(viewModel.currentTurn.value?.hands?.cards?.size).isEqualTo(3)
    }

    @Test
    fun `stay를 호출하여 더 이상 플레이어가 카드를 뽑지 않고 차례를 마칠 수 있다`() {
        val viewModel = viewModel
        viewModel.stay()

        assertThat(viewModel.currentTurn.value?.isReceivable()).isFalse
    }

    @Test
    fun `nextTurn을 호출하여 카드를 받을 수 있는 플레이어에게 턴을 넘길 수 있다`() {
        val viewModel = BlackjackViewModel.of(
            players = players,
            cardDeck = CardDeck.from(
                PlayingCards.from(
                    listOf(
                        PlayingCard.of(Suit.HEARTS, CardNumber.ACE),
                        PlayingCard.of(Suit.HEARTS, CardNumber.EIGHT),
                        PlayingCard.of(Suit.DIAMONDS, CardNumber.ACE),
                        PlayingCard.of(Suit.HEARTS, CardNumber.FOUR),
                        PlayingCard.of(Suit.SPADES, CardNumber.TWO),
                        PlayingCard.of(Suit.HEARTS, CardNumber.SIX)
                    )
                )
            )
        )

        viewModel.hit()
        viewModel.nextTurn()

        assertThat(viewModel.currentTurn.value?.name).isEqualTo("Diamond")
    }
}