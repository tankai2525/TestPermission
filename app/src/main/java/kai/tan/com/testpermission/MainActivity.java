package kai.tan.com.testpermission;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

import com.jiongbull.jlog.JLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 安卓6.0运行权限测试
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "permission";

    private static final int REQUEST_PERMISSION_TOW_CODE = 10000;
    private static final int REQUEST_PERMISSION_PHONE_CODE = 1;
    private static final int REQUEST_PERMISSION_WRITE_CODE = 2;
    private static final int REQUEST_PERMISSION_SETTING_CODE = 3;
    private static final int REQUEST_PERMISSION_ALERT_WINDOW_CODE = 4;
    private static final int REQUEST_PERMISSION_LOCATION_FINE_CODE = 5;
    private static final int REQUEST_PERMISSION_LOCATION_COARSE_CODE = 6;
    private static final int REQUEST_PERMISSION_CAMERA_CODE = 7;
    private static final int REQUEST_INTENT_CAMERA_CODE = 8;
    private static final int REQUEST_PERMISSION_PHONE_CALL_CODE = 9;

    private TelephonyManager tm;
    private Context mContext;
    private BaseApplication base;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        base = (BaseApplication) getApplication();
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //读取imei
        //小米6.0默认拥有该权限，但是清除数据后，就没有了，估计是个bug，解决办法就是主动去请求
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "imei:" + tm.getDeviceId(), Toast.LENGTH_LONG).show();
            }
        });

        //获取只读电话权限
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED) {
                        JLog.d("imei权限1：" + checkSelfPermission(Manifest.permission.READ_PHONE_STATE));
                        //小米6.0 请求imei不提示，默认获取
                        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PERMISSION_PHONE_CODE);
                        JLog.d("imei权限2：" + checkSelfPermission(Manifest.permission.READ_PHONE_STATE));
                    }
                }
            }
        });

        //写文件
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String dir = Environment.getExternalStorageDirectory() + File.separator + "testpermission" + File.separator + System.currentTimeMillis();
                    File file = new File(dir);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                }
            }
        });

        //获取读写权限
        //小米6.0默认拥有该权限
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        JLog.d("读写权限1：" + checkSelfPermission(Manifest.permission.READ_PHONE_STATE));
                        //小米6.0 请求imei不提示，默认获取
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_CODE);

                    }
                }
            }
        });

        //获取系统设置权限
        //小米6.0默认有，清除数据后还有
        //模拟器6.0 默认没有，清除数据后还有
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestWriteSetting();
            }
        });

        //获取悬浮窗权限
        //小米6.0默认没有，清除数据后还有
        //模拟器6.0 默认没有，清除数据后还有
        findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAlertWindow();
            }
        });

        //开启悬浮窗 悬浮窗有问题暂时不管
        findViewById(R.id.button7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FxService.class);
                //启动FxService
                startService(intent);
            }
        });

        //停止悬浮窗
        findViewById(R.id.button8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FxService.class);
                //终止FxService
                stopService(intent);
            }
        });

        //请求多个权限
        findViewById(R.id.button9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    List<String> permissionList = new ArrayList<String>();
                    //如果没有READ_PHONE_STATE权限就请求

                    if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED) {
                        permissionList.add(Manifest.permission.READ_PHONE_STATE);
                    }

                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }

                    if (permissionList.size() > 0) {//如果有权限要申请，才申请。
                        //          Toast.makeText(mAppContext, getString(R.string.app_name) + "需要这些权限，否则不能正常运行", Toast.LENGTH_SHORT).show();
                        String[] permissionArray = (String[]) permissionList.toArray(new String[permissionList.size()]);
                        requestPermissions(permissionArray, REQUEST_PERMISSION_TOW_CODE);
                    }

                }
            }
        });

        //精确定位
        findViewById(R.id.button10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    JLog.d("精确定位");
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION_FINE_CODE);
                    }
                }
            }
        });

        //大致定位
        findViewById(R.id.button11).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    JLog.d("大致定位");
                    if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION_COARSE_CODE);
                    }
                }
            }
        });

        findViewById(R.id.button11_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JLog.d(getPackageName());
                ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                manager.killBackgroundProcesses("kai.tan.com.testpermission");
//                startActivity(new Intent(mContext, LocationActivity.class));
            }
        });

        //摄像头
        findViewById(R.id.button12).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA_CODE);
                    }
                }
            }
        });

        //开启摄像头
        findViewById(R.id.button13).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_INTENT_CAMERA_CODE);
            }
        });

        //请求打电话权限
        findViewById(R.id.button14).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION_PHONE_CALL_CODE);
                    }
                }
            }
        });

//        findViewById(R.id.button15).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intentPhone = new Intent(Intent.ACTION_CALL, Uri.parse("tel:15897351506"));
//                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION_PHONE_CALL_CODE);
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return;
//                }
//                startActivity(intentPhone);
//            }
//        });

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            base.init();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //正常权限
            JLog.d("是否有访问网络权限：" + checkSelfPermission(Manifest.permission.INTERNET));

            //两个特殊权限，它们是这样检查的
            JLog.d("是否有修改系统设置权限：" + Settings.System.canWrite(this));
            JLog.d("是否有悬浮窗权限：" + Settings.canDrawOverlays(this));

            //危险权限 0 是有 -1 是没有
            JLog.d("是否有imei权限：" + checkSelfPermission(Manifest.permission.READ_PHONE_STATE));
            JLog.d("是否有打电话权限：" + checkSelfPermission(Manifest.permission.CALL_PHONE));
            JLog.d("是否有读写权限：" + checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));
            JLog.d("是否有精确定位权限：" + checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION));
            JLog.d("是否有大致定位权限：" + checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION));
            JLog.d("是否有摄像头权限：" + checkSelfPermission(Manifest.permission.CAMERA));

        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //如果这里还没获取到权限，那就确定用户已经彻底拒绝了该权限，那么我们要弹个提示窗告诉用户怎么去开启权限
        if (requestCode == REQUEST_PERMISSION_PHONE_CODE) {
            JLog.d("imei权限3:" + grantResults[0]);
        }

        if (requestCode == REQUEST_PERMISSION_PHONE_CALL_CODE) {
            JLog.d("打电话权限3:" + grantResults[0]);
        }

        if (requestCode == REQUEST_PERMISSION_WRITE_CODE) {
            JLog.d("读写权限3:" + grantResults[0]);
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                base.init();
            }
        }

        if (requestCode == REQUEST_PERMISSION_TOW_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                JLog.d("imei权限3:" + grantResults[0]);
                JLog.d("读写权限3:" + grantResults[1]);
            }
        }

        if (requestCode == REQUEST_PERMISSION_LOCATION_FINE_CODE) {
            JLog.d("精确定位权限3:" + grantResults[0]);
        }

        if (requestCode == REQUEST_PERMISSION_LOCATION_COARSE_CODE) {
            JLog.d("大致定位权限3:" + grantResults[0]);
        }

        if (requestCode == REQUEST_PERMISSION_CAMERA_CODE) {
            JLog.d("摄像头权限3:" + grantResults[0]);
        }

    }

    /**
     * 特殊权限
     * 请求系统设置权限
     */
    private void requestWriteSetting() {
        //        使用Action Settings.ACTION_MANAGE_WRITE_SETTINGS 启动隐式Intent
        //        使用"package:" + getPackageName()携带App的包名信息
        //        使用Settings.System.canWrite方法检测授权结果
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_PERMISSION_SETTING_CODE);
        }
    }

    /**
     * 特殊权限
     * 请求悬浮窗权限
     */
    private void requestAlertWindow() {
        //        使用Action Settings.ACTION_MANAGE_OVERLAY_PERMISSION启动隐式Intent
        //        使用"package:" + getPackageName()携带App的包名信息
        //        使用Settings.canDrawOverlays方法判断授权结果
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_PERMISSION_ALERT_WINDOW_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                JLog.d("是否有修改系统设置权限3：" + Settings.System.canWrite(this));
            }
        }

        if (requestCode == REQUEST_PERMISSION_ALERT_WINDOW_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                JLog.d("是否有悬浮窗权限：" + Settings.canDrawOverlays(this));
            }
        }

        if (requestCode == REQUEST_INTENT_CAMERA_CODE) {
            JLog.d("照相回来了：resultCode：" + resultCode + "-data:" + data);
        }
    }
}
