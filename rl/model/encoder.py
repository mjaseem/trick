import random
from typing import List, Optional

from engine import constants, GameState, PlayerState, Card, Suit, IllegalMoveException, ImmutableTrick

MAX_HAND_SIZE = constants.MAX_CARDS_IN_HAND  # Max number of cards per player
ENCODE_ONLY_LAST_TRICK = True
PLAYER_COUNT = 2
VECTOR_SIZE = 5 * MAX_HAND_SIZE + 4 + (10 if ENCODE_ONLY_LAST_TRICK else (
        5 * PLAYER_COUNT * MAX_HAND_SIZE))  # 4 for trump suit, 13 tricks with 5 cards each


def decode_and_validate(state_vector: List[float], game_state: GameState, action: int, player: str) -> int:
    player_state = next((p for p in game_state.players if p.name == player), None)
    if player_state is None:
        raise ValueError(f"Player {player} not found")

    hand_vector = state_vector[0:5 * MAX_HAND_SIZE]
    card_vector = hand_vector[action * 5:(action + 1) * 5]

    if card_vector == [0.0, 0.0, 0.0, 0.0, 0.0]:
        raise IllegalMoveException(player, "Action corresponds to an empty card")

    selected_card = _decode_card(card_vector)
    lead_card = _lead_card_from_game_state(game_state)
    if lead_card and selected_card.suit != lead_card.suit:
        for card in player_state.hand:
            if card.suit == lead_card.suit:
                raise IllegalMoveException(player, f"Must follow suit: {str(lead_card)}. But picked {selected_card}")
    if selected_card not in player_state.hand:
        raise Exception(player, f"Card {selected_card} not in hand: {player_state.hand}")
    return player_state.hand.index(selected_card)


def encode(game_state: GameState, player: str) -> List[float]:
    state_vector = []

    # Encode player states
    player_state = next(
        (p for p in game_state.players if p.name == player), None
    )
    if player_state is None:
        raise ValueError(f"Player {player} not found")
    state_vector.extend(_encode_player_state(player_state))

    # Encode trick history (current trick)
    tricks_vector = _encode_tricks(game_state, only_current_trick=ENCODE_ONLY_LAST_TRICK)
    state_vector.extend(tricks_vector)

    # Encode trump suit
    state_vector.extend(_suit_encodings[game_state.trump])
    _assert_lead_suit_encoding(state_vector, game_state)

    return state_vector


def _assert_lead_suit_encoding(state_vector: List[float], game_state: GameState) -> None:
    lead_suit = _lead_suit_from_game_state(game_state)
    # Assert the trump suit is encoded correctly
    vector = _lead_suit_from_vector(state_vector)
    decoded_lead_suit = _decode_suit(vector)
    if decoded_lead_suit != lead_suit:
        raise Exception(f"Lead suit encoding mismatch {vector} {decoded_lead_suit} != {lead_suit}")


def _lead_suit_from_game_state(game_state: GameState) -> Optional[Suit]:
    lead_card = _lead_card_from_game_state(game_state)
    return lead_card.suit if lead_card else None

def _lead_card_from_game_state(game_state):
    tricks = game_state.tricks
    return tricks[-1].plays[0][1]\
        if (tricks and tricks[-1].plays and len(tricks[-1].plays) != len(game_state.players)) \
        else None


def _lead_suit_from_vector(state_vector: List[float]) -> List[float]:
    return state_vector[5 * MAX_HAND_SIZE:5 * MAX_HAND_SIZE + 4]


def _decode_suit(suit_vector: List[float]) -> Optional[Suit]:
    if suit_vector == [0.0, 0.0, 0.0, 0.0]:
        return None
    for suit, encoding in _suit_encodings.items():
        if suit_vector == list(encoding):
            return suit
    raise ValueError(f"Invalid suit encoding {suit_vector}")


def _decode_card(card_vector: List[float]) -> Optional[Card]:
    suit = _decode_suit(card_vector[:4])
    rank = card_vector[4] * constants.MAX_CARD_RANK
    if suit is None or not (0 < rank <= constants.MAX_CARD_RANK):
        return None
    return Card(suit, int(rank))


def _encode_tricks(game_state: GameState, only_current_trick: bool) -> List[float]:
    return _encode_current_trick(game_state) if only_current_trick else _encode_trick_history(game_state)


def _encode_trick_history(game_state: GameState) -> List[float]:
    tricks_vector = []
    for trick in game_state.tricks:
        tricks_vector.extend(_encode_trick(trick, len(game_state.players)))
    for _ in range(len(game_state.tricks), MAX_HAND_SIZE):
        tricks_vector.extend(_encode_trick(ImmutableTrick(), len(game_state.players)))
    return tricks_vector


def _encode_current_trick(game_state: GameState) -> List[float]:
    trick = game_state.tricks
    if not trick or len(trick[-1].plays) == len(game_state.players):
        return _encode_trick(ImmutableTrick(), len(game_state.players))
    else:
        return _encode_trick(trick[-1], len(game_state.players))


def _encode_player_state(player_state: PlayerState) -> List[float]:
    player_vector = []

    # Encode the player's hand (each card as a vector of 5 elements)
    padded_card_list = list(player_state.hand) + [None] * (MAX_HAND_SIZE - len(player_state.hand))
    random.shuffle(padded_card_list)
    for card in padded_card_list:
        player_vector.extend(_encode_card(card))

    return player_vector


_suit_encodings = {
    Suit.HEARTS: (1.0, 0.0, 0.0, 0.0),
    Suit.CLUBS: (0.0, 1.0, 0.0, 0.0),
    Suit.SPADES: (0.0, 0.0, 1.0, 0.0),
    Suit.DIAMONDS: (0.0, 0.0, 0.0, 1.0),
}


def _encode_card(card: Optional[Card]) -> List[float]:
    if card is None:
        return [0.0, 0.0, 0.0, 0.0, 0.0]
    card_vector = []
    # Encode suit as one-hot (4 elements)
    suit = card.suit
    card_vector.extend(_suit_encodings[suit])

    # Normalize rank between 0 and 1 (assuming rank 1-Records.MAX_CARDS)
    if not (0 < card.rank <= constants.MAX_CARD_RANK):
        raise ValueError("Bad card")
    card_vector.append(min(1.0, card.rank / constants.MAX_CARD_RANK))

    return card_vector


def _encode_trick(trick: ImmutableTrick, player_count: int) -> List[float]:
    trick_vector = []

    # Encode each card played in the trick (up to 13 cards)
    for play in trick.plays:
        trick_vector.extend(_encode_card(play[1]))

    # Pad with zero vectors if fewer than player_count cards were played
    for _ in range(len(trick.plays), player_count):  # TODO need to indicate the player to support more than one
        trick_vector.extend(_encode_card(None))  # Empty card

    return trick_vector


if __name__ == "__main__":
    new_trick = ImmutableTrick(plays=(("test", Card(Suit.HEARTS, 3)),))
    l = encode(GameState(turn_count=2, tricks=(new_trick,), trump=Suit.SPADES, players=(
        PlayerState("test", (Card(Suit.HEARTS, 3), Card(Suit.HEARTS, 3), Card(Suit.SPADES, 3)), 3),
        PlayerState("test", (Card(Suit.HEARTS, 3), Card(Suit.HEARTS, 3), Card(Suit.SPADES, 3)), 3))), "test")
    print(len(l))
    print(l)
    print(f"hand: {l[:65]}")
    print(f"rest:{l[65:]}")
    print(VECTOR_SIZE)
