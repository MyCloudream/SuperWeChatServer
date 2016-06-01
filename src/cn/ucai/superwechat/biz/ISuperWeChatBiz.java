package cn.ucai.superwechat.biz;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.pojo.Contact;
import cn.ucai.superwechat.pojo.Group;
import cn.ucai.superwechat.pojo.Location;
import cn.ucai.superwechat.pojo.Member;
import cn.ucai.superwechat.pojo.User;


/**
 * Servlet调用的业务逻辑层接口
 * @author chen
 */
public interface ISuperWeChatBiz {
	/**
	 * 注册用户，主要业务包括在用户表和头像表中添加数据，接收客户端传来的图片
	 * @param user
	 * @param request
	 * @return
	 */
	Result register(User user,HttpServletRequest request);
	
	/**
	 * 解除注册，主要业务包括用户表和头像表中删除数据，并删除服务器本地用户图片
	 * @param userName
	 * @return
	 */
	Result unRegister(String userName);

	/**
	 * 登录
	 * @param user
	 * @return
	 */
	Result login(User user);
	/**
	 * 提供头像数据供客户端下载
	 * @param avatarName
	 * @param response
	 */
	void downloadAvatar(String avatarName,String avatarType, HttpServletResponse response);
	/**
	 * 更新用户或群组头像
	 * @param nameOrHxid
	 * @param avatarType
	 * @param request
	 * @return
	 */
	Result uploadAvatarByNameOrHXID(String nameOrHxid, String avatarType, HttpServletRequest request);

	/**
	 * 更新昵称
	 * @param userName
	 * @param userNick
	 * @return
	 */
	Result updateUserNickByName(String userName, String userNick);

	/**
	 * 更新密码
	 * @param userName
	 * @param userPassword
	 * @return
	 */
	Result updateUserPassowrdByName(String userName, String userPassword);

	/**
	 * 分页下载好友列表
	 * @param userName
	 * @param pageId
	 * @param pageSize
	 * @return
	 */
	Result findContactPagesByUserName(String userName, String pageId, String pageSize);

	/**
	 * 添加好友关系
	 * @param name
	 * @param cname
	 * @return
	 */
	Result addContact(String name, String cname);

	/**
	 * 解除好友关系
	 * @param name
	 * @param cname
	 * @return
	 */
	Result delContact(String name, String cname);
	/**
	 * 根据用户名查找用户
	 * @param userName
	 * @return
	 */
	Result findUserByUserName(String userName);
	/**
	 * 根据用户名或昵称模糊查询用户信息
	 * @param userName
	 * @param userNick
	 * @param pageId
	 * @param pageSize
	 * @return
	 */
	Result findUsersForSearch(String userName, String userNick, String pageId, String pageSize);
	/**
	 * 创建群组
	 * @param group
	 * @param request
	 * @return
	 */
	Result createGroup(Group group, HttpServletRequest request);
	/**
	 * 根据群组id更新群组名称
	 * @param groupId
	 * @param groupNewName
	 * @return
	 */
	Result updateGroupNameByGroupId(String groupId, String groupNewName);
	/**
	 * 添加群组成员
	 * @param userName
	 * @param hxId
	 * @return
	 */
	Result addGroupMember(String userName, String hxId);
}
