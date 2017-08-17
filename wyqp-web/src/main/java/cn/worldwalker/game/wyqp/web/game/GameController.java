package cn.worldwalker.game.wyqp.web.game;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.worldwalker.game.wyqp.common.domain.base.BaseMsg;
import cn.worldwalker.game.wyqp.common.domain.base.BaseRequest;
import cn.worldwalker.game.wyqp.common.exception.ExceptionEnum;
import cn.worldwalker.game.wyqp.common.manager.CommonManager;
import cn.worldwalker.game.wyqp.common.result.Result;
import cn.worldwalker.game.wyqp.common.service.BaseGameService;
import cn.worldwalker.game.wyqp.common.service.RedisOperationService;
import cn.worldwalker.game.wyqp.nn.service.NnGameService;

@Controller
@RequestMapping("game/")
public class GameController {
	
	@Autowired
	private RedisOperationService redisOperationService;
	@Resource(name="commonGameService")
	private BaseGameService commonGameService;
	
	@Resource(name="nnGameService")
	private NnGameService nnGameService;
	@Autowired
	private CommonManager commonManager;
	
	@RequestMapping("login")
	@ResponseBody
	public Result login(String code, String deviceType, HttpServletResponse response, HttpServletRequest request){
		response.addHeader("Access-Control-Allow-Origin", "*");
		Result result = null;
		if (redisOperationService.isLoginFuseOpen()) {
			result = commonGameService.login(code, deviceType, request);
		}else{
			result = commonGameService.login1(code, deviceType, request);
		}
		return result;
	}
	
	@RequestMapping("test")
	@ResponseBody
	public Result getIpByRoomId(String token, Long roomId, HttpServletResponse response){
		Result result = new Result();
		response.addHeader("Access-Control-Allow-Origin", "*");
		nnGameService.test();
		return result;
		
	}
	
	@RequestMapping("notice")
	@ResponseBody
	public Result notice(@RequestBody BaseMsg msg){
		Result result = new Result();
		
		if (null == msg || msg.getNoticeType() == null || StringUtils.isBlank(msg.getNoticeContent())) {
			result.setCode(ExceptionEnum.PARAMS_ERROR.index);
			result.setDesc(ExceptionEnum.PARAMS_ERROR.description);
			return result;
		}
		BaseRequest request = new BaseRequest();
		request.setMsg(msg);
		return null;
		
	}
	
}
