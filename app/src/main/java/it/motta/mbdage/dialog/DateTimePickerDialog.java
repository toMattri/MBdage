package it.motta.mbdage.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;

import it.motta.mbdage.R;

@SuppressLint("NonConstantResourceId")
public class DateTimePickerDialog extends Dialog {

    private final String title;
    private final Date dateToSet;
    private Calendar dateChoosed;
    private final Context mContext;

    public DateTimePickerDialog(@NonNull Context context,String title,Date dateToSet) {
        super(context);
        this.mContext = context;
        this.title = title;
        this.dateToSet = dateToSet;
    }

    public DateTimePickerDialog(@NonNull Context context,String title) {
        this(context,title,new Date());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        setContentView(R.layout.dialog_time_picker);

        TextView txtTitle = findViewById(R.id.txtTitle);
        CalendarView calendarView = findViewById(R.id.timePicker);
        Button btChiudi,btConferma;

        btChiudi = findViewById(R.id.btChiudi);
        btConferma = findViewById(R.id.btConferma);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            dateChoosed = Calendar.getInstance();
            dateChoosed.set(year, month, dayOfMonth);
        });

        View.OnClickListener handler = v -> {
            switch (v.getId()){
                case R.id.btChiudi:
                    dateChoosed = null;
                    dismiss();
                    break;
                case R.id.btConferma:
                    dismiss();
                    break;
            }
        };

        txtTitle.setText(title);
        calendarView.setDate(dateToSet.getTime());
        btChiudi.setOnClickListener(handler);
        btConferma.setOnClickListener(handler);
    }

    public Date getDateChoosed() {
        return dateChoosed != null ? dateChoosed.getTime() : null;
    }

    @Override
    public void setOnCancelListener(@Nullable OnCancelListener listener) {
        dateChoosed = null;
        super.setOnCancelListener(listener);
    }
}