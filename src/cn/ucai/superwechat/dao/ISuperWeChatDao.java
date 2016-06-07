package cn.ucai.superwechat.dao;

import java.util.List;

import cn.ucai.superwechat.bean.GroupAvatar;
import cn.ucai.superwechat.bean.LocationUserAvatar;
import cn.ucai.superwechat.bean.MemberUserAvatar;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.bean.UserAvatarContact;
import cn.ucai.superwechat.pojo.Group;
import cn.ucai.superwechat.pojo.Location;
import cn.ucai.superwechat.pojo.Member;
import cn.ucai.superwechat.pojo.User;


/**
 * 数据访问层接口文件
 *
 */
public interface ISuperWeChatDao {
	/**
	 * 添加用户
	 * @param user
	 * @return
	 */
	public boolean addUser(User user);
	/**
	 * 根据用户名查找用户
	 * @param userName
	 * @return
	 */
	public User findUserByUserName(String userName);
	/**
	 * 根据用户名删除用户
	 * @param userName
	 * @return
	 */
	public boolean deleteUser(String userName);
	/**
	 * 根据用户名，查找用户和头像信息
	 * @param mUserName
	 * @return
	 */
	public UserAvatar findUserAvatarByUserName(String userName);
	/**
	 * 更新用户昵称
	 * @param userName
	 * @param userNick
	 * @return
	 */
	public boolean updateNickByName(String userName, String userNick);
	/**
	 * 更新用户密码
	 * @param userName
	 * @param userPassword
	 * @return
	 */
	public boolean updatePasswordByName(String userName, String userPassword);
	/**
	 * 分页查询联系人信息
	 * @param userName
	 * @param pageId
	 * @param pageSize
	 * @return
	 */
	public UserAvatarContact findContactPagesByUserName(String userName, String pageId, String pageSize);
	/**
	 * 查询全部联系人信息
	 * @param userName
	 * @return
	 */
	public UserAvatarContact findContactAllByUserName(String userName);
	/**
	 * 查找cname是否已经是name的好友
	 * @param name
	 * @param cname
	 * @return
	 */
	public boolean findContact(String name, String cname);
	/**
	 * 创建好友关系
	 * @param name
	 * @param cname
	 * @return
	 */
	public boolean addContact(String name, String cname);
	/**
	 * 删除好友关系
	 * @param name
	 * @param cname
	 * @return
	 */
	public boolean delContact(String name, String cname);
	/**
	 * 根据用户名或密码模糊查询用户信息
	 * @param userName
	 * @param userNick
	 * @param pageId
	 * @param pageSize
	 * @return
	 */
	public List<UserAvatar> findUsersForSearch(String userName, String userNick, String pageId, String pageSize);
	/**
	 * 查找制定hxid的群组是否存在
	 * @param mGroupHxid
	 * @return
	 */
	public Group findGroupByHxid(String mGroupHxid);
	/**
	 * 创建群组
	 * @param group
	 * @return
	 */
	public boolean addGroupAndGroupOwnerMember(Group group);
	/**
	 * 根据群组id查找群组是否存在
	 * @param groupId
	 * @return
	 */
	public GroupAvatar findGroupAvatarByGroupId(String groupId);
	/**
	 * 根据环信id查找群组是否存在
	 * @param groupId
	 * @return
	 */
	public GroupAvatar findGroupAvatarByHxId(String hxId);
	/**
	 * 根据群组id更新群组名称
	 * @param groupId
	 * @param groupNewName
	 * @return
	 */
	public boolean updateGroupNameByGroupId(String groupId, String groupNewName);
	/**
	 * 添加群组成员
	 * @param member
	 * @return
	 */
	public boolean addGroupMember(Member member);
	/**
	 * 修改群组人数
	 * @param group
	 * @param affiliationsCount
	 * @return
	 */
	public boolean updateGroupAffiliationsCount(GroupAvatar groupAvatar);
	/**
	 * 添加多个群组成员
	 * @param memberArr
	 * @return
	 */
	public boolean addGroupMembers(Member[] memberArr);
	/**
	 * 根据群组id，下载群组成员
	 * @param groupId
	 * @return
	 */
	public List<MemberUserAvatar> downloadGroupMembersByGroupId(String groupId);
	/**
	 * 根据群组id，分页下载群组成员
	 * @param groupId
	 * @param pageId
	 * @param pageSize
	 * @return
	 */
	public List<MemberUserAvatar> downloadGroupMembersPagesByGroupId(String groupId, String pageId, String pageSize);
	/**
	 * 根据环信id，下载全部群组成员
	 * @param groupId
	 * @param pageId
	 * @param pageSize
	 * @return
	 */
	public List<MemberUserAvatar> downloadGroupMembersByHxId(String hxId);
	/**
	 * 根据环信id，分页下载群组成员
	 * @param groupId
	 * @param pageId
	 * @param pageSize
	 * @return
	 */
	public List<MemberUserAvatar> downloadGroupMembersPagesByHxId(String hxId, String pageId, String pageSize);
	/**
	 * 在群组中删除某指定姓名的用户
	 * @param userName
	 * @param groupId
	 * @return
	 */
	public boolean delGroupMember(String userName, String groupId);
	/**
	 * 删除多个群组成员
	 * @param userNames
	 * @param groupId
	 * @return
	 */
	public boolean delGroupMembers(String userNames, String groupId);
	/**
	 * 删除群组，同时删除成员表中的该群组的所有成员
	 * @param groupId
	 * @return
	 */
	public boolean deleteGroupAndMembers(String groupId);
	/**
	 * 查询某个用户所在的所有群
	 * @param userName
	 * @return
	 */
	public List<GroupAvatar> findAllGroupByUserName(String userName);
	/**
	 * 查找所有的公开群，不包括当前用户已经所在的群
	 * @param userName
	 * @param pageId
	 * @param pageSize
	 * @return
	 */
	public List<GroupAvatar> findPublicGroups(String userName, int pageId, int pageSize);
	/**
	 * 根据群组名称，模糊查询所有匹配的群组
	 * @param groupName
	 * @return
	 */
	public List<GroupAvatar> findGroupByGroupName(String groupName);
	/**
	 * 上传用户地理位置信息
	 * @param location
	 * @return
	 */
	public boolean uploadUserLocation(Location location);
	/**
	 * 更新用户的地理位置信息
	 * @param location
	 * @return
	 */
	public boolean updateUserLocation(Location location);
	/**
	 * 查询距离最近的人
	 * @param userName
	 * @param pageId
	 * @param pageSize
	 * @return
	 */
	public List<LocationUserAvatar> downloadLocation(String userName, String pageId, String pageSize);

}
