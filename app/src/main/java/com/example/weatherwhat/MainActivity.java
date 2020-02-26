package com.example.weatherwhat;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.sql.Array;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{

    Toolbar toolbar;

    Fragment1 fragment1;
    Fragment2 fragment2;
    Fragment3 fragment3;

    public GpsTracker gpsTracker;
    private double latitude;
    private double longitude;

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, add_fab, checklist_fab;

    private LottieAnimationView animationView;

    private TextView cityField;
    private TextView updatedField;
    private TextView currentTemperatureField;
    private TextView weatherIcon;
    ArrayList<String> needs = new ArrayList<>();

    Handler handler;

    public MainActivity() {
        handler = new Handler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        //setSupportActionBar : 액션바 설정
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //액션바 기본 타이틀 보여지지 않게
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        //Fragment : 탭 클릭시 보여줄 화면들
        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();

        //기본으로 첫번째 Fragment를 보여지도록 설정
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();


        //TabLayout에 Tab 3개 추가
        TabLayout tabs = (TabLayout)findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("Today"));
        tabs.addTab(tabs.newTab().setText("My Closet"));
        tabs.addTab(tabs.newTab().setText("DailyLook Community"));

        //탭 선택리스너
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            //탭선택시
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.d("MainActivity", "선택된 탭 : "+position);

                Fragment selected = null;
                if(position==0){
                    selected = fragment1;
                }else if(position==1){
                    selected = fragment2;
                }else if(position==2){
                    selected = fragment3;
                }
                fragment1.needs.clear();

                getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
            }
            //탭선택해제시
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            //선탭된탭을 다시 클릭시
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
