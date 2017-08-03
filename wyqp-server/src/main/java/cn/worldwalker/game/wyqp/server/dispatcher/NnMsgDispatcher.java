package cn.worldwalker.game.wyqp.server.dispatcher;

import io.netty.channel.ChannelHandlerContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.worldwalker.game.wyqp.common.domain.base.BaseRequest;
import cn.worldwalker.game.wyqp.common.domain.base.UserInfo;
import cn.worldwalker.game.wyqp.common.enums.MsgTypeEnum;
import cn.worldwalker.game.wyqp.nn.service.NnGameService;
@Service(value="nnMsgDispatcher")
public class NnMsgDispatcher extends BaseMsgDisPatcher {
	@Autowired
	private NnGameService nnGameService;
	@Override
	public void requestDispatcher(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo) {
		Integer msgType = request.getMsgType();
		MsgTypeEnum msgTypeEnum= MsgTypeEnum.getMsgTypeEnumByType(msgType);
		switch (msgTypeEnum) {
			case createRoom:
				nnGameService.createRoom(ctx, request, userInfo);
				break;
			case entryRoom:
				nnGameService.entryRoom(ctx, request, userInfo);
				break;
			case ready:
				nnGameService.ready(ctx, request, userInfo);
				break;
			case robBanker:
				nnGameService.robBanker(ctx, request, userInfo);
				break;
			case stakeScore:
				nnGameService.stakeScore(ctx, request, userInfo);
				break;
			case showCard:
				nnGameService.showCard(ctx, request, userInfo);
				break;
			case dissolveRoom:
				nnGameService.dissolveRoom(ctx, request, userInfo);
				break;
			case agreeDissolveRoom:
				break;
			case disagreeDissolveRoom:
				break;
			case delRoomConfirmBeforeReturnHall:
				break;
			case refreshRoom:
				break;
			case queryPlayerInfo:
				break;
			case chatMsg:
				break;
			case heartBeat:
				break;
			case userRecord:
				break;
			case userFeedback:
				break;
			case sendEmoticon:
				break;
			case syncPlayerLocation:
				break;
			default:
				break;
		}
	}
	
}
