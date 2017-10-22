package com.example.qingqiclient.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.NoSuchPaddingException;

import devliving.online.securedpreferencestore.DefaultRecoveryHandler;
import devliving.online.securedpreferencestore.SecuredPreferenceStore;


public class LoginFragment extends Fragment implements View .OnClickListener{

    private Button btn_login;
    private EditText tel_edit;
    private EditText password_edit;
    private TextView motto_text;
    private static String LOG = "MainActivity";

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_login, container,false);  ;

        //获得各组件
        btn_login = (Button) v.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        tel_edit = (EditText) v.findViewById(R.id.tel);
        password_edit = (EditText) v.findViewById(R.id.password);
        motto_text  = (TextView) v.findViewById(R.id.motto);

        //要先对加密开源库进行初始化
        try {
            SecuredPreferenceStore.init(getActivity().getApplicationContext(), new DefaultRecoveryHandler());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                //第一步，获得手机号和密码
                String tel = new String();
                String password = new String();
                tel = tel_edit.getText() == null?"":tel_edit.getText().toString();
                password = password_edit.getText() == null?"":password_edit.getText().toString();
                //第二步，确定手机号和密码是否为空或者是否有非法
                if (tel.equals("")){
                    tel_edit.setText("手机号不能为空！");
                    break;
                }
                if (password.equals("")){
                    password_edit.setText("密码不能为空！");
                    break;
                }
                if (!CheckInputUtils.checkTel(tel)){
                    Toast.makeText(getContext(), "手机号输入非法，请输入不带空格的十一位手机号码！", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (!CheckInputUtils.checkPassword(password)){
                    Toast.makeText(getContext(), "密码必须是由字母，数字，下划线组成的6到26位字符串", Toast.LENGTH_SHORT).show();
                    break;
                }
                //第三步，向服务器发送请求验证账号和密码
                String checkStr = Constant.getServer() + "/user/login?tel=" + tel + "&password=" + password;
                System.out.println(checkStr);
                doGet(checkStr);
        }
    }


    /**
     * Get请求，获得返回数据
     *
     * @param urlStr
     * @return
     * @throws Exception
     */
    public void   doGet(final String urlStr)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                HttpURLConnection conn = null;
                InputStream is = null;
                ByteArrayOutputStream baos = null;
                try
                {
                    url = new URL(urlStr);
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
                            //请求成功且验证成功的执行逻辑
                            navigateToAnother();
                        } else if (response.equals("0")){
                            //请求成功但验证不成功的逻辑
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

    private void showWrong() {
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                motto_text.setText("验证失败");
            }
        });
    }

    private void navigateToAnother() {
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //要注意先把tel和password的值存入SharedPreferences中，我们这里用了一个开源库进行加密
                SecuredPreferenceStore preferenceStore = SecuredPreferenceStore.getSharedInstance();
                preferenceStore.edit().putString("tel",tel_edit.getText().toString()).apply();
                preferenceStore.edit().putString("password", password_edit.getText().toString()).apply();
                Intent intent = new Intent(LoginFragment.this.getActivity().getApplicationContext(),All_EI_Info.class);
                startActivity(intent);
            }
        });
    }
}
