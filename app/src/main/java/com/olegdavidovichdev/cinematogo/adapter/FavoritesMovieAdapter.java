package com.olegdavidovichdev.cinematogo.adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.olegdavidovichdev.cinematogo.R;
import com.olegdavidovichdev.cinematogo.activity.FavoritesMovieActivity;
import com.olegdavidovichdev.cinematogo.db.FavoritesMovieDB;
import com.olegdavidovichdev.cinematogo.service.NotificationReceiver;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;


public class FavoritesMovieAdapter extends BaseAdapter {

    private static final String TAG = FavoritesMovieAdapter.class.getSimpleName();
    private SharedPreferences sp;
    private static final String APP_PREFERENCES = "app_preferences";
    private static final String APP_PREFERENCES_BASE_URL_IMAGES = "baseUrlImages";

    private static final String EXTRA_KEY = "key";

    private Context ctx;
    private LayoutInflater layoutInflater;
    private List<FavoritesMovieDB> list;


    public FavoritesMovieAdapter(Context ctx, List<FavoritesMovieDB> list) {
        this.ctx = ctx;
        this.layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = list;
        sp = ctx.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }


    private static class ViewHolder {
        TextView tvTitle;
        TextView tvRelease;
        ImageView bin;
        ImageView image;
        ImageView placeholder;
        Switch sw;
        ProgressBar pb;
    }

    @Override
    public int getCount() {
        if (list == null) return 0;
        else return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_fav_film, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.fav_film_title);
            viewHolder.tvRelease = (TextView) convertView.findViewById(R.id.fav_film_release);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.fav_film_image);
            viewHolder.bin = (ImageView) convertView.findViewById(R.id.fav_film_bin);
            viewHolder.sw = (Switch) convertView.findViewById(R.id.fav_film_switch);
            viewHolder.pb = (ProgressBar) convertView.findViewById(R.id.fav_film_progress_bar);
            viewHolder.placeholder = (ImageView) convertView.findViewById(R.id.fav_film_placeholder);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (!FavoritesMovieDB.listAll(FavoritesMovieDB.class).isEmpty()) {

            final List <FavoritesMovieDB> mov = FavoritesMovieDB.find(FavoritesMovieDB.class, "name = ?", list.get(position).getName());
            final FavoritesMovieDB movie = mov.get(0);

            viewHolder.tvTitle.setText(movie.getName());
            viewHolder.tvRelease.setText(movie.getRelease());
            viewHolder.pb.setVisibility(View.VISIBLE);
            viewHolder.sw.setTag(position);

            if (movie.isEnabled()) {
                viewHolder.sw.setChecked(true);
            }

            String imageUrl = sp.getString(APP_PREFERENCES_BASE_URL_IMAGES, null);
            String size = "w154";
            final String posterPath = movie.getPoster();
            Picasso.with(ctx)
                    .load(imageUrl + size + posterPath)
                    .into(viewHolder.image, new Callback() {
                        @Override
                        public void onSuccess() {
                            viewHolder.pb.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError() {
                            viewHolder.pb.setVisibility(View.INVISIBLE);
                            viewHolder.placeholder.setVisibility(View.VISIBLE);
                        }
                    });

            viewHolder.bin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Are you sure you want to delete '" + movie.getName() + "' from Favorites?");
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(ctx, NotificationReceiver.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,
                                    position, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                            AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
                            am.cancel(pendingIntent);

                            movie.delete();
                            Integer index = (Integer) viewHolder.sw.getTag();
                            list.remove(index.intValue());

                            ListView lv = ((FavoritesMovieActivity) ctx).getListFavFilm();
                            lv.setAdapter(FavoritesMovieAdapter.this);

                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    builder.create().show();
                }
            });

            viewHolder.sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {

                        Calendar now = Calendar.getInstance();
                        now.add(Calendar.SECOND, 15);

                    /*String release = m.getRelease();

                       int targetYear = Integer.parseInt(release.substring(0, 4));
                       int targetMonth = Integer.parseInt(release.substring(5, 7));
                       int targetDay = Integer.parseInt(release.substring(8, 10));
                       int targetHour = new Random().nextInt(15 - 9) + 9;
                       int targetMinute = new Random().nextInt(60);

                       Calendar targetDate = new GregorianCalendar(targetYear, targetMonth - 1,
                               targetDay, targetHour, targetMinute);


                       Log.d(TAG, targetDate.toString());*/

                        Intent intent = new Intent(ctx, NotificationReceiver.class);
                        intent.putExtra(EXTRA_KEY, 2);
                        intent.putExtra("film_name", movie.getName());
                        intent.putExtra("film_release", movie.getRelease());
                        intent.putExtra("film_poster", movie.getPoster());
                        intent.putExtra("notification_id", position);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,
                                position, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
                        am.set(AlarmManager.RTC_WAKEUP, now.getTimeInMillis(), pendingIntent);

                        movie.setEnabled(true);
                        movie.save();

                    } else {

                        Intent intent = new Intent(ctx, NotificationReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,
                                position, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
                        am.cancel(pendingIntent);

                        movie.setEnabled(false);
                        movie.save();
                    }
                }
            });
        }
        return convertView;
    }
}
