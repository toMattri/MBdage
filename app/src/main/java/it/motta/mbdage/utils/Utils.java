package it.motta.mbdage.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import it.motta.mbdage.BuildConfig;
import it.motta.mbdage.activities.SplashActivity;
import it.motta.mbdage.database.DBHandler;
import it.motta.mbdage.dialog.ConfirmDialog;

public class Utils {

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

    public static double calculateDist(double lat1,double lat2,double lon2,double lon1){
        int R = 6371; // Radius of the earth in km
        double dLat = degrade(lat2-lat1);  // deg2rad below
        double dLon = degrade(lon2-lon1);
        double a =
            Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(degrade(lat1)) * Math.cos(degrade(lat2)) *
                    Math.sin(dLon/2) * Math.sin(dLon/2)
            ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c; // Distance in km
        return d;
    }

    private static double degrade(double t) {
        return t * (Math.PI/180);
    }

    public static String md5Work(String text){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(text.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);

            StringBuilder hashtext = new StringBuilder(no.toString(16));
            while (hashtext.length() < 32) {
                hashtext.insert(0, "0");
            }
            return hashtext.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "0";
    }


    public static String subString(String str,int max){
        if(StringUtils.isEmpty(str))
            return "";
        if(str.length() >= max)
            return str.substring(0,max);
        else return str;

    }

}
