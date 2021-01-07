package com.example.localfriendchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localfriendchat.Retrofit.User;

import java.util.List;

public class MainPageAdapter extends RecyclerView.Adapter<MainPageAdapter.AllViewHolder> {

    private List<User> users;

    private static final String TAG = MainPageAdapter.class.getSimpleName();
    final private ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(String username);
    }


    public MainPageAdapter(ListItemClickListener listener,List<User> userall) {
        mOnClickListener = listener;
        users=userall;

    }


    public class AllViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public final ImageView userImage;
        public  final TextView username;

        public AllViewHolder(View view) {
            super(view);

            userImage=(ImageView) view.findViewById(R.id.user_image);
            username=(TextView) view.findViewById(R.id.user_name);
            view.setOnClickListener(this);
        }



        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String userName =users.get(adapterPosition).getUsername();
            mOnClickListener.onListItemClick(userName);

        }


    }

    @Override
    public AllViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.item_mainsearch;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        AllViewHolder viewHolder = new AllViewHolder(view);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(AllViewHolder ViewHolder, int position) {
        String name= users.get(position).getUsername();
        ViewHolder.username.setText(name);
    }


    @Override
    public int getItemCount() {
        return users.size();
    }
}
