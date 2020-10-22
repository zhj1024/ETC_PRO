package com.example.myapplication.util;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;

public class MyApplication extends Application {

    public static ArrayList<Activity> list;
    private static MyApplication myApp = null;

    //要保证每个Activity中使用的MyApplication都是同一个，就用到了单例模式
    //单例模式：只有一个对象，也就是上面的那个myApp只实例化一次
    //由于MyApplication继承自Application，复写的构造函数访问权限不能低于父类的，所以不能为private
    //所以我们在这里实现一个伪单例模式
    public static MyApplication getInstance(){
        if(myApp == null ){
            myApp = new MyApplication();
            list = new ArrayList<Activity>();
        }
        return myApp;
    }
    /**
     * 添加activity到数组中
     * @param activity
     */
    public void addActivity(Activity activity){
        list.add(activity);
    }

    /**
     * 退出应用，结束所有activity
     */
    public void exitApp(){
        for (Activity activity : list) {
            activity.finish();
        }
    }
}
