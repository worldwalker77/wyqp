package cn.worldwalker.game.wyqp.common.service;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cn.worldwalker.game.wyqp.common.channel.ChannelContainer;
import cn.worldwalker.game.wyqp.common.domain.base.BaseMsg;
import cn.worldwalker.game.wyqp.common.domain.base.BasePlayerInfo;
import cn.worldwalker.game.wyqp.common.domain.base.BaseRequest;
import cn.worldwalker.game.wyqp.common.domain.base.BaseRoomInfo;
import cn.worldwalker.game.wyqp.common.domain.base.RedisRelaModel;
import cn.worldwalker.game.wyqp.common.domain.base.UserFeedbackModel;
import cn.worldwalker.game.wyqp.common.domain.base.UserInfo;
import cn.worldwalker.game.wyqp.common.domain.base.UserModel;
import cn.worldwalker.game.wyqp.common.domain.base.UserRecordModel;
import cn.worldwalker.game.wyqp.common.domain.base.WeiXinUserInfo;
import cn.worldwalker.game.wyqp.common.enums.ChatTypeEnum;
import cn.worldwalker.game.wyqp.common.enums.DissolveStatusEnum;
import cn.worldwalker.game.wyqp.common.enums.MsgTypeEnum;
import cn.worldwalker.game.wyqp.common.enums.OnlineStatusEnum;
import cn.worldwalker.game.wyqp.common.enums.PlayerStatusEnum;
import cn.worldwalker.game.wyqp.common.enums.ProductEnum;
import cn.worldwalker.game.wyqp.common.enums.RoomStatusEnum;
import cn.worldwalker.game.wyqp.common.exception.BusinessException;
import cn.worldwalker.game.wyqp.common.exception.ExceptionEnum;
import cn.worldwalker.game.wyqp.common.manager.CommonManager;
import cn.worldwalker.game.wyqp.common.result.Result;
import cn.worldwalker.game.wyqp.common.roomlocks.RoomLockContainer;
import cn.worldwalker.game.wyqp.common.rpc.WeiXinRpc;
import cn.worldwalker.game.wyqp.common.utils.GameUtil;
import cn.worldwalker.game.wyqp.common.utils.IPUtil;
import cn.worldwalker.game.wyqp.common.utils.JsonUtil;
import cn.worldwalker.game.wyqp.common.utils.wxpay.ConfigUtil;
import cn.worldwalker.game.wyqp.common.utils.wxpay.MapUtils;
import cn.worldwalker.game.wyqp.common.utils.wxpay.PayCommonUtil;
import cn.worldwalker.game.wyqp.common.utils.wxpay.WeixinConstant;

import com.google.common.collect.ImmutableMap;

public abstract class BaseGameService {
	
	private final static Log log = LogFactory.getLog(BaseGameService.class);
	
	@Autowired
	public RedisOperationService redisOperationService;
	@Autowired
	public ChannelContainer channelContainer;
	@Autowired
	public CommonManager commonManager;
	
	@Autowired
	public WeiXinRpc weiXinRpc;
	
	public Result login(String code, String deviceType, HttpServletRequest request) {
		Result result = new Result();
		if (StringUtils.isBlank(code)) {
			throw new BusinessException(ExceptionEnum.PARAMS_ERROR);
		}
		WeiXinUserInfo weixinUserInfo = weiXinRpc.getWeiXinUserInfo(code);
		if (null == weixinUserInfo) {
			throw new BusinessException(ExceptionEnum.SYSTEM_ERROR);
		}
		UserModel userModel = commonManager.getUserByWxOpenId(weixinUserInfo.getOpneid());
		if (null == userModel) {
			userModel = new UserModel();
			userModel.setNickName(weixinUserInfo.getName());
			userModel.setHeadImgUrl(weixinUserInfo.getHeadImgUrl());
			userModel.setWxOpenId(weixinUserInfo.getOpneid());
			commonManager.insertUser(userModel);
		}
		/**从redis查看此用户是否有roomId*/
		Integer roomId = null;
		RedisRelaModel redisRelaModel = redisOperationService.getRoomIdGameTypeTimeByOfflinePlayerId(userModel.getId());
		if (redisRelaModel != null) {
			roomId = redisRelaModel.getRoomId();
		}
		UserInfo userInfo = new UserInfo();
		userInfo.setPlayerId(userModel.getId());
		userInfo.setRoomId(roomId);
		userInfo.setNickName(weixinUserInfo.getName());
		userInfo.setLevel(userModel.getUserLevel() == null ? 1 : userModel.getUserLevel());
		userInfo.setServerIp("119.23.57.236");
		userInfo.setPort("9000");
		userInfo.setRemoteIp(IPUtil.getRemoteIp(request));
		String loginToken = GameUtil.genToken(userModel.getId());
		userInfo.setHeadImgUrl(weixinUserInfo.getHeadImgUrl());
		redisOperationService.setUserInfo(loginToken, userInfo);
		userInfo.setToken(loginToken);
		result.setData(userInfo);
		return result;
	}
	
	public Result login1(String code, String deviceType,HttpServletRequest request) {
		Result result = new Result();
		Integer roomId = null;
		Integer playerId = GameUtil.genPlayerId();
		UserInfo userInfo = new UserInfo();
		userInfo.setPlayerId(playerId);
		userInfo.setRoomId(roomId);
		userInfo.setNickName("nickName_" + playerId);
		userInfo.setLevel(1);
		userInfo.setServerIp("119.23.57.236");
		userInfo.setPort("9000");
		userInfo.setRemoteIp(IPUtil.getRemoteIp(request));
		String loginToken =GameUtil.genToken(playerId);
		redisOperationService.setUserInfo(loginToken, userInfo);
		userInfo.setHeadImgUrl("http://wx.qlogo.cn/mmopen/wibbRT31wkCR4W9XNicL2h2pgaLepmrmEsXbWKbV0v9ugtdibibDgR1ybONiaWFtVeVtYWGWhObRiaiaicMgw8zat8Y5p6YzQbjdstE2/0");
		userInfo.setToken(loginToken);
		userInfo.setRoomCardNum(10);
		result.setData(userInfo);
		return result;
	}
	
	public void entryHall(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo){
		Result result = null;
		result = new Result();
		result.setMsgType(MsgTypeEnum.entryHall.msgType);
		Integer playerId = request.getMsg().getPlayerId();
		/**将channel与playerId进行映射*/
		channelContainer.addChannel(ctx, playerId);
		channelContainer.sendTextMsgByPlayerIds(result, playerId);
	}
	
	public void createRoom(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo){
		Result result = null;
		BaseMsg msg = request.getMsg();
		
		/**校验房卡数量是否足够*/
		//TODO
		if (redisOperationService.isLoginFuseOpen()) {
			commonManager.roomCardCheck(userInfo.getPlayerId(), request.getGameType(), msg.getPayType(), msg.getTotalGames());
		}
		
		Integer roomId = GameUtil.genRoomId();
		int i = 0;
		while(i < 3){
			/**如果不存在则跳出循环，此房间号可以使用*/
			if (!redisOperationService.isRoomIdExist(roomId)) {
				break;
			}
			/**如果此房间号存在则重新生成*/
			roomId = GameUtil.genRoomId();
			i++;
			if (i >= 3) {
				log.error("三次生成房号都有重复......");
				channelContainer.sendErrorMsg(ctx, ExceptionEnum.GEN_ROOM_ID_FAIL, request);
			}
		}
		/**将当前房间号设置到userInfo中*/
		userInfo.setRoomId(roomId);
		redisOperationService.setUserInfo(request.getToken(), userInfo);
		
		/**doCreateRoom抽象方法由具体实现类去实现*/
		BaseRoomInfo roomInfo = doCreateRoom(ctx, request, userInfo);
		
		/**组装房间对象*/
		roomInfo.setRoomId(roomId);
		roomInfo.setRoomOwnerId(msg.getPlayerId());
		roomInfo.setPayType(msg.getPayType());
		roomInfo.setTotalGames(msg.getTotalGames());
		roomInfo.setCurGame(0);
		roomInfo.setStatus(RoomStatusEnum.justBegin.status);
		roomInfo.setServerIp(IPUtil.getLocalIp());
		Date date = new Date();
		roomInfo.setCreateTime(date);
		roomInfo.setUpdateTime(date);
		
		List playerList = roomInfo.getPlayerList();
		BasePlayerInfo playerInfo = (BasePlayerInfo)playerList.get(0);
		playerInfo.setPlayerId(msg.getPlayerId());
		playerInfo.setLevel(1);
		playerInfo.setOrder(1);
		playerInfo.setStatus(PlayerStatusEnum.notReady.status);
		playerInfo.setOnlineStatus(OnlineStatusEnum.online.status);
		playerInfo.setRoomCardNum(10);
		playerInfo.setWinTimes(0);
		playerInfo.setLoseTimes(0);
		/**设置地理位置信息*/
		playerInfo.setAddress(msg.getAddress());
		playerInfo.setX(msg.getX());
		playerInfo.setY(msg.getY());
		/**设置当前用户ip*/
		playerInfo.setIp(userInfo.getRemoteIp());
		playerInfo.setNickName(userInfo.getNickName());
		playerInfo.setHeadImgUrl(userInfo.getHeadImgUrl());
		
		
		redisOperationService.setRoomIdGameTypeUpdateTime(roomId, request.getGameType(), new Date());
		redisOperationService.setRoomIdRoomInfo(roomId, roomInfo);
		redisOperationService.setPlayerIdRoomIdGameType(userInfo.getPlayerId(), roomId, request.getGameType());
		
		/**设置返回信息*/
		result = new Result();
		result.setMsgType(MsgTypeEnum.createRoom.msgType);
		result.setGameType(request.getGameType());
		result.setData(roomInfo);
		channelContainer.sendTextMsgByPlayerIds(result, userInfo.getPlayerId());
		/**设置房间锁，此房间的请求排队进入*/
		RoomLockContainer.setLockByRoomId(roomId, new ReentrantLock());
	}
	
	public abstract BaseRoomInfo doCreateRoom(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo);
	
	public void entryRoom(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo) {
		Result result = null;
		BaseMsg msg = request.getMsg();
		Integer playerId = userInfo.getPlayerId();
		/**进入房间的时候，房间号参数是前台传过来的，所以不能从userInfo里面取得*/
		Integer roomId = msg.getRoomId();
		/**参数为空*/
		if (roomId == null) {
			throw new BusinessException(ExceptionEnum.PARAMS_ERROR);
		}
		if (!redisOperationService.isRoomIdExist(roomId)) {
			throw new BusinessException(ExceptionEnum.ROOM_ID_NOT_EXIST);
		}
		
		/**如果是aa支付，则校验房卡数量是否足够*/
		//TODO
//		if (PayTypeEnum.AAPay.type.equals(roomInfo.getPayType())) {
//			ResultCode resultCode = commonService.roomCardCheck(msg.getPlayerId(), roomInfo.getPayType(), roomInfo.getTotalGames());
//			if (!ResultCode.SUCCESS.equals(resultCode)) {
//				ChannelContainer.sendErrorMsg(ctx, resultCode, MsgTypeEnum.entryRoom.msgType, request);
//				return result;
//			}
//		}
		
		userInfo.setRoomId(roomId);
		redisOperationService.setUserInfo(request.getToken(), userInfo);
		
		BaseRoomInfo roomInfo = doEntryRoom(ctx, request, userInfo);
		List playerList = roomInfo.getPlayerList();
		int size = playerList.size();
		if (size >= 6) {
			throw new BusinessException(ExceptionEnum.EXCEED_MAX_PLAYER_NUM);
		}
		for(int i = 0; i < playerList.size(); i++ ){
			BasePlayerInfo tempPlayerInfo = (BasePlayerInfo)playerList.get(i);
			if (playerId.equals(tempPlayerInfo.getPlayerId())) {
				playerList.remove(i);
			}
		}
		/**取list最后一个，即为本次加入的玩家，设置公共信息*/
		BasePlayerInfo playerInfo = (BasePlayerInfo)playerList.get(playerList.size() - 1);
		playerInfo.setPlayerId(userInfo.getPlayerId());
		playerInfo.setNickName(userInfo.getNickName());
		playerInfo.setHeadImgUrl(userInfo.getHeadImgUrl());
		playerInfo.setLevel(1);
		playerInfo.setOrder(playerList.size());
		playerInfo.setStatus(PlayerStatusEnum.notReady.status);
		playerInfo.setOnlineStatus(OnlineStatusEnum.online.status);
		playerInfo.setRoomCardNum(10);
		playerInfo.setWinTimes(0);
		playerInfo.setLoseTimes(0);
		playerInfo.setIp(userInfo.getRemoteIp());
		/**设置地理位置信息*/
		playerInfo.setAddress(msg.getAddress());
		playerInfo.setX(msg.getX());
		playerInfo.setY(msg.getY());
		roomInfo.setUpdateTime(new Date());
		
		redisOperationService.setRoomIdGameTypeUpdateTime(roomId, request.getGameType(), new Date());
		redisOperationService.setRoomIdRoomInfo(roomId, roomInfo);
		redisOperationService.setPlayerIdRoomIdGameType(userInfo.getPlayerId(), roomId, request.getGameType());
		
		result = new Result();
		result.setGameType(request.getGameType());
		result.setMsgType(MsgTypeEnum.entryRoom.msgType);
		result.setData(roomInfo);
		/**给此房间中的所有玩家发送消息*/
		channelContainer.sendTextMsgByPlayerIds(result, GameUtil.getPlayerIdArr(playerList));
	}
	
	public abstract BaseRoomInfo doEntryRoom(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo);
	
	public void dissolveRoom(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo) {
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		result.setData(data);
		result.setGameType(request.getGameType());
		Integer playerId = userInfo.getPlayerId();
		Integer roomId = userInfo.getRoomId();
		BaseRoomInfo roomInfo = getRoomInfo(ctx, request, userInfo);
		
		List playerList = roomInfo.getPlayerList();
		if (!GameUtil.isExistPlayerInRoom(playerId, playerList)) {
			throw new BusinessException(ExceptionEnum.PLAYER_NOT_IN_ROOM);
		}
		GameUtil.setDissolveStatus(playerList, playerId, DissolveStatusEnum.agree);
		roomInfo.setUpdateTime(new Date());
		redisOperationService.setRoomIdRoomInfo(roomId, roomInfo);
		redisOperationService.setRoomIdGameTypeUpdateTime(roomId, new Date());
		if (playerList.size() == 1) {
			/**解散房间*/
			redisOperationService.cleanPlayerAndRoomInfo(roomId, GameUtil.getPlayerIdStrArr(playerList));
			result.setMsgType(MsgTypeEnum.successDissolveRoom.msgType);
			data.put("roomId", roomId);
			channelContainer.sendTextMsgByPlayerIds(result, playerId);
			return;
		}
		result.setMsgType(MsgTypeEnum.dissolveRoom.msgType);
		data.put("roomId", roomId);
		data.put("playerId", playerId);
		channelContainer.sendTextMsgByPlayerIds(result, GameUtil.getPlayerIdArr(playerList));
	}
	
	
	public void agreeDissolveRoom(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo) {
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		result.setData(data);
		result.setGameType(request.getGameType());
		BaseMsg msg = request.getMsg();
		Integer roomId = msg.getRoomId();
		BaseRoomInfo roomInfo = getRoomInfo(ctx, request, userInfo);
		List playerList = roomInfo.getPlayerList();
		if (!GameUtil.isExistPlayerInRoom(msg.getPlayerId(), playerList)) {
			throw new BusinessException(ExceptionEnum.PLAYER_NOT_IN_ROOM);
		}
		int size = playerList.size();
		int agreeDissolveCount = 0;
		for(int i = 0; i < size; i++){
			BasePlayerInfo player = (BasePlayerInfo)playerList.get(i);
			if (player.getPlayerId().equals(msg.getPlayerId())) {
				player.setDissolveStatus(DissolveStatusEnum.agree.status);
			}
			if (DissolveStatusEnum.agree.status.equals(player.getDissolveStatus())) {
				agreeDissolveCount++;
			}
		}
		roomInfo.setUpdateTime(new Date());
		redisOperationService.setRoomIdRoomInfo(roomId, roomInfo);
		/**如果大部分人同意，则推送解散消息并解散房间*/
		if (agreeDissolveCount >= (playerList.size()/2 + 1)) {
			/**解散房间*/
			redisOperationService.cleanPlayerAndRoomInfo(roomId, GameUtil.getPlayerIdStrArr(playerList));
			result.setMsgType(MsgTypeEnum.successDissolveRoom.msgType);
			channelContainer.sendTextMsgByPlayerIds(result, GameUtil.getPlayerIdArr(playerList));
			return ;
		}
		result.setMsgType(MsgTypeEnum.agreeDissolveRoom.msgType);
		data.put("roomId", roomId);
		data.put("playerId", msg.getPlayerId());
		channelContainer.sendTextMsgByPlayerIds(result, GameUtil.getPlayerIdArr(playerList));
	}
	
	public void disagreeDissolveRoom(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo) {
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		result.setData(data);
		result.setGameType(request.getGameType());
		BaseMsg msg = request.getMsg();
		Integer roomId = msg.getRoomId();
		BaseRoomInfo roomInfo = getRoomInfo(ctx, request, userInfo);
		if (null == roomInfo) {
			throw new BusinessException(ExceptionEnum.ROOM_ID_NOT_EXIST);
		}
		List playerList = roomInfo.getPlayerList();
		if (!GameUtil.isExistPlayerInRoom(msg.getPlayerId(), playerList)) {
			throw new BusinessException(ExceptionEnum.PLAYER_NOT_IN_ROOM);
		}
		int size = playerList.size();
		for(int i = 0; i < size; i++){
			BasePlayerInfo player = (BasePlayerInfo)playerList.get(i);
			if (player.getPlayerId().equals(msg.getPlayerId())) {
				player.setDissolveStatus(DissolveStatusEnum.disagree.status);
			}
		}
		roomInfo.setUpdateTime(new Date());
		redisOperationService.setRoomIdRoomInfo(roomId, roomInfo);
		result.setMsgType(MsgTypeEnum.disagreeDissolveRoom.msgType);
		data.put("roomId", roomId);
		data.put("playerId", msg.getPlayerId());
		channelContainer.sendTextMsgByPlayerIds(result, GameUtil.getPlayerIdArr(playerList));
	}
	
	public void delRoomConfirmBeforeReturnHall(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo) {
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		result.setData(data);
		result.setGameType(request.getGameType());
		BaseMsg msg = request.getMsg();
		Integer roomId = msg.getRoomId();
		BaseRoomInfo roomInfo = getRoomInfo(ctx, request, userInfo);
		if (null == roomInfo) {
			throw new BusinessException(ExceptionEnum.ROOM_ID_NOT_EXIST);
		}
		List playerList = roomInfo.getPlayerList();
		if (!GameUtil.isExistPlayerInRoom(msg.getPlayerId(), playerList)) {
			throw new BusinessException(ExceptionEnum.PLAYER_NOT_IN_ROOM);
		}
		
		int agreeDissolveCount = 0;
		int size = playerList.size();
		for(int i = 0; i < size; i++){
			BasePlayerInfo player = (BasePlayerInfo)playerList.get(i);
			if (player.getPlayerId().equals(msg.getPlayerId())) {
				player.setDissolveStatus(DissolveStatusEnum.agree.status);
			}
			if (player.getDissolveStatus().equals(DissolveStatusEnum.agree.status)) {
				agreeDissolveCount++;
			}
		}
		roomInfo.setUpdateTime(new Date());
		redisOperationService.setRoomIdRoomInfo(roomId, roomInfo);
		/**如果所有人都有确认消息，则解散房间*/
		if (agreeDissolveCount >= playerList.size()) {
			/**解散房间*/
			redisOperationService.cleanPlayerAndRoomInfo(roomId, GameUtil.getPlayerIdStrArr(playerList));
		}
		/**通知玩家返回大厅*/
		result.setMsgType(MsgTypeEnum.delRoomConfirmBeforeReturnHall.msgType);
		channelContainer.sendTextMsgByPlayerIds(result, msg.getPlayerId());
		/**将roomId从用户信息中去除*/
		userInfo.setRoomId(null);
		redisOperationService.setUserInfo(request.getToken(), userInfo);
	}
	
	public void chatMsg(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo) {
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		result.setData(data);
		result.setGameType(request.getGameType());
		BaseMsg msg = request.getMsg();
		Integer roomId = msg.getRoomId();
		BaseRoomInfo roomInfo = getRoomInfo(ctx, request, userInfo);
		if (null == roomInfo) {
			throw new BusinessException(ExceptionEnum.ROOM_ID_NOT_EXIST);
		}
		List playerList = roomInfo.getPlayerList();
		if (!GameUtil.isExistPlayerInRoom(msg.getPlayerId(), playerList)) {
			throw new BusinessException(ExceptionEnum.PLAYER_NOT_IN_ROOM);
		}
		result.setMsgType(MsgTypeEnum.chatMsg.msgType);
		if (ChatTypeEnum.specialEmotion.type == msg.getChatType()) {
			data.put("playerId", msg.getPlayerId());
			data.put("otherPlayerId", msg.getOtherPlayerId());
			data.put("chatMsg", msg.getChatMsg());
			data.put("chatType", msg.getChatType());
			List<Integer> playerIdList = new ArrayList<Integer>();
			Integer[] playerIdArr = new Integer[2];
			playerIdArr[0] = msg.getPlayerId();
			playerIdArr[1] = msg.getOtherPlayerId();
			channelContainer.sendTextMsgByPlayerIds(result, playerIdArr);
		}else if(ChatTypeEnum.voiceChat.type == msg.getChatType()){
			data.put("playerId", msg.getPlayerId());
			data.put("chatMsg", msg.getChatMsg());
			data.put("chatType", msg.getChatType());
			channelContainer.sendTextMsgByPlayerIds(result, GameUtil.getPlayerIdArrWithOutSelf(playerList, msg.getPlayerId()));
		}
		
		data.put("playerId", msg.getPlayerId());
		data.put("chatMsg", msg.getChatMsg());
		data.put("chatType", msg.getChatType());
		channelContainer.sendTextMsgByPlayerIds(result, GameUtil.getPlayerIdArr(playerList));
	}
	
	public void syncPlayerLocation(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo) {
		Result result = new Result();
		result.setGameType(request.getGameType());
		result.setMsgType(MsgTypeEnum.syncPlayerLocation.msgType);
		BaseMsg msg = request.getMsg();
		userInfo.setAddress(msg.getAddress());
		userInfo.setX(msg.getX());
		userInfo.setY(msg.getY());
		redisOperationService.setUserInfo(request.getToken(), userInfo);
	}
	
	public void queryPlayerInfo(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo) {
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		result.setData(data);
		result.setGameType(request.getGameType());
		BaseMsg msg = request.getMsg();
		Integer roomId = msg.getRoomId();
		BaseRoomInfo roomInfo = getRoomInfo(ctx, request, userInfo);
		if (null == roomInfo) {
			throw new BusinessException(ExceptionEnum.ROOM_ID_NOT_EXIST);
		}
		List playerList = roomInfo.getPlayerList();
		if (!GameUtil.isExistPlayerInRoom(msg.getPlayerId(), playerList)) {
			throw new BusinessException(ExceptionEnum.PLAYER_NOT_IN_ROOM);
		}
		Integer otherPlayerId = msg.getOtherPlayerId();
		Integer playerId = msg.getPlayerId();
		BasePlayerInfo otherPlayer = null;
		BasePlayerInfo curPlayer = null;
		int size = playerList.size();
		for(int i = 0; i < size; i++){
			BasePlayerInfo player = (BasePlayerInfo)playerList.get(i);
			if (player.getPlayerId().equals(otherPlayerId)) {
				otherPlayer = player;
			}else if(player.getPlayerId().equals(playerId)){
				curPlayer = player;
			}
		}
		if (otherPlayer != null) {
			data.put("playerId", otherPlayer.getPlayerId());
			data.put("nickName", otherPlayer.getNickName());
			data.put("headImgUrl", otherPlayer.getHeadImgUrl());
			data.put("address", otherPlayer.getAddress());
			String distance = GameUtil.getLatLngDistance(curPlayer, otherPlayer);
			data.put("distance", distance);
		}else{
			data.put("playerId", curPlayer.getPlayerId());
			data.put("nickName", curPlayer.getNickName());
			data.put("headImgUrl", curPlayer.getHeadImgUrl());
			data.put("address", curPlayer.getAddress());
		}
		result.setMsgType(MsgTypeEnum.queryPlayerInfo.msgType);
		channelContainer.sendTextMsgByPlayerIds(result, msg.getPlayerId());
	}
	
	public void userRecord(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo) {
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		result.setData(data);
		result.setGameType(request.getGameType());
		BaseMsg msg = request.getMsg();
		UserRecordModel qmodel = new UserRecordModel();
		qmodel.setGameType(request.getGameType());
		qmodel.setPlayerId(userInfo.getPlayerId());
		List<UserRecordModel> list = commonManager.getUserRecord(qmodel);
		for(UserRecordModel model : list){
			model.setNickNameList(JsonUtil.toObject(model.getNickNames(), List.class));
		}
		result.setMsgType(MsgTypeEnum.userRecord.msgType);
		result.setData(list);
		channelContainer.sendTextMsgByPlayerIds(result, msg.getPlayerId());
	}

	public void userFeedback(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo) {
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		result.setData(data);
		result.setGameType(request.getGameType());
		BaseMsg msg = request.getMsg();
		UserFeedbackModel model = new UserFeedbackModel();
		model.setPlayerId(msg.getPlayerId());
		model.setMobilePhone(msg.getMobilePhone());
		model.setFeedBack(msg.getFeedBack());
		model.setType(msg.getFeedBackType());
		commonManager.insertFeedback(model);
		result.setMsgType(MsgTypeEnum.userFeedback.msgType);
		channelContainer.sendTextMsgByPlayerIds(result, msg.getPlayerId());
	}
	
	public abstract BaseRoomInfo getRoomInfo(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo);
	
	public void ready(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo){}
	
	public void refreshRoom(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo){
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		result.setData(data);
		
		BaseMsg msg = request.getMsg();
		Integer roomId = msg.getRoomId();
		Integer playerId = msg.getPlayerId();
		List<BaseRoomInfo> roomInfoList = doRefreshRoom(ctx, request, userInfo);
		BaseRoomInfo roomInfo = roomInfoList.get(0);
		BaseRoomInfo returnRoomInfo = roomInfoList.get(1);
		if (null == roomInfo) {
			channelContainer.sendTextMsgByPlayerIds(new Result(0, MsgTypeEnum.entryHall.msgType), playerId);
			return;
		}
		List playerList = roomInfo.getPlayerList();
		if (!GameUtil.isExistPlayerInRoom(playerId, playerList)) {
			channelContainer.sendTextMsgByPlayerIds(new Result(0, MsgTypeEnum.entryHall.msgType), playerId);
			return;
		}
		result.setGameType(roomInfo.getGameType());
		result.setMsgType(MsgTypeEnum.refreshRoom.msgType);
		result.setData(returnRoomInfo);
		/**返回给当前玩家刷新信息*/
		channelContainer.sendTextMsgByPlayerIds(result, playerId);
		
		
		/**设置当前玩家缓存中为在线状态*/
		GameUtil.setOnlineStatus(playerList, playerId, OnlineStatusEnum.online);
		redisOperationService.setRoomIdRoomInfo(roomId, roomInfo);
		/**给其他的玩家发送当前玩家上线通知*/
		Result result1 = new Result();
		Map<String, Object> data1 = new HashMap<String, Object>();
		result1.setData(data1);
		data1.put("playerId", msg.getPlayerId());
		result1.setGameType(roomInfo.getGameType());
		result1.setMsgType(MsgTypeEnum.onlineNotice.msgType);
		channelContainer.sendTextMsgByPlayerIds(result1, GameUtil.getPlayerIdArrWithOutSelf(playerList, playerId));
		/**删除此玩家的离线标记*/
		redisOperationService.hdelOfflinePlayerIdRoomIdGameTypeTime(playerId);
	}
	
	public abstract List<BaseRoomInfo> doRefreshRoom(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo);
	
	public Result unifiedOrder(Integer productId, Integer playerId, String ip){
		Result result = new Result();
		ProductEnum productEnum = ProductEnum.getProductEnum(productId);
		if (productEnum == null) {
			result.setCode(ExceptionEnum.PARAMS_ERROR.index);
			result.setDesc(ExceptionEnum.PARAMS_ERROR.description);
			return result;
		}
		Long orderId = commonManager.insertOrder(playerId, productId, productEnum.roomCardNum, productEnum.price);
		
		
		
		return null;
	}
	
	/**
	 * 生成订单信息
	 * 
	 * @param ip
	 * @param orderId
	 * @return
	 */
	private SortedMap<String, Object> prepareOrder(String ip, String orderId, int price) {
		Map<String, Object> oparams = ImmutableMap.<String, Object> builder()
				.put("appid", ConfigUtil.APPID)// 服务号的应用号
				.put("body", WeixinConstant.PRODUCT_BODY)// 商品描述
				.put("mch_id", ConfigUtil.MCH_ID)// 商户号 ？
				.put("nonce_str", PayCommonUtil.CreateNoncestr())// 16随机字符串(大小写字母加数字)
				.put("out_trade_no", orderId)// 商户订单号
				.put("total_fee", price)// 支付金额 单位分 注意:前端负责传入分
				.put("spbill_create_ip", ip)// IP地址
				.put("notify_url", ConfigUtil.NOTIFY_URL) // 微信回调地址
				.put("trade_type", ConfigUtil.TRADE_TYPE)// 支付类型 app
				.build();
		return MapUtils.sortMap(oparams);
	}
}
