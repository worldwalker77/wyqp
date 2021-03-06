package cn.worldwalker.game.wyqp.common.service;

import io.netty.channel.ChannelHandlerContext;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cn.worldwalker.game.wyqp.common.channel.ChannelContainer;
import cn.worldwalker.game.wyqp.common.dao.UserDao;
import cn.worldwalker.game.wyqp.common.domain.base.BaseMsg;
import cn.worldwalker.game.wyqp.common.domain.base.BasePlayerInfo;
import cn.worldwalker.game.wyqp.common.domain.base.BaseRequest;
import cn.worldwalker.game.wyqp.common.domain.base.BaseRoomInfo;
import cn.worldwalker.game.wyqp.common.domain.base.RedisRelaModel;
import cn.worldwalker.game.wyqp.common.domain.base.UserInfo;
import cn.worldwalker.game.wyqp.common.domain.base.UserModel;
import cn.worldwalker.game.wyqp.common.domain.base.WeiXinUserInfo;
import cn.worldwalker.game.wyqp.common.enums.DissolveStatusEnum;
import cn.worldwalker.game.wyqp.common.enums.MsgTypeEnum;
import cn.worldwalker.game.wyqp.common.enums.PlayerStatusEnum;
import cn.worldwalker.game.wyqp.common.enums.RoomStatusEnum;
import cn.worldwalker.game.wyqp.common.exception.BusinessException;
import cn.worldwalker.game.wyqp.common.exception.ExceptionEnum;
import cn.worldwalker.game.wyqp.common.result.Result;
import cn.worldwalker.game.wyqp.common.roomlocks.RoomLockContainer;
import cn.worldwalker.game.wyqp.common.rpc.WeiXinRpc;
import cn.worldwalker.game.wyqp.common.utils.GameUtil;
import cn.worldwalker.game.wyqp.common.utils.IPUtil;

public abstract class BaseGameService {
	
	private final static Log log = LogFactory.getLog(BaseGameService.class);
	
	@Autowired
	public RedisOperationService redisOperationService;
	@Autowired
	public ChannelContainer channelContainer;
	
	@Autowired
	public UserDao userDao;
	
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
		UserModel userModel = userDao.getUserByWxOpenId(weixinUserInfo.getOpneid());
		if (null == userModel) {
			userModel = new UserModel();
			userModel.setNickName(weixinUserInfo.getName());
			userModel.setHeadImgUrl(weixinUserInfo.getHeadImgUrl());
			userModel.setWxOpenId(weixinUserInfo.getOpneid());
			userDao.insertUser(userModel);
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
		userInfo.setPort("3389");
		userInfo.setRemoteIp(IPUtil.getRemoteIp(request));
		String loginToken = GameUtil.genToken(userModel.getId());
		userInfo.setHeadImgUrl(weixinUserInfo.getHeadImgUrl());
		redisOperationService.setUserInfo(loginToken, userInfo);
		userInfo.setToken(loginToken);
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
//		ResultCode resultCode = commonService.roomCardCheck(msg.getPlayerId(), msg.getPayType(), msg.getTotalGames());
//		if (!ResultCode.SUCCESS.equals(resultCode)) {
//			channelContainer.sendErrorMsg(ctx, resultCode, MsgTypeEnum.createRoom.msgType, request);
//			return result;
//		}
		
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
		roomInfo.setRoomBankerId(msg.getPlayerId());
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
		Integer roomId = userInfo.getRoomId();
		/**参数为空*/
		if (roomId == null) {
			throw new BusinessException(ExceptionEnum.PARAMS_ERROR);
		}
		if (redisOperationService.isRoomIdExist(roomId)) {
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
		BasePlayerInfo playerInfo = (BasePlayerInfo)playerList.get(playerList.size() - 1);
		playerInfo.setPlayerId(userInfo.getPlayerId());
		playerInfo.setNickName(userInfo.getNickName());
		playerInfo.setHeadImgUrl(userInfo.getHeadImgUrl());
		playerInfo.setLevel(1);
		playerInfo.setOrder(playerList.size());
		playerInfo.setStatus(PlayerStatusEnum.notReady.status);
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
		result.setMsgType(request.getMsgType());
		request.setGameType(1);
		result.setData(roomInfo);
		/**给此房间中的所有玩家发送消息*/
		channelContainer.sendTextMsgByPlayerIds(result, GameUtil.getPlayerIdArr(playerList));
	}
	
	public abstract BaseRoomInfo doEntryRoom(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo);
	
	public void dissolveRoom(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo) {
		Result result = new Result();
		Map<String, Object> data = new HashMap<String, Object>();
		result.setData(data);
		
		Integer playerId = userInfo.getPlayerId();
		Integer roomId = userInfo.getRoomId();
		BaseRoomInfo roomInfo = doDissolveRoom(ctx, request, userInfo);
		
		List playerList = roomInfo.getPlayerList();
		if (!GameUtil.isExistPlayerInRoom(playerId, playerList)) {
			throw new BusinessException(ExceptionEnum.PLAYER_NOT_IN_ROOM);
		}
		GameUtil.setDissolveStatus(playerList, playerId, DissolveStatusEnum.disagree);
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
	
	public abstract BaseRoomInfo doDissolveRoom(ChannelHandlerContext ctx, BaseRequest request, UserInfo userInfo);
}
