package com.example.qingqiclient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.qingqiclient.adapter.FragmentAdapter;
import com.example.qingqiclient.fragment.ForgetPasswordFragment;
import com.example.qingqiclient.fragment.LoginFragment;
import com.example.qingqiclient.fragment.RegisterFragment;
import com.example.qingqiclient.fragment.ResetPasswordFragment;
import com.example.qingqiclient.utils.Constant;
import com.example.qingqiclient.utils.FontManager;
import com.example.qingqiclient.utils.Secret;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.crypto.NoSuchPaddingException;

import devliving.online.securedpreferencestore.DefaultRecoveryHandler;
import devliving.online.securedpreferencestore.SecuredPreferenceStore;

public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, View.OnClickListener{


    private ViewPager myviewpager;
    //fragment的集合，对应每个子页面
    private ArrayList<Fragment> fragments;

    //选项卡中的按钮
    private Button btn_first;
    private Button btn_second;
    private Button btn_third;
    private Button btn_four;
    //作为指示标签的按钮
    private ImageView cursor;
    //标志指示标签的横坐标
    float cursorX = 0;
    //所有按钮的宽度的数组
    private int[] widthArgs;
    //所有标题按钮的数组
    private Button[] btnArgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //对字体进行初始化改变
        FontManager.changeFonts((ViewGroup) getWindow().getDecorView().findViewById(R.id.main_acticity), (Activity) this);
        initView();
    }

    //初始化布局
    public void initView(){
        myviewpager = (ViewPager)this.findViewById(R.id.myviewpager);

        btn_first = (Button)this.findViewById(R.id.btn_first);
        btn_second = (Button)this.findViewById(R.id.btn_second);
        btn_third = (Button)this.findViewById(R.id.btn_third);
        btn_four = (Button)this.findViewById(R.id.btn_four);
        //初始化按钮数组
        btnArgs = new Button[]{btn_first,btn_second,btn_third,btn_four};
        //指示标签设置为红色
        cursor = (ImageView)this.findViewById(R.id.cursor_btn);
        cursor.setBackgroundColor(Color.RED);

        btn_first.setOnClickListener(this);
        btn_second.setOnClickListener(this);
        btn_third.setOnClickListener(this);
        btn_four.setOnClickListener(this);


        //先重置所有按钮颜色
        resetButtonColor();
        //再将第一个按钮字体设置为红色，表示默认选中第一个
        btn_first.setTextColor(Color.RED);

        fragments = new ArrayList<Fragment>();
        fragments.add(new LoginFragment());
        fragments.add(new RegisterFragment());
        fragments.add(new ForgetPasswordFragment());
        fragments.add(new ResetPasswordFragment());
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(),fragments);

        myviewpager.setAdapter(adapter);
        myviewpager.setOnPageChangeListener(this);

        resetButtonColor();
        btn_first.setTextColor(Color.RED);

    }

    //重置所有按钮的颜色
    public void resetButtonColor(){
        btn_first.setBackgroundColor(Color.parseColor("#DCDCDC"));
        btn_second.setBackgroundColor(Color.parseColor("#DCDCDC"));
        btn_third.setBackgroundColor(Color.parseColor("#DCDCDC"));
        btn_four.setBackgroundColor(Color.parseColor("#DCDCDC"));

        btn_first.setTextColor(Color.BLACK);
        btn_second.setTextColor(Color.BLACK);
        btn_third.setTextColor(Color.BLACK);
        btn_four.setTextColor(Color.BLACK);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(widthArgs==null){
            widthArgs = new int[]{btn_first.getWidth(),
                    btn_second.getWidth(),
                    btn_third.getWidth(),
                    btn_four.getWidth(),
                    };
        }
        //每次滑动首先重置所有按钮的颜色
        resetButtonColor();
        //将滑动到的当前按钮颜色设置为红色
        btnArgs[position].setTextColor(Color.RED);
        cursorAnim(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //每次滑动首先重置所有按钮的颜色
        resetButtonColor();
        //将滑动到的当前按钮颜色设置为红色
        btnArgs[state].setTextColor(Color.RED);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_first:
                myviewpager.setCurrentItem(0);
                break;
            case R.id.btn_second:
                myviewpager.setCurrentItem(1);
                break;
            case R.id.btn_third:
                myviewpager.setCurrentItem(2);
                break;
            case R.id.btn_four:
                myviewpager.setCurrentItem(3);
                break;

        }
    }

    //指示器的跳转，传入当前所处的页面的下标
    public void cursorAnim(int curItem){
        //每次调用，就将指示器的横坐标设置为0，即开始的位置
        cursorX = 0;
        //再根据当前的curItem来设置指示器的宽度
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)cursor.getLayoutParams();
        //减去边距*2，以对齐标题栏文字
        lp.width = widthArgs[curItem]-btnArgs[0].getPaddingLeft()*2;
        cursor.setLayoutParams(lp);
        //循环获取当前页之前的所有页面的宽度
        for(int i=0; i<curItem; i++){
            cursorX = cursorX + btnArgs[i].getWidth();
        }
        //再加上当前页面的左边距，即为指示器当前应处的位置
        cursor.setX(cursorX+btnArgs[curItem].getPaddingLeft());
    }
}
