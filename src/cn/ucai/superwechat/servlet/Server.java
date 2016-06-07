package cn.ucai.superwechat.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.biz.ISuperWeChatBiz;
import cn.ucai.superwechat.biz.SuperWeChatBiz;
import cn.ucai.superwechat.pojo.Group;
import cn.ucai.superwechat.pojo.Location;
import cn.ucai.superwechat.pojo.User;
import cn.ucai.superwechat.utils.JsonUtil;
import cn.ucai.superwechat.utils.Utils;

@WebServlet("/Server")
public class Server extends HttpServlet {
	private static final long serialVersionUID = -3135817168732302431L;
	ISuperWeChatBiz biz;
	
	public Server(){
		super();
		biz = new SuperWeChatBiz();
	}

	protected void doGet(HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		String requestType = request.getParameter(I.KEY_REQUEST);
		response.setContentType("text/html;charset=utf-8");
		if(requestType==null){
			doIllegalRequest(response);
			return;
		}
		switch(requestType){
		default:
			doIllegalRequest(response);
			break;
		case I.REQUEST_SERVERSTATUS:
			getServerStatus(request, response);
			break;
		case I.REQUEST_UNREGISTER:
			unRegister(request,response);
			break;
		case I.REQUEST_LOGIN:
			login(response, request);
			break;
		case I.REQUEST_DOWNLOAD_AVATAR:
			downloadAvatar(request, response);
			break;
		case I.REQUEST_UPDATE_USER_NICK:
			updateUserNickByName(request,response);
			break;
		case I.REQUEST_UPDATE_USER_PASSWORD:
			updateUserPassowrdByName(request,response);
			break;
		// 下载好友列表，显示全部数据
		case I.REQUEST_DOWNLOAD_CONTACT_ALL_LIST:
			downloadContactAllList(request, response);
			break;
			// 下载好友列表，分页显示的数据
		case I.REQUEST_DOWNLOAD_CONTACT_PAGE_LIST:
			downloadContactPageList(request, response);
			break;
		case I.REQUEST_ADD_CONTACT:
			addContact(request, response);
			break;
		case I.REQUEST_DELETE_CONTACT:
			deleteContact(request, response);
			break;
		case I.REQUEST_FIND_USER:
			findUserByUserName(request, response);
			break;
		case I.REQUEST_FIND_USERS_FOR_SEARCH:
			findUsersForSearch(request, response);
			break;
		case I.REQUEST_UPDATE_GROUP_NAME:
			updateGroupName(request,response);
			break;
		case I.REQUEST_ADD_GROUP_MEMBER:
			addGroupMember(request,response);
			break;
		case I.REQUEST_ADD_GROUP_MEMBERS:
			addGroupMembers(request,response);
			break;
		case I.REQUEST_DOWNLOAD_GROUP_MEMBERS:
			downloadGroupMembersByGroupId(request,response);
			break;
		case I.REQUEST_DOWNLOAD_GROUP_MEMBERS_BY_LIMIT:
			downloadGroupMembersPagesByGroupId(request,response);
			break;
		case I.REQUEST_DOWNLOAD_GROUP_MEMBERS_BY_HXID:
			downloadGroupMembersByHxId(request,response);
			break;
		case I.REQUEST_DOWNLOAD_GROUP_MEMBERS_BY_HXID_LIMIT:
			downloadGroupMembersPagesByHxId(request,response);
			break;
		case I.REQUEST_DELETE_GROUP_MEMBER:
			deleteGroupMember(request,response);
			break;
		case I.REQUEST_DELETE_GROUP_MEMBERS:
			deleteGroupMembers(request,response);
			break;
		case I.REQUEST_DELETE_GROUP:
			deleteGroup(request,response);
			break;
		case I.REQUEST_FIND_GROUP_BY_ID:
			findGroupByGroupId(request,response);
			break;
		case I.REQUEST_FIND_GROUP_BY_HXID:
			findGroupByHxId(request,response);
			break;
		case I.REQUEST_FIND_GROUP_BY_USER_NAME:
			findAllGroupByUserName(request,response);
			break;
		case I.REQUEST_FIND_PUBLIC_GROUPS:
			findPublicGroups(request,response);
			break;
		case I.REQUEST_FIND_GROUP_BY_GROUP_NAME:
			findGroupByGroupName(request,response);
			break;
		case I.REQUEST_UPLOAD_LOCATION:
			uploadLocation(request, response);
			break;
		case I.REQUEST_UPDATE_LOCATION:
			updateLocation(request, response);
			break;
		case I.REQUEST_DOWNLOAD_LOCATION:
			downloadLocation(request, response);
			break;
			/*case I.REQUEST_DOWNLOAD_GROUP_AVATAR:
			downloadGroupAvatar(request, response);
			break;
		case I.REQUEST_DOWNLOAD_CONTACT_ALL_LIST:
			downloadContactAllList(request, response);
			break;
		case I.REQUEST_FIND_USERS:
			findUsersByUserName(request, response);
			break;
		case I.REQUEST_FIND_USERS_BY_NICK:
			findUsersByNick(request, response);
			break;
		case I.REQUEST_ADD_GROUP_MEMBER_BY_USERNAME:
			addGroupMemberByUserName(request,response);
			break;
		case I.REQUEST_DOWNLOAD_GROUP_MEMBERS:
			downloadGroupMembers(request,response);
			break;
		case I.REQUEST_DOWNLOAD_GROUP_MEMBERS_BY_LIMIT:
			downloadGroupMembersByLimit(request,response);
			break;
		case I.REQUEST_DOWNLOAD_GROUP_MEMBERS_BY_HXID:
			downloadGroupMembersByHXID(request,response);
			break;
		case I.REQUEST_DOWNLOAD_GROUP_MEMBERS_BY_HXID_LIMIT:
			downloadGroupMembersByHXIDLimit(request,response);
			break;
		case I.REQUEST_DELETE_GROUP_MEMBERS:
			deleteGroupMembers(request,response);
			break;
		case I.REQUEST_DOWNLOAD_GROUPS:
			downloadAllGroups(request,response);
			break;
		case I.REQUEST_FIND_GROUP_BY_ID:
			findGroupById(request,response);
			break;
		case I.REQUEST_FIND_PUBLIC_GROUP_BY_HXID:
			findPublicGroupByHXID(request,response);
			break;
		case I.REQUEST_UPDATE_USER_NICK:
			updateUserNickByName(request,response);
			break;
		*/
		}
	}

	private void downloadLocation(HttpServletRequest request, HttpServletResponse response) {
		String userName = request.getParameter(I.Location.USER_NAME);
		String pageId = request.getParameter(I.PAGE_ID);
		String pageSize = request.getParameter(I.PAGE_SIZE);
		Result result = biz.downloadLocation(userName,pageId,pageSize);
		JsonUtil.writeJsonToClient(result, response);
	}

	/**
	 * 更新用户地理位置
	 * @param request
	 * @param response
	 */
	private void updateLocation(HttpServletRequest request,
			HttpServletResponse response) {
		String userName = request.getParameter(I.Location.USER_NAME);
		String latitude = request.getParameter(I.Location.LATITUDE);
		String longitude = request.getParameter(I.Location.LONGITUDE);
		String isSearched = request.getParameter(I.Location.IS_SEARCHED);
		Location location = new Location(userName, 
				Double.parseDouble(latitude), Double.parseDouble(longitude), 
				Boolean.parseBoolean(isSearched),System.currentTimeMillis()+"");
		Result result = biz.updateUserLocation(location);
		JsonUtil.writeJsonToClient(result, response);
	}

	/**
	 * 上传用户地理位置
	 * @param request
	 * @param response
	 */
	private void uploadLocation(HttpServletRequest request,
			HttpServletResponse response) {
		String userName = request.getParameter(I.Location.USER_NAME);
		String latitude = request.getParameter(I.Location.LATITUDE);
		String longitude = request.getParameter(I.Location.LONGITUDE);
		Location location = new Location(userName, 
				Double.parseDouble(latitude), Double.parseDouble(longitude), 
				Utils.int2boolean(I.LOCATION_IS_SEARCH_ALLOW),System.currentTimeMillis()+"");
		Result result = biz.uploadUserLocation(location);
		JsonUtil.writeJsonToClient(result, response);
	}

	/**
	 * 根据群组名称，模糊查询群组信息
	 * @param request
	 * @param response
	 */
	private void findGroupByGroupName(HttpServletRequest request, HttpServletResponse response) {
		String groupName = request.getParameter(I.Group.NAME);
		Result result = biz.findGroupByGroupName(groupName);
		JsonUtil.writeJsonToClient(result, response);
	}

	/**
	 * 查找所有的公开群
	 * @param request
	 * @param response
	 */
	private void findPublicGroups(HttpServletRequest request, HttpServletResponse response) {
		String userName = request.getParameter(I.User.USER_NAME);
		int pageId = Integer.parseInt(request.getParameter(I.PAGE_ID));
		int pageSize = Integer.parseInt(request.getParameter(I.PAGE_SIZE));
		Result result = biz.findPublicGroups(userName, pageId, pageSize);
		JsonUtil.writeJsonToClient(result, response);
	}

	private void findAllGroupByUserName(HttpServletRequest request, HttpServletResponse response) {
		String userName = request.getParameter(I.User.USER_NAME);
		Result result = biz.findAllGroupByUserName(userName);
		JsonUtil.writeJsonToClient(result, response);
		
	}

	private void findGroupByHxId(HttpServletRequest request, HttpServletResponse response) {
		String hxId = request.getParameter(I.Group.HX_ID);
		Result result = biz.findGroupByHxId(hxId);
		JsonUtil.writeJsonToClient(result, response);
	}

	private void findGroupByGroupId(HttpServletRequest request, HttpServletResponse response) {
		String groupId = request.getParameter(I.Group.GROUP_ID);
		Result result = biz.findGroupByGroupId(groupId);
		JsonUtil.writeJsonToClient(result, response);
	}

	private void deleteGroup(HttpServletRequest request, HttpServletResponse response) {
		String groupId = request.getParameter(I.Group.GROUP_ID);
		Result result = biz.deleteGroup(groupId);
		JsonUtil.writeJsonToClient(result, response);
	}

	private void deleteGroupMembers(HttpServletRequest request, HttpServletResponse response) {
		String userNames = request.getParameter(I.Member.USER_NAME);
		String groupId = request.getParameter(I.Member.GROUP_ID);
		Result result = biz.deleteGroupMembers(userNames, groupId);
		JsonUtil.writeJsonToClient(result, response);
	}

	private void deleteGroupMember(HttpServletRequest request, HttpServletResponse response) {
		String groupId = request.getParameter(I.Member.GROUP_ID);
		String userName = request.getParameter(I.Member.USER_NAME);
		Result result = biz.deleteGroupMember(userName,groupId);
		JsonUtil.writeJsonToClient(result, response);
	}

	/**
	 * 根据环信id，下载全部群组成员
	 * @param request
	 * @param response
	 */
	private void downloadGroupMembersByHxId(HttpServletRequest request, HttpServletResponse response) {
		String hxId = request.getParameter(I.Member.GROUP_HX_ID);
		Result result = biz.downloadGroupMembersByHxId(hxId);
		JsonUtil.writeJsonToClient(result, response);
	}
	
	/**
	 * 根据环信id，分页下载群组成员
	 * @param request
	 * @param response
	 */
	private void downloadGroupMembersPagesByHxId(HttpServletRequest request, HttpServletResponse response) {
		String hxId = request.getParameter(I.Member.GROUP_HX_ID);
		String pageId = request.getParameter(I.PAGE_ID);
		String pageSize = request.getParameter(I.PAGE_SIZE);
		Result result = biz.downloadGroupMembersPagesByHxId(hxId,pageId,pageSize);
		JsonUtil.writeJsonToClient(result, response);
	}

	private void downloadGroupMembersByGroupId(HttpServletRequest request, HttpServletResponse response) {
		String groupId = request.getParameter(I.Member.GROUP_ID);
		// 下载全部群组成员
		Result result = biz.downloadGroupMembersByGroupId(groupId);
		JsonUtil.writeJsonToClient(result, response);
	}
	
	
	private void downloadGroupMembersPagesByGroupId(HttpServletRequest request, HttpServletResponse response) {
		String groupId = request.getParameter(I.Member.GROUP_ID);
		String pageId = request.getParameter(I.PAGE_ID);
		String pageSize = request.getParameter(I.PAGE_SIZE);
		// 分页下载群组成员
		Result result = biz.downloadGroupMembersPagesByGroupId(groupId,pageId,pageSize);
		JsonUtil.writeJsonToClient(result, response);		
	}

	private void addGroupMembers(HttpServletRequest request, HttpServletResponse response) {
		String userNameArr = request.getParameter(I.Member.USER_NAME);
		String hxId = request.getParameter(I.Member.GROUP_HX_ID);
		Result result = biz.addGroupMembers(userNameArr,hxId);
		JsonUtil.writeJsonToClient(result, response);
	}

	/**
	 * 添加成员信息
	 * 此处应该修改为事务操作！！！！！！！
	 * @param request
	 * @param response
	 */
	private void addGroupMember(HttpServletRequest request, HttpServletResponse response) {
		String userName = request.getParameter(I.Member.USER_NAME);
		String hxId = request.getParameter(I.Member.GROUP_HX_ID);
		Result result = biz.addGroupMember(userName,hxId);
		JsonUtil.writeJsonToClient(result, response);
	}

	private void updateGroupName(HttpServletRequest request, HttpServletResponse response) {
		String groupId = request.getParameter(I.Group.GROUP_ID);
		String groupNewName = request.getParameter(I.Group.NAME);
		Result result = biz.updateGroupNameByGroupId(groupId,groupNewName);
		JsonUtil.writeJsonToClient(result, response);
	}

	/**
	 * 根据用户名或昵称，模糊分页查询用户数据信息
	 * @param request
	 * @param response
	 */
	private void findUsersForSearch(HttpServletRequest request, HttpServletResponse response) {
		String userName = request.getParameter(I.User.USER_NAME);
		String userNick = request.getParameter(I.User.NICK);
		String pageId = request.getParameter(I.PAGE_ID);
		String pageSize = request.getParameter(I.PAGE_SIZE);
		Result result = biz.findUsersForSearch(userName,userNick,pageId,pageSize);
		JsonUtil.writeJsonToClient(result, response);
	}

	/**
	 * 根据用户名精确查找用户信息
	 * @param request
	 * @param response
	 */
	private void findUserByUserName(HttpServletRequest request, HttpServletResponse response) {
		String userName = request.getParameter(I.User.USER_NAME);
		Result result = biz.findUserByUserName(userName);
		JsonUtil.writeJsonToClient(result, response);
	}

	/**
	 * 删除好友关系
	 * @param request
	 * @param response
	 */
	private void deleteContact(HttpServletRequest request, HttpServletResponse response) {
		String name = request.getParameter(I.Contact.USER_NAME);
		String cname = request.getParameter(I.Contact.CU_NAME);
		Result result = biz.delContact(name,cname);
		JsonUtil.writeJsonToClient(result, response);
	}

	/**
	 * 添加好友关系
	 */
	private void addContact(HttpServletRequest request, HttpServletResponse response) {
		String name = request.getParameter(I.Contact.USER_NAME);
		String cname = request.getParameter(I.Contact.CU_NAME);
		Result result = biz.addContact(name,cname);
		JsonUtil.writeJsonToClient(result, response);
	}

	/**
	 * 分页下载好友列表
	 * @param request
	 * @param response
	 */
	private void downloadContactPageList(HttpServletRequest request, HttpServletResponse response) {
		String userName = request.getParameter(I.Contact.USER_NAME);
		String pageId = request.getParameter(I.PAGE_ID);
		String pageSize = request.getParameter(I.PAGE_SIZE);
		Result result = biz.findContactPagesByUserName(userName, pageId, pageSize);
		JsonUtil.writeJsonToClient(result, response);
		
	}
	
	/**
	 * 下载所有好友列表
	 * @param request
	 * @param response
	 */
	private void downloadContactAllList(HttpServletRequest request, HttpServletResponse response) {
		String userName = request.getParameter(I.Contact.USER_NAME);
		Result result = biz.findContactAllByUserName(userName);
		JsonUtil.writeJsonToClient(result, response);
		
	}

	private void updateUserPassowrdByName(HttpServletRequest request, HttpServletResponse response) {
		String userName = request.getParameter(I.User.USER_NAME);
		String userPassword = request.getParameter(I.User.PASSWORD);
		Result result = biz.updateUserPassowrdByName(userName,userPassword);
		JsonUtil.writeJsonToClient(result, response);
	}

	private void updateUserNickByName(HttpServletRequest request, HttpServletResponse response) {
		String userName = request.getParameter(I.User.USER_NAME);
		String userNick = request.getParameter(I.User.NICK);
		Result result = biz.updateUserNickByName(userName,userNick);
		JsonUtil.writeJsonToClient(result, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException,IOException{
		String requestType = request.getParameter(I.KEY_REQUEST);
		System.out.println("doGet requestType="+requestType);
		switch(requestType){
			case I.REQUEST_REGISTER:
				register(request, response);
				break;
			case I.REQUEST_UPLOAD_AVATAR:
				uploadAvatarByNameOrHXID(request,response);
				break;
			// 创建群组
			case I.REQUEST_CREATE_GROUP:
				createGroup(request,response);
				break;
			default:
				doIllegalRequest(response);
		}
	}

	/**
	 * 创建群组
	 * @param request
	 * @param response
	 */
	private void createGroup(HttpServletRequest request, HttpServletResponse response) {
		String hxid = request.getParameter(I.Group.HX_ID);
		String name = request.getParameter(I.Group.NAME);
		String desc = request.getParameter(I.Group.DESCRIPTION);
		String owner = request.getParameter(I.Group.OWNER);
		String isPublic = request.getParameter(I.Group.IS_PUBLIC);
		String allowInvites = request.getParameter(I.Group.ALLOW_INVITES);
		Group group = new Group(hxid, name, desc, owner, System.currentTimeMillis()+"", I.GROUP_MAX_USERS_DEFAULT, I.GROUP_AFFILIATIONS_COUNT_DEFAULT, Boolean.parseBoolean(isPublic), Boolean.parseBoolean(allowInvites));
		Result result = biz.createGroup(group,request);
		JsonUtil.writeJsonToClient(result, response);
	}

	private void uploadAvatarByNameOrHXID(HttpServletRequest request, HttpServletResponse response) {
		String type = request.getParameter(I.AVATAR_TYPE);
		String nameOrHxid = request.getParameter(I.NAME_OR_HXID);
		Result result = biz.uploadAvatarByNameOrHXID(nameOrHxid, type, request);
		JsonUtil.writeJsonToClient(result, response);
	}

	/**
	 * 提供图片数据供客户端下载
	 * @param request
	 * @param response
	 */
	private void downloadAvatar(HttpServletRequest request, HttpServletResponse response) {
		String avatarName = request.getParameter(I.AVATAR_NAME);
		String avatarType = request.getParameter(I.AVATAR_TYPE);
		biz.downloadAvatar(avatarName,avatarType,response);
	}

	/**
	 * 登录
	 * @param response
	 * @param request
	 */
	private void login(HttpServletResponse response, HttpServletRequest request) {
		String userName = request.getParameter(I.User.USER_NAME);
		String password = request.getParameter(I.User.PASSWORD);
		User user = new User();
		user.setMUserName(userName);
		user.setMUserPassword(password);
		Result result = biz.login(user);
		JsonUtil.writeJsonToClient(result, response);
	}

	/**
	 * 解除注册
	 * @param request
	 * @param response
	 */
	private void unRegister(HttpServletRequest request, HttpServletResponse response) {
		// 接收用户参数
		String userName = request.getParameter(I.User.USER_NAME);
		Result result = biz.unRegister(userName);
		JsonUtil.writeJsonToClient(result, response);
	}

	/**
	 * 处理非法请求
	 * @param response
	 */
	private void doIllegalRequest(HttpServletResponse response) {
		Result result = new Result();
		result.setRetCode(I.MSG_ILLEGAL_REQUEST);
		result.setRetMsg(false);
		JsonUtil.writeJsonToClient(result,response);
	}
	
	/**
	 * 获得服务端连接状态，并返回给客户端
	 * @param request
	 * @param response
	 */
	private void getServerStatus(HttpServletRequest request,
			HttpServletResponse response){
		Result result = new Result(true,I.MSG_CONNECTION_SUCCESS);
		JsonUtil.writeJsonToClient(result, response);
	}
	
	/**
	 * 注册账户
	 * @param request
	 * @param response
	 */
	private void register(HttpServletRequest request, HttpServletResponse response) {
		// 步骤1-从request中获取userName、nick、password
		String userName = request.getParameter(I.User.USER_NAME);
		String nick = request.getParameter(I.User.NICK);
		String password = request.getParameter(I.User.PASSWORD);
		User user = new User(userName,password,nick);
		Result result = biz.register(user,request);
		JsonUtil.writeJsonToClient(result, response);
	}
}
