import java.awt.List;
import java.io.File;
import java.util.ArrayList;

public class test {

    public static ArrayList<String> getFiles(String filepath){
    	File file = new File("C:\\\\Users\\\\24349\\\\eclipse-workspace\\\\voice_serve1.0\\\\pcmdata\\\\");
	    String[] fileNameLists = file.list();
		for(int i = 0; i < fileNameLists.length; i ++){
			//String[] temp = fileNameLists[i].split("\\.");// .号需要进行转义  加上右双斜杠
			System.out.println(fileNameLists[i]);
		}
		return null;
    }
    
    public static void main(String[] args) {
        //添加文件路径
        File dir = new File("./chuangjian");
        if (!dir.exists()) {// 判断目录是否存在     
            dir.mkdir();   
            System.out.println("创建成功");
        }
    }
}