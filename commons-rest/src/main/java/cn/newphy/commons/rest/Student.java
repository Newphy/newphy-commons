package cn.newphy.commons.rest;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Student {

	private String name;
	private int age;
	private BigDecimal amount;
	private Map<String, Integer> subjectScore;
	private Map<String, Student> partners = new LinkedHashMap<>();
	private List<Student> classmates;

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
	 * @return the age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @param age
	 *            the age to set
	 */
	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * @return the subjectScore
	 */
	public Map<String, Integer> getSubjectScore() {
		return subjectScore;
	}

	/**
	 * @param subjectScore
	 *            the subjectScore to set
	 */
	public void setSubjectScore(Map<String, Integer> subjectScore) {
		this.subjectScore = subjectScore;
	}

	/**
	 * @return the classmates
	 */
	public List<Student> getClassmates() {
		return classmates;
	}

	/**
	 * @param classmates
	 *            the classmates to set
	 */
	public void setClassmates(List<Student> classmates) {
		this.classmates = classmates;
	}

	/**
	 * @return the partners
	 */
	public Map<String, Student> getPartners() {
		return partners;
	}

	/**
	 * @param partners the partners to set
	 */
	public void setPartners(Map<String, Student> partners) {
		this.partners = partners;
	}
	
	

}
