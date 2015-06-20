package com.example.arslan.vkclient;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

class WallListAdapter extends ArrayAdapter<WallPost>{
    private Activity context;
    private List<WallPost> wallPosts;

    public WallListAdapter(Activity context, List<WallPost> wallPosts){
        super(context,R.layout.layout_wallpost_single, wallPosts);
        this.context = context;
        this.wallPosts = wallPosts;
    }
    static class ViewHolder{
        ImageView avatarImage;
        TextView name;
        TextView description;
        GridView attachments;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_wallpost_single, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.avatarImage = (ImageView)convertView.findViewById(R.id.avatar);
            viewHolder.description= (TextView) convertView.findViewById(R.id.description);
            viewHolder.name = (TextView)convertView.findViewById(R.id.name);
            viewHolder.attachments = (GridView)convertView.findViewById(R.id.gridview);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Picasso.with(context)
                .load(wallPosts.get(position).getAvatarURL())
                .placeholder(R.drawable.placeholder_)
                .into(viewHolder.avatarImage);

        viewHolder.description.setText(wallPosts.get(position).getText());
        if(viewHolder.description.getText()==null)
            viewHolder.description.setVisibility(View.GONE);
        viewHolder.name.setText(wallPosts.get(position).getFrom());
        viewHolder.attachments.setAdapter(new ImageAdapter(context, wallPosts.get(position).getAttachments()));

        Log.e("GETVIEW CALLED",String.valueOf(position));
        return convertView;
    }
}
