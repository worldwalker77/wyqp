package cn.worldwalker.game.wyqp.common.manager;

import cn.worldwalker.game.wyqp.common.enums.RoomCardOperationEnum;

public interface CommonManager {
	public Integer doDeductRoomCard(Integer playerId, Integer payType, Integer totalGames, RoomCardOperationEnum operationEnum);
}
