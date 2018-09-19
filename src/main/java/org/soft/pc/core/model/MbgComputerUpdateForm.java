package org.soft.pc.core.model;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class MbgComputerUpdateForm {
	
	private Integer id;
	
	@NotBlank(message="商品名称不能为空")
	private String trademark;
	
	@NotNull(message="商品价格不能为空")
	private Float price;
	
	private String pic;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTrademark() {
		return trademark;
	}

	public void setTrademark(String trademark) {
		this.trademark = trademark;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

}
