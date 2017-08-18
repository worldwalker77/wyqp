package cn.worldwalker.game.wyqp.common.exception;

public enum ExceptionEnum {
	
	SYSTEM_ERROR(-1, "系统异常"),
    SUCCESS(0, "成功"),
    NEED_LOGIN(1, "需要登录"),
    PARAMS_ERROR(2, "参数为空或者错误"),
    ROOM_ID_NOT_EXIST(3, "房间号不存在"),
    GEN_ROOM_ID_FAIL(4, "生成房间号失败"),
    PLAYER_NOT_IN_ROOM(5, "玩家不在房间中"),
    ROOM_CARD_DEDUCT_THREE_TIMES_FAIL(6, "三次扣除房卡重试失败"),
    ROOM_BANKER_CAN_NOT_STAKE_SCORE(7, "庄家不能压分"),
    EXCEED_MAX_PLAYER_NUM(8, "此房间已满"),
    ROOM_CARD_NOT_ENOUGH(9, "房卡数不够"),
    INSERT_ORDER_FAIL(10, "插入订单失败"),
    UPDATE_ORDER_FAIL(11, "更新订单支付状态失败"),
    UNIFIED_ORDER_FAIL(12, "统一下单失败"),
    BIND_PROXY_FAIL(13, "绑定代理失败"),
    PROXY_NOT_EXIST(14, "次代理不存在"),
    HAS_BIND_PROXY(15, "玩家已经绑定代理"),
    UPDATE_USER_ROOM_CARD_FAIL(16, "更新用户房卡失败"),
    UPDATE_PROXY_INCOME_FAIL(17, "更新代理总收益失败"),
    NEED_BIND_PROXY(18, "必须绑定推广后才能够购买房卡，请前往推广菜单绑定推广号");

    public Integer   index;
    public String description;

    private ExceptionEnum(Integer index, String description) {
        this.index = index;
        this.description = description;
    }
    
    public static ExceptionEnum getExceptionEnum(Integer index){
    	for(ExceptionEnum exceptionEnum : ExceptionEnum.values()){
    		if (exceptionEnum.index.equals(index)) {
				return exceptionEnum;
			}
    	}
    	return null;
    }
}
