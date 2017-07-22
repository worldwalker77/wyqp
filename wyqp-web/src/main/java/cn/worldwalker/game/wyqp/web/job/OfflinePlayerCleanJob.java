package cn.worldwalker.game.wyqp.web.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cn.worldwalker.game.wyqp.common.channel.ChannelContainer;
import cn.worldwalker.game.wyqp.common.domain.base.BaseRoomInfo;
import cn.worldwalker.game.wyqp.common.domain.base.RedisRelaModel;
import cn.worldwalker.game.wyqp.common.domain.mj.MjRoomInfo;
import cn.worldwalker.game.wyqp.common.enums.GameTypeEnum;
import cn.worldwalker.game.wyqp.common.enums.MsgTypeEnum;
import cn.worldwalker.game.wyqp.common.result.Result;
import cn.worldwalker.game.wyqp.common.service.RedisOperationService;
import cn.worldwalker.game.wyqp.common.utils.GameUtil;


public class OfflinePlayerCleanJob extends SingleServerJobByRedis{
	 
	@Autowired
	private RedisOperationService redisOperationService;
	@Autowired
	private ChannelContainer channelContainer;
	/**
	 * 清除游戏房间中离线超过20分钟的玩家信息及房间信息
	 */
	@Override
	public void execute() {
		List<RedisRelaModel> list = redisOperationService.getAllOfflinePlayerIdRoomIdGameTypeTime();
		for(RedisRelaModel model : list){
			if (System.currentTimeMillis() - model.getUpdateTime() > 20*60*1000L) {
				BaseRoomInfo roomInfo = null;
				if (GameTypeEnum.thmj.gameType.equals(model.getGameType()) ) {
					roomInfo = redisOperationService.getRoomInfoByRoomId(model.getRoomId(), MjRoomInfo.class);
				}
				if (roomInfo == null) {
					/**如果无房间信息，则说明可能其他离线玩家已经将房间删除，不需要再推送消息给其他玩家*/
					redisOperationService.cleanPlayerAndRoomInfo(model.getRoomId(), String.valueOf(model.getPlayerId()));
				}
				List playerList = roomInfo.getPlayerList();
				Result result = new Result();
				result.setMsgType(MsgTypeEnum.dissolveRoomCausedByOffline.msgType);
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("playerId", model.getPlayerId());
				result.setData(data);
				channelContainer.sendTextMsgByPlayerIds(result, GameUtil.getPlayerIdArrWithOutSelf(playerList, model.getPlayerId()));
				redisOperationService.cleanPlayerAndRoomInfo(model.getRoomId(), GameUtil.getPlayerIdStrArr(playerList));
			}
		}
	}
	
}
