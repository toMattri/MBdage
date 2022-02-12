package it.motta.mbdage.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import it.motta.mbdage.BuildConfig;
import it.motta.mbdage.R;
import it.motta.mbdage.database.DBHandler;
import it.motta.mbdage.models.Utente;
import it.motta.mbdage.models.Varco;
import it.motta.mbdage.models.evalue.TypeDialog;
import it.motta.mbdage.models.filter.FilterPassaggi;
import it.motta.mbdage.utils.Utils;

@SuppressLint({"NonConstantResourceId","SimpleDateFormat"})
public class FilterPassaggiDialog extends Dialog implements View.OnClickListener{

    private final FilterPassaggi filterPassaggiOld;
    private FilterPassaggi filter;
    private final Utente utente;
    private final Context mContext;
    private  TextInputEditText edtDataDal,edtDataAl;
    private Spinner spinnerVarco;
    private ArrayList<Varco> varcos;
    public FilterPassaggiDialog(@NonNull Context context, FilterPassaggi filterPassaggi, Utente utente) {
        super(context);
        this.mContext = context;
        this.utente = utente;
        this.filterPassaggiOld = filterPassaggi;
        this.filter = filterPassaggi;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(false);
        setContentView(R.layout.dialog_filter_passaggi);
        Button btChiudi,btConferma;
        ImageButton btReset  = findViewById(R.id.btDelete);
        btChiudi = findViewById(R.id.btChiudi);
        btConferma = findViewById(R.id.btConferma);

        spinnerVarco = findViewById(R.id.spnVarco);
        edtDataAl = findViewById(R.id.edtDataAl);
        edtDataDal = findViewById(R.id.edtDataDal);

        btChiudi.setOnClickListener(this);
        btConferma.setOnClickListener(this);
        btReset.setOnClickListener(this);

        edtDataAl.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                saveDataRegistrazione(edtDataAl,R.string.data_al);
            return true;
        });

        edtDataDal.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                saveDataRegistrazione(edtDataDal,R.string.data_dal);
            return true;
        });

        try {
            String data = new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat( "yyyy-MM-dd").parse(filter.getTimeDal()));
            edtDataDal.setText((data));
            data = new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat( "yyyy-MM-dd").parse(filter.getTimeAl()));
            edtDataAl.setText((data));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        varcos = new ArrayList<>();
        varcos.add(new Varco(0,0,0,"Tutti",""));
        varcos.addAll(DBHandler.getIstance(mContext).getVarchi());

        String[] varchi = new String[varcos.size()];
        int selected = 0;
        for(int i = 0;i<varcos.size();i++){
            varchi[i] = varcos.get(i).getDescrizione();
            if(varcos.get(i).getId() == filter.getIdVarco())
                selected = i;
        }
        spinnerVarco.setAdapter(new ArrayAdapter<>(mContext,R.layout.support_simple_spinner_dropdown_item,varchi));
        spinnerVarco.setSelection(selected);

    }


    private void saveDataRegistrazione(TextInputEditText edt,int idResource) {
        try {
            Date date = Objects.requireNonNull(edt.getText()).toString().length() == 0 ? Utils.getFirstDayOfYear(20) : new SimpleDateFormat("dd/MM/yyyy").parse(edt.getText().toString());
            DateTimePickerDialog dateTimePickerDialog = new DateTimePickerDialog(mContext, mContext.getResources().getString(idResource), date);
            dateTimePickerDialog.setOnDismissListener(dialog -> {
                if (dateTimePickerDialog.getDateChoosed() != null)
                    edt.setText(new SimpleDateFormat("dd/MM/yyyy").format(dateTimePickerDialog.getDateChoosed()));
            });
            dateTimePickerDialog.show();
        } catch (ParseException e) {
            if (BuildConfig.DEBUG)
                e.printStackTrace();
        }
    }

    public FilterPassaggi getFilter() {
        return filter;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btDelete:
                filter = new FilterPassaggi(utente.getId());
                break;
            case R.id.btChiudi:
                filter = filterPassaggiOld;
                break;
            case R.id.btConferma:
                String dataDal,dataAl;
                try {
                    String data = edtDataDal.getText().toString() ;
                    if (StringUtils.isEmpty(data)) {
                        new CustomDialog(mContext, mContext.getResources().getString(R.string.attenzione), mContext.getResources().getString(R.string.err_dats_dal), TypeDialog.WARING).show();
                        return;
                    }

                    dataDal = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd/MM/yyyy").parse(data));

                    data = edtDataAl.getText().toString();
                    if (StringUtils.isEmpty(data)) {
                        new CustomDialog(mContext, mContext.getResources().getString(R.string.attenzione), mContext.getResources().getString(R.string.err_dats_al), TypeDialog.WARING).show();
                        return;
                    }
                    dataAl = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd/MM/yyyy").parse(data));
                }catch (Exception ex){
                    ex.printStackTrace();
                    new CustomDialog(mContext, mContext.getResources().getString(R.string.errore), mContext.getResources().getString(R.string.err_generico), TypeDialog.WARING).show();
                    return;
                }
                filter = new FilterPassaggi(utente.getId(),(int)varcos.get(spinnerVarco.getSelectedItemPosition()).getId(),dataDal,dataAl);
                break;
        }
        dismiss();
    }

}