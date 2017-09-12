package cn.worldwalker.game.wyqp.common.enums;

public enum RoomCardConsumeEnum {
	
	nn_roomOwnerPayTenGames(1, 1, 10, 4, "房主付费,10局4张房卡"),
	nn_roomOwnerPayTwentyGames(1, 1, 20, 7, "房主付费,20局7张房卡"),
	nn_AAPayTenGames(1, 2, 10, 1, "AA制付费,10局每人1张房卡"),
	nn_AAPayTwentyGames(1, 2, 20, 2, "AA制付费,20局每人2张房卡"),
	
	jh_roomOwnerPayTenGames(3, 1, 10, 4, "房主付费10局需4张房卡"),
	jh_roomOwnerPayTwentyGames(3, 1, 20, 7, "房主付费20局需7张房卡"),
	jh_roomOwnerPayThirtyGames(3, 1, 30, 10, "房主付费30局需10张房卡"),
	jh_AAPayTenGames(3, 2, 10, 1, "AA制付费10局每人需1张房卡"),
	jh_AAPayTwentyGames(3, 2, 20, 2, "AA制付费20局每人需2张房卡"),
	jh_AAPayThirtyGames(3, 2, 30, 3, "AA制付费30局每人需3张房卡"),
	jh_winnerPayTenGames(3, 3, 10, 4, "赢家付费10局需4张房卡"),
	jh_winnerPayTwentyGames(3, 3, 20, 7, "赢家付费20局需7张房卡"),
	jh_winnerPayThirtyGames(3, 3, 30, 10, "赢家付费30局需10张房卡");
	
	public Integer gameType;
	public Integer payType;
	public Integer totalGames;
	public Integer needRoomCardNum;
	public String desc;
	
	private RoomCardConsumeEnum(Integer gameType, Integer payType, Integer totalGames, Integer needRoomCardNum, String desc){
		this.gameType = gameType;
		this.payType = payType;
		this.totalGames = totalGames;
		this.needRoomCardNum = needRoomCardNum;
		this.desc = desc;
	}
	
	public static RoomCardConsumeEnum getRoomCardConsumeEnum(Integer gameType, Integer payType, Integer totalGames){
		for(RoomCardConsumeEnum consumeEnum : RoomCardConsumeEnum.values()){
			if (consumeEnum.gameType.equals(gameType) && consumeEnum.payType.equals(payType) && consumeEnum.totalGames.equals(totalGames)) {
				return consumeEnum;
			}
		}
		return null;
	}
}
