package cn.worldwalker.game.wyqp.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
		return MD5Util.encryptByMD5(temp);
	}
	public static void main(String[] args) {
		System.out.println(genToken(123456));
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
		
}
