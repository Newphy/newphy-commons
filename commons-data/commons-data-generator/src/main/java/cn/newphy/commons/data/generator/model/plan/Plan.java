package cn.newphy.commons.data.generator.model.plan;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.newphy.commons.data.generator.model.IdEntity;

@SuppressWarnings("serial")
public class Plan extends IdEntity {

	private String name;
	private String group;
	private String targetPath;
	private String pkgPath;
	@JsonIgnore
	private String content;
	private String file;

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
	 * @return the targetPath
	 */
	public String getTargetPath() {
		return targetPath;
	}

	/**
	 * @param targetPath the targetPath to set
	 */
	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}

	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * @return the pkgPath
	 */
	public String getPkgPath() {
		return pkgPath;
	}

	/**
	 * @param pkgPath the pkgPath to set
	 */
	public void setPkgPath(String pkgPath) {
		this.pkgPath = pkgPath;
	}
	
	

}
