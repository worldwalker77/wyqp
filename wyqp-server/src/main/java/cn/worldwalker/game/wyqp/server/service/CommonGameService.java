package cn.worldwalker.game.wyqp.server.service;

import io.netty.channel.ChannelHandlerContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.worldwalker.game.wyqp.common.domain.base.BaseRequest;
import cn.worldwalker.game.wyqp.common.domain.base.BaseRoomInfo;
import cn.worldwalker.game.wyqp.common.domain.base.RedisRelaModel;
import cn.worldwalker.game.wyqp.common.domain.base.UserInfo;
import cn.worldwalker.game.wyqp.common.enums.GameTypeEnum;
import cn.worldwalker.game.wyqp.common.service.BaseGameService;
import cn.worldwalker.game.wyqp.mj.service.MjGameService;
import cn.worldwalker.game.wyqp.nn.service.NnGameService;

@Service(value="commonGameService")
public class CommonGameService extends BaseGameService{
	
	@Autowired
	private NnGameService nnGameService;
	
	@Autowired
	private MjGameService mjGameService;
	
	public void commonEntryRoom(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo){
		Integer roomId = userInfo.getRoomId();
		RedisRelaModel rrm = redisOperationService.getGameTypeUpdateTimeByRoomId(roomId);
		Integer realGameType = rrm.getGameType();
		/**设置真是的gameType*/
		request.setGameType(realGameType);
		GameTypeEnum gameTypeEnum = GameTypeEnum.getGameTypeEnumByType(realGameType);
		switch (gameTypeEnum) {
			case nn:
				nnGameService.entryRoom(ctx, request, userInfo);
				break;
			case mj:
				mjGameService.entryRoom(ctx, request, userInfo);
				break;
			default:
				break;
			}
	}
	
	
	public void commonRefreshRoom(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo){
		Integer roomId = userInfo.getRoomId();
		RedisRelaModel rrm = redisOperationService.getGameTypeUpdateTimeByRoomId(roomId);
		Integer realGameType = rrm.getGameType();
		/**设置真是的gameType*/
		request.setGameType(realGameType);
		GameTypeEnum gameTypeEnum = GameTypeEnum.getGameTypeEnumByType(realGameType);
		switch (gameTypeEnum) {
			case nn:
				nnGameService.entryRoom(ctx, request, userInfo);
				break;
			case mj:
				mjGameService.entryRoom(ctx, request, userInfo);
				break;
			default:
				break;
			}
	}
	
	
	@Override
	public BaseRoomInfo doCreateRoom(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo) {
		return null;
	}

	@Override
	public BaseRoomInfo doEntryRoom(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo) {
		return null;
	}

	@Override
	public BaseRoomInfo doDissolveRoom(ChannelHandlerContext ctx,BaseRequest request, UserInfo userInfo) {
		return null;
	}


	@Override
	public BaseRoomInfo doRefreshRoom(ChannelHandlerContext ctx,BaseRequest request, UserInfo userInfo, BaseRoomInfo newRoomInfo) {
		return null;
	}
	
}
