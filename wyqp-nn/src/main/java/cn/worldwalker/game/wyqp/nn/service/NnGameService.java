package cn.worldwalker.game.wyqp.nn.service;

import io.netty.channel.ChannelHandlerContext;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.worldwalker.game.wyqp.common.domain.base.BaseRequest;
import cn.worldwalker.game.wyqp.common.domain.base.BaseRoomInfo;
import cn.worldwalker.game.wyqp.common.domain.base.Card;
import cn.worldwalker.game.wyqp.common.domain.base.UserInfo;
import cn.worldwalker.game.wyqp.common.domain.nn.NnMsg;
import cn.worldwalker.game.wyqp.common.domain.nn.NnPlayerInfo;
import cn.worldwalker.game.wyqp.common.domain.nn.NnRoomInfo;
import cn.worldwalker.game.wyqp.common.enums.DissolveStatusEnum;
import cn.worldwalker.game.wyqp.common.enums.GameTypeEnum;
import cn.worldwalker.game.wyqp.common.enums.MsgTypeEnum;
import cn.worldwalker.game.wyqp.common.enums.RoomStatusEnum;
import cn.worldwalker.game.wyqp.common.exception.BusinessException;
import cn.worldwalker.game.wyqp.common.exception.ExceptionEnum;
import cn.worldwalker.game.wyqp.common.result.Result;
import cn.worldwalker.game.wyqp.common.service.BaseGameService;
import cn.worldwalker.game.wyqp.common.utils.GameUtil;
import cn.worldwalker.game.wyqp.nn.cards.CardResource;
import cn.worldwalker.game.wyqp.nn.cards.CardRule;
import cn.worldwalker.game.wyqp.nn.enums.NnPlayerStatusEnum;
import cn.worldwalker.game.wyqp.nn.enums.NnRoomBankerTypeEnum;
import cn.worldwalker.game.wyqp.nn.enums.NnRoomStatusEnum;

public class NnGameService extends BaseGameService{

	@Override
	public BaseRoomInfo doCreateRoom(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo) {
		NnMsg msg = (NnMsg)request.getMsg();
		if (msg.getRoomBankerType() == null) {
			throw new BusinessException(ExceptionEnum.PARAMS_ERROR);
		}
		NnRoomInfo roomInfo = redisOperationService.getRoomInfoByRoomId(userInfo.getRoomId(), NnRoomInfo.class);
		roomInfo.setRoomBankerType(msg.getRoomBankerType());
		return roomInfo;
	}

	@Override
	public BaseRoomInfo doEntryRoom(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo) {
		NnRoomInfo roomInfo = redisOperationService.getRoomInfoByRoomId(userInfo.getRoomId(), NnRoomInfo.class);
		List playerList = roomInfo.getPlayerList();
		NnPlayerInfo player = new NnPlayerInfo();
		playerList.add(player);
		return roomInfo;
	}

	@Override
	public BaseRoomInfo doDissolveRoom(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo) {
		return null;
	}
	public void ready(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo) {
		Result result = new Result();
		result.setGameType(GameTypeEnum.nn.gameType);
		Map<String, Object> data = new HashMap<String, Object>();
		result.setData(data);
		Integer playerId = userInfo.getPlayerId();
		Integer roomId = userInfo.getRoomId();
		NnRoomInfo roomInfo = redisOperationService.getRoomInfoByRoomId(roomId, NnRoomInfo.class);
		List playerList = roomInfo.getPlayerList();
		/**玩家已经准备计数*/
		int readyCount = 0;
		int size = playerList.size();
		for(int i = 0; i < size; i++){
			NnPlayerInfo player = (NnPlayerInfo)playerList.get(i);
			if (player.getPlayerId().equals(playerId)) {
				/**设置状态为已准备*/
				player.setStatus(NnPlayerStatusEnum.ready.status);
			}
			if (NnPlayerStatusEnum.ready.status.equals(player.getStatus())) {
				readyCount++;
			}
		}
		
		/**如果已经准备的人数据大于1并且等于房间内所有玩家的数目，则开始发牌*/
		if (readyCount > 1 && readyCount == size) {
			/**开始发牌时将房间内当前局数+1*/
			roomInfo.setCurGame(roomInfo.getCurGame() + 1);
			/**发牌*/
			List<List<Card>> playerCards = CardResource.dealCards(size);
			/**为每个玩家设置牌及牌型*/
			for(int i = 0; i < size; i++ ){
				NnPlayerInfo player = (NnPlayerInfo)playerList.get(i);
				player.setCardList(playerCards.get(i));
				player.setCardType(CardRule.calculateCardType(playerCards.get(i)));
				player.setCurScore(0);
				/**设置每个玩家的解散房间状态为不同意解散，后面大结算返回大厅的时候回根据此状态判断是否解散房间*/
				player.setDissolveStatus(DissolveStatusEnum.disagree.status);
			}
			/**如果是抢庄类型，则设置房间状态为抢庄阶段*/
			if (NnRoomBankerTypeEnum.robBanker.type.equals(roomInfo.getRoomBankerType())) {
				roomInfo.setStatus(NnRoomStatusEnum.inRob.status);
			}else{
				roomInfo.setStatus(RoomStatusEnum.inGame.status);
			}
			
			roomInfo.setUpdateTime(new Date());
			/**设置庄家*/
			setRoomBankerId(roomInfo);
			redisOperationService.setRoomIdRoomInfo(roomId, roomInfo);
			
			data.put("roomId", roomInfo.getRoomId());
			data.put("roomOwnerId", roomInfo.getRoomOwnerId());
			data.put("totalGames", roomInfo.getTotalGames());
			data.put("curGame", roomInfo.getCurGame());
			/**如果是抢庄类型，则给每个玩家返回四张牌，并通知准备抢庄*/
			if (NnRoomBankerTypeEnum.robBanker.type.equals(roomInfo.getRoomBankerType())) {
				result.setMsgType(MsgTypeEnum.readyRobBanker.msgType);
				for(int i = 0; i < size; i++ ){
					NnPlayerInfo player = (NnPlayerInfo)playerList.get(i);
					List<Card> cardList = player.getCardList().subList(0, 4);
					data.put("cardList", cardList);
					channelContainer.sendTextMsgByPlayerIds(result, player.getPlayerId());
				}
			}else{/**如果是非抢庄类型，则通知玩家谁是庄家并准备压分*/
				result.setMsgType(MsgTypeEnum.readyStake.msgType);
				data.put("roomBankerId", roomInfo.getRoomBankerId());
				channelContainer.sendTextMsgByPlayerIds(result, GameUtil.getPlayerIdArr(playerList));
			}
			
			return;
		}
		roomInfo.setUpdateTime(new Date());
		redisOperationService.setRoomIdRoomInfo(roomId, roomInfo);
		result.setMsgType(MsgTypeEnum.ready.msgType);
		data.put("playerId", playerId);
		channelContainer.sendTextMsgByPlayerIds(result, GameUtil.getPlayerIdArr(playerList));
	}
	
	/**
	 * 抢庄
	 * 
	 * */
	public void robBanker(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo){
		
		Result result = new Result();
		result.setGameType(GameTypeEnum.nn.gameType);
		Map<String, Object> data = new HashMap<String, Object>();
		result.setData(data);
		NnMsg msg = (NnMsg)request.getMsg();
		Integer playerId = userInfo.getPlayerId();
		Integer roomId = userInfo.getRoomId();
		NnRoomInfo roomInfo = redisOperationService.getRoomInfoByRoomId(roomId, NnRoomInfo.class);
		List playerList = roomInfo.getPlayerList();
		/**玩家已经抢庄计数*/
		int robCount = 0;
		int size = getReadyNotRobNum(playerList);
		for(int i = 0; i < size; i++){
			NnPlayerInfo player = (NnPlayerInfo)playerList.get(i);
			if (player.getPlayerId().equals(playerId)) {
				/**设置状态为已抢庄*/
				player.setStatus(msg.getIsRobBanker());
				player.setRobBankerTime(System.currentTimeMillis());
			}
			if (NnPlayerStatusEnum.notRob.status.equals(player.getStatus()) || NnPlayerStatusEnum.rob.status.equals(player.getStatus())) {
				robCount++;
			}
		}
		
		/**如果都抢完庄,则通知玩家庄家并开始压分*/
		if (robCount > 1 && robCount == size) {
			result.setMsgType(MsgTypeEnum.readyStake.msgType);
			data.put("roomBankerId", roomInfo.getRoomBankerId());
			channelContainer.sendTextMsgByPlayerIds(result, GameUtil.getPlayerIdArr(playerList));
			return ;
		}
		
		data.put("playerId", playerId);
		data.put("isRobBanker", msg.getIsRobBanker());
		channelContainer.sendTextMsgByPlayerIds(result, GameUtil.getPlayerIdArr(playerList));
	}
	
	public void robBankerOverTime(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo){
		
		
		
		
	}
	
	
	
	
	
	
	
	private int getReadyNotRobNum(List playerList){
		
		return playerList.size();
	}
	
	
	private void setRoomBankerId(NnRoomInfo roomInfo){
		
		NnRoomBankerTypeEnum typeEnum = NnRoomBankerTypeEnum.getNnRoomBankerTypeEnum(roomInfo.getRoomBankerType());
		
		switch (typeEnum) {
			case inTurnBanker:
				/**如果房间中庄家id为null则说明房间是刚创建的，直接设置房主为庄家*/
				if (roomInfo.getRoomBankerId() == null) {
					roomInfo.setRoomBankerId(roomInfo.getRoomOwnerId());
				}else{
					List playerList = roomInfo.getPlayerList();
					int size = playerList.size();
					for(int i = 0; i < size; i++){
						NnPlayerInfo player = (NnPlayerInfo)playerList.get(i);
						if (player.getPlayerId().equals(roomInfo.getRoomBankerId())) {
							if (i == size - 1) {
								roomInfo.setRoomBankerId(((NnPlayerInfo)playerList.get(0)).getPlayerId());
								break;
							}else{
								roomInfo.setRoomBankerId(((NnPlayerInfo)playerList.get(i + 1)).getPlayerId());
								break;
							}
						}
					}
				}
				break;
			case overLordBanker:
				roomInfo.setRoomBankerId(roomInfo.getRoomOwnerId());		
				break;
			case robBanker:
				List playerList = roomInfo.getPlayerList();
				int size = playerList.size();
				NnPlayerInfo bankerPlayer = (NnPlayerInfo)playerList.get(0);
				for(int i = 0; i < size; i++){
					NnPlayerInfo player = (NnPlayerInfo)playerList.get(i);
					if (NnPlayerStatusEnum.rob.status.equals(player.getStatus())) {
						if (!NnPlayerStatusEnum.rob.status.equals(bankerPlayer.getStatus())) {
							bankerPlayer = player;
						}
						if (bankerPlayer.getRobBankerTime() > player.getRobBankerTime()) {
							bankerPlayer = player;
						}
					}
				}
				/**如果有人抢庄，则最先抢的那个人的庄*/
				if (bankerPlayer != null) {
					roomInfo.setRoomBankerId(bankerPlayer.getPlayerId());
				}else{/**如果都没抢庄的话，默认是上个赢家的庄，如果是第一局，则是房主的庄*/
					if (roomInfo.getCurWinnerId() != null) {
						roomInfo.setRoomBankerId(roomInfo.getCurWinnerId());
					}else{
						roomInfo.setRoomBankerId(roomInfo.getRoomOwnerId());
					}
				}
				break;
			case winnerBanker:
				if (roomInfo.getCurWinnerId() == null) {
					roomInfo.setRoomBankerId(roomInfo.getRoomOwnerId());
				}else{
					roomInfo.setRoomBankerId(roomInfo.getCurWinnerId());
				}
				break;
			default:
				break;
		}
	}

}
