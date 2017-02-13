package cn.newphy.commons.data.generator.menu;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import cn.newphy.commons.data.generator.utils.JAXBUtils;

public class MenuFactory {

	private static MenuList menuList = null;
	private static Map<String, Menu> menuMap = new HashMap<>();
	
	public static MenuList getMenuList() {
		if(menuList == null) {
			init();
		}
		return menuList;
	}
	
	public static Menu getMenu(String code) {
		if(menuList == null) {
			init();
		}
		return menuMap.get(code);
	}
	
	private static void init() {
		synchronized (MenuFactory.class) {
			if(menuList == null) {
				try {
					InputStream in = MenuFactory.class.getResourceAsStream("/config/menu.xml");
					menuList = JAXBUtils.convertToObject(in, MenuList.class);
					if(menuList != null) {
						List<Menu> menus = menuList.getMenus();
						if(menus != null) {
							for (Menu menu : menus) {
								menuMap.put(menu.getCode(), menu);
								if(menu.getSubmenus() != null) {
									for (Menu submenu : menu.getSubmenus()) {
										menuMap.put(submenu.getCode(), submenu);
										submenu.setParent(menu);
									}
								}
							}
						}
					}
				} catch (JAXBException e) {
					throw new IllegalStateException("解析菜单文件出错");
				}
			}
		}
	}
}
