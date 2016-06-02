package cn.ucai.superwechat.bean;

public class MemberUserAvatar extends UserAvatar {
	private Integer mmemberId;
	private String mmemberUserName;
	private Integer mmemberGroupId;
	private String mmemberGroupHxid;
	private Integer mmemberPermission;
	public MemberUserAvatar() {
		super();
	}
	public MemberUserAvatar(String muserName, String muserPassword, String muserNick, Integer muserUnreadMsgCount,
			Integer mavatarId, String mavatarUserName, String mavatarPath, Integer mavatarType,
			String mavatarLastUpdateTime,Integer mmemberId, String mmemberUserName, Integer mmemberGroupId, String mmemberGroupHxid,
			Integer mmemberPermission) {
		super(muserName, muserPassword, muserNick, muserUnreadMsgCount, mavatarId, mavatarUserName, mavatarPath, mavatarType,
				mavatarLastUpdateTime);
		this.mmemberId = mmemberId;
		this.mmemberUserName = mmemberUserName;
		this.mmemberGroupId = mmemberGroupId;
		this.mmemberGroupHxid = mmemberGroupHxid;
		this.mmemberPermission = mmemberPermission;
	}
	public Integer getMMemberId() {
		return mmemberId;
	}
	public void setMMemberId(Integer mmemberId) {
		this.mmemberId = mmemberId;
	}
	public String getMMemberUserName() {
		return mmemberUserName;
	}
	public void setMMemberUserName(String mmemberUserName) {
		this.mmemberUserName = mmemberUserName;
	}
	public Integer getMMemberGroupId() {
		return mmemberGroupId;
	}
	public void setMMemberGroupId(Integer mmemberGroupId) {
		this.mmemberGroupId = mmemberGroupId;
	}
	public String getMMemberGroupHxid() {
		return mmemberGroupHxid;
	}
	public void setMMemberGroupHxid(String mmemberGroupHxid) {
		this.mmemberGroupHxid = mmemberGroupHxid;
	}
	public Integer getMMemberPermission() {
		return mmemberPermission;
	}
	public void setMMemberPermission(Integer mmemberPermission) {
		this.mmemberPermission = mmemberPermission;
	}
	@Override
	public String toString() {
		return "MemberUserAvatar [mmemberId=" + mmemberId + ", mmemberUserName=" + mmemberUserName + ", mmemberGroupId="
				+ mmemberGroupId + ", mmemberGroupHxid=" + mmemberGroupHxid + ", mmemberPermission=" + mmemberPermission
				+ "]";
	}
	
}