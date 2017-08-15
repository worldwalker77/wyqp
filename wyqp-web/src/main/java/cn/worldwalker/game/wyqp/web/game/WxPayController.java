package cn.worldwalker.game.wyqp.web.game;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.worldwalker.game.wyqp.common.domain.base.UserInfo;
import cn.worldwalker.game.wyqp.common.exception.BusinessException;
import cn.worldwalker.game.wyqp.common.exception.ExceptionEnum;
import cn.worldwalker.game.wyqp.common.result.Result;
import cn.worldwalker.game.wyqp.common.service.BaseGameService;
import cn.worldwalker.game.wyqp.common.service.RedisOperationService;
import cn.worldwalker.game.wyqp.common.utils.IPUtil;

@Controller
@RequestMapping("game/")
public class WxPayController {
	
	@Autowired
	private RedisOperationService redisOperationService;
	
	@Resource(name="commonGameService")
	private BaseGameService commonGameService;
	
	@RequestMapping("unifiedOrder")
	@ResponseBody
	public Result unifiedOrder(String token, Integer productId, HttpServletRequest request){
		Result result = new Result();
		if (StringUtils.isEmpty(token) || productId == null) {
			result.setCode(ExceptionEnum.PARAMS_ERROR.index);
			result.setDesc(ExceptionEnum.PARAMS_ERROR.description);
			return result;
		}
		UserInfo userInfo = redisOperationService.getUserInfo(token);
		if (userInfo == null) {
			result.setCode(ExceptionEnum.NEED_LOGIN.index);
			result.setDesc(ExceptionEnum.NEED_LOGIN.description);
			return result;
		}
		String ip = IPUtil.getRemoteIp(request);
		
		return result;
	}
	
	@RequestMapping("wxPayCallBack")
	@ResponseBody
	public String wxPayCallBack(HttpServletRequest request){
		
		return null;
		
	}
	
	@RequestMapping("notice")
	@ResponseBody
	public Result queryOrder(String outTradeNo, String transactionId){
		Result result = new Result();
		
		return result;
	}
	
}
