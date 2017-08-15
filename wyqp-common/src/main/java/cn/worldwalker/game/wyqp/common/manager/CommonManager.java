package cn.worldwalker.game.wyqp.common.manager;

import java.util.List;

import cn.worldwalker.game.wyqp.common.domain.base.BaseRoomInfo;
import cn.worldwalker.game.wyqp.common.domain.base.ProductModel;
import cn.worldwalker.game.wyqp.common.domain.base.UserFeedbackModel;
import cn.worldwalker.game.wyqp.common.domain.base.UserModel;
import cn.worldwalker.game.wyqp.common.domain.base.UserRecordModel;
import cn.worldwalker.game.wyqp.common.enums.RoomCardOperationEnum;

public interface CommonManager {
	
	public UserModel getUserByWxOpenId(String openId);
	
	public void insertUser(UserModel userModel);
	
	public void insertFeedback(UserFeedbackModel model);
	
	public List<UserRecordModel> getUserRecord(UserRecordModel model);
	
	public void deductRoomCard(BaseRoomInfo roomInfo, RoomCardOperationEnum operationEnum);
	
	public Integer doDeductRoomCard(Integer gameType, Integer payType, Integer totalGames, RoomCardOperationEnum operationEnum, Integer playerId);
	
	public void addUserRecord(BaseRoomInfo roomInfo);
	
	public void roomCardCheck(Integer playerId, Integer gameType, Integer payType, Integer totalGames);
	
	public Long insertOrder(Integer playerId, Integer productId, Integer roomCardNum, Integer price);
	 
	public void updateOrder(Long orderId, String transactionId, Integer wxPayPrice);
	
	public ProductModel getProductById(Integer productId);
	 
	public List<ProductModel> getProductList();
}
