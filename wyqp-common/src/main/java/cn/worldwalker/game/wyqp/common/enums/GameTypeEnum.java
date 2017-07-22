package cn.worldwalker.game.wyqp.common.enums;

public enum GameTypeEnum {
	
	nn(1, "牛牛"),
	mj(2, "麻将");
	
	public Integer gameType;
	public String desc;
	
	private GameTypeEnum(int gameType, String desc){
		this.gameType = gameType;
		this.desc = desc;
	}
	
	public static GameTypeEnum getGameTypeEnumByType(Integer gameType){
		for(GameTypeEnum gameTypeEnum : GameTypeEnum.values()){
			if (gameType == gameTypeEnum.gameType) {
				return gameTypeEnum;
			}
		}
		return null;
	}
	
}
