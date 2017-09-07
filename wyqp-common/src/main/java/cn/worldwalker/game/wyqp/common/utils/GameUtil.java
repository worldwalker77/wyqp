package cn.worldwalker.game.wyqp.common.utils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import cn.worldwalker.game.wyqp.common.domain.base.BasePlayerInfo;
import cn.worldwalker.game.wyqp.common.enums.DissolveStatusEnum;
import cn.worldwalker.game.wyqp.common.enums.OnlineStatusEnum;

public class GameUtil {
	
	public static int genRoomId(){
		int max=999999;
        int min=100000;
        Random random = new Random();
        int s = random.nextInt(max)%(max-min+1) + min;	
		return s;
	}
	
	public static Integer genPlayerId(){
		int max=999999;
		int min=100000;
        Random random = new Random();
        int s = random.nextInt(max)%(max-min+1) + min;
		return s;
	}
	public static String genToken(Integer playerId){
		String temp = playerId + System.currentTimeMillis() + Thread.currentThread().getName();
		return MD5Util1.encryptByMD5(temp);
	}
	public static void main(String[] args) {
		System.out.println(genToken(null));
	}
	public static Integer[] getPlayerIdArr(List playerList){
		int size = playerList.size();
		Integer[] arr = new Integer[size];
		for(int i = 0; i < size; i++){
			BasePlayerInfo playerInfo = (BasePlayerInfo)playerList.get(i);
			arr[i] = playerInfo.getPlayerId();
		}
		return arr;
	}
	
	public static String[] getPlayerIdStrArr(List playerList){
		int size = playerList.size();
		String[] arr = new String[size];
		for(int i = 0; i < size; i++){
			BasePlayerInfo playerInfo = (BasePlayerInfo)playerList.get(i);
			arr[i] = String.valueOf(playerInfo.getPlayerId());
		}
		return arr;
	}
	
	public static Integer[] getPlayerIdArrWithOutSelf(List playerList, Integer playerId){
		int size = playerList.size();
		List<Integer> playerIdList = new ArrayList<Integer>();
		for(int i = 0; i < size; i++){
			BasePlayerInfo playerInfo = (BasePlayerInfo)playerList.get(i);
			if (!playerId.equals(playerInfo.getPlayerId())) {
				playerIdList.add(playerInfo.getPlayerId());
			}
		}
		Integer[] arr = new Integer[playerIdList.size()];
		playerIdList.toArray(arr);
		return arr;
	}
	
	public static Integer[] getPlayerIdArrWithOutRoomBanker(List playerList, Integer roomBankerId){
		int size = playerList.size();
		List<Integer> playerIdList = new ArrayList<Integer>();
		for(int i = 0; i < size; i++){
			BasePlayerInfo playerInfo = (BasePlayerInfo)playerList.get(i);
			if (!roomBankerId.equals(playerInfo.getPlayerId())) {
				playerIdList.add(playerInfo.getPlayerId());
			}
		}
		Integer[] arr = new Integer[playerIdList.size()];
		playerIdList.toArray(arr);
		return arr;
	}
	
	public static boolean isExistPlayerInRoom(Integer playerId, List playerList){
		int size = playerList.size();
		for(int i = 0; i < size; i++){
			BasePlayerInfo playerInfo = (BasePlayerInfo)playerList.get(i);
			if (playerInfo.getPlayerId().equals(playerId)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 设置玩家解散状态
	 * @param playerList
	 * @param playerId
	 * @param statusEnum
	 */
	public static void setDissolveStatus(List playerList, Integer playerId, DissolveStatusEnum statusEnum){
		int size = playerList.size();
		for(int i = 0; i < size; i++){
			BasePlayerInfo playerInfo = (BasePlayerInfo)playerList.get(i);
			if (playerInfo.getPlayerId().equals(playerId)) {
				playerInfo.setDissolveStatus(statusEnum.status);
			}
		}
	}
	
	/**
	 * 设置玩家在线状态
	 * @param playerList
	 * @param playerId
	 * @param statusEnum
	 */
	public static void setOnlineStatus(List playerList, Integer playerId, OnlineStatusEnum onlineStatusEnum){
		int size = playerList.size();
		for(int i = 0; i < size; i++){
			BasePlayerInfo playerInfo = (BasePlayerInfo)playerList.get(i);
			if (playerInfo.getPlayerId().equals(playerId)) {
				playerInfo.setOnlineStatus(onlineStatusEnum.status);
			}
		}
	}
	/**
	 * 设置玩家状态
	 * @param playerList
	 * @param playerId
	 * @param status
	 */
	public static void setPlayerStatus(List playerList, Integer playerId,  Integer status){
		int size = playerList.size();
		for(int i = 0; i < size; i++){
			BasePlayerInfo playerInfo = (BasePlayerInfo)playerList.get(i);
			if (playerInfo.getPlayerId().equals(playerId)) {
				playerInfo.setStatus(status);
			}
		}
	}
	
	/**
	   * 计算两点之间距离
	   * @param start
	   * @param end
	   * @return String  多少m ,  多少km
	   */
	public static String getLatLngDistance(BasePlayerInfo curPlayer, BasePlayerInfo otherPlayer){
	   String startXStr = curPlayer.getX();
	   String startYStr = curPlayer.getY();
	   String endXStr = otherPlayer.getX();
	   String endYStr = otherPlayer.getY();
	   if (null == curPlayer || null == otherPlayer 
		   || StringUtils.isBlank(curPlayer.getX()) || StringUtils.isBlank(curPlayer.getX())
		   || StringUtils.isBlank(otherPlayer.getX()) || StringUtils.isBlank(otherPlayer.getX())) {
		   return null;
	   }
	   if ("0.0".equals(startXStr) && "0.0".equals(startYStr) || "0.0".equals(endXStr) && "0.0".equals(endYStr)) {
		   return null;
	   }
	   double lat1 = (Math.PI/180)*Double.valueOf(startXStr);
	   double lat2 = (Math.PI/180)*Double.valueOf(endXStr);
	   
	   double lon1 = (Math.PI/180)*Double.valueOf(startYStr);
	   double lon2 = (Math.PI/180)*Double.valueOf(endYStr);
	   
	   //地球半径
	   double R = 6371.004;
	   
	   //两点间距离 m，如果想要米的话，结果*1000就可以了
	   double dis =  Math.acos(Math.sin(lat1)*Math.sin(lat2)+Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1))*R;
	   NumberFormat nFormat = NumberFormat.getNumberInstance();  //数字格式化对象
	   if(dis < 1){               //当小于1千米的时候用,用米做单位保留一位小数
	    
	    nFormat.setMaximumFractionDigits(1);    //已可以设置为0，这样跟百度地图APP中计算的一样 
	    dis *= 1000;
	    
	    return nFormat.format(dis)+"m";
	   }else{
	    nFormat.setMaximumFractionDigits(2);
	    return nFormat.format(dis)+"km";
	   }

	 }
		
}
