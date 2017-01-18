package com.klz.news.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.klz.news.model.Joke;
import com.klz.news.R;

import java.util.ArrayList;

/**
 * Created by Kong on 2016/6/16 0016.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    public ArrayList<Joke> datas = null;
    public Context context;

    private MyItemClickListener mItemClickListener;
    private MyItemLongClickListener mItemLongClickListener;

    public void setOnItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setOnItemLongClickListener(MyItemLongClickListener listener) {
        this.mItemLongClickListener = listener;
    }

    public MyAdapter(Context context, ArrayList<Joke> datas) {
        this.context = context;
        this.datas = datas;
    }

    public void setDatas(ArrayList<Joke> c) {
        datas.addAll(c);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_new, parent, false);
        return new ViewHolder(view, mItemClickListener, mItemLongClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Picasso.with(context).load(datas.get(position).getImage_url()).placeholder(R.mipmap.ic_launcher).
        //transform(new CircleTransform()).into(holder.image);
        holder.title.setText(datas.get(position).getTitle());
        holder.con.setText(datas.get(position).getText());
        Log.d("position", "" + position);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView title;
        public TextView con;
        public ImageView image;
        public TextView url;
        private MyItemClickListener mListener;
        private MyItemLongClickListener mLongClickListener;

        public ViewHolder(View itemView, MyItemClickListener listener, MyItemLongClickListener longClickListener) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.list_item_new_title);
            con = (TextView) itemView.findViewById(R.id.list_item_new_abstract);
            image = (ImageView) itemView.findViewById(R.id.list_item_new_image);
            url = (TextView) itemView.findViewById(R.id.list_item_new_url);
            this.mListener = listener;
            this.mLongClickListener = longClickListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(v, getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mLongClickListener != null) {
                mLongClickListener.onItemLongClick(v, getPosition());
            }
            return true;
        }
    }

    public interface MyItemClickListener {
        public void onItemClick(View view, int postion);
    }

    public interface MyItemLongClickListener {
        public void onItemLongClick(View view, int postion);
    }
}
