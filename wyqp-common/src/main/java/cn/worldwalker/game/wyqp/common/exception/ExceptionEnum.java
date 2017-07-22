package cn.worldwalker.game.wyqp.common.exception;

public enum ExceptionEnum {
	
	SYSTEM_ERROR(-1, "成功"),
    SUCCESS(0, "成功"),
    NEED_LOGIN(1, "参数为空或者错误"),
    PARAMS_ERROR(2, "需要登录"),
    ROOM_ID_NOT_EXIST(3, "房间号不存在"),
    GEN_ROOM_ID_FAIL(4, "生成房间号失败"),
    PLAYER_NOT_IN_ROOM(5, "玩家不在房间中"),
    ROOM_CARD_DEDUCT_THREE_TIMES_FAIL(6, "三次扣除房卡重试失败");

    public Integer   index;
    public String description;

    private ExceptionEnum(Integer index, String description) {
        this.index = index;
        this.description = description;
    }
}
