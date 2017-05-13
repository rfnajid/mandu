package com.nnn.mandu;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by seven on 5/11/2017.
 */

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder>{

    Context ctx;
    List<Currency> data;

    int lastSelectedPos=0;

    public CurrencyAdapter(Context ctx, List<Currency> data) {
        this.ctx=ctx;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_currency,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Currency c = data.get(position);
        holder.text.setText(c.getName());
        holder.checkBox.setChecked(position==lastSelectedPos);

        Glide.with(ctx)
                .load(Global.IMGURL+c.getFlag())
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView text;
        AppCompatCheckBox checkBox;

        ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
            text = (TextView) itemView.findViewById(R.id.text);
            checkBox = (AppCompatCheckBox) itemView.findViewById(R.id.checkbox);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    lastSelectedPos = getAdapterPosition();
                    notifyItemRangeChanged(0, data.size());
                }
            });
        }
    }

    public int getLastSelectedPos() {
        return lastSelectedPos;
    }
}