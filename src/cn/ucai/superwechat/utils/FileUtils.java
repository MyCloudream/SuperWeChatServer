package cn.ucai.superwechat.utils;

import java.io.File;

public class FileUtils {

	/**
	 * 修改本地缓存的图片名称
	 * @param path
	 * @param oldImageName
	 * @param newImageName
	 */
	public static void renameImageFileName(String path,String oldImageName, 
			String newImageName) {
		File oldFile = new File(path,oldImageName);
		File newFile = new File(path,newImageName);
		boolean isRename = oldFile.renameTo(newFile);
		System.out.println("修改图片文件名成功:"+isRename);
	}
}
