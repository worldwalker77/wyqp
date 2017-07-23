package cn.worldwalker.game.wyqp.nn.enums;

public enum NnRoomStatusEnum {
	
	justBegin(1, "刚开始准备阶段"),
	inRob(2, "抢庄中"),
	inGame(3, "小局中"),
	curGameOver(4, "小局结束"),
	totalGameOver(5, "一圈结束");
	
	public Integer status;
	public String desc;
	
	private NnRoomStatusEnum(Integer status, String desc){
		this.status = status;
		this.desc = desc;
	}
	
	public static NnRoomStatusEnum getRoomStatusEnum(Integer status){
		for(NnRoomStatusEnum statusEnum : NnRoomStatusEnum.values()){
			if (statusEnum.status.equals(status)) {
				return statusEnum;
			}
		}
		return null;
	}
}
