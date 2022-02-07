package it.motta.mbdage.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import it.motta.mbdage.BuildConfig;
import it.motta.mbdage.activities.SplashActivity;
import it.motta.mbdage.database.DBHandler;
import it.motta.mbdage.dialog.ConfirmDialog;

public class Utils {

    public static Bitmap getRoundedCornerBitmap(@NonNull Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap getRoundedCornerDrawable(@NonNull Drawable drawable, int pixels) {
        return getRoundedCornerBitmap(drawableToBitmap(drawable),pixels);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @SuppressLint("SimpleDateFormat")
    public static String dateQuery(Date date){
        String strData = "";
        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            strData = simpleDateFormat.format(date);
        }catch (Exception exx){
            exx.printStackTrace();
        }
        return strData;
    }

    public static Date getDateLessDay(int day){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -day);
        return  calendar.getTime();
    }

    public static Date getFirstDayOfYear(int lessYear){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -lessYear * 365);
        calendar.add(Calendar.DAY_OF_YEAR, - (calendar.get(Calendar.DAY_OF_YEAR) -1));

        return  calendar.getTime();
    }

    public static boolean internetAvailability(){
        try {
            InetAddress ipAddr = InetAddress.getByName("google.it");
            return !ipAddr.getHostAddress().equals("");
        } catch (Exception e) {
            if(BuildConfig.DEBUG)e.printStackTrace();
            return false;
        }
    }


    public static void logOut(Context context){
        ConfirmDialog confirmDialog = new ConfirmDialog(((AppCompatActivity)context).getSupportFragmentManager(),"Attenzione","Sei sicuro di disconneterti da questo utente?");
        confirmDialog.setPositiveClickListner("Conferma",v -> {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            DBHandler.getIstance(context).deleteUtente();
            context.startActivity(new Intent(context, SplashActivity.class));
            ((Activity)context).finish();
        });
        confirmDialog.setNegativeClickListner("Annulla",null);
        confirmDialog.show();
    }


    public static boolean valideEmail(String email){
        String regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])\n";
        return email.matches(regex);
    }
}
