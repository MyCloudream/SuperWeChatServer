package cn.ucai.superwechat.biz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.ucai.superwechat.bean.GroupAvatar;
import cn.ucai.superwechat.bean.LocationUserAvatar;
import cn.ucai.superwechat.bean.MemberUserAvatar;
import cn.ucai.superwechat.bean.Pager;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.dao.ISuperWeChatDao;
import cn.ucai.superwechat.dao.SuperWeChatDao;
import cn.ucai.superwechat.pojo.Group;
import cn.ucai.superwechat.pojo.Location;
import cn.ucai.superwechat.pojo.User;
import cn.ucai.superwechat.servlet.I;
import cn.ucai.superwechat.utils.PropertiesUtil;

public class SuperWeChatBiz implements ISuperWeChatBiz {
	private ISuperWeChatDao dao;
	public SuperWeChatBiz(){
		dao = new SuperWeChatDao();
	}

	@Override
	public Result register(User user,HttpServletRequest request){
		Result result = null;
		User u = dao.findUserByUserName(user.getMUserName());
		if(u==null){
			if(uploadAvatar(user.getMUserName(),I.AVATAR_TYPE_USER_PATH,request)){// 头像上传成功
				if(dao.addUser(user)){// 注册成功
					result = new Result(true,I.MSG_SUCCESS);
				}else{// 注册失败
					result = new Result(false,I.MSG_REGISTER_FAIL);
					// 删除本地图片
					deleteAvatar(PropertiesUtil.getValue("avatar_path","path.properties")+I.AVATAR_TYPE_USER_PATH,user.getMUserName());
				}
			}else{
				result = new Result(false,I.MSG_UPLOAD_AVATAR_FAIL);
			}
		}else{// 用户已存在
			result = new Result(false,I.MSG_REGISTER_USERNAME_EXISTS);
		}
		return result;
	}

	/**
	 * 删除头像
	 * @param name
	 * @param path
	 * @return
	 */
	private boolean deleteAvatar(String name,String path){
		File file = new File(path,name);
		if(file.exists()){
			file.delete();
			return true;
		}
		return false;
	}
	
	/**
	 * 上传头像
	 * @param name
	 * @param type
	 * @param request
	 * @return
	 */
	public boolean uploadAvatar(String name, String type, HttpServletRequest request) {
		String path;
		switch (type) {
		case I.AVATAR_TYPE_USER_PATH:
			path = PropertiesUtil.getValue("avatar_path","path.properties") + I.AVATAR_TYPE_USER_PATH + I.BACKSLASH;
			break;
		case I.AVATAR_TYPE_GROUP_PATH:
		default:
			path = PropertiesUtil.getValue("avatar_path","path.properties") + I.AVATAR_TYPE_GROUP_PATH + I.BACKSLASH;
			break;
		}
		String fileName = name + I.AVATAR_SUFFIX_JPG;
		System.out.println("头像上传路径:" + path + fileName);
		File file = new File(path,fileName);
		FileOutputStream fos = null;
		InputStream is = null;
		try {
			fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024 * 8];
			int len;
			is = request.getInputStream();
			while ((len = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally{
			closeStream(is);
			closeStream(fos);
		}
		return true;
	}
	
	@Override
	public Result unRegister(String userName) {
		Result result = new Result();
		boolean isDelete = dao.deleteUser(userName);
		if(isDelete){
			String path = PropertiesUtil.getValue("avatar_path","path.properties") + I.AVATAR_TYPE_USER_PATH 
					+ I.BACKSLASH + userName + I.AVATAR_SUFFIX_JPG;
			File file = new File(path);
			if (file.exists()){
				file.delete();
				result.setRetMsg(true);
				result.setRetCode(I.MSG_SUCCESS);
			}
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_UNREGISTER_FAIL);
		}
		return result;
	}
	
	/**
	 * 关闭文件输出流
	 * @param fos
	 */
	private void closeStream(OutputStream fos) {
		try {
			if(fos!=null){
				fos.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭文件输入流
	 * @param fis
	 */
	private void closeStream(InputStream fis) {
		try {
			if(fis!=null){
				fis.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Result login(User user) {
		Result result = new Result();
		User u = dao.findUserByUserName(user.getMUserName());
		if(u!=null){
			if(u.getMUserPassword().equals(user.getMUserPassword())){
				result.setRetMsg(true);
				result.setRetCode(I.MSG_SUCCESS);
				UserAvatar ua = dao.findUserAvatarByUserName(user.getMUserName());
				result.setRetData(ua);
			}else{
				result.setRetMsg(false);
				result.setRetCode(I.MSG_LOGIN_ERROR_PASSWORD);
			}
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_LOGIN_UNKNOW_USER);
		}
		return result;
	}

	@Override
	public void downloadAvatar(String avatarName,String avatarType,HttpServletResponse response) {
		File file = null;
		if(avatarType.equals(I.AVATAR_TYPE_USER_PATH)){
			file = new File(PropertiesUtil.getValue("avatar_path","path.properties") + I.AVATAR_TYPE_USER_PATH 
					+ I.BACKSLASH + avatarName + I.AVATAR_SUFFIX_JPG);
		}else if(avatarType.equals(I.AVATAR_TYPE_GROUP_PATH)){
			file = new File(PropertiesUtil.getValue("avatar_path","path.properties") + I.AVATAR_TYPE_GROUP_PATH 
					+ I.BACKSLASH + avatarName + I.AVATAR_SUFFIX_JPG);
		}
		System.out.println("---"+file);
		if (!file.exists()) {
			System.out.println("头像不存在");
			return;
		}
		FileInputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(file);
			response.setContentType("image/jpeg");
			out = response.getOutputStream();
			int len;
			byte[] buffer = new byte[1024];
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			System.out.println("头像下载完毕");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeStream(in);
			closeStream(out);
		}
	}

	@Override
	public Result uploadAvatarByNameOrHXID(String nameOrHxid, String avatarType, HttpServletRequest request) {
		Result result = new Result();
		if(uploadAvatar(nameOrHxid,avatarType,request)){
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_UPLOAD_AVATAR_FAIL);
		}
		return result;
	}

	@Override
	public Result updateUserNickByName(String userName, String userNick) {
		Result result = new Result();
		UserAvatar ua = dao.findUserAvatarByUserName(userName);
		if(ua==null){
			result.setRetMsg(false);
			result.setRetCode(I.MSG_LOGIN_UNKNOW_USER);
		}else{
			if(dao.updateNickByName(userName,userNick)){
				result.setRetMsg(true);
				ua.setMUserNick(userNick);
				result.setRetData(ua);
				result.setRetCode(I.MSG_SUCCESS);
			}else{
				result.setRetMsg(false);
				result.setRetCode(I.MSG_USER_UPDATE_NICK_FAIL);
			}
		}
		return result;
	}

	@Override
	public Result updateUserPassowrdByName(String userName, String userPassword) {
		Result result = new Result();
		UserAvatar ua = dao.findUserAvatarByUserName(userName);
		if(ua==null){
			result.setRetMsg(false);
			result.setRetCode(I.MSG_LOGIN_UNKNOW_USER);
		}else{
			if(dao.updatePasswordByName(userName,userPassword)){
				result.setRetMsg(true);
//				ua.setMUserPassword(userPassword);
				result.setRetData(ua);
				result.setRetCode(I.MSG_SUCCESS);
			}else{
				result.setRetMsg(false);
				result.setRetCode(I.MSG_USER_UPDATE_PASSWORD_FAIL);
			}
		}
		return result;
	}

	@Override
	public Result findContactPagesByUserName(String userName, String pageId, String pageSize) {
		Result result = new Result();
		List<UserAvatar> listUserAvatar = dao.findContactPagesByUserName(userName, pageId, pageSize);
		if(listUserAvatar!=null){
			Pager pager = getPager(pageId, listUserAvatar);
			result.setRetData(pager);
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_GET_CONTACT_PAGES_FAIL);
		}
		return result;
	}

	/**
	 * 添加好友关系：
	 * 1、如果已经存在好友关系，则返回相关信息
	 * 2、不存在关系，则建立关系
	 * 3、建立失败，返回失败信息，建立成功，返回被添加用户的信息
	 */
	@Override
	public Result addContact(String name, String cname) {
		Result result = new Result();
		boolean isContact = dao.findContact(name,cname);
		if(isContact){
			result.setRetMsg(false);
			result.setRetCode(I.MSG_CONTACT_FIRENDED);
		}else{
			boolean addContact = dao.addContact(name,cname);
			if(addContact){
				result.setRetMsg(true);
				result.setRetCode(I.MSG_SUCCESS);
				UserAvatar ua = dao.findUserAvatarByUserName(cname);
				result.setRetData(ua);
			}else{
				result.setRetMsg(false);
				result.setRetCode(I.MSG_CONTACT_ADD_FAIL);
			}
		}
		return result;
	}

	/**
	 * 删除好友关系
	 * 1、删除成功，返回true
	 * 2、删除失败，返回false即可
	 */
	@Override
	public Result delContact(String name, String cname) {
		Result result = new Result();
		boolean delContact = dao.delContact(name,cname);
		if(delContact){
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_CONTACT_DEL_FAIL);
		}
		return result;
	}

	/**
	 * 根据用户名查找用户
	 * 1、查找到了则返回成功和查找到的用户信息
	 * 2、查找不到则返回false
	 */
	@Override
	public Result findUserByUserName(String userName) {
		Result result = new Result();
		UserAvatar ua = dao.findUserAvatarByUserName(userName);
		if(ua==null){
			result.setRetMsg(false);
			result.setRetCode(I.MSG_LOGIN_UNKNOW_USER);
		}else{
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
			result.setRetData(ua);
		}
		return result;
	}

	/**
	 * 根据用户名或昵称，模糊分页查询数据信息
	 */
	@Override
	public Result findUsersForSearch(String userName, String userNick, String pageId, String pageSize) {
		Result result = new Result();
		List<UserAvatar> uaList = dao.findUsersForSearch(userName, userNick, pageId, pageSize);
		if(uaList==null){
			result.setRetMsg(false);
			result.setRetCode(I.MSG_LOGIN_UNKNOW_USER);
		}else{
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
			Pager pager = getPager(pageId, uaList);
			result.setRetData(pager);
		}
		return result;
	}

	/**
	 * 创建群组
	 */
	@Override
	public Result createGroup(Group group, HttpServletRequest request) {
		Result result = null;
		Group groupFind = dao.findGroupByHxid(group.getMGroupHxid());
		if(groupFind==null){
			if(uploadAvatar(group.getMGroupHxid(),I.AVATAR_TYPE_GROUP_PATH,request)){// 头像上传成功
				if(dao.addGroupAndGroupOwnerMember(group)){// 添加群组成功
					result = new Result(true,I.MSG_SUCCESS);
				}else{// 添加群组失败
					result = new Result(false,I.MSG_GROUP_CREATE_FAIL);
					// 删除本地图片
					deleteAvatar(PropertiesUtil.getValue("avatar_path","path.properties")+I.AVATAR_TYPE_GROUP_PATH,group.getMGroupHxid());
				}
			}else{
				result = new Result(false,I.MSG_UPLOAD_AVATAR_FAIL);
			}
		}else{// 群组已存在
			result = new Result(false,I.MSG_GROUP_HXID_EXISTS);
		}
		return result;
	}

	@Override
	public Result updateGroupNameByGroupId(String groupId, String groupNewName) {
		Result result = new Result();
		GroupAvatar ga = dao.findGroupAvatarByGroupId(groupId);
		if(ga==null){
			result.setRetMsg(false);
			result.setRetCode(I.MSG_GROUP_UNKONW);
		}else{
			if(dao.updateGroupNameByGroupId(groupId,groupNewName)){
				result.setRetMsg(true);
				ga.setMGroupName(groupNewName);
				result.setRetData(ga);
				result.setRetCode(I.MSG_SUCCESS);
			}else{
				result.setRetMsg(false);
				result.setRetCode(I.MSG_USER_UPDATE_NICK_FAIL);
			}
		}
		return result;
	}

	/**
	 * @param userName
	 * @param hxId
	 * @return
	 */
	@Override
	public Result addGroupMember(String userName, String hxId) {
		Result result = new Result();
		GroupAvatar groupAvatar = dao.findGroupAvatarByHxId(hxId);
		if(groupAvatar!=null){
			if(dao.addGroupMemberAndUpdateGroupAffiliationsCount(userName,groupAvatar)){// 添加成员成功
				result.setRetMsg(true);
				result.setRetData(groupAvatar);
				result.setRetCode(I.MSG_SUCCESS);
				return result;
			}
		}
		result.setRetCode(I.MSG_GROUP_ADD_MEMBER_FAIL);
		result.setRetMsg(false);
		return result;
	}

	@Override
	public Result addGroupMembers(String userNameArr, String hxId) {
		Result result = new Result();
		GroupAvatar groupAvatar = dao.findGroupAvatarByHxId(hxId);
		if(groupAvatar!=null){
			if(dao.addGroupMembersAndUpdateGroupAffiliationsCount(userNameArr,groupAvatar)){// 添加成员成功
				result.setRetMsg(true);
				result.setRetData(groupAvatar);
				result.setRetCode(I.MSG_SUCCESS);
				return result;
			}
		}
		result.setRetCode(I.MSG_GROUP_ADD_MEMBER_FAIL);
		result.setRetMsg(false);
		return result;
	}

	@Override
	public Result downloadGroupMembersByGroupId(String groupId) {
		Result result = new Result();
		List<MemberUserAvatar> listMemberUserAvatar = dao.downloadGroupMembersByGroupId(groupId);
		if(listMemberUserAvatar!=null){
			result.setRetData(listMemberUserAvatar);
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_GROUP_GET_MEMBERS_FAIL);
		}
		return result;
	}
	
	@Override
	public Result downloadGroupMembersPagesByGroupId(String groupId, String pageId, String pageSize) {
		Result result = new Result();
		List<MemberUserAvatar> listMemberUserAvatar = dao.downloadGroupMembersPagesByGroupId(groupId, pageId, pageSize);
		if(listMemberUserAvatar!=null){
			Pager pager = getPager(pageId, listMemberUserAvatar);
			result.setRetData(pager);
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_GROUP_GET_MEMBERS_FAIL);
		}
		return result;
	}
	
	/**
	 * 将分页查询得到的内容封装为Pager类
	 * @param pageId
	 * @param list
	 * @param maxRecord
	 * @return
	 */
	public Pager getPager(String pageId,List<?> list){
		Pager pager = new Pager();
		pager.setCurrentPage(Integer.parseInt(pageId));
		pager.setMaxRecord(list.size());
		pager.setPageData(list);
		return pager;
	}

	@Override
	public Result downloadGroupMembersByHxId(String hxId) {
		Result result = new Result();
		List<MemberUserAvatar> listMemberUserAvatar = dao.downloadGroupMembersByHxId(hxId);
		if(listMemberUserAvatar!=null){
			result.setRetData(listMemberUserAvatar);
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_GROUP_GET_MEMBERS_FAIL);
		}
		return result;
	}
	
	@Override   //downloadGroupMembersPagesByHxId
	public Result downloadGroupMembersPagesByHxId(String hxId, String pageId, String pageSize) {
		Result result = new Result();
		List<MemberUserAvatar> listMemberUserAvatar = dao.downloadGroupMembersPagesByHxId(hxId, pageId, pageSize);
		if(listMemberUserAvatar!=null){
			Pager pager = getPager(pageId, listMemberUserAvatar);
			result.setRetData(pager);
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_GROUP_GET_MEMBERS_FAIL);
		}
		return result;
	}

	@Override
	public Result deleteGroupMember(String userName, String groupId) {
		Result result = new Result();
		GroupAvatar groupAvatar = dao.findGroupAvatarByGroupId(groupId);
		if(groupAvatar!=null){
			if(dao.delGroupMemberAndUpdateGroupAffiliationsCount(userName,groupAvatar)){// 删除群成员
				result.setRetMsg(true);
				result.setRetData(groupAvatar);
				result.setRetCode(I.MSG_SUCCESS);
				return result;
			}
		}
		result.setRetCode(I.MSG_GROUP_DELETE_MEMBER_FAIL);
		result.setRetMsg(false);
		return result;
	}

	@Override
	public Result deleteGroupMembers(String userNames, String groupId) {
		GroupAvatar groupAvatar = dao.findGroupAvatarByGroupId(groupId);
		Result result = new Result();
		if(groupAvatar!=null){
			if(dao.delGroupMembersAndUpdateGroupAffiliationsCount(userNames, groupAvatar)){
				result.setRetMsg(true);
				result.setRetCode(I.MSG_SUCCESS);
				return result;
			}
		}
		result.setRetCode(I.MSG_GROUP_DELETE_MEMBERS_FAIL);
		result.setRetMsg(false);
		return result;
	}

	@Override
	public Result deleteGroup(String groupId) {
		Result result = new Result();
		if(dao.deleteGroupAndMembers(groupId)){
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_GROUP_DELETE_FAIL);
		}
		return result;
	}

	@Override
	public Result findGroupByGroupId(String groupId) {
		Result result = new Result();
		GroupAvatar ga = dao.findGroupAvatarByGroupId(groupId);
		if(ga!=null){
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
			result.setRetData(ga);
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_GROUP_FIND_BY_GOURP_ID_FAIL);
		}
		return result;
	}

	@Override
	public Result findGroupByHxId(String hxId) {
		Result result = new Result();
		GroupAvatar ga = dao.findGroupAvatarByHxId(hxId);
		if(ga!=null){
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
			result.setRetData(ga);
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_GROUP_FIND_BY_HX_ID_FAIL);
		}
		return result;
	}

	/**
	 * 查找某一指定用户的所有的群（所在的所有群）
	 */
	@Override
	public Result findAllGroupByUserName(String userName) {
		Result result = new Result();
		List<GroupAvatar> listGroupAdater = dao.findAllGroupByUserName(userName);
		if(listGroupAdater!=null){
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
			result.setRetData(listGroupAdater);
		}else{
			result.setRetCode(I.MSG_GROUP_FIND_BY_USER_NAME_FAIL);
			result.setRetMsg(false);
		}
		return result;
	}

	/**
	 * 查找所有的公开群，不包括当前用户已经所在的群
	 */
	@Override
	public Result findPublicGroups(String userName, int pageId, int pageSize) {
		Result result = new Result();
		List<GroupAvatar> listGroupAdater = dao.findPublicGroups(userName,pageId,pageSize);
		if(listGroupAdater!=null){
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
			Pager pager = getPager(pageId+"", listGroupAdater);
			result.setRetData(pager);
		}else{
			result.setRetCode(I.MSG_PUBLIC_GROUP_FAIL);
			result.setRetMsg(false);
		}
		return result;
	}

	/**
	 * 根据群组名称，模糊查询所有匹配的群组
	 */
	@Override
	public Result findGroupByGroupName(String groupName) {
		Result result = new Result();
		List<GroupAvatar> listGroupAdater = dao.findGroupByGroupName(groupName);
		if(listGroupAdater!=null){
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
			result.setRetData(listGroupAdater);
		}else{
			result.setRetCode(I.MSG_GROUP_FIND_BY_GROUP_NAME_FAIL);
			result.setRetMsg(false);
		}
		return result;
	}

	@Override
	public Result uploadUserLocation(Location location) {
		Result result = new Result();
		if(dao.uploadUserLocation(location)){
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_LOCATION_UPLOAD_FAIL);
		}
		return result;
	}

	@Override
	public Result updateUserLocation(Location location) {
		Result result = new Result();
		if(dao.updateUserLocation(location)){
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_LOCATION_UPDATE_FAIL);
		}
		return result;
	}

	@Override
	public Result findContactAllByUserName(String userName) {
		Result result = new Result();
		List<UserAvatar> listUserAvatar = dao.findContactAllByUserName(userName);
		if(listUserAvatar!=null){
			result.setRetData(listUserAvatar);
			result.setRetMsg(true);
			result.setRetCode(I.MSG_SUCCESS);
		}else{
			result.setRetMsg(false);
			result.setRetCode(I.MSG_GET_CONTACT_ALL_FAIL);
		}
		return result;
	}

	@Override
	public Result downloadLocation(String userName, String pageId, String pageSize) {
		Result result = new Result();
		List<LocationUserAvatar> listLocationUserAvatar = dao.downloadLocation(userName,pageId,pageSize);
		if(listLocationUserAvatar==null){
			result.setRetMsg(false);
			result.setRetCode(I.MSG_LOCATION_GET_FAIL);
			
		}else{
			result.setRetMsg(true);
			Pager pager = getPager(pageId, listLocationUserAvatar);
			result.setRetData(pager);
			result.setRetCode(I.MSG_SUCCESS);
		}
		return result;
		
	}
}
