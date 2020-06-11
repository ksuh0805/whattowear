package com.example.weatherwhat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ramotion.foldingcell.FoldingCell;

public class ClothViewHolder extends RecyclerView.ViewHolder {

    public TextView nameView;
    public TextView username;
    public ImageView imageView;
    public ImageView head_image;
    public TextView contentNameView;
    public TextView contentNameView2;
    public TextView infoView;
    public TextView content_cloth_name;
    public TextView content_cloth_info;
    public ImageView category_image;
    public TextView content_cloth_location;

    public ClothViewHolder(final View itemView) {
        super(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = getAdapterPosition();
                if(pos != RecyclerView.NO_POSITION){
                    FoldingCell fc = (FoldingCell) itemView.findViewById(R.id.folding_cell_ex);
                    fc.toggle(false);
                }
            }
        });

        nameView = itemView.findViewById(R.id.cell_name);
        username = itemView.findViewById(R.id.user);
        imageView = itemView.findViewById(R.id.title_image);
        head_image = itemView.findViewById(R.id.head_image);
        contentNameView = itemView.findViewById(R.id.content_name_view);
        contentNameView2 = itemView.findViewById(R.id.content_name);
        infoView = itemView.findViewById(R.id.cell_info);
        category_image = itemView.findViewById(R.id.content_category);
        content_cloth_name = itemView.findViewById(R.id.content_cloth_name);
        content_cloth_info = itemView.findViewById(R.id.content_cloth_info);
        content_cloth_location = itemView.findViewById(R.id.content_cloth_location);
    }

    public void bindToCloth(Context context, Cloth cloth) {
        nameView.setText(cloth.name);
        contentNameView.setText(cloth.category);
        contentNameView2.setText(cloth.name);
        username.setText(cloth.author + " 's");
        //numStarsView.setText(String.valueOf(post.starCount));
        infoView.setText(cloth.info);
        content_cloth_name.setText(cloth.name);
        content_cloth_info.setText(cloth.info);
        Glide.with(context).load(cloth.imgUrl).into(imageView);
        Glide.with(context).load(cloth.imgUrl).into(head_image);

        Log.d("stringk", String.valueOf(cloth.category));
        if(String.valueOf(cloth.category).equals("반팔")){
            Log.d("stringk", "ddddddd");
            category_image.setImageResource(R.drawable.tshirt);
        }else if(cloth.category.equals("가디건")){
            category_image.setImageResource(R.drawable.cardigan);
        }else if(cloth.category.equals("긴팔")){
            category_image.setImageResource(R.drawable.longsleeve);
        }else if(cloth.category.equals("패딩")){
            category_image.setImageResource(R.drawable.padding);
        }else if(cloth.category.equals("긴바지")){
            category_image.setImageResource(R.drawable.jean);
        }else if(cloth.category.equals("반바지")) {
            category_image.setImageResource(R.drawable.shorts);
        }else if(cloth.category.equals("치마")) {
            category_image.setImageResource(R.drawable.skirt);
        }
    }
}
