package cn.worldwalker.game.wyqp.nn.enums;

public enum NnPlayerStatusEnum {
	
	notReady(1, "未准备"),
	ready(2, "已准备"),
	notRob(3, "不抢庄"),
	rob(4, "抢庄");
	
	public Integer status;
	public String desc;
	
	private NnPlayerStatusEnum(Integer status, String desc){
		this.status = status;
		this.desc = desc;
	}
}
