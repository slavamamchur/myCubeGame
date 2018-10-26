package com.sadgames.dicegame.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.sadgames.dicegame.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Gallery gallery;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        gallery = findViewById(R.id.gallery_menu);
        gallery.setAdapter(new ImageAdapter(this, R.layout.menu_item_layout, getData()));
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position,long id)
            {
                final Class<?>[] items = {GameListActivity.class, GameInstanceListActivity.class, SettingsActivity.class};

                startActivity(new Intent(getApplicationContext(), items[position]));
            }
        });

        gallery.setSpacing(20);
        gallery.setSelection(1);

    }

    @Override
    protected void onResume() {
        super.onResume();

        gallery.setSelection(1);
    }

    public class ImageAdapter extends ArrayAdapter {
        private Context context;
        private int layoutResourceId;
        private ArrayList data = new ArrayList();

        public ImageAdapter(Context context, int layoutResourceId, ArrayList data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder = null;

            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new ViewHolder();
                holder.imageTitle = (TextView) row.findViewById(R.id.text);
                holder.image = (ImageView) row.findViewById(R.id.image);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            ImageItem item = (ImageItem) data.get(position);
            holder.imageTitle.setText(item.getTitle());
            holder.image.setBackgroundColor(position == 0 ? 0xFF0080FF : 0xFFEEEEEE);
            holder.image.setImageDrawable(item.getImage());

            return row;
        }

        class ViewHolder {
            TextView imageTitle;
            ImageView image;
        }
    }

    public class ImageItem {
        private Drawable image;
        private String title;

        public ImageItem(Drawable image, String title) {
            super();
            this.image = image;
            this.title = title;
        }

        public Drawable getImage() {
            return image;
        }

        public void setImage(Drawable image) {
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();

        imageItems.add(new ImageItem(VectorDrawableCompat.create(MainActivity.this.getResources(), R.drawable.new_game_ico_cl, MainActivity.this.getTheme()), "New GameEntity"));
        imageItems.add(new ImageItem(VectorDrawableCompat.create(MainActivity.this.getResources(), R.drawable.load_game_ico_cl, MainActivity.this.getTheme()), "Load GameEntity"));
        imageItems.add(new ImageItem(VectorDrawableCompat.create(MainActivity.this.getResources(), R.drawable.settings_ico_cl, MainActivity.this.getTheme()), "Settings"));

        return imageItems;
    }
}
