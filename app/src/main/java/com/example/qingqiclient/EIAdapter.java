package com.example.qingqiclient;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.qingqiclient.entity.EI;

import java.util.List;

/**
 * Created by Administrator on 2017/10/18 0018.
 * 这是一个EI适配器
 */

public class EIAdapter extends RecyclerView.Adapter<EIAdapter.ViewHolder> {

    private List<EI> eiList;

    public EIAdapter(List<EI> ei) {
        eiList = ei;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_ei, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EI ei = eiList.get(position);
        holder.awb.setText(ei.getAwb());
        holder.state.setText(ei.getState().toString());
    }

    @Override
    public int getItemCount() {
        return eiList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView awb;
        TextView state;

        public ViewHolder(View view) {
            super(view);
            this.awb = view.findViewById(R.id.awb) ;
            this.state = view.findViewById(R.id.state);
        }
    }
}
