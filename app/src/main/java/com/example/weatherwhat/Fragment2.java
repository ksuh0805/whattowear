package com.example.weatherwhat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.ramotion.foldingcell.FoldingCell;

public class Fragment2 extends Fragment{
    private ImageView imageview;

    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<Cloth, ClothViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2, container, false);

        context = getContext();
        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = view.findViewById(R.id.ClothesList);
        mRecycler.setHasFixedSize(true);

        FloatingActionButton fabRegister = view.findViewById(R.id.fabRegister);
        fabRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ClosetActivity.class));
            }
        });
        /*
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
        Glide.with(this).load("http://10.0.2.2/firstapp/kk.jpg").into(imageview);*/

        //"http://10.0.2.2/firstapp/kk.jpg"
        // set on click event listener to list view

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query clothesQuery = getQuery(mDatabase);

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
    }

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

    public Query getQuery(DatabaseReference databaseReference){
        return databaseReference.child("user-clothes")
                .child(getUid());

    }
}
