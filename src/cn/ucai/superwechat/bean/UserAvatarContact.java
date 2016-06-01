package cn.ucai.superwechat.bean;

import java.util.List;

public class UserAvatarContact{
	private Integer mcontactId;
	private String mcontactUserName;
	private List<UserAvatar> listUserAvatar;
	public UserAvatarContact() {
		super();
	}
	public Integer getMContactId() {
		return mcontactId;
	}
	public void setMContactId(Integer mcontactId) {
		this.mcontactId = mcontactId;
	}
	public String getMContactUserName() {
		return mcontactUserName;
	}
	public void setMContactUserName(String mcontactUserName) {
		this.mcontactUserName = mcontactUserName;
	}
	public List<UserAvatar> getListUserAvatar() {
		return listUserAvatar;
	}
	public void setListUserAvatar(List<UserAvatar> listUserAvatar) {
		this.listUserAvatar = listUserAvatar;
	}
	@Override
	public String toString() {
		return "UserAvatarContact [mcontactId=" + mcontactId + ", mcontactUserName=" + mcontactUserName
				+ ", listUserAvatar=" + listUserAvatar + "]";
	}

}
