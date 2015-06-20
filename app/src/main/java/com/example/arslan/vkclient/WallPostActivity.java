package com.example.arslan.vkclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class WallPostActivity extends AppCompatActivity {

    private ImageView avatarImage;
    private TextView name;
    private TextView description;
    private GridView attachments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpost);
        avatarImage = (ImageView)findViewById(R.id.avatar);
        name = (TextView)findViewById(R.id.name);
        description = (TextView)findViewById(R.id.description);
        attachments = (GridView)findViewById(R.id.gridview);
        Intent i = getIntent();
        Bundle b = i.getExtras();
        WallPost post = (WallPost)b.getSerializable(WallActivity.WALLPOST);
        attachments.setAdapter(new ImageAdapter(getApplicationContext(),post.getAttachments()));
        name.setText(post.getFrom());
        description.setText(post.getText());
        Picasso.with(getApplicationContext())
                .load(post.getAvatarURL())
                .placeholder(R.drawable.placeholder_)
                .into(avatarImage);

    }
}
