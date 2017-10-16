package cn.worldwalker.game.wyqp.mj.enums;

public enum ChiPaiEnum {
	
	CHI(1, "吃"),
	PENG(2, "碰"),
	GANG(3, "杠");
	
	public Integer type;
	
	public String desc;
	
	private ChiPaiEnum(Integer type, String desc){
		this.type = type;
		this.desc = desc;
	}
}
