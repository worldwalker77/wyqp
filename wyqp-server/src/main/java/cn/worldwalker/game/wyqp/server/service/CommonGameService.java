package cn.worldwalker.game.wyqp.server.service;

import org.springframework.stereotype.Service;

import io.netty.channel.ChannelHandlerContext;
import cn.worldwalker.game.wyqp.common.domain.base.BaseRequest;
import cn.worldwalker.game.wyqp.common.domain.base.BaseRoomInfo;
import cn.worldwalker.game.wyqp.common.domain.base.UserInfo;
import cn.worldwalker.game.wyqp.common.service.BaseGameService;

@Service(value="commonGameService")
public class CommonGameService extends BaseGameService{

	@Override
	public BaseRoomInfo doCreateRoom(ChannelHandlerContext ctx,
			BaseRequest request, UserInfo userInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseRoomInfo doEntryRoom(ChannelHandlerContext ctx,
			BaseRequest request, UserInfo userInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseRoomInfo doDissolveRoom(ChannelHandlerContext ctx,
			BaseRequest request, UserInfo userInfo) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
