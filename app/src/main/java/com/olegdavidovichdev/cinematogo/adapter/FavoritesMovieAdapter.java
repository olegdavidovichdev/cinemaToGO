package com.olegdavidovichdev.cinematogo.adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.olegdavidovichdev.cinematogo.R;
import com.olegdavidovichdev.cinematogo.db.FavoritesMovieDB;
import com.olegdavidovichdev.cinematogo.service.NotificationReceiver;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Oleg on 01.12.2016.
 */

public class FavoritesMovieAdapter extends BaseAdapter {

    private static final String TAG = FavoritesMovieAdapter.class.getSimpleName();
    private SharedPreferences sp;
    private static final String APP_PREFERENCES = "app_preferences";
    private static final String APP_PREFERENCES_BASE_URL_IMAGES = "baseUrlImages";

    private static final String EXTRA_KEY = "key";
    private static int counter = 0;

    private Context ctx;
    private LayoutInflater layoutInflater;
    private List<FavoritesMovieDB> list;

    private ArrayList<Integer> al = new ArrayList<>();


    public FavoritesMovieAdapter(Context ctx, List<FavoritesMovieDB> list) {
        this.ctx = ctx;
        this.layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = list;
        sp = ctx.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }


    static class ViewHolder {
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
        return list.size();
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

        final FavoritesMovieDB movie = list.get(position);

        viewHolder.tvTitle.setText(movie.getName());
        viewHolder.tvRelease.setText(movie.getRelease());
        viewHolder.pb.setVisibility(View.VISIBLE);


        String imageUrl = sp.getString(APP_PREFERENCES_BASE_URL_IMAGES, null);
        String size = "w154";
        final String posterPath = movie.getPoster();
    //    Log.d(TAG, imageUrl + size + posterPath);
        Log.d(TAG, "getView");
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

                        SharedPreferences.Editor e = sp.edit();
                        e.remove(String.valueOf(position));
                        e.apply();

                        movie.delete();
                        list.remove(position);

                        Log.d(TAG, "adapter: remove item = " + FavoritesMovieDB.listAll(FavoritesMovieDB.class));

                        e.putString(String.valueOf(position), list.get(position).getName());
                        e.apply();

                        Log.d(TAG, "del = " + sp.getAll().toString());

                        FavoritesMovieAdapter.this.notifyDataSetChanged();

                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.create().show();
            }
        });

     //   Log.d(TAG, "!!!! = " + sp.getString(String.valueOf(position), null));
     //   Log.d(TAG, "list[0] = " + list.get(0));

        Log.d(TAG, "check1 = " + ((FavoritesMovieDB) getItem(0)).getName());
        Log.d(TAG, "check2 = " + sp.getString(String.valueOf(0), ""));

        Log.d(TAG, "check1 = " + ((FavoritesMovieDB) getItem(1)).getName());
        Log.d(TAG, "check2 = " + sp.getString(String.valueOf(1), ""));

     //   Log.d(TAG, "check1 = " + ((FavoritesMovieDB) getItem(2)).getName());
     //   Log.d(TAG, "check2 = " + sp.getString(String.valueOf(2), ""));


        if (sp.contains(String.valueOf(position))) {
            if (((FavoritesMovieDB) getItem(position)).getName().equals(sp.getString(String.valueOf(position), ""))) {
                viewHolder.sw.setChecked(true);

            }
        }


        viewHolder.sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    counter++;
                    Log.d(TAG, "counter = " + counter);

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


                    SharedPreferences.Editor e = sp.edit();
                    e.putString(String.valueOf(position), movie.getName());
                    e.apply();

                    Log.d(TAG, "if = " + sp.getAll().toString());

                    /*List<FavoritesMovieDB> l = FavoritesMovieDB.find(FavoritesMovieDB.class, "name = ?" , movie.getName());
                    FavoritesMovieDB m = l.get(0);
                    m.setCheck(true);*/

                } else {

                    Intent intent = new Intent(ctx, NotificationReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,
                            position, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
                    am.cancel(pendingIntent);

                    SharedPreferences.Editor e = sp.edit();
                    e.remove(String.valueOf(position));
                    e.apply();

                    Log.d(TAG, "else = " + sp.getAll().toString());
                    /*List<FavoritesMovieDB> l = FavoritesMovieDB.find(FavoritesMovieDB.class, "name = ?" , movie.getName());
                    FavoritesMovieDB m = l.get(0);
                    m.setCheck(false);*/
                }
            }
        });


        /*View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_fav_film, parent, false);
        }*/

        /*((TextView) view.findViewById(R.id.fav_film_title)).setText(movie.getName());
        ((TextView) view.findViewById(R.id.fav_film_release)).setText(movie.getRelease());*/

        /*sp = ctx.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        String imageUrl = sp.getString(APP_PREFERENCES_BASE_URL_IMAGES, null);
        String size = "w154";
        String posterPath = movie.getPoster();

        Log.d(TAG, imageUrl + size + posterPath);

        final ProgressBar pb = (ProgressBar) view.findViewById(R.id.fav_film_progress_bar);
        pb.setVisibility(View.VISIBLE);
        Picasso.with(view.getContext())
                .load(imageUrl + size + posterPath)
                .into(((ImageView) view.findViewById(R.id.fav_film_image)), new Callback() {
                    @Override
                    public void onSuccess() {
                        pb.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {

                    }
                });

        final ImageView bin = (ImageView) view.findViewById(R.id.fav_film_bin);
        bin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Are you sure you want to delete '" + movie.getName() + "' from Favorites?");
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        movie.delete();
                        list.remove(position);
                        FavoritesMovieAdapter.this.notifyDataSetChanged();
                        Log.d(TAG, "adapter: remove item = " + FavoritesMovieDB.listAll(FavoritesMovieDB.class));
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.create().show();
            }
        });


        final Switch sw = (Switch) view.findViewById(R.id.fav_film_switch);

        if (sp.getAll().size() != 1) {
            Log.d(TAG, sp.getAll().toString());
            sw.setChecked(sp.getBoolean("Switch " + position, false));
        }
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    counter++;
                    Log.d(TAG, "counter = " + counter);

                    Calendar now = Calendar.getInstance();
                    now.add(Calendar.SECOND, 15);
                                *//*String release = m.getRelease();

                                int targetYear = Integer.parseInt(release.substring(0, 4));
                                int targetMonth = Integer.parseInt(release.substring(5, 7));
                                int targetDay = Integer.parseInt(release.substring(8, 10));
                                int targetHour = new Random().nextInt(15 - 9) + 9;
                                int targetMinute = new Random().nextInt(60);

                                Calendar targetDate = new GregorianCalendar(targetYear, targetMonth - 1,
                                        targetDay, targetHour, targetMinute);


                                Log.d(TAG, targetDate.toString());*//*

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

                    sp = ctx.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor e = sp.edit();
                    e.putBoolean("Switch "  + position, true);
                    e.apply();

                } else {
                    Intent intent = new Intent(ctx, NotificationReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,
                            position, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
                    am.cancel(pendingIntent);

                    sp = ctx.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor e = sp.edit();
                    e.putBoolean("Switch "  + position, false);
                    e.apply();
                }
            }
        });*/

        return convertView;
    }
}
