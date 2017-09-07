package cn.worldwalker.game.wyqp.web.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.worldwalker.game.wyqp.common.domain.base.BaseMsg;
import cn.worldwalker.game.wyqp.common.domain.base.BaseRequest;
import cn.worldwalker.game.wyqp.common.domain.base.RecordModel;
import cn.worldwalker.game.wyqp.common.domain.base.UserRecordModel;
import cn.worldwalker.game.wyqp.common.domain.base.VersionModel;
import cn.worldwalker.game.wyqp.common.exception.ExceptionEnum;
import cn.worldwalker.game.wyqp.common.manager.CommonManager;
import cn.worldwalker.game.wyqp.common.result.Result;
import cn.worldwalker.game.wyqp.common.service.BaseGameService;
import cn.worldwalker.game.wyqp.common.service.RedisOperationService;
import cn.worldwalker.game.wyqp.common.utils.JsonUtil;
import cn.worldwalker.game.wyqp.nn.service.NnGameService;

@Controller
@RequestMapping("game/")
public class GameController {
	
	public final static Log log = LogFactory.getLog(GameController.class);
	
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
		try {
			if (redisOperationService.isLoginFuseOpen()) {
				result = commonGameService.login(code, deviceType, request);
			}else{
				result = commonGameService.login1(code, deviceType, request);
			}
		} catch (Exception e) {
			log.error("", e);
			result = new Result();
			result.setCode(1);
			result.setDesc("系统异常");
		}
		return result;
	}
	
	@RequestMapping("version")
	@ResponseBody
	public Map<String, Object> version(HttpServletResponse response){
		VersionModel versionModel = commonManager.getVersion(new VersionModel());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code_url", versionModel.getCodeUrl());
		map.put("update_url", versionModel.getUpdateUrl());
		response.addHeader("Access-Control-Allow-Origin", "*");
		return map;
		
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
	
	@RequestMapping("test")
	@ResponseBody
	public Result test(){
		Result result = new Result();
		UserRecordModel pmodel = new UserRecordModel();
		pmodel.setPlayerId(102384);
		pmodel.setGameType(1);
		List<UserRecordModel> list = commonManager.getUserRecord(pmodel);
		result.setData(list);
		return result;
		
	}
	
}
