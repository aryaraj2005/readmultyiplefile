package com.example.readmultiplefile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class myAdapter extends RecyclerView.Adapter<myAdapter.myViewHolder> {
   ArrayList<String> files, status;

    public myAdapter(ArrayList<String> files, ArrayList<String> status) {
        this.files = files;
        this.status = status;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
           View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singrle_row_design , parent , false);
           return  new myViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        String filename= files.get(position);
        if(filename.length()>=15){
            filename = filename.substring(0 , 15) + ".....";
        }
        holder.filtxt.setText(filename);

        String filestatus = status.get(position);
        if (filestatus.equals("loading...")){
            holder.proimg.setImageResource(R.drawable.progrss);
        }else{
            holder.proimg.setImageResource(R.drawable.check);
        }
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public  class  myViewHolder extends RecyclerView.ViewHolder{
          ImageView fileimg , proimg;
          TextView filtxt;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            fileimg = itemView.findViewById(R.id.fileimg);
            proimg = itemView.findViewById(R.id.progimg);
            filtxt = itemView.findViewById(R.id.filenametext);
        }
    }
}
