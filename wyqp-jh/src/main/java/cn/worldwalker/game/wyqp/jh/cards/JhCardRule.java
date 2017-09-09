package cn.worldwalker.game.wyqp.jh.cards;

import java.util.List;

import cn.worldwalker.game.wyqp.common.domain.base.BasePlayerInfo;
import cn.worldwalker.game.wyqp.common.domain.base.Card;
import cn.worldwalker.game.wyqp.jh.enums.JhCardTypeEnum;

public class JhCardRule {
	
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
			return JhCardTypeEnum.BOMB.cardType;
		}
		/**同花顺*/
		if ((card1.getCardValue().equals(card0.getCardValue() + 1) && card2.getCardValue().equals(card1.getCardValue() + 1)
			|| card0.getCardValue() == 14 && card1.getCardValue() == 2 && card2.getCardValue() == 3)
			&& card1.getCardSuit().equals(card0.getCardSuit()) && card2.getCardSuit().equals(card1.getCardSuit())) {
			return JhCardTypeEnum.STRAIGHT.cardType;
		}
		/**金花*/
		if (card1.getCardSuit().equals(card0.getCardSuit()) && card2.getCardSuit().equals(card1.getCardSuit())) {
			return JhCardTypeEnum.JINHUA.cardType;
		}
		/**普通顺子*/
		if (card1.getCardValue().equals(card0.getCardValue() + 1) && card2.getCardValue().equals(card1.getCardValue() + 1)
			|| card0.getCardValue() == 14 && card1.getCardValue() == 2 && card2.getCardValue() == 3) {
			return JhCardTypeEnum.STRAIGHT.cardType;
		}
		/**对子*/
		if (card0.getCardValue().equals(card1.getCardValue()) || card1.getCardValue().equals(card2.getCardValue()) || card0.getCardValue().equals(card2.getCardValue())) {
			return JhCardTypeEnum.PAIR.cardType;
		}
		/**单个*/
		return JhCardTypeEnum.SINGLE.cardType;
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
	 * 比较牌大小，返回赢家
	 * @param playerCards
	 */
	public static BasePlayerInfo comparePlayerCards(List<BasePlayerInfo> playerList){
		BasePlayerInfo maxPlayer = playerList.get(0);
		int playerNum = playerList.size();
		for(int k = 1; k < playerNum; k++){
			BasePlayerInfo curPlayer = playerList.get(k);
			if (compareTwoPlayerCards(maxPlayer, curPlayer) < 0) {
				maxPlayer = curPlayer;
			}
		}
		return maxPlayer;
	}
	/**
	 * 比较两个玩家牌的大小
	 * @param player1
	 * @param player2
	 * @return -1：player1 < player2 0：player1 == player2 1：player1 > player2
	 */
	public static int compareTwoPlayerCards(BasePlayerInfo player1, BasePlayerInfo player2){
		int result = 0;
		if (player1.getCardType() > player2.getCardType()) {
			result = 1;
		}else if(player1.getCardType().equals(player2.getCardType())){//如果牌型相同则比较每张牌的大小或花色
			JhCardTypeEnum cardTypeEnum = JhCardTypeEnum.getCardTypeEnum(player1.getCardType());
			switch (cardTypeEnum) {
				case BOMB:
					if (compareTwoCardWithCardValue(player1.getCardList().get(0), player2.getCardList().get(0)) > 0) {
						result = 1;
					}else{
						result = -1;
					}
					break;
				case FLUSH:
					if (compareTwoCardWithCardValueAndSuit(player1.getCardList().get(2), player2.getCardList().get(2)) > 0) {
						result = 1;
					}else{
						result = -1;
					}
					break;
				case JINHUA:
					result = jinhuaCompare(player1, player2);
					break;
				case STRAIGHT:
					if (compareTwoCardWithCardValueAndSuit(player1.getCardList().get(2), player2.getCardList().get(2)) > 1) {
						result = 1;
					}else{
						result = -1;
					}
					break;
				case PAIR:
					result = pairCompare(player1, player2);
					break;
				case SINGLE:
					result = singleCompare(player1, player2);
					break;
	
				default:
					break;
			}
			
		}else{
			result =  -1;
		}
		
		return result;
	}
	
	/**
	 * 单个比较
	 * @param player1
	 * @param player2
	 * @return
	 */
	public static int singleCompare(BasePlayerInfo player1, BasePlayerInfo player2){
		int result = 0;
		/**如果三张牌的牌值相等，则志需要比较最大牌的花色*/
		if (compareTwoCardWithCardValue(player1.getCardList().get(0), player2.getCardList().get(0)) == 0 
			&& compareTwoCardWithCardValue(player1.getCardList().get(1), player2.getCardList().get(1)) == 0
			&& compareTwoCardWithCardValue(player1.getCardList().get(2), player2.getCardList().get(2)) == 0) {
			if (compareTwoCardWithCardSuit(player1.getCardList().get(2), player2.getCardList().get(2)) > 0) {
				result = 1;
			}else{
				result = -1;
			}
			/**最小的两张牌值相等，则只需要比较最大的牌的牌值*/
		}else if(compareTwoCardWithCardValue(player1.getCardList().get(0), player2.getCardList().get(0)) == 0 
				&& compareTwoCardWithCardValue(player1.getCardList().get(1), player2.getCardList().get(1)) == 0){
			if (compareTwoCardWithCardValue(player1.getCardList().get(2), player2.getCardList().get(2)) > 0) {
				result = 1;
			}else{
				result = -1;
			}
			/**最大的两张牌值相等，则只需要比较最小的牌的牌值*/
		}else if(compareTwoCardWithCardValue(player1.getCardList().get(2), player2.getCardList().get(2)) == 0 
				&& compareTwoCardWithCardValue(player1.getCardList().get(1), player2.getCardList().get(1)) == 0){
			if (compareTwoCardWithCardValue(player1.getCardList().get(0), player2.getCardList().get(0)) > 0) {
				result = 1;
			}else{
				result = -1;
			}
			/**最小的牌和最大的牌的牌值相等，则只需要比较中间牌的牌值*/
		}else if(compareTwoCardWithCardValue(player1.getCardList().get(0), player2.getCardList().get(0)) == 0 
				&& compareTwoCardWithCardValue(player1.getCardList().get(2), player2.getCardList().get(2)) == 0){
			if (compareTwoCardWithCardValue(player1.getCardList().get(1), player2.getCardList().get(1)) > 0) {
				result = 1;
			}else{
				result = -1;
			}
			/**最大的一张牌牌值相等，则只需要比较第二大的牌的牌值*/
		}else if(compareTwoCardWithCardValue(player1.getCardList().get(2), player2.getCardList().get(2)) == 0){
			if (compareTwoCardWithCardValue(player1.getCardList().get(1), player2.getCardList().get(1)) > 0) {
				result = 1;
			}else{
				result = -1;
			}
			/**其他情况，则只需要比较最大牌的牌值*/
		}else{
			if (compareTwoCardWithCardValue(player1.getCardList().get(2), player2.getCardList().get(2)) > 0) {
				result = 1;
			}else{
				result = -1;
			}
		}
		return result;
	}
	
	/**
	 * 金花比较
	 * @param player1
	 * @param player2
	 * @return
	 */
	public static int jinhuaCompare(BasePlayerInfo player1, BasePlayerInfo player2){
		int result = 0;
		/**如果三张牌的牌值相等，则志需要比较最大牌的花色*/
		if (compareTwoCardWithCardValue(player1.getCardList().get(0), player2.getCardList().get(0)) == 0 
			&& compareTwoCardWithCardValue(player1.getCardList().get(1), player2.getCardList().get(1)) == 0
			&& compareTwoCardWithCardValue(player1.getCardList().get(2), player2.getCardList().get(2)) == 0) {
			if (compareTwoCardWithCardSuit(player1.getCardList().get(2), player2.getCardList().get(2)) > 0) {
				result = 1;
			}else{
				result = -1;
			}
			/**最小的两张牌值相等，则只需要比较最大的牌的牌值*/
		}else if(compareTwoCardWithCardValue(player1.getCardList().get(0), player2.getCardList().get(0)) == 0 
				&& compareTwoCardWithCardValue(player1.getCardList().get(1), player2.getCardList().get(1)) == 0){
			if (compareTwoCardWithCardValue(player1.getCardList().get(2), player2.getCardList().get(2)) > 0) {
				result = 1;
			}else{
				result = -1;
			}
			/**最大的两张牌值相等，则只需要比较最小的牌的牌值*/
		}else if(compareTwoCardWithCardValue(player1.getCardList().get(2), player2.getCardList().get(2)) == 0 
				&& compareTwoCardWithCardValue(player1.getCardList().get(1), player2.getCardList().get(1)) == 0){
			if (compareTwoCardWithCardValue(player1.getCardList().get(0), player2.getCardList().get(0)) > 0) {
				result = 1;
			}else{
				result = -1;
			}
			/**最小的牌和最大的牌的牌值相等，则只需要比较中间牌的牌值*/
		}else if(compareTwoCardWithCardValue(player1.getCardList().get(0), player2.getCardList().get(0)) == 0 
				&& compareTwoCardWithCardValue(player1.getCardList().get(2), player2.getCardList().get(2)) == 0){
			if (compareTwoCardWithCardValue(player1.getCardList().get(1), player2.getCardList().get(1)) > 0) {
				result = 1;
			}else{
				result = -1;
			}
			/**最大的一张牌牌值相等，则只需要比较第二大的牌的牌值*/
		}else if(compareTwoCardWithCardValue(player1.getCardList().get(2), player2.getCardList().get(2)) == 0){
			if (compareTwoCardWithCardValue(player1.getCardList().get(1), player2.getCardList().get(1)) > 0) {
				result = 1;
			}else{
				result = -1;
			}
			/**其他情况，则只需要比较最大牌的牌值*/
		}else{
			if (compareTwoCardWithCardValue(player1.getCardList().get(2), player2.getCardList().get(2)) > 0) {
				result = 1;
			}else{
				result = -1;
			}
		}
		return result;
	}
	/**
	 * 对子比较
	 * @param player1
	 * @param player2
	 * @return
	 */
	public static int pairCompare(BasePlayerInfo player1, BasePlayerInfo player2){
		int result = 0;
		/**如果三张牌的牌值相等，则志需要比较最大牌的花色*/
		if (compareTwoCardWithCardValue(player1.getCardList().get(0), player2.getCardList().get(0)) == 0 
			&& compareTwoCardWithCardValue(player1.getCardList().get(1), player2.getCardList().get(1)) == 0
			&& compareTwoCardWithCardValue(player1.getCardList().get(2), player2.getCardList().get(2)) == 0) {
			
			if (compareTwoCardWithCardSuit(player1.getCardList().get(2), player2.getCardList().get(2)) > 0) {
				result = 1;
			}else{
				result = -1;
			}
			/**如果较小的两张牌牌值相等，则只需要比较最大的牌的牌值*/
		}else if(compareTwoCardWithCardValue(player1.getCardList().get(0), player1.getCardList().get(1)) == 0 
				&& compareTwoCardWithCardValue(player2.getCardList().get(0), player2.getCardList().get(1)) == 0
				&& compareTwoCardWithCardValue(player1.getCardList().get(0), player2.getCardList().get(0)) == 0){
			if (compareTwoCardWithCardValue(player1.getCardList().get(2), player2.getCardList().get(2)) > 0) {
				result = 1;
			}else{
				result = -1;
			}
			/**如果较大的两张牌牌值相等，则只需要比较最小的牌的牌值*/
		}else if(compareTwoCardWithCardValue(player1.getCardList().get(1), player1.getCardList().get(2)) == 0 
				&& compareTwoCardWithCardValue(player2.getCardList().get(1), player2.getCardList().get(2)) == 0
				&& compareTwoCardWithCardValue(player1.getCardList().get(1), player2.getCardList().get(1)) == 0){
			if (compareTwoCardWithCardValue(player1.getCardList().get(0), player2.getCardList().get(0)) > 0) {
				result = 1;
			}else{
				result = -1;
			}
			/**其他情况直接比较对子大小,只需要比较牌列表中的第二个牌值就行*/
		}else{
			if (compareTwoCardWithCardValue(player1.getCardList().get(1), player2.getCardList().get(1)) > 0 ) {
				result = 1;
			}else{
				result = -1;
			}
		}
		return result;
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
