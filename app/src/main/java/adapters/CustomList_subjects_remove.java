package adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import functions.DBhelp;
import functions.TableEntry;
import functions.ButtonAnimation;
import nsit.app.com.nsitapp.R;

public class CustomList_subjects_remove extends ArrayAdapter<String> {
    private final Activity context;
    LinearLayout rem;
    private final ArrayList<String> status, date, ids;

    public CustomList_subjects_remove(Activity context, ArrayList<String> a, ArrayList<String> b, ArrayList<String> c) {
        super(context, R.layout.subject_list_item, a);
        this.context = context;
        status = a;
        date = b;
        ids = c;
    }

    private class ViewHolder {
        TextView stat, dat;
        LinearLayout rem;

    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder = null;
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            view = mInflater.inflate(R.layout.subject_list_item_remove, null);
            holder = new ViewHolder();
            holder.stat = (TextView) view.findViewById(R.id.status);
            holder.dat = (TextView) view.findViewById(R.id.date);
            holder.rem = (LinearLayout) view.findViewById(R.id.rem);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (status.get(position).equals("Missed"))
            holder.stat.setTextColor(Color.parseColor("#ff3300"));
        else
            holder.stat.setTextColor(Color.parseColor("#33cc00"));
        holder.stat.setText(status.get(position));

        String s = getDate(Long.parseLong(date.get(position)), "dd MMMM yyyy");        //Convert date format
        holder.dat.setText(s);
        DBhelp mDbHelper = new DBhelp(getContext());
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

        holder.rem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Remove");
                builder.setMessage("Are you sure you want to remove this date?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        db.delete(TableEntry.TABLE_NAME, TableEntry._ID
                                + " = " + ids.get(position), null);
                        Log.e("yo", "deleted");
                        status.remove(position);
                        date.remove(position);
                        ids.remove(position);
                        notifyDataSetChanged();

                    }
                });
                builder.setNegativeButton("No", null);
                ButtonAnimation btnAnimation = new ButtonAnimation();
                btnAnimation.animateButton(view, getContext());
                builder.show();
            }
        });

        AnimationSet set = new AnimationSet(true);
        TranslateAnimation slide = new TranslateAnimation(-100, 0, -100, 0);
        slide.setInterpolator(new DecelerateInterpolator(5.0f));
        slide.setDuration(300);
        Animation fade = new AlphaAnimation(0, 1.0f);
        fade.setInterpolator(new DecelerateInterpolator(5.0f));
        fade.setDuration(300);
        set.addAnimation(slide);
        set.addAnimation(fade);
        view.startAnimation(set);
        return view;
    }


    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}

