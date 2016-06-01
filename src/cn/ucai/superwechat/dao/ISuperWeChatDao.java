package cn.ucai.superwechat.dao;

import java.util.List;

import cn.ucai.superwechat.bean.GroupAvatar;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.bean.UserAvatarContact;
import cn.ucai.superwechat.pojo.Group;
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
	 * 
	 * @param group
	 * @param affiliationsCount
	 * @return
	 */
	public boolean updateGroupAffiliationsCount(GroupAvatar groupAvatar);
}
