package preprocessing.utility;

import java.io.File;

public class GetFileName {
	
	/**
	 * 获取输入文件的文件名
	 * @param filePath 输入文件所在路径
	 * @return 返回不带扩展名的文件名
	 */
	public String getFileName(String filePath){
		// 获得的文件名带文件类型的扩展名（例如：pos.txt）
		File tempFile = new File(filePath.trim()); 
		String fileNameExtension = tempFile.getName(); // 用于保存带扩展名的文件名
		// 去掉文件类型的扩展名（例如：pos）
		String fileName = null;						   // 用于保存不带扩展名的文件名
		int dot = fileNameExtension.lastIndexOf(".");
		if((dot > -1) && (dot < fileNameExtension.length())) {
			fileName = fileNameExtension.substring(0, dot);
		}
//		System.out.println(fileName);
		return fileName;
	}
	
}
