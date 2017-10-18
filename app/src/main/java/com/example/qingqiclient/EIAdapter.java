package com.example.qingqiclient;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.qingqiclient.entity.EI;

import java.util.List;

import static android.support.v4.content.ContextCompat.startActivity;

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
        final ViewHolder holder = new ViewHolder(view);

        //注册点击事件
        holder.eiView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                EI ei = eiList.get(position);

                //取出对应的ei的id
                Long id = ei.getId();
                //把Id传给下一个活动，EI的详细信息界面
                Intent intent = new Intent(view.getContext(), EI_Info.class);
                intent.putExtra("id",id);
                view.getContext().startActivity(intent);
            }
        });
        return holder;
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
        View eiView;
        TextView awb;
        TextView state;

        public ViewHolder(View view) {
            super(view);
            eiView = view;
            this.awb = view.findViewById(R.id.awb) ;
            this.state = view.findViewById(R.id.state);
        }
    }
}
