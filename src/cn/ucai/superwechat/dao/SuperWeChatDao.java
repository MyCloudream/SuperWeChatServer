package cn.ucai.superwechat.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.ucai.superwechat.bean.GroupAvatar;
import cn.ucai.superwechat.bean.MemberUserAvatar;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.bean.UserAvatarContact;
import cn.ucai.superwechat.pojo.Contact;
import cn.ucai.superwechat.pojo.Group;
import cn.ucai.superwechat.pojo.Member;
import cn.ucai.superwechat.pojo.User;
import cn.ucai.superwechat.servlet.I;
import cn.ucai.superwechat.utils.JdbcUtils;
import cn.ucai.superwechat.utils.Utils;


/**
 * 数据访问层
 */
public class SuperWeChatDao implements ISuperWeChatDao {
	
	private final String SQL_QUERY_AVATAR = ","+ I.Avatar.TABLE_NAME;
	private final String SQL_QUERY_LOCATION = ","+ I.Location.TABLE_NAME;
	private final String SQL_QUERY_USER = ","+ I.User.TABLE_NAME;
	private final String SQL_QUERY_GROUP = ","+ I.Group.TABLE_NAME;
	private final String SQL_COMPARE_USER_NAME_AVATAR = " and " + I.User.USER_NAME + "=" + I.Avatar.USER_NAME + " ";
//	private final String SQL_COMPARE_USER_ID_AVATAR = " and " + I.User.USER_ID + "=" + I.Avatar.USER_ID + " ";
//	private final String SQL_COMPARE_USER_NAME_LOCATION = " and " + I.User.USER_NAME + "=" + I.Location.USER_NAME + " ";
//	private final String SQL_COMPARE_USER_ID_LOCATION = " and " + I.User.USER_ID + "=" + I.Location.USER_ID + " ";
//	private final String SQL_COMPARE_USER_NAME_CONTACT = " and " + I.User.USER_NAME + "=" + I.Contact.CU_NAME + " ";
//	private final String SQL_COMPARE_USER_ID_CONTACT = " and " + I.User.USER_ID + "=" + I.Contact.CU_ID + " ";
	private final String SQL_COMPARE_GROUP_ID_MEMBER = " and " + I.Member.GROUP_ID + "=" + I.Group.GROUP_ID + " ";
	private final String SQL_COMPARE_AVATAR_USER = " and " + I.Avatar.AVATAR_TYPE + "=0 ";
	private final String SQL_COMPARE_AVATAR_GROUP = " and " + I.Avatar.AVATAR_TYPE + "=1 ";
//	private final String SQL_COMPARE_GROUP_ID_AVATAR = " and " + I.Group.GROUP_ID + "=" + I.Avatar.USER_ID + " ";
//	private final String SQL_COMPARE_GROUP_HXID_AVATAR = " and " + I.Group.HX_ID + "=" + I.Avatar.USER_NAME + " ";
//	private final String SQL_COMPARE_USER_ID_MEMBER = " and " + I.User.USER_ID + "=" + I.Member.USER_ID + " ";
	private final String SQL_COMPARE_PUBLIC_GROUP = " and " + I.Group.IS_PUBLIC + "=1 ";

	@Override
	public User findUserByUserName(String userName) {
		System.out.println("SuperQQDao.findUserByUserName,userName=" + userName);
		ResultSet set = null;
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.User.TABLE_NAME + SQL_QUERY_AVATAR + " where " + I.User.USER_NAME + "=?"
				+ SQL_COMPARE_USER_NAME_AVATAR + SQL_COMPARE_AVATAR_USER;
		System.out.println("connection=" + connection + ",sql=" + sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, userName);
			set = statement.executeQuery();
			if (set.next()) {
				User user = new User();
				System.out.println("user=" + userName.toString());
				return user;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(set, statement, connection);
		}
		return null;
	}
	
	@Override
	public boolean addUser(User user) {
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		try {
			// 关闭事务的自动提交
			connection.setAutoCommit(false);
			String sql = "insert into " + I.User.TABLE_NAME + "(" + I.User.USER_NAME + "," + I.User.PASSWORD + ","
					+ I.User.NICK + "," + I.User.UN_READ_MSG_COUNT + ")values(?,?,?,?)";
			statement = connection.prepareStatement(sql);
			statement.setString(1, user.getMUserName());
			statement.setString(2, user.getMUserPassword());
			statement.setString(3, user.getMUserNick());
			statement.setInt(4, 0);
			statement.executeUpdate();
			
			sql = "insert into " + I.Avatar.TABLE_NAME + "(" + I.Avatar.USER_NAME + "," + I.Avatar.AVATAR_PATH + ","
					+ I.Avatar.AVATAR_TYPE + ","+I.Avatar.UPDATE_TIME+")values(?,?,?,?)";
			statement = connection.prepareStatement(sql);
			statement.setString(1, user.getMUserName());
			statement.setString(2, I.AVATAR_TYPE_USER_PATH);
			statement.setInt(3, I.AVATAR_TYPE_USER);
			statement.setString(4,System.currentTimeMillis()+"");
			statement.executeUpdate();
			// 提交事务
			connection.commit();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			// 回滚事务
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return false;
		} finally {
			JdbcUtils.closeAll(null, statement, connection);
		}
	}
	
	@Override
	public boolean deleteUser(String userName) {
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		try {
			// 关闭事务的自动提交
			connection.setAutoCommit(false);
			String sql = "delete from " + I.User.TABLE_NAME + " where " + I.User.USER_NAME + "=?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, userName);
			int countUser = statement.executeUpdate();
			
			sql = "delete from "+ I.Avatar.TABLE_NAME + " where " + I.Avatar.USER_NAME + "=?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, userName);
			int countAvatar = statement.executeUpdate();
			// 提交事务
			if(countUser > 0 && countAvatar > 0){
				connection.commit();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			JdbcUtils.closeAll(null, statement, connection);
		}
		return false;
	}

	@Override
	public UserAvatar findUserAvatarByUserName(String userName) {
		ResultSet rs = null;
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.User.TABLE_NAME +","+ I.Avatar.TABLE_NAME + 
				" where " + I.User.USER_NAME + "=? "
				+ " and " + I.User.USER_NAME + "=" + I.Avatar.USER_NAME;
		System.out.println("connection=" + connection + ",sql=" + sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, userName);
			rs = statement.executeQuery();
			if (rs.next()) {
				UserAvatar userAvatar = new UserAvatar();
				initUserAvatar(rs,userAvatar);
				/*userAvatar.setMUserName(rs.getString(I.User.USER_NAME));
				userAvatar.setMUserPassword(rs.getString(I.User.PASSWORD));
				userAvatar.setMUserNick(rs.getString(I.User.NICK));
				userAvatar.setMUserUnreadMsgCount(rs.getInt(I.User.UN_READ_MSG_COUNT));
				userAvatar.setMAvatarId(rs.getInt(I.Avatar.AVATAR_ID));
				userAvatar.setMAvatarUserName(rs.getString(I.Avatar.USER_NAME));
				userAvatar.setMAvatarPath(rs.getString(I.Avatar.AVATAR_PATH));
				userAvatar.setMAvatarType(rs.getInt(I.Avatar.AVATAR_TYPE));
				userAvatar.setMAvatarLastUpdateTime(rs.getString(I.Avatar.UPDATE_TIME));*/
				return userAvatar;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(rs, statement, connection);
		}
		return null;
	}

	private void initUserAvatar(ResultSet rs,UserAvatar userAvatar) throws SQLException {
		userAvatar.setMUserName(rs.getString(I.User.USER_NAME));
		userAvatar.setMUserPassword(rs.getString(I.User.PASSWORD));
		userAvatar.setMUserNick(rs.getString(I.User.NICK));
		userAvatar.setMUserUnreadMsgCount(rs.getInt(I.User.UN_READ_MSG_COUNT));
		userAvatar.setMAvatarId(rs.getInt(I.Avatar.AVATAR_ID));
		userAvatar.setMAvatarUserName(rs.getString(I.Avatar.USER_NAME));
		userAvatar.setMAvatarPath(rs.getString(I.Avatar.AVATAR_PATH));
		userAvatar.setMAvatarType(rs.getInt(I.Avatar.AVATAR_TYPE));
		userAvatar.setMAvatarLastUpdateTime(rs.getString(I.Avatar.UPDATE_TIME));
	}
	
	/**
	 * 从set中获取contact表中的一条记录
	 * 
	 * @param set
	 * @return
	 * @throws SQLException
	 */
	private void initContact(ResultSet set, Contact contact) throws SQLException {
		contact.setMContactId(set.getInt(I.Contact.CONTACT_ID));
		contact.setMContactUserName(set.getString(I.Contact.USER_NAME));
		contact.setMContactCname(set.getString(I.Contact.CU_NAME));
	}
	
	@Override
	public boolean updateNickByName(String userName, String userNick) {
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "update " + I.User.TABLE_NAME + " set " + I.User.NICK + "=?" + " where " + I.User.USER_NAME + "=?";
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, userNick);
			statement.setString(2, userName);
			int count = statement.executeUpdate();
			return count == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(null, statement, connection);
		}
		return false;
	}

	@Override
	public boolean updatePasswordByName(String userName, String userPassword) {
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "update " + I.User.TABLE_NAME + " set " + I.User.PASSWORD + "=?" + " where " + I.User.USER_NAME + "=?";
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, userPassword);
			statement.setString(2, userName);
			int count = statement.executeUpdate();
			return count == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(null, statement, connection);
		}
		return false;
	}

	@Override
	public UserAvatarContact findContactPagesByUserName(String userName, String pageId, String pageSize) {
		ResultSet rs = null;
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.Contact.TABLE_NAME + ","+ I.User.TABLE_NAME + ","+ I.Avatar.TABLE_NAME 
				+ " where "	+ I.Contact.USER_NAME + "=?" 
				+ " and " + I.User.USER_NAME + "=" + I.Contact.CU_NAME + " " 
				+ " and " + I.User.USER_NAME + "=" + I.Avatar.USER_NAME + " ";
		if(pageId!=null&&pageSize!=null){
			sql += " limit ?,?";
		}
		System.out.println("connection=" + connection + ",sql=" + sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, userName);
			if(pageId!=null&&pageSize!=null){
				Integer niPageId = Integer.parseInt(pageId);
				Integer niPageSize = Integer.parseInt(pageSize);
				statement.setInt(2, (niPageId-1)*niPageSize);
				statement.setInt(3, niPageSize);
			}
			rs = statement.executeQuery();
			boolean flag = true;
			UserAvatarContact uac = new UserAvatarContact();
			List<UserAvatar> listUserAvatar = new ArrayList<UserAvatar>();
			while (rs.next()) {
				if(flag){
					uac.setMContactId(rs.getInt(I.Contact.CONTACT_ID));
					uac.setMContactUserName(rs.getString(I.Contact.USER_NAME));
					flag = false;
				}
				UserAvatar ua = new UserAvatar();
				initUserAvatar(rs,ua);
				listUserAvatar.add(ua);
			}
			uac.setListUserAvatar(listUserAvatar);
			return uac;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(rs, statement, connection);
		}
		return null;
	}

	/**
	 * 查找cname是否是name的好友
	 */
	@Override
	public boolean findContact(String name, String cname) {
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.Contact.TABLE_NAME
				+ " where "	+ I.Contact.USER_NAME + "=?" 
				+ " and " + I.Contact.CU_NAME + "=?";
		System.out.println("connection=" + connection + ",sql=" + sql);
		ResultSet rs = null;
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, name);
			statement.setString(2, cname);
			rs = statement.executeQuery();
			if(rs.next()){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(rs, statement, connection);
		}
		return false;
	}

	/**
	 * 创建好友关系
	 */
	@Override
	public boolean addContact(String name, String cname) {
		Connection connection = JdbcUtils.getConnection();
		PreparedStatement statement = null;
		try {
			String sql = "insert into " + I.Contact.TABLE_NAME 
					+ "(" + I.Contact.USER_NAME + "," + I.Contact.CU_NAME + ")values(?,?)";
			statement = connection.prepareStatement(sql);
			statement.setString(1, name);
			statement.setString(2, cname);
			int count = statement.executeUpdate();
			if(count>0){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(null, statement, connection);
		}
		return false;
	}

	/**
	 * 删除好友关系
	 */
	@Override
	public boolean delContact(String name, String cname) {
		Connection connection = JdbcUtils.getConnection();
		PreparedStatement statement = null;
		try {
			String sql = "delete from " + I.Contact.TABLE_NAME 
					+ " where " + I.Contact.USER_NAME + "=?"
					+ "and "+I.Contact.CU_NAME + "=?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, name);
			statement.setString(2, cname);
			int countUser = statement.executeUpdate();
			if(countUser > 0){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(null, statement, connection);
		}
		return false;
	}

	@Override
	public List<UserAvatar> findUsersForSearch(String userName, String userNick, String pageId, String pageSize) {
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.User.TABLE_NAME +","+ I.Avatar.TABLE_NAME + 
				" where " + I.User.USER_NAME + "=" + I.Avatar.USER_NAME;
		if(userName!=null){
			sql += " and "+I.User.USER_NAME +" like ?";
		}
		if(userNick!=null){
			sql += " and "+I.User.NICK +" like ?";
		}
		sql += "limit ?,?";
		System.out.println("connection=" + connection + ",sql=" + sql);
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = connection.prepareStatement(sql);
			if(userName!=null){
				statement.setString(1, "%"+userName+"%");
				if(userNick!=null){
					statement.setString(2, "%"+userNick+"%");
					statement.setInt(3, (Integer.parseInt(pageId)-1)*Integer.parseInt(pageSize));
					statement.setInt(4, Integer.parseInt(pageSize));
				}else{
					statement.setInt(2, (Integer.parseInt(pageId)-1)*Integer.parseInt(pageSize));
					statement.setInt(3, Integer.parseInt(pageSize));
				}
			}else{
				if(userNick!=null){
					statement.setString(1, "%"+userNick+"%");
					statement.setInt(2, (Integer.parseInt(pageId)-1)*Integer.parseInt(pageSize));
					statement.setInt(3, Integer.parseInt(pageSize));
				}else{
					statement.setInt(1, (Integer.parseInt(pageId)-1)*Integer.parseInt(pageSize));
					statement.setInt(2, Integer.parseInt(pageSize));
				}
			}
			rs = statement.executeQuery();
			List<UserAvatar> uaList = new ArrayList<UserAvatar>();
			while (rs.next()) {
				UserAvatar userAvatar = new UserAvatar();
				initUserAvatar(rs,userAvatar);
				uaList.add(userAvatar);
			}
			return uaList;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(rs, statement, connection);
		}
		return null;
	}

	@Override
	public Group findGroupByHxid(String mGroupHxid) {
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.Group.TABLE_NAME
				+ " where " + I.Group.HX_ID + "=?";
		ResultSet rs = null;
		PreparedStatement statement = null;
		Group group = new Group();
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, mGroupHxid);
			rs = statement.executeQuery();
			if (rs.next()) {
				initGroup(rs, group);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(rs, statement, connection);
		}
		return null;
	}

	@Override
	public boolean addGroupAndGroupOwnerMember(Group group) {
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		try {
			// 关闭事务的自动提交
			connection.setAutoCommit(false);
			String sql = "insert into " + I.Group.TABLE_NAME + "(" 
					+ I.Group.HX_ID + "," + I.Group.NAME + ","
					+ I.Group.DESCRIPTION + "," + I.Group.OWNER + "," 
					+ I.Group.MODIFIED_TIME + "," + I.Group.MAX_USERS+ "," 
					+ I.Group.AFFILIATIONS_COUNT + "," + I.Group.IS_PUBLIC + "," 
					+ I.Group.ALLOW_INVITES + ")values(?,?,?,?,?,?,?,?,?)";
			statement = connection.prepareStatement(sql);
			statement.setString(1, group.getMGroupHxid());
			statement.setString(2, group.getMGroupName());
			statement.setString(3, group.getMGroupDescription());
			statement.setString(4, group.getMGroupOwner());
			statement.setString(5, group.getMGroupLastModifiedTime());
			statement.setInt(6, group.getMGroupMaxUsers());
			statement.setInt(7, group.getMGroupAffiliationsCount());
			statement.setInt(8, Utils.boolean2int(group.getMGroupIsPublic()));
			statement.setInt(9, Utils.boolean2int(group.getMGroupAllowInvites()));
			statement.executeUpdate();
			
			sql = "insert into " + I.Avatar.TABLE_NAME + "(" + I.Avatar.USER_NAME + "," + I.Avatar.AVATAR_PATH + ","
					+ I.Avatar.AVATAR_TYPE + ","+I.Avatar.UPDATE_TIME+")values(?,?,?,?)";
			statement = connection.prepareStatement(sql);
			statement.setString(1, group.getMGroupHxid());
			statement.setString(2, I.AVATAR_TYPE_GROUP_PATH);
			statement.setInt(3, I.AVATAR_TYPE_GROUP);
			statement.setString(4,System.currentTimeMillis()+"");
			statement.executeUpdate();
			
			sql = "insert into " + I.Member.TABLE_NAME + 
					"(" + I.Member.USER_NAME + "," + I.Member.GROUP_ID + ","
					+ I.Member.GROUP_HX_ID + ","+I.Member.PERMISSION+")values(?,?,?,?)";
			statement = connection.prepareStatement(sql);
			statement.setString(1, group.getMGroupOwner());
			statement.setInt(2, group.getMGroupId());
			statement.setString(3, group.getMGroupHxid());
			statement.setInt(4,I.PERMISSION_OWNER);
			statement.executeUpdate();
			// 提交事务
			connection.commit();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			// 回滚事务
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return false;
		} finally {
			JdbcUtils.closeAll(null, statement, connection);
		}
	}

	@Override
	public GroupAvatar findGroupAvatarByGroupId(String groupId) {
		ResultSet rs = null;
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.Group.TABLE_NAME +","+ I.Avatar.TABLE_NAME + 
				" where " + I.Group.GROUP_ID + "=? "
				+ " and " + I.Group.NAME + "=" + I.Avatar.USER_NAME;
		System.out.println("connection=" + connection + ",sql=" + sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, groupId);
			rs = statement.executeQuery();
			if (rs.next()) {
				GroupAvatar groupAvatar = new GroupAvatar();
				initGroupAvatar(rs,groupAvatar);
				/*userAvatar.setMUserName(rs.getString(I.User.USER_NAME));
				userAvatar.setMUserPassword(rs.getString(I.User.PASSWORD));
				userAvatar.setMUserNick(rs.getString(I.User.NICK));
				userAvatar.setMUserUnreadMsgCount(rs.getInt(I.User.UN_READ_MSG_COUNT));
				userAvatar.setMAvatarId(rs.getInt(I.Avatar.AVATAR_ID));
				userAvatar.setMAvatarUserName(rs.getString(I.Avatar.USER_NAME));
				userAvatar.setMAvatarPath(rs.getString(I.Avatar.AVATAR_PATH));
				userAvatar.setMAvatarType(rs.getInt(I.Avatar.AVATAR_TYPE));
				userAvatar.setMAvatarLastUpdateTime(rs.getString(I.Avatar.UPDATE_TIME));*/
				return groupAvatar;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(rs, statement, connection);
		}
		return null;
	}
	@Override
	public GroupAvatar findGroupAvatarByHxId(String hxId) {
		ResultSet rs = null;
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.Group.TABLE_NAME +","+ I.Avatar.TABLE_NAME + 
				" where " + I.Group.HX_ID + "=? "
				+ " and " + I.Group.NAME + "=" + I.Avatar.USER_NAME;
		System.out.println("connection=" + connection + ",sql=" + sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, hxId);
			rs = statement.executeQuery();
			if (rs.next()) {
				GroupAvatar groupAvatar = new GroupAvatar();
				initGroupAvatar(rs,groupAvatar);
				/*userAvatar.setMUserName(rs.getString(I.User.USER_NAME));
				userAvatar.setMUserPassword(rs.getString(I.User.PASSWORD));
				userAvatar.setMUserNick(rs.getString(I.User.NICK));
				userAvatar.setMUserUnreadMsgCount(rs.getInt(I.User.UN_READ_MSG_COUNT));
				userAvatar.setMAvatarId(rs.getInt(I.Avatar.AVATAR_ID));
				userAvatar.setMAvatarUserName(rs.getString(I.Avatar.USER_NAME));
				userAvatar.setMAvatarPath(rs.getString(I.Avatar.AVATAR_PATH));
				userAvatar.setMAvatarType(rs.getInt(I.Avatar.AVATAR_TYPE));
				userAvatar.setMAvatarLastUpdateTime(rs.getString(I.Avatar.UPDATE_TIME));*/
				return groupAvatar;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(rs, statement, connection);
		}
		return null;
	}
	
	private void initGroupAvatar(ResultSet rs, GroupAvatar groupAvatar) throws SQLException {
		groupAvatar.setMGroupId(rs.getInt(I.Group.GROUP_ID));
		groupAvatar.setMGroupHxid(rs.getString(I.Group.HX_ID));
		groupAvatar.setMGroupName(rs.getString(I.Group.NAME));
		groupAvatar.setMGroupDescription(rs.getString(I.Group.DESCRIPTION));
		groupAvatar.setMGroupOwner(rs.getString(I.Group.OWNER));
		groupAvatar.setMGroupLastModifiedTime(rs.getString(I.Group.MODIFIED_TIME));
		groupAvatar.setMGroupMaxUsers(rs.getInt(I.Group.MAX_USERS));
		groupAvatar.setMGroupAffiliationsCount(rs.getInt(I.Group.AFFILIATIONS_COUNT));
		groupAvatar.setMGroupIsPublic(Utils.int2boolean(rs.getInt(I.Group.IS_PUBLIC)));
		groupAvatar.setMGroupAllowInvites(Utils.int2boolean(rs.getInt(I.Group.ALLOW_INVITES)));
		groupAvatar.setMAvatarId(rs.getInt(I.Avatar.AVATAR_ID));
		groupAvatar.setMAvatarUserName(rs.getString(I.Avatar.USER_NAME));
		groupAvatar.setMAvatarPath(rs.getString(I.Avatar.AVATAR_PATH));
		groupAvatar.setMAvatarType(rs.getInt(I.Avatar.AVATAR_TYPE));
		groupAvatar.setMAvatarLastUpdateTime(rs.getString(I.Avatar.UPDATE_TIME));
	}

	private void initGroup(ResultSet rs, Group group) throws SQLException {
		group.setMGroupId(rs.getInt(I.Group.GROUP_ID));
		group.setMGroupHxid(rs.getString(I.Group.HX_ID));
		group.setMGroupName(rs.getString(I.Group.NAME));
		group.setMGroupDescription(rs.getString(I.Group.DESCRIPTION));
		group.setMGroupOwner(rs.getString(I.Group.OWNER));
		group.setMGroupLastModifiedTime(rs.getString(I.Group.MODIFIED_TIME));
		group.setMGroupMaxUsers(rs.getInt(I.Group.MAX_USERS));
		group.setMGroupAffiliationsCount(rs.getInt(I.Group.AFFILIATIONS_COUNT));
		group.setMGroupIsPublic(Utils.int2boolean(rs.getInt(I.Group.IS_PUBLIC)));
		group.setMGroupAllowInvites(Utils.int2boolean(rs.getInt(I.Group.ALLOW_INVITES)));
	}
	
	@Override
	public boolean updateGroupNameByGroupId(String groupId, String groupNewName) {
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "update " + I.Group.TABLE_NAME + " set " + I.Group.NAME + "=?" + " where " + I.Group.GROUP_ID + "=?";
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, groupNewName);
			statement.setString(2, groupId);
			int count = statement.executeUpdate();
			return count == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(null, statement, connection);
		}
		return false;
	}

	/**
	 * 添加群组成员信息
	 */
	@Override
	public boolean addGroupMember(Member member) {
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "insert into " + I.Member.TABLE_NAME + "(" 
				+ I.Member.USER_NAME + "," + I.Member.GROUP_ID + "," 
				+ I.Member.GROUP_HX_ID + "," + I.Member.PERMISSION 
				+ ")values(?,?,?,?)";
		System.out.println("connection=" + connection + ",sql=" + sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, member.getMMemberUserName());
			statement.setInt(2, member.getMMemberGroupId());
			statement.setString(3, member.getMMemberGroupHxid());
			statement.setInt(4, member.getMMemberPermission());
			int count = statement.executeUpdate();
			return count > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(null, statement, connection);
		}
		return false;
	}

	@Override
	public boolean updateGroupAffiliationsCount(GroupAvatar groupAvatar) {
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "update " + I.Group.TABLE_NAME + " set " + I.Group.AFFILIATIONS_COUNT + "=?,"
				+ I.Group.MODIFIED_TIME + "=?" + " where " + I.Group.GROUP_ID + "=?";
		try {
			statement = connection.prepareStatement(sql);
			statement.setInt(1, groupAvatar.getMGroupAffiliationsCount());
			statement.setString(2, groupAvatar.getMGroupLastModifiedTime());
			statement.setInt(3, groupAvatar.getMGroupId());
			int count = statement.executeUpdate();
			return count > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(null, statement, connection);
		}
		return false;
	}

	@Override
	public boolean addGroupMembers(Member[] memberArr) {
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "insert into " + I.Member.TABLE_NAME + "(" 
				+ I.Member.USER_NAME + "," + I.Member.GROUP_ID + "," 
				+ I.Member.GROUP_HX_ID + "," + I.Member.PERMISSION 
				+ ")values(?,?,?,?)";
		for(int i=1;i<memberArr.length;i++){
			sql += ",(?,?,?,?)";
		}
		System.out.println("connection=" + connection + ",sql=" + sql);
		try {
			statement = connection.prepareStatement(sql);
			for(int i=0;i<memberArr.length;i++){
				statement.setString((i+1), memberArr[i].getMMemberUserName());
				statement.setInt((i+2), memberArr[i].getMMemberGroupId());
				statement.setString((i+3), memberArr[i].getMMemberGroupHxid());
				statement.setInt((i+4), memberArr[i].getMMemberPermission());
			}
			int count = statement.executeUpdate();
			return count > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(null, statement, connection);
		}
		return false;
	}

	/**
	 * 根据群组id，下载群组成员，如果有pageId和pageSize，则分页下载
	 */
	@Override
	public List<MemberUserAvatar> downloadGroupMembersByGroupId(String groupId, String pageId, String pageSize) {
		List<MemberUserAvatar> list = new ArrayList<MemberUserAvatar>();
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.Member.TABLE_NAME +","+ I.Avatar.TABLE_NAME + I.User.TABLE_NAME +  
				" where " + I.Member.GROUP_ID + "=? "
				+ " and " + I.Member.USER_NAME + "=" + I.User.USER_NAME
				+ " and " + I.Avatar.USER_NAME + "=" + I.User.USER_NAME;
		if(pageId!=null&&pageSize!=null){
			sql += " limit ?,?";
		}
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, groupId);
			if(pageId!=null&&pageSize!=null){
				Integer niPageId = Integer.parseInt(pageId);
				Integer niPageSize = Integer.parseInt(pageSize);
				statement.setInt(2, (niPageId-1)*niPageSize);
				statement.setInt(3, niPageSize);
			}
			rs = statement.executeQuery();
			if (rs.next()) {
				MemberUserAvatar memberUserAvatar = new MemberUserAvatar();
				initMemberUserAvatar(rs,memberUserAvatar);
				list.add(memberUserAvatar);
				return list;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(rs, statement, connection);
		}
		return null;
	}

	private void initMemberUserAvatar(ResultSet rs, MemberUserAvatar memberUserAvatar) throws SQLException {
		initUserAvatar(rs, memberUserAvatar);
		memberUserAvatar.setMMemberId(rs.getInt(I.Member.MEMBER_ID));
		memberUserAvatar.setMMemberUserName(rs.getString(I.Member.USER_NAME));
		memberUserAvatar.setMMemberGroupId(rs.getInt(I.Member.GROUP_ID));
		memberUserAvatar.setMMemberGroupHxid(rs.getString(I.Member.GROUP_HX_ID));
		memberUserAvatar.setMMemberPermission(rs.getInt(I.Member.PERMISSION));
	}

	/**
	 * 根据环信id，下载群组成员，如果有pageId和pageSize，则分页下载
	 */
	@Override
	public List<MemberUserAvatar> downloadGroupMembersByHxId(String hxId, String pageId, String pageSize) {
		List<MemberUserAvatar> list = new ArrayList<MemberUserAvatar>();
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.Member.TABLE_NAME +","+ I.Avatar.TABLE_NAME + I.User.TABLE_NAME +  
				" where " + I.Member.GROUP_HX_ID + "=? "
				+ " and " + I.Member.USER_NAME + "=" + I.User.USER_NAME
				+ " and " + I.Avatar.USER_NAME + "=" + I.User.USER_NAME;
		if(pageId!=null&&pageSize!=null){
			sql += " limit ?,?";
		}
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, hxId);
			if(pageId!=null&&pageSize!=null){
				Integer niPageId = Integer.parseInt(pageId);
				Integer niPageSize = Integer.parseInt(pageSize);
				statement.setInt(2, (niPageId-1)*niPageSize);
				statement.setInt(3, niPageSize);
			}
			rs = statement.executeQuery();
			if (rs.next()) {
				MemberUserAvatar memberUserAvatar = new MemberUserAvatar();
				initMemberUserAvatar(rs,memberUserAvatar);
				list.add(memberUserAvatar);
				return list;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(rs, statement, connection);
		}
		return null;
	}

	/**
	 * 删除指定群成员
	 */
	@Override
	public boolean delGroupMember(String userName, String groupId) {
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "delete from " + I.Member.TABLE_NAME + " where " + I.Member.USER_NAME + "=?" + " and "
				+ I.Member.GROUP_ID + " =?";
		System.out.println("connection=" + connection + ",sql=" + sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, userName);
			statement.setInt(2, Integer.parseInt(groupId));
			int count = statement.executeUpdate();
			return count > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
				return false;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			JdbcUtils.closeAll(null, statement, connection);
		}
		return false;
	}

	@Override
	public boolean delGroupMembers(String userNames, String groupId) {
		Connection connection = JdbcUtils.getConnection();
		String sql = "delete from " + I.Member.TABLE_NAME + " where " + I.Member.USER_NAME + " in ("+"?) " + " and "
				+ I.Member.GROUP_ID + " =?";
		System.out.println("connection=" + connection + ",sql=" + sql);
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, userNames);
			statement.setInt(2, Integer.parseInt(groupId));
			int count = statement.executeUpdate();
			return count > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
				return false;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			JdbcUtils.closeAll(null, statement, connection);
		}
		return false;
	}

	@Override
	public boolean deleteGroupAndMembers(String groupId) {
		Connection connection = JdbcUtils.getConnection();
		PreparedStatement statement = null;
		try {
			connection.setAutoCommit(false);
			String sql = "delete from " + I.Group.TABLE_NAME + " where " + I.Group.GROUP_ID + "=?";
			System.out.println("connection=" + connection + ",sql=" + sql);
			statement = connection.prepareStatement(sql);
			statement.setInt(1, Integer.parseInt(groupId));
			statement.executeUpdate();
			
			sql = "delete from " + I.Member.TABLE_NAME + " where " + I.Member.GROUP_ID + "=?";
			System.out.println("connection=" + connection + ",sql=" + sql);
			statement = connection.prepareStatement(sql);
			statement.setInt(1, Integer.parseInt(groupId));
			statement.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
				return false;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			JdbcUtils.closeAll(null, statement, connection);
		}
		return false;
	}
}
