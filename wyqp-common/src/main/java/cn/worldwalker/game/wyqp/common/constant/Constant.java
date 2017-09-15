package cn.worldwalker.game.wyqp.common.constant;

import cn.worldwalker.game.wyqp.common.utils.CustomizedPropertyConfigurer;

public class Constant {
	
	public final static String curCompany = CustomizedPropertyConfigurer.getContextProperty("cur.company");
	/**roomId与roomInfo的映射*/
	public final static String roomIdRoomInfoMap = curCompany + "_room_id_room_info_map";
	/**roomId与gameType,updateTime的映射*/
	public final static String roomIdGameTypeUpdateTimeMap = curCompany + "_room_id_game_type_update_time_map";
	/**playerId与roomId,gameType的映射*/
	public final static String playerIdRoomIdGameTypeMap = curCompany + "_player_id_room_id_game_type_map";
	/**offline playerId与roomId,gameType,time的映射关系*/
	public final static String offlinePlayerIdRoomIdGameTypeTimeMap = curCompany + "_offline_player_id_romm_id_game_type_time_map";
	
	
	/**ip与此ip上连接数的映射关系*/
	public final static String ipConnectCountMap = curCompany + "_ip_connect_count_map";
	/**房卡操作失败数据list*/
	public final static String roomCardOperationFailList = curCompany + "_room_card_operation_fail_list";
	
	/**请求和返回信息日志打印开关*/
	public final static String logInfoFuse = curCompany + "_log_info_fuse";
	/**登录切换开关*/
	public final static String loginFuse = curCompany + "_login_fuse";
	
	/**本机ip地址*/
	public final static String localIp = CustomizedPropertyConfigurer.getContextProperty("local.ip");
	
	/**牛牛中，庄类型为抢庄的时候，机器ip与房间id，time的list 映射*/
	public final static String ipRoomIdTimeMap = curCompany + "_ip_room_id_time_map_" + localIp;
	

	public static String noticeMsg = "游戏忠告:文明游戏，禁止赌博及其他违法行为  游戏代理及相关咨询加微信：" + CustomizedPropertyConfigurer.getContextProperty("proxy.cus.weixin");
	
	
	/******************金花相关********************/
	/**底注*/
	public final static Integer stakeButtom = 1;
	/**押注的上限*/
	public final static Integer stakeLimit = 10;
	/**跟注次数上限*/
	public final static Integer stakeTimesLimit = 30;
	
	
	/***微信appid,appsecrect**/
	public final static String APPID = CustomizedPropertyConfigurer.getContextProperty("APPID");// 应用号
	public final static String APP_SECRECT = CustomizedPropertyConfigurer.getContextProperty("APP_SECRECT");// 应用密码
	
	
	/**微信登录相关*/
	public static String getWXUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + "ACCESS_TOKEN&openid=OPENID";
	public static String getOpenidAndAccessCode = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + APPID + "&secret=" + APP_SECRECT + "&grant_type=authorization_code&code=CODE";
	
	/**微信支付相关*/
	public final static String MCH_ID = CustomizedPropertyConfigurer.getContextProperty("MCH_ID");// 商户号 xxxx 公众号商户id
	public final static String API_KEY = CustomizedPropertyConfigurer.getContextProperty("API_KEY");// API密钥
	public final static String SIGN_TYPE = "MD5";// 签名加密方式
	public final static String TRADE_TYPE = "APP";// 支付类型
	/**微信支付统一订单接口*/
	public final static String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	/**微信支付回调地址*/
	public final static String WEIXIN_PAY_CALL_BACK_URL = CustomizedPropertyConfigurer.getContextProperty("weixin.pay.call.back.url");
	
}
