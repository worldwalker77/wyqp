package cn.worldwalker.game.wyqp.web.job;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cn.worldwalker.game.wyqp.common.channel.ChannelContainer;
import cn.worldwalker.game.wyqp.common.domain.base.BaseRoomInfo;
import cn.worldwalker.game.wyqp.common.domain.base.RedisRelaModel;
import cn.worldwalker.game.wyqp.common.domain.mj.MjRoomInfo;
import cn.worldwalker.game.wyqp.common.enums.GameTypeEnum;
import cn.worldwalker.game.wyqp.common.service.RedisOperationService;
import cn.worldwalker.game.wyqp.common.utils.GameUtil;


public class RoomInfoCleanJob extends SingleServerJobByRedis {
	@Autowired
	private RedisOperationService redisOperationService;
	@Autowired
	private ChannelContainer channelContainer;
	@Override
	public void execute() {
		
		List<RedisRelaModel> list = redisOperationService.getAllRoomIdGameTypeUpdateTime();
		for(RedisRelaModel model : list){
			if (System.currentTimeMillis() - model.getUpdateTime() > 2*60*60*1000) {
				BaseRoomInfo roomInfo = null;
				if (GameTypeEnum.thmj.gameType.equals(model.getGameType()) ) {
					roomInfo = redisOperationService.getRoomInfoByRoomId(model.getRoomId(), MjRoomInfo.class);
				}
				if (roomInfo == null) {
					redisOperationService.delGameTypeUpdateTimeByRoomId(model.getRoomId());
					return;
				}
				List playerList = roomInfo.getPlayerList();
				redisOperationService.cleanPlayerAndRoomInfo(model.getRoomId(), GameUtil.getPlayerIdStrArr(playerList));
			}
		}
	}
	
}
