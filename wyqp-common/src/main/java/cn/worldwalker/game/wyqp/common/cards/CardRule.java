package cn.worldwalker.game.wyqp.common.cards;

import java.util.List;

import cn.worldwalker.game.wyqp.common.domain.base.Card;
import cn.worldwalker.game.wyqp.common.enums.CardTypeEnum;

public class CardRule {
	
	/**
	 * 计算牌型
	 * @param cardList
	 * @return
	 */
	public static Integer calculateCardType(List<Card> cardList){
		Card card0 = cardList.get(0);
		Card card1 = cardList.get(1);
		Card card2 = cardList.get(2);
		/**炸弹*/
		if (card0.getCardValue().equals(card1.getCardValue()) && card1.getCardValue().equals(card2.getCardValue())) {
			return CardTypeEnum.BOMB.cardType;
		}
		/**同花顺*/
		if ((card1.getCardValue().equals(card0.getCardValue() + 1) && card2.getCardValue().equals(card1.getCardValue() + 1)
			|| card0.getCardValue() == 14 && card1.getCardValue() == 2 && card2.getCardValue() == 3)
			&& card1.getCardSuit().equals(card0.getCardSuit()) && card2.getCardSuit().equals(card1.getCardSuit())) {
			return CardTypeEnum.STRAIGHT.cardType;
		}
		/**金花*/
		if (card1.getCardSuit().equals(card0.getCardSuit()) && card2.getCardSuit().equals(card1.getCardSuit())) {
			return CardTypeEnum.JINHUA.cardType;
		}
		/**普通顺子*/
		if (card1.getCardValue().equals(card0.getCardValue() + 1) && card2.getCardValue().equals(card1.getCardValue() + 1)
			|| card0.getCardValue() == 14 && card1.getCardValue() == 2 && card2.getCardValue() == 3) {
			return CardTypeEnum.STRAIGHT.cardType;
		}
		/**对子*/
		if (card0.getCardValue().equals(card1.getCardValue()) || card1.getCardValue().equals(card2.getCardValue()) || card0.getCardValue().equals(card2.getCardValue())) {
			return CardTypeEnum.PAIR.cardType;
		}
		/**单个*/
		return CardTypeEnum.SINGLE.cardType;
	}
	
	/**
	 * 对玩家的牌进行排序，从小到大
	 * @param playerCards
	 */
	public static void rankCards(List<List<Card>> playerCards){
		for(List<Card> cards : playerCards){
			int size = cards.size();
			for(int i = 0; i< size - 1; i++){
				for(int j = 0; j < size - 1 - i; j++){
					if (cards.get(j).getCardValue() > cards.get(j + 1).getCardValue()) {
						Card tempCard = cards.get(j);
						cards.set(j, cards.get(j + 1));
						cards.set(j + 1, tempCard);
					}else if(cards.get(j).getCardValue().equals(cards.get(j + 1).getCardValue())){
						if (cards.get(j).getCardSuit() >  cards.get(j + 1).getCardSuit()) {
							Card tempCard = cards.get(j);
							cards.set(j, cards.get(j + 1));
							cards.set(j + 1, tempCard);
						}
					}
				}
			}
		}
		/**处理顺子123这种特殊情况*/
		for(List<Card> cards : playerCards){
			Card card0 = cards.get(0);
			Card card1 = cards.get(1);
			Card card2 = cards.get(2);
			if (card0.getCardValue() == 2 && card1.getCardValue() == 3 && card2.getCardValue() == 14) {
				cards.set(2, card1);
				cards.set(1, card0);
				cards.set(0, card2);
			}
		}
	}
	
	/**
	 * 比较两张牌的大小(牌值和花色综合比较)
	 * @param card1
	 * @param card2
	 * @return
	 */
	public static int compareTwoCardWithCardValueAndSuit(Card card1, Card card2){
		if (card1.getCardValue() > card2.getCardValue()) {
			return 1;
		}else if(card1.getCardValue().equals(card2.getCardValue())){//如果牌值相等，则继续比较花色
			/**一副牌中牌值相同，花色肯定不同*/
			if (card1.getCardSuit() > card2.getCardSuit()) {
				return 1;
			}else{
				return -1;
			}
		}else{
			return -1;
		}
	}
	
	/**
	 * 比较两张牌的牌值大小
	 * @param card1
	 * @param card2
	 * @return
	 */
	public static int compareTwoCardWithCardValue(Card card1, Card card2){
		if (card1.getCardValue() > card2.getCardValue()) {
			return 1;
		}else if(card1.getCardValue().equals(card2.getCardValue())){//如果牌值相等，则继续比较花色
			return 0;
		}else{
			return -1;
		}
	}
	
	/**
	 * 比较两张牌的花色大小
	 * @param card1
	 * @param card2
	 * @return
	 */
	public static int compareTwoCardWithCardSuit(Card card1, Card card2){
		if (card1.getCardSuit() > card2.getCardSuit()) {
			return 1;
		}else{
			return -1;
		}
	}
	
}
