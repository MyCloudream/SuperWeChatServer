package cn.ucai.superwechat.pojo;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	private String muserName;
	private String muserPassword;
	private String muserNick;
	private Integer muserUnreadMsgCount;
	public User(){
		super();
	}
	
	public User(String muserName,String muserPassword,String muserNick){
		this.muserName = muserName;
		this.muserPassword = muserPassword;
		this.muserNick = muserNick;
	}
	public User(String muserName,String muserPassword,String muserNick,Integer muserUnreadMsgCount){
		this.muserName = muserName;
		this.muserPassword = muserPassword;
		this.muserNick = muserNick;
		this.muserUnreadMsgCount = muserUnreadMsgCount;
	}
	
	public String getMUserName() {
 		return this.muserName;
 	}
 	
	public void setMUserName(String muserName){
		this.muserName = muserName;
	}
 	
 	public String getMUserPassword() {
 		return this.muserPassword;
 	}
 	
	public void setMUserPassword(String muserPassword){
		this.muserPassword = muserPassword;
	}
 	
 	public String getMUserNick() {
 		return this.muserNick;
 	}
 	
	public void setMUserNick(String muserNick){
		this.muserNick = muserNick;
	}
 	
 	public Integer getMUserUnreadMsgCount() {
 		return this.muserUnreadMsgCount;
 	}
 	
	public void setMUserUnreadMsgCount(Integer muserUnreadMsgCount){
		this.muserUnreadMsgCount = muserUnreadMsgCount;
	}
 	
 	 	@Override
 	public String toString() {
 		return "User ["
 	 	+ "this.muserName=" + muserName
	 	+ "this.muserPassword=" + muserPassword
	 	+ "this.muserNick=" + muserNick
	 	+ "this.muserUnreadMsgCount=" + muserUnreadMsgCount
		;
 	}
 
}