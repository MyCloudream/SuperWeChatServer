package cn.ucai.superwechat.utils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	/**
	 * 向数组末尾添加一个元素
	 * @param list
	 * @param t
	 * @return
	 */
	public static <T> T[] add(T[] list, T t){
		if (t != null){
			list = Arrays.copyOf(list, list.length+1);
			list[list.length-1] = t;
		}
		return list;
	}
	
	public static boolean int2boolean(int i) {
		if(i>0){
			return true;
		}
		return false;
	}
	
	public static int boolean2int(boolean i) {
		if(i){
			return 1;
		}
		return 0;
	}

	/**
	 * 删除members中的member字符串，包括member前面或后面的逗号
	 * @param menbers
	 * @param member
	 * @return
	 */
	public static String deleteMember(String members, String member) {
		Pattern pattern = null;
		Matcher matcher = null;
		if(members.equals(member)){
			return "";
		}
		pattern = Pattern.compile("\\,"+member+"\\,");
		if(members.startsWith(member+",")){
			members = ","+members;
			matcher = pattern.matcher(members);
			members = matcher.replaceAll("");
		}else if(members.endsWith(","+member)){
			members += ",";
			matcher = pattern.matcher(members);
			members = matcher.replaceAll("");
		}else if(members.contains(","+member+",")){
			matcher = pattern.matcher(members);
			members = matcher.replaceAll(",");
		}
		return members;
	}
}
