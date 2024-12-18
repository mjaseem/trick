from engine import constants, Strategy


class PromptingStrategy(Strategy):

    def __init__(self):
        self.next_play = constants.MAX_CARD_RANK

    def choose_card(self, hand, history, trick, trump_suit):
        tmp = self.next_play
        self.next_play =  constants.MAX_CARD_RANK
        return tmp

    def should_wait(self):
        return self.next_play ==  constants.MAX_CARD_RANK

    def set_next_play(self, next_play):
        self.next_play = next_play
