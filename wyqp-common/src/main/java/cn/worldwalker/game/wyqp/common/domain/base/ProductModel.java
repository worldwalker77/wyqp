package cn.worldwalker.game.wyqp.common.domain.base;

public class ProductModel {
	/**商品id**/
	private Integer productId;
	/**房卡数量**/
	private Integer roomCardNum;
	/**价格，单位分**/
	private Integer price;
	
	private String remark;
	
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public Integer getRoomCardNum() {
		return roomCardNum;
	}
	public void setRoomCardNum(Integer roomCardNum) {
		this.roomCardNum = roomCardNum;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
