package com.example.weatherwhat;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.ramotion.foldingcell.FoldingCell;

public class Fragment2 extends Fragment{
    private ImageView imageview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2, container, false);

        imageview = (ImageView) view.findViewById(R.id.imageview);

        // get our folding cell
        final FoldingCell fc = (FoldingCell) view.findViewById(R.id.folding_cell);

        // attach click listener to folding cell
        fc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fc.toggle(false);
            }
        });
        Glide.with(this).load("http://10.0.2.2/firstapp/kk.jpg").into(imageview);


        return view;
    }
}
