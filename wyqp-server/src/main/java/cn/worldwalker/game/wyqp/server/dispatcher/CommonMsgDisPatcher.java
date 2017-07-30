package cn.worldwalker.game.wyqp.server.dispatcher;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.locks.Lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.worldwalker.game.wyqp.common.domain.base.BaseMsg;
import cn.worldwalker.game.wyqp.common.domain.base.BaseRequest;
import cn.worldwalker.game.wyqp.common.domain.base.UserInfo;
import cn.worldwalker.game.wyqp.common.enums.MsgTypeEnum;
import cn.worldwalker.game.wyqp.common.exception.ExceptionEnum;
import cn.worldwalker.game.wyqp.server.service.CommonGameService;

@Service(value="commonMsgDispatcher")
public class CommonMsgDisPatcher extends BaseMsgDisPatcher{
	
	@Autowired
	private CommonGameService commonGameService;
	
	@Override
	public void requestDispatcher(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo) {
		BaseMsg msg = request.getMsg();
		Integer msgType = request.getMsgType();
		MsgTypeEnum msgTypeEnum= MsgTypeEnum.getMsgTypeEnumByType(msgType);
		Lock lock = null;
		try {
			switch (msgTypeEnum) {
				case entryHall:
					commonGameService.entryHall(ctx, request, userInfo);
					break;
				case syncPlayerLocation:
					break;
				case entryRoom:
					commonGameService.commonEntryRoom(ctx, request, userInfo);
					break;
				case refreshRoom:
					break;
				case userRecord:
					break;
				case userFeedback:
					break;
				default:
					break;
				}
		} catch (Exception e) {
			channelContainer.sendErrorMsg(ctx, ExceptionEnum.SYSTEM_ERROR, request);
		} finally{
			if (lock != null) {
				lock.unlock();
			}
		}
		
	
	}

}
