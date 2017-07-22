package cn.worldwalker.game.wyqp.server.dispatcher;

import io.netty.channel.ChannelHandlerContext;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import cn.worldwalker.game.wyqp.common.domain.base.BaseRequest;
import cn.worldwalker.game.wyqp.common.domain.mj.MjRequest;
import cn.worldwalker.game.wyqp.common.enums.GameTypeEnum;
import cn.worldwalker.game.wyqp.common.exception.BusinessException;
import cn.worldwalker.game.wyqp.common.exception.ExceptionEnum;
import cn.worldwalker.game.wyqp.common.utils.JsonUtil;
@Service
public class GameDispather {
	
	private static final Logger log = Logger.getLogger(GameDispather.class);
	
	@Resource(name="mjMsgDisPatcher")
	private BaseMsgDisPatcher mjMsgDisPatcher;
	
	public void gameProcess(ChannelHandlerContext ctx, String textMsg){
		JSONObject obj = JSONObject.fromObject(textMsg);
		Integer gameType = obj.getInt("gameType");
		GameTypeEnum gameTypeEnum = GameTypeEnum.getGameTypeEnumByType(gameType);
		BaseRequest request = null;
		try {
			switch (gameTypeEnum) {
				case nn:
					
					break;
				case mj:
					request = JsonUtil.toObject(textMsg, MjRequest.class);
					mjMsgDisPatcher.textMsgProcess(ctx, request);
					break;
				default:
					break;
				}
		} catch (BusinessException e) {
			log.error(e.getBussinessCode() + ":" + e.getMessage(), e);
			
		}catch (Exception e) {
			log.error(ExceptionEnum.SYSTEM_ERROR.index + ":" + ExceptionEnum.SYSTEM_ERROR.description, e);
			
		}
	}
}
