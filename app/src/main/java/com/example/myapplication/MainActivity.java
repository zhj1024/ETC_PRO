package com.example.myapplication;

import android.graphics.Color;
import android.graphics.ImageFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.pojo.MySession;
import com.example.myapplication.ui.dashboard.DashboardFragment;
import com.example.myapplication.ui.home.HomeFragment;
import com.example.myapplication.util.ConstantClass;
import com.example.myapplication.util.MyApplication;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard)
                .build();

//        , R.id.navigation_notifications
        final MySession mySession = (MySession) getApplication();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        Glide.with(this).load(R.mipmap.logo).bitmapTransform(new BlurTransformation(this, 20));
        if (mySession.getSession_token() != null){
            HomeFragment fragment = new HomeFragment();
            Bundle bundle = new Bundle();
            bundle.putString("data",mySession.getSession_token());
            fragment.setArguments(bundle);//数据传递到fragment中
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment,fragment);
            fragmentTransaction.commit();
        }





//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.BLUE);//改变状态栏颜色（可以和应用的标题颜色一样）
//            window.setNavigationBarColor(Color.TRANSPARENT);
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            long secondClickBack = System.currentTimeMillis();
            if(secondClickBack - ConstantClass.firstClickBack >2000){
                ConstantClass.firstClickBack = System.currentTimeMillis();
                Toast.makeText(this, "再按一次退出当前应用", Toast.LENGTH_LONG).show();
            }else{
                //finish();
                //System.exit(0);
                finish();
                System.exit(0);
                //MyApplication.getInstance().exitApp();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(new Bundle());
    }


    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        //super.onAttachFragment(fragment);
    }

}