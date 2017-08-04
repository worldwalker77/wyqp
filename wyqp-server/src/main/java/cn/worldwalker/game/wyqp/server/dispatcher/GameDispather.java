package cn.worldwalker.game.wyqp.server.dispatcher;

import io.netty.channel.ChannelHandlerContext;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.worldwalker.game.wyqp.common.channel.ChannelContainer;
import cn.worldwalker.game.wyqp.common.domain.base.BaseRequest;
import cn.worldwalker.game.wyqp.common.domain.mj.MjRequest;
import cn.worldwalker.game.wyqp.common.domain.nn.NnRequest;
import cn.worldwalker.game.wyqp.common.enums.GameTypeEnum;
import cn.worldwalker.game.wyqp.common.exception.BusinessException;
import cn.worldwalker.game.wyqp.common.exception.ExceptionEnum;
import cn.worldwalker.game.wyqp.common.utils.JsonUtil;


@Service
public class GameDispather {
	private static final Log log = LogFactory.getLog(ChannelContainer.class);
	@Autowired
	public ChannelContainer channelContainer;
	@Resource(name="mjMsgDisPatcher")
	private BaseMsgDisPatcher mjMsgDisPatcher;
	@Resource(name="nnMsgDispatcher")
	private BaseMsgDisPatcher nnMsgDispatcher;
	@Resource(name="commonMsgDispatcher")
	private BaseMsgDisPatcher commonMsgDispatcher;
	
	public void gameProcess(ChannelHandlerContext ctx, String textMsg){
		JSONObject obj = JSONObject.fromObject(textMsg);
		Integer gameType = obj.getInt("gameType");
		GameTypeEnum gameTypeEnum = GameTypeEnum.getGameTypeEnumByType(gameType);
		BaseRequest request = null;
		try {
			switch (gameTypeEnum) {
				case common:
					request = JsonUtil.toObject(textMsg, BaseRequest.class);
					commonMsgDispatcher.textMsgProcess(ctx, request);
					break;
				case nn:
					request = JsonUtil.toObject(textMsg, NnRequest.class);
					nnMsgDispatcher.textMsgProcess(ctx, request);
					break;
				case mj:
					request = JsonUtil.toObject(textMsg, MjRequest.class);
					mjMsgDisPatcher.textMsgProcess(ctx, request);
					break;
				default:
					break;
				}
		} catch (BusinessException e) {
			log.error(e.getBussinessCode() + ":" + e.getMessage() + ", request:" + JsonUtil.toJson(request));
			channelContainer.sendErrorMsg(ctx, ExceptionEnum.getExceptionEnum(e.getBussinessCode()), request);
			
		}catch (Exception e1) {
			log.error("系统异常, request:" + JsonUtil.toJson(request), e1);
			channelContainer.sendErrorMsg(ctx, ExceptionEnum.SYSTEM_ERROR, request);
		}
	}
}
