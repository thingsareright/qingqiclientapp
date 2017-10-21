package com.example.qingqiclient.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.qingqiclient.All_EI_Info;
import com.example.qingqiclient.R;
import com.example.qingqiclient.utils.CheckInputUtils;
import com.example.qingqiclient.utils.Constant;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import devliving.online.securedpreferencestore.SecuredPreferenceStore;

/**
 *这个碎片主要用于在已经知道原密码的情况下更改密码
 */
public class ResetPasswordFragment extends Fragment implements View.OnClickListener{

    //先定义一下用到的控件
    private EditText tel;
    private EditText old_password;
    private EditText new_password;
    private EditText new_password_confirmed;
    private Button submmit_btn;


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_reset_password, container, false);
        //初始化控件
        tel = (EditText) v.findViewById(R.id.tel);
        old_password = (EditText) v.findViewById(R.id.old_password);
        new_password = (EditText) v.findViewById(R.id.new_password);
        new_password_confirmed = (EditText) v.findViewById(R.id.new_password_confirm);
        //设置按钮点击事件
        submmit_btn = (Button) v.findViewById(R.id.submmit);
        submmit_btn.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.submmit:
                //第一步，先看老密码，新密码，新密码的确认密码是否符合密码格式
                if (CheckInputUtils.checkPassword(old_password.getText().toString()) == false) {
                    Toast.makeText(getContext(), "原密码输入错误", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (CheckInputUtils.checkPassword(new_password.getText().toString()) == false ||
                        CheckInputUtils.checkPassword(new_password_confirmed.getText().toString()) == false){
                    Toast.makeText(getContext(), "新密码输入不符合密码格式", Toast.LENGTH_SHORT).show();
                }
                String tel_str = tel.getText().toString();
                String old_password_str = old_password.getText().toString();
                String new_password_str = new_password.getText().toString();
                String new_password_confirm_str = new_password_confirmed.getText().toString();
                //判断新的两个密码是否相同
                if (!new_password_str.equals(new_password_confirm_str)){
                    Toast.makeText(getContext(), "新密码不一致", Toast.LENGTH_SHORT).show();
                    break;
                }
                //判断手机号是否输入符合格式
                if (CheckInputUtils.checkTel(tel_str) == false){
                    Toast.makeText(getContext(), "手机号不符合格式", Toast.LENGTH_SHORT).show();
                    break;
                }

                //通过网络请求进行验证
                //先组合网络请求url
                String url = Constant.getServer() + "/user/changepassword?tel=" + tel_str + "&password="
                 + old_password_str + "&newpassword=" + new_password_confirm_str;
                sendResetPasswordRequestWithOkHttp(url);

                break;
            default:
                break;
        }
    }

    private void sendResetPasswordRequestWithOkHttp(final String url_str) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                HttpURLConnection conn = null;
                InputStream is = null;
                ByteArrayOutputStream baos = null;
                try
                {
                    url = new URL(url_str);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("accept", "*/*");
                    conn.setRequestProperty("connection", "Keep-Alive");


                    //下面对获取到的输入流进行读取
                    is = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder responseBulder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine() )!= null){
                        responseBulder.append(line);
                    }
                    String response = responseBulder.toString();
                    if (response != null ){
                        if (response.equals("1")){
                            //请求更改密码成功的执行逻辑
                            navigateToAnother();
                        } else if (response.equals("0")){
                            //请求成功但更改密码不成功的逻辑
                            showWrong();
                        }
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                } finally
                {
                    try
                    {
                        if (is != null)
                            is.close();
                    } catch (IOException e)
                    {
                    }
                    try
                    {
                        if (baos != null)
                            baos.close();
                    } catch (IOException e)
                    {
                    }
                    conn.disconnect();
                }



            }
        }).start();
    }


    /**
     * 更改密码不成功的逻辑
     */
    private void showWrong() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), "更改密码不成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 更改密码成功的逻辑
     */
    private void navigateToAnother() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //把账户和新密码存入SharedPreference
                Toast.makeText(getContext(), "更改密码成功", Toast.LENGTH_SHORT).show();
                //要注意先把tel和password的值存入SharedPreferences中，我们这里用了一个开源库进行加密
                SecuredPreferenceStore preferenceStore = SecuredPreferenceStore.getSharedInstance();
                preferenceStore.edit().putString("tel",tel.getText().toString()).apply();
                preferenceStore.edit().putString("password", new_password.getText().toString()).apply();
                Intent intent = new Intent(ResetPasswordFragment.this.getActivity(),All_EI_Info.class);
                startActivity(intent);
            }
        });
    }
}
