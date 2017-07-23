package cn.worldwalker.game.wyqp.common.domain.nn;

import cn.worldwalker.game.wyqp.common.domain.base.BaseMsg;

public class NnMsg extends BaseMsg{
	
	private Integer roomBankerType;
	/**是否抢庄 3不抢 4 抢*/
	private Integer isRobBanker;
	
	public Integer getRoomBankerType() {
		return roomBankerType;
	}

	public void setRoomBankerType(Integer roomBankerType) {
		this.roomBankerType = roomBankerType;
	}

	public Integer getIsRobBanker() {
		return isRobBanker;
	}

	public void setIsRobBanker(Integer isRobBanker) {
		this.isRobBanker = isRobBanker;
	}
	
}
