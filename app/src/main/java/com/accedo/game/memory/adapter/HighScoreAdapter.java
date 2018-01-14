package com.accedo.game.memory.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.accedo.game.memory.R;
import com.accedo.game.memory.database.EntityUser;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nathaniel on 14/1/18.
 */

public class HighScoreAdapter extends ArrayAdapter<EntityUser> {

    private Context context;

    static class ViewHolder {
        @BindView(R.id.rank)
        TextView tvRank;
        @BindView(R.id.name)
        TextView tvName;
        @BindView(R.id.score)
        TextView tvScore;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public HighScoreAdapter(Context context) {
        super(context, R.layout.score_row);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.score_row, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        final EntityUser user = getItem(position);

        int rank = position + 1;
        holder.tvRank.setText(rank + "");
        holder.tvName.setText(user.name);
        holder.tvScore.setText(user.score + "");


        return convertView;
    }
}
