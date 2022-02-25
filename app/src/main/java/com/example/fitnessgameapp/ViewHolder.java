package com.example.fitnessgameapp;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {

    View view;

    public ViewHolder(@NonNull View itemView) {

        super(itemView);

        view = itemView;
    }

    public void setDetails(Context ctx, String title, String description, String image) {

        TextView mTitleTv = view.findViewById(R.id.rTitleTv);

        ImageView mImageTv = view.findViewById(R.id.rImageView);

        mTitleTv.setText(title);

        Picasso.get().load(image).into(mImageTv);
    }

}