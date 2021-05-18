package com.iflytek.util;


public class mood_analysis {
	static File_ways file_ways = new File_ways();
	static int ChaJudge = 100;// 最大差值比这个大就是大
	static int AvergeJudge = 1000; // 平均心情值比这个高那就是高
	private static final String MOOD_RECORD_PATH = "./data";
	private static final String MOOD_RECORD_NAME = "mood_record.txt";
	private static final String MOOD_DATA_PATH = "./fix_path/心情分析数据";
	
	public static String GetMoodAnalysis()// 根据心情值来查询相应的文件并返回 值是
	{
		String origin_data = file_ways.get_recent_name(MOOD_RECORD_PATH, MOOD_RECORD_NAME);
		String huashu = "";
		String TempStr[] = origin_data.split(" ");
		String zhi_lei = "低";
		String cha_lei = "小";
		int zhi = Integer.parseInt(TempStr[1]);
		int cha = Integer.parseInt(TempStr[0]);

		if(cha>ChaJudge)
		{
			cha_lei = "大";
			huashu += "最近你的情绪波动比较大,";
		}
		else
		{
			huashu += "最近你的情绪波动还算稳定,";
		}
		
		if(zhi>AvergeJudge)
		{
			zhi_lei = "高";
			huashu += "你的心情状态非常不错,下面一句话与君共勉:";
		}
		else
		{
			huashu += "你的心情状态不是很好,建议:";
		}
		String result = huashu;
		result += file_ways.get_random_line(MOOD_DATA_PATH + "//" + zhi_lei);
		result += file_ways.get_random_line(MOOD_DATA_PATH + "//" + cha_lei);
		if(zhi == 0 && cha == 0)
			result = "Good morning, and in case I don't see ya, good afternoon, good evening, and good night!                                                 当前暂无待分析的心情数据";
		return result;
	}
	public static void main(String[] args) {
		file_ways.build_many_file("F:\\temp\\final_desine\\服务端\\心情图表\\心情分析数据\\高\\temp.txt","F:\\temp\\final_desine\\服务端\\心情图表\\心情分析数据\\高");
	
	}
	
}
