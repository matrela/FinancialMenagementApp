package com.example.application;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    List<DataModel> dataList;
    Context context;

    //PL money format (2 decimals)
    DecimalFormat plDf = new DecimalFormat("#,##0.00");

    public RecyclerViewAdapter(List<DataModel> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_line_data, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ColorStateList oldColors =  holder.tv_name.getTextColors(); //save original color

        if (dataList.get(position).getAmount() < 0) {
            holder.tv_amount.setTextColor(Color.RED);
        }else{
            holder.tv_amount.setTextColor(oldColors); //restore original color
        }
        holder.tv_amount.setText(String.valueOf(plDf.format(dataList.get(position).getAmount())) + " PLN");

        holder.tv_name.setText(dataList.get(position).getName());
        holder.tv_category.setText(dataList.get(position).getCategory());
        holder.tv_date.setText(dataList.get(position).getDate());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send the control to the DisplaySingleDataActivity
                Intent intent = new Intent(context, DisplaySingleDataActivity.class);
                intent.putExtra("id", dataList.get(holder.getAdapterPosition()).getId());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView iv_categoryPic;
        TextView tv_name;
        TextView tv_category;
        TextView tv_amount;
        TextView tv_date;

        ConstraintLayout parentLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_categoryPic = itemView.findViewById(R.id.categoryPic);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_category = itemView.findViewById(R.id.tv_category);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            tv_date = itemView.findViewById(R.id.tv_date);

            parentLayout = itemView.findViewById(R.id.oneLineDataLayout);

        }
    }
}
