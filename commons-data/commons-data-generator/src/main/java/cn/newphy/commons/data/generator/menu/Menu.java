package cn.newphy.commons.data.generator.menu;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Menu implements Serializable {
 
	private static final long serialVersionUID = -108971225250266721L;
	
	private String name;
	private String code;
	private String url;
	private String icon;
	private Menu parent;

	private List<Menu> submenus;

	/**
	 * @return the name
	 */
	@XmlAttribute
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the code
	 */
	@XmlAttribute
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the url
	 */
	@XmlAttribute
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the icon
	 */
	@XmlAttribute
	public String getIcon() {
		return icon;
	}

	/**
	 * @param icon
	 *            the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * @return the subMenus
	 */
	@XmlElement(name="menu")
	public List<Menu> getSubmenus() {
		return submenus;
	}

	/**
	 * @param subMenus
	 *            the subMenus to set
	 */
	public void setSubmenus(List<Menu> submenus) {
		this.submenus = submenus;
	}

	/**
	 * @return the parent
	 */
	public Menu getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(Menu parent) {
		this.parent = parent;
	}

	
}
