package cn.worldwalker.game.wyqp.common.manager;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.worldwalker.game.wyqp.common.dao.RoomCardLogDao;
import cn.worldwalker.game.wyqp.common.dao.UserDao;
import cn.worldwalker.game.wyqp.common.domain.base.RoomCardLogModel;
import cn.worldwalker.game.wyqp.common.domain.base.UserModel;
import cn.worldwalker.game.wyqp.common.enums.RoomCardConsumeEnum;
import cn.worldwalker.game.wyqp.common.enums.RoomCardOperationEnum;
import cn.worldwalker.game.wyqp.common.exception.BusinessException;
import cn.worldwalker.game.wyqp.common.exception.ExceptionEnum;
@Component
public class CommonManagerImpl implements CommonManager{
	
	private static final Log log = LogFactory.getLog(CommonManagerImpl.class);
	
	@Autowired
	private UserDao userDao;
	@Autowired
	private RoomCardLogDao roomCardLogDao;
	
	@Transactional
	@Override
	public Integer doDeductRoomCard(Integer playerId, Integer payType, Integer totalGames, RoomCardOperationEnum operationEnum){
		RoomCardConsumeEnum consumeEnum = RoomCardConsumeEnum.getRoomCardConsumeEnum(payType, totalGames);
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
}
