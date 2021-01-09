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
        void onListItemClick(String username,String socket_id);
    }


    public MainPageAdapter(ListItemClickListener listener,List<User> userall) {
        mOnClickListener = listener;
        users=userall;

    }


    public class AllViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public final ImageView userImage;
        public  final TextView username, fullname;

        public AllViewHolder(View view) {
            super(view);

            userImage=(ImageView) view.findViewById(R.id.user_image);
            username=(TextView) view.findViewById(R.id.user_name);
            fullname=(TextView) view.findViewById(R.id.full_name);
            view.setOnClickListener(this);
        }



        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String userName =users.get(adapterPosition).getUsername();
            String socket_id =users.get(adapterPosition).getSocket_id();
            mOnClickListener.onListItemClick(userName,socket_id);

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
        String fname= users.get(position).getFirstName();
        String lname= users.get(position).getLastName();
        String full_name=fname+" "+lname;
        ViewHolder.username.setText(name);
        ViewHolder.fullname.setText(full_name);
    }


    @Override
    public int getItemCount() {
        return users.size();
    }
}
