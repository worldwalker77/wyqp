package cn.worldwalker.game.wyqp.common.constant;

import cn.worldwalker.game.wyqp.common.utils.IPUtil;

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
	
	
	/**牛牛中，庄类型为抢庄的时候，机器ip与房间id，time的list 映射*/
	public final static String wyqpIpRoomIdTimeMap = "wyqp_ip_room_id_time_map_" + IPUtil.getLocalIp();
	
	 // // 第三方用户唯一凭证
    public static String appId = "wx8c5ef676dbc373bf";
    // // 第三方用户唯一凭证密钥
    public static String appSecret = "24ad27bef1473bb34c53c5dc9b4f6927";
	
	public static String getWXUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + "ACCESS_TOKEN&openid=OPENID";
	
	public static String getOpenidAndAccessCode = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appId + "&secret=" + appSecret + "&grant_type=authorization_code&code=CODE";

	public static String wyqpNoticeMsg = "游戏忠告:文明游戏，禁止赌博及其他违法行为  游戏代理及相关咨询加微信：suixing1881";
	
	
	/******************金花相关********************/
	/**底注*/
	public final static Integer stakeButtom = 1;
	/**押注的上限*/
	public final static Integer stakeLimit = 10;
	/**跟注次数上限*/
	public final static Integer stakeTimesLimit = 30;
}
