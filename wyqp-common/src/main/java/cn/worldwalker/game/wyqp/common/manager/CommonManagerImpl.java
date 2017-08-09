package cn.worldwalker.game.wyqp.common.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.worldwalker.game.wyqp.common.dao.RoomCardLogDao;
import cn.worldwalker.game.wyqp.common.dao.UserDao;
import cn.worldwalker.game.wyqp.common.dao.UserFeedbackDao;
import cn.worldwalker.game.wyqp.common.dao.UserRecordDao;
import cn.worldwalker.game.wyqp.common.domain.base.BasePlayerInfo;
import cn.worldwalker.game.wyqp.common.domain.base.BaseRoomInfo;
import cn.worldwalker.game.wyqp.common.domain.base.RoomCardLogModel;
import cn.worldwalker.game.wyqp.common.domain.base.UserFeedbackModel;
import cn.worldwalker.game.wyqp.common.domain.base.UserModel;
import cn.worldwalker.game.wyqp.common.domain.base.UserRecordModel;
import cn.worldwalker.game.wyqp.common.enums.RoomCardConsumeEnum;
import cn.worldwalker.game.wyqp.common.enums.RoomCardOperationEnum;
import cn.worldwalker.game.wyqp.common.exception.BusinessException;
import cn.worldwalker.game.wyqp.common.exception.ExceptionEnum;
import cn.worldwalker.game.wyqp.common.utils.JsonUtil;
@Component
public class CommonManagerImpl implements CommonManager{
	
	private static final Log log = LogFactory.getLog(CommonManagerImpl.class);
	
	@Autowired
	private UserDao userDao;
	@Autowired
	private RoomCardLogDao roomCardLogDao;
	@Autowired
	private UserFeedbackDao userFeedbackDao;
	@Autowired
	private UserRecordDao userRecordDao;
	
	@Override
	public UserModel getUserByWxOpenId(String openId){
		return userDao.getUserByWxOpenId(openId);
	}
	@Override
	public void insertUser(UserModel userModel){
		userDao.insertUser(userModel);
	}
	
	@Transactional
	@Override
	public void deductRoomCard(BaseRoomInfo roomInfo, RoomCardOperationEnum operationEnum){
		List<Integer> playerIdList = new ArrayList<Integer>();
		if (roomInfo.getPayType() == 1) {/**房主付费*/
			playerIdList.add(roomInfo.getRoomOwnerId());
		}else{/**AA付费*/
			List playerList = roomInfo.getPlayerList();
			int size = playerList.size();
			for(int i = 0; i < size; i++){
				BasePlayerInfo player = (BasePlayerInfo)playerList.get(i);
				playerIdList.add(player.getPlayerId());
			}
		}
		for(Integer playerId : playerIdList){
			doDeductRoomCard(roomInfo.getGameType(), roomInfo.getPayType(), roomInfo.getTotalGames(), operationEnum, playerId);
		}
	}
	@Override
	public Integer doDeductRoomCard(Integer gameType, Integer payType, Integer totalGames, RoomCardOperationEnum operationEnum, Integer playerId){
		RoomCardConsumeEnum consumeEnum = RoomCardConsumeEnum.getRoomCardConsumeEnum(gameType, payType, totalGames);
		Map<String, Object> map = new HashMap<String, Object>();
		int re = 0;
		int reTryCount = 1;
		UserModel userModel = null;
		do {
			userModel = userDao.getUserById(playerId);
			map.put("id", playerId);
			map.put("deductNum", consumeEnum.needRoomCardNum);
			map.put("roomCardNum", userModel.getRoomCardNum());
			map.put("updateTime", userModel.getUpdateTime());
			re = userDao.deductRoomCard(map);
			if (re == 1) {
				break;
			}
			reTryCount++;
			log.info("扣除房卡重试第" + reTryCount + "次");
		} while (reTryCount < 4);/**扣除房卡重试三次*/
		if (reTryCount == 4) {
			throw new BusinessException(ExceptionEnum.ROOM_CARD_DEDUCT_THREE_TIMES_FAIL);
		}
		RoomCardLogModel roomCardLogModel = new RoomCardLogModel();
		roomCardLogModel.setGameType(gameType);
		roomCardLogModel.setPlayerId(playerId);
		roomCardLogModel.setDiffRoomCardNum(consumeEnum.needRoomCardNum);
		roomCardLogModel.setPreRoomCardNum(userModel.getRoomCardNum());
		Integer curRoomCardNum = userModel.getRoomCardNum() - consumeEnum.needRoomCardNum;
		roomCardLogModel.setCurRoomCardNum(curRoomCardNum);
		roomCardLogModel.setOperatorId(playerId);
		roomCardLogModel.setOperatorType(operationEnum.type);
		roomCardLogModel.setCreateTime(new Date());
		roomCardLogDao.insertRoomCardLog(roomCardLogModel);
		return curRoomCardNum;
	}
	@Override
	public void insertFeedback(UserFeedbackModel model) {
		userFeedbackDao.insertFeedback(model);
	}
	@Override
	public List<UserRecordModel> getUserRecord(UserRecordModel model) {
		return userRecordDao.getUserRecord(model);
	}
	@Override
	public void addUserRecord(BaseRoomInfo roomInfo) {
		List playerList = roomInfo.getPlayerList();
		if (CollectionUtils.isEmpty(playerList)) {
			return;
		}
		int size = playerList.size();
		List<String> nickNameList = new ArrayList<String>();
		for(int i = 0; i < size; i++){
			BasePlayerInfo player = (BasePlayerInfo)playerList.get(i);
			nickNameList.add(player.getNickName());
		}
		String nickNames = JsonUtil.toJson(nickNameList);
		List<UserRecordModel> modelList = new ArrayList<UserRecordModel>();
		Date createTime = new Date();
		for(int i = 0; i < size; i++){
			BasePlayerInfo player = (BasePlayerInfo)playerList.get(i);
			UserRecordModel model = new UserRecordModel();
			model.setGameType(roomInfo.getGameType());
			model.setPlayerId(player.getPlayerId());
			model.setRoomId(roomInfo.getRoomId());
			model.setPayType(roomInfo.getPayType());
			model.setTotalGames(roomInfo.getTotalGames());
			model.setScore(player.getTotalScore());
			model.setNickNames(nickNames);
			model.setRemark(RoomCardConsumeEnum.getRoomCardConsumeEnum(roomInfo.getGameType(), roomInfo.getPayType(), roomInfo.getTotalGames()).desc);
			model.setCreateTime(createTime);
			modelList.add(model);
		}
		userRecordDao.batchInsertRecord(modelList);
	}
	@Override
	public void roomCardCheck(Integer playerId, Integer gameType, Integer payType, Integer totalGames) {
		RoomCardConsumeEnum consumeEnum = RoomCardConsumeEnum.getRoomCardConsumeEnum(gameType,payType, totalGames);
		if (consumeEnum == null) {
			throw new BusinessException(ExceptionEnum.PARAMS_ERROR);
		}
		UserModel userModel = userDao.getUserById(playerId);
		Integer roomCardNum = userModel.getRoomCardNum();
		if (roomCardNum < consumeEnum.needRoomCardNum) {
			throw new BusinessException(ExceptionEnum.ROOM_CARD_NOT_ENOUGH);
		}
	}
}
