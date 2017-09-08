package cn.worldwalker.game.wyqp.common.domain.jh;

import cn.worldwalker.game.wyqp.common.domain.base.BaseMsg;

public class JhMsg extends BaseMsg{
	
	private Integer stakeLimit;
	
	private Integer stakeTimesLimit;
	
	public Integer getStakeLimit() {
		return stakeLimit;
	}
	public void setStakeLimit(Integer stakeLimit) {
		this.stakeLimit = stakeLimit;
	}
	public Integer getStakeTimesLimit() {
		return stakeTimesLimit;
	}
	public void setStakeTimesLimit(Integer stakeTimesLimit) {
		this.stakeTimesLimit = stakeTimesLimit;
	}
	
}
