package com.example.weatherwhat;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ramotion.foldingcell.FoldingCell;

public class ClothViewHolder extends RecyclerView.ViewHolder {

    public TextView nameView;
    public TextView categoryView;
    public ImageView imageView;
    public ImageView head_image;
    public TextView numStarsView;
    public TextView infoView;

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
        //categoryView = itemView.findViewById(R.id.postAuthor);
        imageView = itemView.findViewById(R.id.imageview_ex);
        head_image = itemView.findViewById(R.id.head_image);
        //numStarsView = itemView.findViewById(R.id.postNumStars);
        infoView = itemView.findViewById(R.id.cell_info);
    }

    public void bindToCloth(Context context, Cloth cloth) {
        nameView.setText(cloth.name);
        //categoryView.setText(cloth.category);
        //numStarsView.setText(String.valueOf(post.starCount));
        infoView.setText(cloth.info);
        Glide.with(context).load(cloth.imgUrl).into(imageView);
        Glide.with(context).load(cloth.imgUrl).into(head_image);
    }
}
