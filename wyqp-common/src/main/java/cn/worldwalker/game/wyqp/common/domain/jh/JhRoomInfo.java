package cn.worldwalker.game.wyqp.common.domain.jh;

import java.util.ArrayList;
import java.util.List;

import cn.worldwalker.game.wyqp.common.domain.base.BaseRoomInfo;

public class JhRoomInfo  extends BaseRoomInfo{
	
	private Integer roomBankerType;
	
	private Integer multipleLimit;
	
	private List<JhPlayerInfo> playerList = new ArrayList<JhPlayerInfo>();
	
	public Integer getRoomBankerType() {
		return roomBankerType;
	}
	public void setRoomBankerType(Integer roomBankerType) {
		this.roomBankerType = roomBankerType;
	}
	public Integer getMultipleLimit() {
		return multipleLimit;
	}
	public void setMultipleLimit(Integer multipleLimit) {
		this.multipleLimit = multipleLimit;
	}
	public List<JhPlayerInfo> getPlayerList() {
		return playerList;
	}
	public void setPlayerList(List<JhPlayerInfo> playerList) {
		this.playerList = playerList;
	}
	
	
}
