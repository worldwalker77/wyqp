package cn.worldwalker.game.wyqp.common.domain.nn;

import cn.worldwalker.game.wyqp.common.domain.base.BasePlayerInfo;

public class NnPlayerInfo extends BasePlayerInfo{
	/**押注分数 1,2,3,4,5*/
	private Integer stakeScore;
	/**玩家抢庄时间*/
	private Long robBankerTime;
	
	public Integer getStakeScore() {
		return stakeScore;
	}

	public void setStakeScore(Integer stakeScore) {
		this.stakeScore = stakeScore;
	}

	public Long getRobBankerTime() {
		return robBankerTime;
	}

	public void setRobBankerTime(Long robBankerTime) {
		this.robBankerTime = robBankerTime;
	}

}
