package cn.worldwalker.game.wyqp.common.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.worldwalker.game.wyqp.common.constant.Constant;
import cn.worldwalker.game.wyqp.common.domain.base.BaseRoomInfo;
import cn.worldwalker.game.wyqp.common.domain.base.RedisRelaModel;
import cn.worldwalker.game.wyqp.common.domain.base.RoomCardOperationFailInfo;
import cn.worldwalker.game.wyqp.common.domain.base.UserInfo;
import cn.worldwalker.game.wyqp.common.enums.RoomCardOperationEnum;
import cn.worldwalker.game.wyqp.common.roomlocks.RoomLockContainer;
import cn.worldwalker.game.wyqp.common.utils.IPUtil;
import cn.worldwalker.game.wyqp.common.utils.JsonUtil;
import cn.worldwalker.game.wyqp.common.utils.redis.JedisTemplate;

@Component
public class RedisOperationService {
	@Autowired
	private JedisTemplate jedisTemplate;
	
	/**房间是否存在*/
	public boolean isRoomIdExist(Integer roomId){
		return jedisTemplate.hexists(Constant.wyqpRoomIdGameTypeUpdateTimeMap, String.valueOf(roomId));
	}
	
	/**roomId->roomInfo 映射*/
	public void setRoomIdRoomInfo(Integer roomId, BaseRoomInfo roomInfo){
		jedisTemplate.hset(Constant.wyqpRoomIdRoomInfoMap, String.valueOf(roomId), JsonUtil.toJson(roomInfo));
	}
	
	public <T> T getRoomInfoByRoomId(Integer roomId, Class<T> clazz){
		String roomInfoStr = jedisTemplate.hget(Constant.wyqpRoomIdRoomInfoMap, String.valueOf(roomId));
		if (StringUtils.isBlank(roomInfoStr)) {
			return null;
		}
		return JsonUtil.toObject(roomInfoStr, clazz);
	}
	
	/**roomId->gameType,updateTime 映射*/
	public void setRoomIdGameTypeUpdateTime(Integer roomId, Integer gameType, Date updateTime){
		jedisTemplate.hset(Constant.wyqpRoomIdGameTypeUpdateTimeMap, String.valueOf(roomId), gameType + "_" + updateTime.getTime());
	}
	
	public void setRoomIdGameTypeUpdateTime(Integer roomId, Date updateTime){
		RedisRelaModel redisRelaModel = getGameTypeUpdateTimeByRoomId(roomId);
		jedisTemplate.hset(Constant.wyqpRoomIdGameTypeUpdateTimeMap, String.valueOf(roomId), redisRelaModel.getGameType() + "_" + updateTime.getTime());
	}
	
	public RedisRelaModel getGameTypeUpdateTimeByRoomId(Integer roomId){
		String str = jedisTemplate.hget(Constant.wyqpRoomIdGameTypeUpdateTimeMap, String.valueOf(roomId));
		if (StringUtils.isBlank(str)) {
			return null;
		}
		String[] arr = str.split("_");
		return new RedisRelaModel(null, roomId, Integer.valueOf(arr[0]), Long.valueOf(arr[1]));
	}
	
	public void delGameTypeUpdateTimeByRoomId(Integer roomId){
		jedisTemplate.hdel(Constant.wyqpRoomIdGameTypeUpdateTimeMap, String.valueOf(roomId));
	}
	
	
	public List<RedisRelaModel> getAllRoomIdGameTypeUpdateTime(){
		Map<String, String> map = jedisTemplate.hgetAll(Constant.wyqpRoomIdGameTypeUpdateTimeMap);
		if (map == null) {
			return null;
		}
		List<RedisRelaModel> list = new ArrayList<RedisRelaModel>();
		Set<Entry<String, String>> set = map.entrySet();
		for(Entry<String, String> entry : set){
			String key = entry.getKey();
			String value = entry.getValue();
			String[] arr = value.split("_");
			list.add(new RedisRelaModel(null, Integer.valueOf(key), Integer.valueOf(arr[0]), Long.valueOf(arr[1])));
		}
		return list;
	}
	
	/**playerId->roomId,gameType 映射*/
	public void setPlayerIdRoomIdGameType(Integer playerId, Integer roomId, Integer gameType){
		jedisTemplate.hset(Constant.wyqpPlayerIdRoomIdGameTypeMap, String.valueOf(playerId), roomId + "_" + gameType);
	}
	
	public RedisRelaModel getRoomIdGameTypeByPlayerId(Integer playerId){
		String str = jedisTemplate.hget(Constant.wyqpPlayerIdRoomIdGameTypeMap, String.valueOf(playerId));
		if (StringUtils.isBlank(str)) {
			return null;
		}
		String[] arr = str.split("_");
		return new RedisRelaModel(playerId, Integer.valueOf(arr[0]), Integer.valueOf(arr[1]), null);
	}
	
	/**offline playerId->roomId,gameType 映射*/
	public void setOfflinePlayerIdRoomIdGameTypeTime(Integer playerId, Integer roomId, Integer gameType, Date time){
		jedisTemplate.hset(Constant.wyqpOfflinePlayerIdRoomIdGameTypeTimeMap, String.valueOf(playerId), roomId + "_" + gameType + "_" + time.getTime());
	}
	
	public void hdelOfflinePlayerIdRoomIdGameTypeTime(Integer playerId){
		jedisTemplate.hdel(Constant.wyqpOfflinePlayerIdRoomIdGameTypeTimeMap, String.valueOf(playerId));
	}
	
	public RedisRelaModel getRoomIdGameTypeTimeByOfflinePlayerId(Integer playerId){
		String str = jedisTemplate.hget(Constant.wyqpOfflinePlayerIdRoomIdGameTypeTimeMap, String.valueOf(playerId));
		if (StringUtils.isBlank(str)) {
			return null;
		}
		String[] arr = str.split("_");
		return new RedisRelaModel(playerId, Integer.valueOf(arr[0]), Integer.valueOf(arr[1]), Long.valueOf(arr[2]));
	}
	
	public List<RedisRelaModel> getAllOfflinePlayerIdRoomIdGameTypeTime(){
		Map<String, String> map = jedisTemplate.hgetAll(Constant.wyqpOfflinePlayerIdRoomIdGameTypeTimeMap);
		if (map == null) {
			return null;
		}
		List<RedisRelaModel> list = new ArrayList<RedisRelaModel>();
		Set<Entry<String, String>> set = map.entrySet();
		for(Entry<String, String> entry : set){
			String key = entry.getKey();
			String value = entry.getValue();
			String[] arr = value.split("_");
			list.add(new RedisRelaModel(Integer.valueOf(key), Integer.valueOf(arr[0]), Integer.valueOf(arr[1]), Long.valueOf(arr[2])));
		}
		return list;
	}
	
	/**ip->roomId->time 映射*/
	public void setIpRoomIdTime(Integer roomId){
		jedisTemplate.hset(Constant.wyqpIpRoomIdTimeMap, String.valueOf(roomId), String.valueOf(System.currentTimeMillis()));
	}
	
	public void delIpRoomIdTime(Integer roomId){
		jedisTemplate.hdel(Constant.wyqpIpRoomIdTimeMap, String.valueOf(roomId));
	}
	
	public Map<String, String> getAllIpRoomIdTime(){
		return jedisTemplate.hgetAll(Constant.wyqpIpRoomIdTimeMap);
	}
	
	
	/**用户userInfo设置*/
	public void setUserInfo(String token, UserInfo userInfo){
		jedisTemplate.setex(token, JsonUtil.toJson(userInfo), 3600*2);
	}
	
	public void expireUserInfo(String token){
		jedisTemplate.expire(token, 3600*2);
	}
	
	public UserInfo getUserInfo(String token){
		String temp = jedisTemplate.get(token);
		if (StringUtils.isNotBlank(temp)) {
			return JsonUtil.toObject(temp, UserInfo.class);
		}
		return null;
	}
	
	
	/****/
	public void incrIpConnectCount(int incrBy){
		String ip = IPUtil.getLocalIp();
  	    if (StringUtils.isNotBlank(ip)) {
  	    	jedisTemplate.hincrBy(Constant.wyqpIpConnectCountMap, IPUtil.getLocalIp(), incrBy);
  	    }
	}
	
	/**清理用户及房间信息*/
	public void cleanPlayerAndRoomInfo(Integer roomId, String... playerIds){
		jedisTemplate.hdel(Constant.wyqpRoomIdRoomInfoMap, String.valueOf(roomId));
		jedisTemplate.hdel(Constant.wyqpRoomIdGameTypeUpdateTimeMap, String.valueOf(roomId));
		jedisTemplate.hdel(Constant.wyqpPlayerIdRoomIdGameTypeMap, playerIds);
		jedisTemplate.hdel(Constant.wyqpOfflinePlayerIdRoomIdGameTypeTimeMap, playerIds);
		RoomLockContainer.delLockByRoomId(roomId);
	}
	
	/***/
	public void lpushRoomCardOperationFailInfo(Integer playerId, Integer gameType, Integer payType, 
													Integer totalGames, RoomCardOperationEnum roomCardOperationEnum){
		RoomCardOperationFailInfo failInfo = new RoomCardOperationFailInfo(playerId,gameType,payType,totalGames,roomCardOperationEnum.type);
		jedisTemplate.lpush(Constant.wyqpRoomCardOperationFailList, JsonUtil.toJson(failInfo));
		
	}
	
	public RoomCardOperationFailInfo rpopRoomCardOperationFailInfo(){
		String failInfoStr = jedisTemplate.rpop(Constant.wyqpRoomCardOperationFailList);
		if (StringUtils.isBlank(failInfoStr)) {
			return null;
		}
		return JsonUtil.toObject(failInfoStr, RoomCardOperationFailInfo.class);
	}
	
	
	public boolean isLogFuseOpen(){
		if ("1".equals(jedisTemplate.get(Constant.wyqpLogInfoFuse))) {
			return true;
		}
		return false;
	}
	
	public boolean isLoginFuseOpen(){
		if ("1".equals(jedisTemplate.get(Constant.wyqpLoginFuse))) {
			return true;
		}
		return false;
	}
	
}
