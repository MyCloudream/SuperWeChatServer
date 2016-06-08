package cn.ucai.superwechat.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.ucai.superwechat.bean.*;
import cn.ucai.superwechat.pojo.*;
import cn.ucai.superwechat.servlet.I;
import cn.ucai.superwechat.utils.JdbcUtils;
import cn.ucai.superwechat.utils.Utils;


/**
 * 数据访问层
 */
public class SuperWeChatDao implements ISuperWeChatDao {
	@Override
	public User findUserByUserName(String userName) {
		ResultSet set = null;
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.User.TABLE_NAME + ","+ I.Avatar.TABLE_NAME + " where " + I.User.USER_NAME + "=?"
				+ " and " + I.User.USER_NAME + " = " + I.Avatar.USER_NAME + " and " + I.Avatar.AVATAR_TYPE + "=0 ";
		System.out.println("connection=" + connection + ",sql=" + sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, userName);
			set = statement.executeQuery();
			if (set.next()) {
				User user = new User();
				user.setMUserPassword(set.getString(I.User.PASSWORD));
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
					+ I.User.NICK + ")values(?,?,?)";
			System.out.println("addUser:"+sql);
			statement = connection.prepareStatement(sql);
			statement.setString(1, user.getMUserName());
			statement.setString(2, user.getMUserPassword());
			statement.setString(3, user.getMUserNick());
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
//		userAvatar.setMUserPassword(rs.getString(I.User.PASSWORD));
		userAvatar.setMUserNick(rs.getString(I.User.NICK));
//		userAvatar.setMUserUnreadMsgCount(rs.getInt(I.User.UN_READ_MSG_COUNT));
		userAvatar.setMAvatarId(rs.getInt(I.Avatar.AVATAR_ID));
//		userAvatar.setMAvatarUserName(rs.getString(I.Avatar.USER_NAME));
		userAvatar.setMAvatarPath(rs.getString(I.Avatar.AVATAR_PATH));
		userAvatar.setMAvatarType(rs.getInt(I.Avatar.AVATAR_TYPE));
		userAvatar.setMAvatarLastUpdateTime(rs.getString(I.Avatar.UPDATE_TIME));
	}
	
	private void initLocation(ResultSet rs,LocationUserAvatar locationUserAvatar) throws SQLException {
		locationUserAvatar.setMLocationId(rs.getInt(I.Location.LOCATION_ID));
//		locationUserAvatar.setMLocationUserName(rs.getString(I.Location.USER_NAME));
		locationUserAvatar.setMLocationLatitude(rs.getDouble(I.Location.LATITUDE));
		locationUserAvatar.setMLocationLongitude(rs.getDouble(I.Location.LONGITUDE));
		locationUserAvatar.setMLocationIsSearched(Utils.int2boolean(rs.getInt(I.Location.IS_SEARCHED)));
		locationUserAvatar.setMLocationLastUpdateTime(rs.getString(I.Location.UPDATE_TIME));
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
	public List<UserAvatar> findContactPagesByUserName(String userName, String pageId, String pageSize) {
		ResultSet rs = null;
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.Contact.TABLE_NAME + ","+ I.User.TABLE_NAME + ","+ I.Avatar.TABLE_NAME 
				+ " where "	+ I.Contact.USER_NAME + "=?" 
				+ " and " + I.User.USER_NAME + "=" + I.Contact.CU_NAME + " " 
				+ " and " + I.User.USER_NAME + "=" + I.Avatar.USER_NAME + " "
				+ " limit ?,?";
		System.out.println("connection=" + connection + ",sql=" + sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, userName);
			Integer niPageId = Integer.parseInt(pageId);
			Integer niPageSize = Integer.parseInt(pageSize);
			statement.setInt(2, (niPageId-1)*niPageSize);
			statement.setInt(3, niPageSize);
			rs = statement.executeQuery();
			List<UserAvatar> listUserAvatar = new ArrayList<UserAvatar>();
			while (rs.next()) {
				UserAvatar ua = new UserAvatar();
				initUserAvatar(rs,ua);
				listUserAvatar.add(ua);
			}
			return listUserAvatar;
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
		Connection connection = JdbcUtils.getConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			// 关闭事务的自动提交
			connection.setAutoCommit(false);
			String sql = "insert into " + I.Group.TABLE_NAME + "(" 
					+ I.Group.HX_ID + "," + I.Group.NAME + ","
					+ I.Group.DESCRIPTION + "," + I.Group.OWNER + "," 
					+ I.Group.MODIFIED_TIME + "," + I.Group.MAX_USERS+ "," 
					+ I.Group.AFFILIATIONS_COUNT + "," + I.Group.IS_PUBLIC + "," 
					+ I.Group.ALLOW_INVITES + ")values(?,?,?,?,?,?,?,?,?)";
			statement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
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
			
			int gourpId = -1;
			rs = statement.getGeneratedKeys();
			if (rs != null && rs.next()) {
				int id = rs.getInt(1);
				System.out.println("dao.createGroup,id=" + id);
				gourpId =  id;
			}
			
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
			statement.setInt(2, gourpId);
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
				+ " and " + I.Group.HX_ID + "=" + I.Avatar.USER_NAME;
		System.out.println("connection=" + connection + ",sql=" + sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, groupId);
			rs = statement.executeQuery();
			if (rs.next()) {
				GroupAvatar groupAvatar = new GroupAvatar();
				initGroupAvatar(rs,groupAvatar);
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
				+ " and " + I.Group.HX_ID + "=" + I.Avatar.USER_NAME;
		System.out.println("connection=" + connection + ",sql=" + sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, hxId);
			rs = statement.executeQuery();
			if (rs.next()) {
				GroupAvatar groupAvatar = new GroupAvatar();
				initGroupAvatar(rs,groupAvatar);
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
	public boolean addGroupMemberAndUpdateGroupAffiliationsCount(String userName,GroupAvatar groupAvatar) {
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		try {
			connection.setAutoCommit(false);
			Member member = new Member(userName,groupAvatar.getMGroupId(),groupAvatar.getMGroupHxid(),I.PERMISSION_NORMAL);
			String sql = "insert into " + I.Member.TABLE_NAME + "(" 
					+ I.Member.USER_NAME + "," + I.Member.GROUP_ID + "," 
					+ I.Member.GROUP_HX_ID + "," + I.Member.PERMISSION 
					+ ")values(?,?,?,?)";
			System.out.println("connection=" + connection + ",sql=" + sql);
			statement = connection.prepareStatement(sql);
			statement.setString(1, member.getMMemberUserName());
			statement.setInt(2, member.getMMemberGroupId());
			statement.setString(3, member.getMMemberGroupHxid());
			statement.setInt(4, member.getMMemberPermission());
			int count1 = statement.executeUpdate();
			
			sql = "update " + I.Group.TABLE_NAME + " set " + I.Group.AFFILIATIONS_COUNT + "=?,"
					+ I.Group.MODIFIED_TIME + "=?" + " where " + I.Group.GROUP_ID + "=?";
			System.out.println("sql"+sql);
			statement = connection.prepareStatement(sql);
			// 1、实体类同步更新
			groupAvatar.setMGroupAffiliationsCount(groupAvatar.getMGroupAffiliationsCount()+1);
			statement.setInt(1, groupAvatar.getMGroupAffiliationsCount());
			groupAvatar.setMAvatarLastUpdateTime(System.currentTimeMillis()+"");
			statement.setString(2, groupAvatar.getMAvatarLastUpdateTime());
			statement.setInt(3, groupAvatar.getMGroupId());
			int count2 = statement.executeUpdate();
			connection.commit();
			return count1 > 0 && count2>0;
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
	public boolean addGroupMembersAndUpdateGroupAffiliationsCount(String userNameArr,GroupAvatar groupAvatar) {
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		try {
			connection.setAutoCommit(false);
			String[] userNames = userNameArr.split(",");
			Member[] memberArr = new Member[userNames.length];
			for(int i=0;i<userNames.length;i++){
				memberArr[i] = new Member(userNames[i],groupAvatar.getMGroupId(),groupAvatar.getMGroupHxid(),I.PERMISSION_NORMAL);
			}
			String sql = "insert into " + I.Member.TABLE_NAME + "(" 
					+ I.Member.USER_NAME + "," + I.Member.GROUP_ID + "," 
					+ I.Member.GROUP_HX_ID + "," + I.Member.PERMISSION 
					+ ")values(?,?,?,?)";
			System.out.println("connection=" + connection + ",sql=" + sql);
			statement = connection.prepareStatement(sql);
			for(int i=0;i<memberArr.length;i++){
				statement.setString(1, memberArr[i].getMMemberUserName());
				statement.setInt(2, memberArr[i].getMMemberGroupId());
				statement.setString(3, memberArr[i].getMMemberGroupHxid());
				statement.setInt(4, memberArr[i].getMMemberPermission());
				statement.addBatch();
			}
			
			int[] countArr = statement.executeBatch();
			System.out.println(Arrays.toString(countArr));
			System.out.println(countArr.length);
			
			sql = "update " + I.Group.TABLE_NAME + " set " + I.Group.AFFILIATIONS_COUNT + "=?,"
					+ I.Group.MODIFIED_TIME + "=?" + " where " + I.Group.GROUP_ID + "=?";
			System.out.println("sql"+sql);
			statement = connection.prepareStatement(sql);
			
			groupAvatar.setMGroupAffiliationsCount(groupAvatar.getMGroupAffiliationsCount()+memberArr.length);
			statement.setInt(1, groupAvatar.getMGroupAffiliationsCount());
			groupAvatar.setMAvatarLastUpdateTime(System.currentTimeMillis()+"");
			statement.setString(2, groupAvatar.getMAvatarLastUpdateTime());
			statement.setInt(3, groupAvatar.getMGroupId());
			int count2 = statement.executeUpdate();
			connection.commit();
			return countArr.length > 0 && count2>0;
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

	/**
	 * 根据群组id，下载群组成员
	 */
	@Override
	public List<MemberUserAvatar> downloadGroupMembersByGroupId(String groupId) {
		List<MemberUserAvatar> list = new ArrayList<MemberUserAvatar>();
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.Member.TABLE_NAME +","+ I.Avatar.TABLE_NAME +","+ I.User.TABLE_NAME +  
				" where " + I.Member.GROUP_ID + "=? "
				+ " and " + I.Member.USER_NAME + "=" + I.User.USER_NAME
				+ " and " + I.Avatar.USER_NAME + "=" + I.User.USER_NAME;
		System.out.println("sql:"+sql);
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, groupId);
			rs = statement.executeQuery();
			while (rs.next()) {
				MemberUserAvatar memberUserAvatar = new MemberUserAvatar();
				initMemberUserAvatar(rs,memberUserAvatar);
				list.add(memberUserAvatar);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(rs, statement, connection);
		}
		return null;
	}
	/**
	 * 根据群组id，分页下载群组成员
	 */
	@Override
	public List<MemberUserAvatar> downloadGroupMembersPagesByGroupId(String groupId, String pageId, String pageSize) {
		List<MemberUserAvatar> list = new ArrayList<MemberUserAvatar>();
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.Member.TABLE_NAME +","+ I.Avatar.TABLE_NAME +","+ I.User.TABLE_NAME +  
				" where " + I.Member.GROUP_ID + "=? "
				+ " and " + I.Member.USER_NAME + "=" + I.User.USER_NAME
				+ " and " + I.Avatar.USER_NAME + "=" + I.User.USER_NAME
				+ " limit ?,?";
		System.out.println("sql:"+sql);
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, groupId);
			Integer niPageId = Integer.parseInt(pageId);
			Integer niPageSize = Integer.parseInt(pageSize);
			statement.setInt(2, (niPageId-1)*niPageSize);
			statement.setInt(3, niPageSize);
			rs = statement.executeQuery();
			while (rs.next()) {
				MemberUserAvatar memberUserAvatar = new MemberUserAvatar();
				initMemberUserAvatar(rs,memberUserAvatar);
				list.add(memberUserAvatar);
			}
			return list;
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
//		memberUserAvatar.setMMemberUserName(rs.getString(I.Member.USER_NAME));
		memberUserAvatar.setMMemberGroupId(rs.getInt(I.Member.GROUP_ID));
		memberUserAvatar.setMMemberGroupHxid(rs.getString(I.Member.GROUP_HX_ID));
		memberUserAvatar.setMMemberPermission(rs.getInt(I.Member.PERMISSION));
	}

	/**
	 * 根据环信id，下载群组成员
	 */
	@Override
	public List<MemberUserAvatar> downloadGroupMembersByHxId(String hxId) {
		List<MemberUserAvatar> list = new ArrayList<MemberUserAvatar>();
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.Member.TABLE_NAME +","+ I.Avatar.TABLE_NAME +","+ I.User.TABLE_NAME +  
				" where " + I.Member.GROUP_HX_ID + "=? "
				+ " and " + I.Member.USER_NAME + "=" + I.User.USER_NAME
				+ " and " + I.Avatar.USER_NAME + "=" + I.User.USER_NAME;
		System.out.println("sql:"+sql);
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, hxId);
			rs = statement.executeQuery();
			while (rs.next()) {
				MemberUserAvatar memberUserAvatar = new MemberUserAvatar();
				initMemberUserAvatar(rs,memberUserAvatar);
				list.add(memberUserAvatar);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(rs, statement, connection);
		}
		return null;
	}
	/**
	 * 根据环信id，下载群组成员，如果有pageId和pageSize，则分页下载
	 */
	@Override
	public List<MemberUserAvatar> downloadGroupMembersPagesByHxId(String hxId, String pageId, String pageSize) {
		List<MemberUserAvatar> list = new ArrayList<MemberUserAvatar>();
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.Member.TABLE_NAME +","+ I.Avatar.TABLE_NAME +","+ I.User.TABLE_NAME +  
				" where " + I.Member.GROUP_HX_ID + "=? "
				+ " and " + I.Member.USER_NAME + "=" + I.User.USER_NAME
				+ " and " + I.Avatar.USER_NAME + "=" + I.User.USER_NAME
				+ " limit ?,?";
		System.out.println("sql:"+sql);
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, hxId);
			Integer niPageId = Integer.parseInt(pageId);
			Integer niPageSize = Integer.parseInt(pageSize);
			statement.setInt(2, (niPageId-1)*niPageSize);
			statement.setInt(3, niPageSize);
			rs = statement.executeQuery();
			while (rs.next()) {
				MemberUserAvatar memberUserAvatar = new MemberUserAvatar();
				initMemberUserAvatar(rs,memberUserAvatar);
				list.add(memberUserAvatar);
			}
			return list;
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
	public boolean delGroupMemberAndUpdateGroupAffiliationsCount(String userName, GroupAvatar groupAvatar) {
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		try {
			connection.setAutoCommit(false);
			String sql = "delete from " + I.Member.TABLE_NAME + " where " + I.Member.USER_NAME + "=?" + " and "
					+ I.Member.GROUP_ID + " =?";
			System.out.println("connection=" + connection + ",sql=" + sql);
			statement = connection.prepareStatement(sql);
			statement.setString(1, userName);
			statement.setInt(2, groupAvatar.getMGroupId());
			int count1 = statement.executeUpdate();
			
			sql = "update " + I.Group.TABLE_NAME + " set " + I.Group.AFFILIATIONS_COUNT + "=?,"
					+ I.Group.MODIFIED_TIME + "=?" + " where " + I.Group.GROUP_ID + "=?";
			System.out.println("sql:"+sql);
			statement = connection.prepareStatement(sql);
			statement.setInt(1, groupAvatar.getMGroupAffiliationsCount()-1);
			statement.setString(2, System.currentTimeMillis()+"");
			statement.setInt(3, groupAvatar.getMGroupId());
			int count2 = statement.executeUpdate();
			connection.commit();
			return count1 > 0 && count2 > 0;
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
	public boolean delGroupMembersAndUpdateGroupAffiliationsCount(String userNames, GroupAvatar groupAvatar) {
		Connection connection = JdbcUtils.getConnection();
		PreparedStatement statement = null;
		try {
			connection.setAutoCommit(false);
			String sql = "delete from " + I.Member.TABLE_NAME + " where " + I.Member.USER_NAME + " in ("+"?) " + " and "
					+ I.Member.GROUP_ID + " =?";
			System.out.println("connection=" + connection + ",sql=" + sql);
			statement = connection.prepareStatement(sql);
			String[] userNameArr = userNames.split(",");
			for(String userName:userNameArr){
				statement.setString(1, userName);
				statement.setInt(2, groupAvatar.getMGroupId());
				statement.addBatch();
			}
			int[] count1 = statement.executeBatch();
			
			sql = "update " + I.Group.TABLE_NAME + " set " + I.Group.AFFILIATIONS_COUNT + "=?,"
					+ I.Group.MODIFIED_TIME + "=?" + " where " + I.Group.GROUP_ID + "=?";
			System.out.println("sql"+sql);
			statement = connection.prepareStatement(sql);
			statement.setInt(1, groupAvatar.getMGroupAffiliationsCount()-userNames.split(",").length);
			statement.setString(2, System.currentTimeMillis()+"");
			statement.setInt(3, groupAvatar.getMGroupId());
			int count2 = statement.executeUpdate();
			connection.commit();
			return count1.length > 0 && count2 > 0;
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

	/**
	 * 注意三者的删除顺序问题
	 * @param groupId
	 * @return
	 */
	@Override
	public boolean deleteGroupAndMembers(String groupId) {
		Connection connection = JdbcUtils.getConnection();
		PreparedStatement statement = null;
		try {
			connection.setAutoCommit(false);
			// 删除群成员
			String sql = "delete from " + I.Member.TABLE_NAME + " where " + I.Member.GROUP_ID + "=?";
			System.out.println("connection=" + connection + ",sql=" + sql);
			statement = connection.prepareStatement(sql);
			statement.setInt(1, Integer.parseInt(groupId));
			statement.executeUpdate();
			// 删除群组头像
			sql = "delete from " + I.Avatar.TABLE_NAME + " where " + I.Avatar.USER_NAME 
					+ "=(select "+I.Group.HX_ID+" from "+I.Group.TABLE_NAME+" where "+I.Group.GROUP_ID+"=?)";
			System.out.println("connection=" + connection + ",sql=" + sql);
			statement = connection.prepareStatement(sql);
			statement.setInt(1, Integer.parseInt(groupId));
			statement.executeUpdate();
			
			// 删除群组
			sql = "delete from " + I.Group.TABLE_NAME + " where " + I.Group.GROUP_ID + "=?";
			System.out.println("connection=" + connection + ",sql=" + sql);
			statement = connection.prepareStatement(sql);
			statement.setInt(1, Integer.parseInt(groupId));
			statement.executeUpdate();
			connection.commit();
			return true;
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

	/**
	 * 查找某一指定用户所在的所有群
	 */
	@Override
	public List<GroupAvatar> findAllGroupByUserName(String userName) {
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.Member.TABLE_NAME + ","+ I.Group.TABLE_NAME + ","+ I.Avatar.TABLE_NAME + " where "
				+ I.Member.USER_NAME + "=?" + 
				" and " + I.Member.GROUP_ID + "=" + I.Group.GROUP_ID 
				+ " and " + I.Avatar.AVATAR_TYPE + "=1 "
				+ " and " + I.Group.HX_ID + "=" + I.Avatar.USER_NAME + " ";
		System.out.println("connection=" + connection + ",sql=" + sql);
		PreparedStatement statement = null;
		ResultSet rs = null;
		List<GroupAvatar> listGroupAvatar = new ArrayList<GroupAvatar>();
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, userName);
			rs = statement.executeQuery();
			while (rs.next()) {
				GroupAvatar ga = new GroupAvatar();
				initGroupAvatar(rs, ga);
				listGroupAvatar.add(ga);
			}
			return listGroupAvatar;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(rs, statement, connection);
		}
		return null;
	}

	/**
	 * 查找所有的公开群，不包括当前用户已经所在的群
	 */
	@Override
	public List<GroupAvatar> findPublicGroups(String userName, int pageId, int pageSize) {
		List<GroupAvatar> listGroupAvatar = new ArrayList<GroupAvatar>();
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.Group.TABLE_NAME + ","+ I.Avatar.TABLE_NAME 
				+ " where " + I.Group.IS_PUBLIC + "=?"
				+ " and " + I.Group.HX_ID + "=" + I.Avatar.USER_NAME
				+ " and " + I.Avatar.AVATAR_TYPE + "=1 "
				+ " and " + I.Group.GROUP_ID + " not in ("
				+ "select distinct " + I.Member.GROUP_ID + " from " + I.Member.TABLE_NAME + " where " + I.Member.USER_NAME + "=?"
				+ ") limit ?,?";
		System.out.println("connection=" + connection + ",sql=" + sql);
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = connection.prepareStatement(sql);
			statement.setInt(1, I.GROUP_PUBLIC);
			statement.setString(2, userName);
			statement.setInt(3, (pageId-1)*pageSize);
			statement.setInt(4, pageSize);
			rs = statement.executeQuery();
			while (rs.next()) {
				GroupAvatar groupAvatar = new GroupAvatar();
				initGroupAvatar(rs, groupAvatar);
				listGroupAvatar.add(groupAvatar);
			}
			return listGroupAvatar;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(rs, statement, connection);
		}
		return null;
	}

	@Override
	public List<GroupAvatar> findGroupByGroupName(String groupName) {
		List<GroupAvatar> listGroupAvatar = new ArrayList<GroupAvatar>();
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.Group.TABLE_NAME + ","+ I.Avatar.TABLE_NAME 
				+ " where " + I.Group.NAME + " like ?"
				+ " and " + I.Group.HX_ID + "=" + I.Avatar.USER_NAME
				+ " and " + I.Avatar.AVATAR_TYPE + "=1 ";
		System.out.println("connection=" + connection + ",sql=" + sql);
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, "%"+groupName+"%");
			rs = statement.executeQuery();
			while (rs.next()) {
				GroupAvatar groupAvatar = new GroupAvatar();
				initGroupAvatar(rs, groupAvatar);
				listGroupAvatar.add(groupAvatar);
			}
			return listGroupAvatar;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(rs, statement, connection);
		}
		return null;
	}

	@Override
	public boolean uploadUserLocation(Location location) {
		Connection connection = JdbcUtils.getConnection();
		String sql = "insert into " + I.Location.TABLE_NAME + 
				"(" + I.Location.USER_NAME + "," + I.Location.LATITUDE + "," + I.Location.LONGITUDE + "," + I.Location.IS_SEARCHED + ","
				+ I.Location.UPDATE_TIME + ")values(?,?,?,?,?)";
		System.out.println("connection=" + connection + ",sql=" + sql);
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, location.getMLocationUserName());
			statement.setDouble(2, location.getMLocationLatitude());
			statement.setDouble(3, location.getMLocationLongitude());
			statement.setInt(4, Utils.boolean2int(location.getMLocationIsSearched()));
			statement.setString(5, location.getMLocationLastUpdateTime());
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

	@Override
	public boolean updateUserLocation(Location location) {
		Connection connection = JdbcUtils.getConnection();
		String sql = "update " + I.Location.TABLE_NAME + " set " + I.Location.LATITUDE + "=?," + I.Location.LONGITUDE
				+ "=?," + I.Location.IS_SEARCHED + "=?," + I.Location.UPDATE_TIME + "=?" + " where "
				+ I.Location.USER_NAME + "=?";
		System.out.println("connection=" + connection + ",sql=" + sql);
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(sql);
			statement.setDouble(1, location.getMLocationLatitude());
			statement.setDouble(2, location.getMLocationLongitude());
			statement.setInt(3, Utils.boolean2int(location.getMLocationIsSearched()));
			statement.setString(4, location.getMLocationLastUpdateTime());
			statement.setString(5, location.getMLocationUserName());
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
	public List<UserAvatar> findContactAllByUserName(String userName) {
		ResultSet rs = null;
		PreparedStatement statement = null;
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.Contact.TABLE_NAME + ","+ I.User.TABLE_NAME + ","+ I.Avatar.TABLE_NAME 
				+ " where "	+ I.Contact.USER_NAME + "=?" 
				+ " and " + I.User.USER_NAME + "=" + I.Contact.CU_NAME + " " 
				+ " and " + I.User.USER_NAME + "=" + I.Avatar.USER_NAME + " ";
		System.out.println("connection=" + connection + ",sql=" + sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, userName);
			rs = statement.executeQuery();
			List<UserAvatar> listUserAvatar = new ArrayList<UserAvatar>();
			while (rs.next()) {
				UserAvatar ua = new UserAvatar();
				initUserAvatar(rs,ua);
				listUserAvatar.add(ua);
			}
			return listUserAvatar;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(rs, statement, connection);
		}
		return null;
	}

	public Location getLocationByUserName(String userName){
		Connection connection = JdbcUtils.getConnection();
		String sql = "select * from " + I.Location.TABLE_NAME+" where "+I.Location.USER_NAME + "=?";
		PreparedStatement statement = null;
		ResultSet rs = null;
		Location location = new Location();
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, userName);
			rs = statement.executeQuery();
			if(rs.next()){
				location.setMLocationLatitude(rs.getDouble(I.Location.LATITUDE));
				location.setMLocationLongitude(rs.getDouble(I.Location.LONGITUDE));
			}
			return location;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(rs, statement, connection);
		}
		return null;
	}
	
	@Override
	public List<LocationUserAvatar> downloadLocation(String userName, String pageId, String pageSize) {
		Connection connection = JdbcUtils.getConnection();
		Location location = getLocationByUserName(userName);
		String sql = "SELECT *,LEFT ((2 * ASIN (SQRT (POW (SIN ((RADIANS ("+location.getMLocationLatitude()+") - RADIANS ("+I.Location.LATITUDE+")) / 2),"
						+"2) + COS (RADIANS("+location.getMLocationLatitude()+")) * COS (RADIANS("+I.Location.LATITUDE+")) * POW ("
						+"SIN ((RADIANS ("+location.getMLocationLongitude()+") - RADIANS ("+I.Location.LONGITUDE+")) / 2),2))) * 6378.137),4)"
						+ " AS distance"
						+ " FROM "+I.User.TABLE_NAME+","+I.Avatar.TABLE_NAME+","+I.Location.TABLE_NAME
						+" where "+I.User.USER_NAME+" != ? and "+I.User.USER_NAME+" = "+I.Location.USER_NAME
						+" and "+I.User.USER_NAME +" = " +I.Avatar.USER_NAME +" and "+ I.Avatar.AVATAR_TYPE + "= 0"
						+" HAVING distance <= "+I.DEFAULT_DISTANCE+" ORDER BY distance ASC limit ?,?";
		System.out.println("connection=" + connection + ",sql=" + sql);
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, userName);
			Integer niPageId = Integer.parseInt(pageId);
			Integer niPageSize = Integer.parseInt(pageSize);
			statement.setInt(2, (niPageId-1)*niPageSize);
			statement.setInt(3, niPageSize);
			rs = statement.executeQuery();
			List<LocationUserAvatar> listLocationUserAvatar = new ArrayList<LocationUserAvatar>();
			while (rs.next()) {
				LocationUserAvatar lua = new LocationUserAvatar();
				initUserAvatar(rs,lua);
				initLocation(rs,lua);
				lua.setDistance(rs.getDouble("distance"));
				listLocationUserAvatar.add(lua);
			}
			return listLocationUserAvatar;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.closeAll(rs, statement, connection);
		}
		return null;
	}
}
