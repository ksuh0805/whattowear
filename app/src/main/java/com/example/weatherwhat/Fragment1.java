package com.example.weatherwhat;

import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Fragment1 extends Fragment implements View.OnClickListener{
    public GpsTracker gpsTracker;
    private double latitude;
    private double longitude;

    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Cloth, ClothViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private Context context;
    private LinearLayoutManager mManager;

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, add_fab, checklist_fab;

    private LottieAnimationView animationView;

    private double temp_max;
    private TextView max;
    private TextView cityField;
    //private TextView updatedField;
    private TextView currentTemperatureField;
    //private TextView weatherIcon;
    ArrayList<String> needs = new ArrayList<>();
    Query clothesQuery;

    Handler handler;

    public Fragment1() {
        handler = new Handler();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, container, false);
        cityField = (TextView) view.findViewById(R.id.city_field);
        //updatedField = (TextView) view.findViewById(R.id.updated_field);
        currentTemperatureField = (TextView) view.findViewById(R.id.current_temperature_field);
        //weatherIcon = (TextView) findViewById(R.id.weather_icon);
        //max = (TextView) view.findViewById(R.id.max);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        context = getContext();
        mRecycler = view.findViewById(R.id.ClothList);
        mRecycler.setHasFixedSize(true);

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
        Log.d("latitude",latitude+ ", " +longitude);
        needs.add("a");
        needs.add("b");
        String cloth = "가디건";
        //clothesQuery = getQuery(mDatabase, cloth);
        return view;
    }

    private void updateWeatherData(final double lat, final double lon) {
        new Thread() {
            public void run() {
                final JSONObject json = RemoteFetch.getJSON(getContext(), lat, lon);
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
            String formatTemp = "현재 기온 : " + main.getDouble("temp") + " ℃";
            //currentTemperatureField.setText(formatTemp);
            temp_max = main.getDouble("temp_max");
            String formatMax = "오늘의 최고 기온 : "+temp_max+" ℃ / 최저 기온 : " +main.getDouble("temp_min") + " ℃";
            currentTemperatureField.setText(formatMax);

            if(temp_max >= 28){
                clothesQuery = getQuery(mDatabase, "반팔");
            }else if(temp_max < 28 && temp_max >= 20){
                clothesQuery = getQuery(mDatabase, "긴팔");
            }else if(temp_max <20 && temp_max >= 17 ){
                Log.d("maxxx", "ddddx");
                clothesQuery = getQuery(mDatabase, "가디건");
            }else if(temp_max <17 && temp_max >= 12 ){
                clothesQuery = getQuery(mDatabase, "자켓");
            }else if(temp_max <12 && temp_max >= 5 ){
                clothesQuery = getQuery(mDatabase, "코트");
            }else{
                clothesQuery = getQuery(mDatabase, "패딩");
            }

            max.setText(formatMax);
            if(main.getDouble("temp_max") >= 30){
                needs.add("양산");
            }else{
                needs.add("노양산");
            }

            /*
            // Set update message
            DateFormat df = DateFormat.getDateTimeInstance();
            String updateTime = df.format(new Date((json.getLong("dt") + json.getLong("timezone")) * 1000));
            String updateMsg = "마지막 업데이트: ";
            String updateText = updateMsg + updateTime;
            updatedField.setText(updateText);
            Log.d("TAG", "NOdd");
            */

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
    public void setClear(){
        needs.clear();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        /*
        Query clothesQuery = getQuery(mDatabase, "반팔");

        Log.d("maxxx", String.valueOf(temp_max));
        if(temp_max >= 28){
            clothesQuery = getQuery(mDatabase, "반팔");
        }else if(temp_max < 28 && temp_max >= 20){
            clothesQuery = getQuery(mDatabase, "긴팔");
        }else if(temp_max <20 && temp_max >= 17 ){
            Log.d("maxxx", "ddddx");
            clothesQuery = getQuery(mDatabase, "가디건");
        }else if(temp_max <17 && temp_max >= 12 ){
            clothesQuery = getQuery(mDatabase, "자켓");
        }else if(temp_max <12 && temp_max >= 5 ){
            clothesQuery = getQuery(mDatabase, "코트");
        }else{
            clothesQuery = getQuery(mDatabase, "패딩");
        }
        //Query clothes2Query = getQuery(mDatabase, "반팔");*/


        if(clothesQuery == null){
        try{
            Thread.sleep(20000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }}
        else{
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Cloth>()
                .setQuery(clothesQuery, Cloth.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Cloth, ClothViewHolder>(options) {

            @Override
            public ClothViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new ClothViewHolder(inflater.inflate(R.layout.cell, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(ClothViewHolder viewHolder, int position, final Cloth model) {
                final DatabaseReference clothRef = getRef(position);

                // Set click listener for the whole post view
                /*final String clothKey = clothRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                        intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, clothKey);
                        startActivity(intent);
                    }
                });*/


                // Bind Post to ViewHolder
                viewHolder.bindToCloth(context, model);
            }
        };
        mRecycler.setAdapter(mAdapter);
    }}

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public Query getQuery(DatabaseReference databaseReference, String cloth){

        Log.d("xmaxxx", cloth);
        //return databaseReference.child("user-clothes-category").child(getUid()).child("반팔");
         return databaseReference.child("user-clothes-category").child(getUid()).child(cloth);
    }
}
