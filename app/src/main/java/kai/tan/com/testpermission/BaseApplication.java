package kai.tan.com.testpermission;

import android.app.Activity;
import android.app.Application;
import android.os.Build;

import com.jiongbull.jlog.JLog;
import com.jiongbull.jlog.constant.LogLevel;

import java.util.Stack;

/**
 * 
 * Created by tankai on 2016/4/1.
 */
public class BaseApplication extends Application {

    //Activity堆
    private Stack<Activity> activityStack = new Stack<Activity>();

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化log日志工具,发布记得关闭注释
        JLog.init(getApplicationContext()).setDebug(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

        } else {
            init();
        }
    }

    //初始化
    public void init() {
        JLog.getSettings().getLogLevelsForFile().add(LogLevel.DEBUG);
        JLog.getSettings().writeToFile(true).setLogDir("jlog_permission");
    }


}
