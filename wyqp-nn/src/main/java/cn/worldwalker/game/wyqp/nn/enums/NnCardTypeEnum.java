package cn.worldwalker.game.wyqp.nn.enums;

public enum NnCardTypeEnum {
	
	NO_NIU(0, "无牛"),
	NIU_1(1, "牛一"),
	NIU_2(2, "牛二"),
	NIU_3(3, "牛三"),
	NIU_4(4, "牛四"),
	NIU_5(5, "牛五"),
	NIU_6(6, "牛六"),
	NIU_7(7, "牛七"),
	NIU_8(8, "牛八"),
	NIU_9(9, "牛九"),
	NIU_NIU(10, "牛牛"),
	GOLD_NIU(11, "五花牛"),
	FIVE_SMALL_NIU(12, "五小牛"),
	BOMB_NIU(13, "炸弹牛");
	
	public Integer cardType;
	public String desc;
	
	private NnCardTypeEnum(Integer cardType, String desc){
		this.cardType = cardType;
		this.desc = desc;
	}
	
	public static NnCardTypeEnum getNnCardTypeEnum(Integer cardType){
		for(NnCardTypeEnum cardTypeEnum : NnCardTypeEnum.values()){
			if (cardTypeEnum.cardType.equals(cardType)) {
				return cardTypeEnum;
			}
		}
		return null;
	}
}
