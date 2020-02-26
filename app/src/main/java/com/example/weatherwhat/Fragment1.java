package com.example.weatherwhat;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Fragment1 extends Fragment implements View.OnClickListener{
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

    public Fragment1() {
        handler = new Handler();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, container, false);
        cityField = (TextView) view.findViewById(R.id.city_field);
        updatedField = (TextView) view.findViewById(R.id.updated_field);
        currentTemperatureField = (TextView) view.findViewById(R.id.current_temperature_field);
        //weatherIcon = (TextView) findViewById(R.id.weather_icon);

        animationView = (LottieAnimationView) view.findViewById(R.id.lottie);

        gpsTracker = new GpsTracker(getActivity());
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();

        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        add_fab = (FloatingActionButton) view.findViewById(R.id.add_fab);
        checklist_fab = (FloatingActionButton) view.findViewById(R.id.checklist_fab);

        fab.setOnClickListener(this);
        add_fab.setOnClickListener(this);
        checklist_fab.setOnClickListener(this);

        updateWeatherData(latitude, longitude);
        needs.add("a");
        needs.add("b");
        return view;
    }

    private void updateWeatherData(final double lat, final double lon) {
        new Thread() {
            public void run() {
                final JSONObject json = RemoteFetch.getJSON(getContext(), 37.399319, 126.966911);
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(getContext(),
                                    R.string.place_not_found,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json) {
        try {
            // Parse JSON object and array using json object
            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");

            // Set location (city and country)
            cityField.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            /* set details field
            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + main.getString("humidity") + "%" +
                            "\n" + "Pressure: " + main.getString("pressure") + " hPa");
*/
            // Set temperature field
            String formatTemp = main.getDouble("temp") + " ℃";
            if(main.getDouble("temp_max") >= 30){
                needs.add("양산");
            }else{
                needs.add("노양산");
            }
            currentTemperatureField.setText(formatTemp);

            // Set update message
            DateFormat df = DateFormat.getDateTimeInstance();
            String updateTime = df.format(new Date(json.getLong("dt") * 1000));
            String updateMsg = "Last update: ";
            String updateText = updateMsg + updateTime;
            updatedField.setText(updateText);
            Log.d("TAG", "NOdd");

            // Use setWeatherIcon method - pass 2 parameters id and icon
            setWeatherIcon(details.getInt("id"), details.getString("icon"));

        } catch (Exception e) {
            Log.e("SimpleWeather", "One or more fields not found in the JSON data" + e);
        }
    }

    private void setWeatherIcon(int actualId, String openIcon) {
        int id = actualId / 100;
        String icon = "";
        if (actualId == 800) {
            if (openIcon.equals("01d")) {
                //icon = MainActivity.this.getString(R.string.weather_sunny);
                animationView.setAnimation("sunny2.json");
                needs.add("양산");
            } else {
                //icon = MainActivity.this.getString(R.string.weather_clear_night);
                animationView.setAnimation("clear_night.json");
            }
        } else {
            switch (id) {
                case 2:
                    //icon = MainActivity.this.getString(R.string.weather_thunder);
                    animationView.setAnimation("thunder.json");
                    needs.add("우산");
                    break;
                case 3:
                    //icon = MainActivity.this.getString(R.string.weather_drizzle);
                    animationView.setAnimation("rain.json");
                    needs.add("우산");
                    break;
                case 7:
                    animationView.setAnimation("haze.json");
                    //icon = MainActivity.this.getString(R.string.weather_foggy);
                    break;
                case 8:
                    animationView.setAnimation("cloudy.json");
                    //icon = MainActivity.this.getString(R.string.weather_cloudy);
                    break;
                case 6:
                    animationView.setAnimation("snow.json");
                    //icon = MainActivity.this.getString(R.string.weather_snowy);
                    needs.add("우산(눈)");
                    break;
                case 5:
                    if(actualId == 502 || actualId == 503 || actualId == 504){
                        animationView.setAnimation("storm.json");}
                    else{
                        animationView.setAnimation("rain.json");
                    }
                    //icon = MainActivity.this.getString(R.string.weather_rainy);
                    needs.add("우산");
                    break;
            }
        }
        //weatherIcon.setText(icon);
        animationView.playAnimation();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                anim();
                Toast.makeText(getActivity(), "Check List", Toast.LENGTH_SHORT).show();
                break;
            case R.id.add_fab:
                anim();
                //Toast.makeText(this, "Button1", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder ad = new AlertDialog.Builder(getContext());

                ad.setTitle("내일 준비물");       // 제목 설정
                ad.setMessage("준비물을 입력해주세요");   // 내용 설정

                // EditText 삽입하기
                final EditText et = new EditText(getContext());
                ad.setView(et);

                // 확인 버튼 설정
                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("TAG", "Yes Btn Click");

                        // Text 값 받아서 로그 남기기
                        String value = et.getText().toString();
                        value = value.trim();
                        if(value.length() == 0){
                            Log.d("TAG", "NO");
                        }else {
                            needs.add(value);
                            Log.d("TAG", "TTT");
                        }
                        Log.d("TAG", value);

                        dialog.dismiss();     //닫기
                        // Event
                    }
                });

                // 취소 버튼 설정
                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("TAG","No Btn Click");
                        dialog.dismiss();     //닫기
                        // Event
                    }
                });
                // 창 띄우기
                ad.show();
                break;
            case R.id.checklist_fab:
                anim();
                //Toast.makeText(this, "Button2", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("오늘의 준비물");
                String items = "";
                if(needs.isEmpty()) {
                    builder.setMessage("없음");
                }else {
                    for(int i = 0; i<needs.size(); i++){
                        if(i == needs.size()-1){
                            items += needs.get(i);
                        }else{
                            items += needs.get(i) + ", ";
                        }
                    }
                    Log.d("errrrr", items);
                    builder.setMessage(items);
                }
                builder.setPositiveButton("챙겼어요", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        needs.clear();
                        Toast.makeText(getActivity(), "목록을 삭제했습니다", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("아직 못 챙겼어요", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        //needs.clear();
                        //Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alertDialog = builder.create();

                alertDialog.show();
                break;
        }
    }
    public void anim() {

        if (isFabOpen) {
            add_fab.startAnimation(fab_close);
            checklist_fab.startAnimation(fab_close);
            add_fab.setClickable(false);
            checklist_fab.setClickable(false);
            isFabOpen = false;
        } else {
            add_fab.startAnimation(fab_open);
            checklist_fab.startAnimation(fab_open);
            add_fab.setClickable(true);
            checklist_fab.setClickable(true);
            isFabOpen = true;
        }
    }
}
