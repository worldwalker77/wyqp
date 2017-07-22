package cn.worldwalker.game.wyqp.server.dispatcher;

import io.netty.channel.ChannelHandlerContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import cn.worldwalker.game.wyqp.common.channel.ChannelContainer;
import cn.worldwalker.game.wyqp.common.domain.base.BaseMsg;
import cn.worldwalker.game.wyqp.common.domain.base.BaseRequest;
import cn.worldwalker.game.wyqp.common.domain.base.UserInfo;
import cn.worldwalker.game.wyqp.common.enums.MsgTypeEnum;
import cn.worldwalker.game.wyqp.common.exception.BusinessException;
import cn.worldwalker.game.wyqp.common.exception.ExceptionEnum;
import cn.worldwalker.game.wyqp.common.service.RedisOperationService;

public abstract class BaseMsgDisPatcher {
	@Autowired
	public RedisOperationService redisOperationService;
	@Autowired
	public ChannelContainer channelContainer;
	
	public void textMsgProcess(ChannelHandlerContext ctx, BaseRequest request){
		
		/**参数校验*/
		if (null == request || request.getGameType() == null || request.getMsgType() == null || StringUtils.isBlank(request.getToken())) {
			throw new BusinessException(ExceptionEnum.PARAMS_ERROR);
		}
		/**
		 * token登录检验
		 */
		String token = request.getToken();
		UserInfo userInfo = redisOperationService.getUserInfo(token);
		if (userInfo == null) {
			throw new BusinessException(ExceptionEnum.NEED_LOGIN);
		}
		redisOperationService.expireUserInfo(token);
		/**自动设置playerId和roomId*/
		BaseMsg msg = request.getMsg();
		if (msg == null) {
			msg = new BaseMsg();
			request.setMsg(msg);
		}
		msg.setPlayerId(userInfo.getPlayerId());
		if (!MsgTypeEnum.entryRoom.equals(MsgTypeEnum.getMsgTypeEnumByType(request.getMsgType()))) {
			msg.setRoomId(userInfo.getRoomId());
		}
		
		requestDispatcher(ctx, request, userInfo);
	}
	
	public abstract void requestDispatcher(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo);
}
