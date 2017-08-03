package cn.worldwalker.game.wyqp.common.constant;

public class Constant {
	
	/**roomId与roomInfo的映射*/
	public final static String wyqpRoomIdRoomInfoMap = "wyqp_room_id_room_info_map";
	/**roomId与gameType,updateTime的映射*/
	public final static String wyqpRoomIdGameTypeUpdateTimeMap = "wyqp_room_id_game_type_update_time_map";
	/**playerId与roomId,gameType的映射*/
	public final static String wyqpPlayerIdRoomIdGameTypeMap = "wyqp_player_id_room_id_game_type_map";
	/**offline playerId与roomId,gameType,time的映射关系*/
	public final static String wyqpOfflinePlayerIdRoomIdGameTypeTimeMap = "wyqp_offline_player_id_romm_id_game_type_time_map";
	
	
	/**ip与此ip上连接数的映射关系*/
	public final static String wyqpIpConnectCountMap = "wyqp_ip_connect_count_map";
	/**房卡操作失败数据list*/
	public final static String wyqpRoomCardOperationFailList = "wyqp_room_card_operation_fail_list";
	
	/**请求和返回信息日志打印开关*/
	public final static String wyqpLogInfoFuse = "wyqp_log_info_fuse";
	/**登录切换开关*/
	public final static String wyqpLoginFuse = "wyqp_login_fuse";
	
	 // // 第三方用户唯一凭证
    public static String appId = "wx7681f478b552345a";
    // // 第三方用户唯一凭证密钥
    public static String appSecret = "aa78590d5de26457296c3dd4fc8c1a14";
	
	public static String getWXUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + "ACCESS_TOKEN&openid=OPENID";
	
	public static String getOpenidAndAccessCode = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appId + "&secret=" + appSecret + "&grant_type=authorization_code&code=CODE";

	
}
