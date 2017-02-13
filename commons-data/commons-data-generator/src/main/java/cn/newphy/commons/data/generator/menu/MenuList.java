package cn.newphy.commons.data.generator.menu;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MenuList implements Serializable {
	private static final long serialVersionUID = -4751552916124994455L;
	

	private List<Menu> menus;

	/**
	 * @return the menus
	 */
	@XmlElement(name="menu")
	public List<Menu> getMenus() {
		return menus;
	}

	/**
	 * @param menus
	 *            the menus to set
	 */
	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}

}
