package com.patsage;

import java.util.ArrayList;

public class GPSearchObject {
	
	private int user_id = 0;
	private int project_id = 0;
	private ArrayList<String> keywords = null;
	private String before_field = null;
	private String before_val = null;
	private String after_field = null;
	private String after_val = null;
	private String assignee = null;
	private String inventor = null;
	private String cpc = null;
	private String patent_office = null;
	private String language = null;
	private String filing_status = null;
	private String patent_type = null;
	private String citing_patent = null;
	private int active = 0;
	private int LinkNum = 0;
	private String search_url = null;
	
	
	
	/**
	 * @return the user_id
	 */
	public int getUser_id() {
		return user_id;
	}




	/**
	 * @param user_id the user_id to set
	 */
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}




	/**
	 * @return the project_id
	 */
	public int getProject_id() {
		return project_id;
	}




	/**
	 * @param project_id the project_id to set
	 */
	public void setProject_id(int project_id) {
		this.project_id = project_id;
	}




	/**
	 * @return the keywords
	 */
	public ArrayList<String> getKeywords() {
		return keywords;
	}




	/**
	 * @param keywords the keywords to set
	 */
	public void setKeywords(ArrayList<String> keywords) {
		this.keywords = keywords;
	}




	/**
	 * @return the before_field
	 */
	public String getBefore_field() {
		return before_field;
	}




	/**
	 * @param before_field the before_field to set
	 */
	public void setBefore_field(String before_field) {
		this.before_field = before_field;
	}




	/**
	 * @return the before_val
	 */
	public String getBefore_val() {
		return before_val;
	}




	/**
	 * @param before_val the before_val to set
	 */
	public void setBefore_val(String before_val) {
		this.before_val = before_val;
	}




	/**
	 * @return the after_field
	 */
	public String getAfter_field() {
		return after_field;
	}




	/**
	 * @param after_field the after_field to set
	 */
	public void setAfter_field(String after_field) {
		this.after_field = after_field;
	}




	/**
	 * @return the after_val
	 */
	public String getAfter_val() {
		return after_val;
	}




	/**
	 * @param after_val the after_val to set
	 */
	public void setAfter_val(String after_val) {
		this.after_val = after_val;
	}




	/**
	 * @return the assignee
	 */
	public String getAssignee() {
		return assignee;
	}




	/**
	 * @param assignee the assignee to set
	 */
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}




	/**
	 * @return the inventor
	 */
	public String getInventor() {
		return inventor;
	}




	/**
	 * @param inventor the inventor to set
	 */
	public void setInventor(String inventor) {
		this.inventor = inventor;
	}




	/**
	 * @return the cpc
	 */
	public String getCpc() {
		return cpc;
	}




	/**
	 * @param cpc the cpc to set
	 */
	public void setCpc(String cpc) {
		this.cpc = cpc;
	}




	/**
	 * @return the patent_office
	 */
	public String getPatent_office() {
		return patent_office;
	}




	/**
	 * @param patent_office the patent_office to set
	 */
	public void setPatent_office(String patent_office) {
		this.patent_office = patent_office;
	}




	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}




	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}




	/**
	 * @return the filing_status
	 */
	public String getFiling_status() {
		return filing_status;
	}




	/**
	 * @param filing_status the filing_status to set
	 */
	public void setFiling_status(String filing_status) {
		this.filing_status = filing_status;
	}




	/**
	 * @return the patent_type
	 */
	public String getPatent_type() {
		return patent_type;
	}




	/**
	 * @param patent_type the patent_type to set
	 */
	public void setPatent_type(String patent_type) {
		this.patent_type = patent_type;
	}




	/**
	 * @return the citing_patent
	 */
	public String getCiting_patent() {
		return citing_patent;
	}




	/**
	 * @param citing_patent the citing_patent to set
	 */
	public void setCiting_patent(String citing_patent) {
		this.citing_patent = citing_patent;
	}




	/**
	 * @return the active
	 */
	public int getActive() {
		return active;
	}




	/**
	 * @param active the active to set
	 */
	public void setActive(int active) {
		this.active = active;
	}




	/**
	 * @return the linkNum
	 */
	public int getLinkNum() {
		return LinkNum;
	}




	/**
	 * @param linkNum the linkNum to set
	 */
	public void setLinkNum(int linkNum) {
		LinkNum = linkNum;
	}




	/**
	 * @return the search_url
	 */
	public String getSearch_url() {
		return search_url;
	}




	/**
	 * @param search_url the search_url to set
	 */
	public void setSearch_url(String search_url) {
		this.search_url = search_url;
	}

}
