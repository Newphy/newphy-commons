package cn.newphy.commons.data.generator.model.ds;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.newphy.commons.data.generator.model.IdEntity;

@SuppressWarnings("serial")
public class Ds extends IdEntity {


	private String driverClass;
	private String name;
	private String url;
	private String user;
	private String passwd;
	
	private List<Schema> schemas;

	@JsonIgnore
	public DSType getDSType() {
		if(url != null && url.toLowerCase().contains("mysql")) {
			return DSType.MYSQL;
		}
		return DSType.UNKNOWN;
	}

	
	/**
	 * @return the driverClass
	 */
	public String getDriverClass() {
		return driverClass;
	}

	/**
	 * @param driverClass the driverClass to set
	 */
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	/**
	 * @return the name
	 */
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
	 * @return the url
	 */
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
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}



	/**
	 * @return the passwd
	 */
	public String getPasswd() {
		return passwd;
	}

	/**
	 * @param passwd the passwd to set
	 */
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	/**
	 * @return the schemas
	 */
	public List<Schema> getSchemas() {
		return schemas;
	}

	/**
	 * @param schemas the schemas to set
	 */
	public void setSchemas(List<Schema> schemas) {
		this.schemas = schemas;
	}

	
}
