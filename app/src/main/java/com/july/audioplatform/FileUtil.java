package com.july.audioplatform;

import android.os.Environment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @ProjectName: AudioPlatform
 * @Package: com.july.audioplatform
 * @ClassName: FileUtil$
 * @Description: 用于管理记录的文件内容
 * @Author: Administrator
 * @CreateDate: 2019/7/27 13:35
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/7/27 13:35
 * @UpdateRemark: 更新内容
 * @Version: 1.0
 */
public class FileUtil {
    public static String absolutePath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/RecordData/";
    public static final int WAV=1;
    public static final int ACC=2;
    public static String getFilePathName(int type,String date){
        String path=absolutePath;
        if(type==WAV){
            path+="Audio "+date+".wav";
        }else if(type==ACC){
            path+="Acceleration "+date+".txt";
        }
        return path;
    }
    public static String getTime(){
        String currentTime;
        DateFormat df;
        df = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss", Locale.US);
        currentTime=df.format(new Date());
        return currentTime;
    }
}
