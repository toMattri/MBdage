package it.motta.mbdage.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import it.motta.mbdage.BuildConfig;
import it.motta.mbdage.R;
import it.motta.mbdage.database.DBHandler;
import it.motta.mbdage.dialog.CustomDialog;
import it.motta.mbdage.dialog.DateTimePickerDialog;
import it.motta.mbdage.interfaces.IAccessOperation;
import it.motta.mbdage.response.ResponseAccess;
import it.motta.mbdage.models.Utente;
import it.motta.mbdage.models.evalue.TypeDialog;
import it.motta.mbdage.models.evalue.TypeLogin;
import it.motta.mbdage.models.evalue.TypeUtente;
import it.motta.mbdage.utils.TraduceComunication;
import it.motta.mbdage.utils.Utils;
import it.motta.mbdage.worker.LoadVarchiWoker;
import it.motta.mbdage.worker.LoginWorker;
import it.motta.mbdage.worker.RegisterWorker;
import it.motta.mbdage.worker.UpdateTokenWorker;

@SuppressLint({"ResourceType","ClickableViewAccessibility","NonConstantResourceId"})
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;
    private SignInButton signInButton,signInButtonGit;
    private TextInputEditText edtEmailAccedi,edtPasswordAccedi,edtNomeRegistrati,edtCognomeRegistrati,edtEmailRegistrati,edtPasswordRegistrati,edtDataRegistrati;
    private FirebaseAuth mAuth;
    private FirebaseMessaging mMessaging;

    private OAuthProvider.Builder provider;
    private GoogleSignInClient mGoogleSignInClient;
    private Animation centerToRight,leftToCenter;
    private Button btAccedi, btRegistrati;
    private TextView txtRegistrati,txtAccedi;
    private CardView cardLogin, cardRegistrati;
    private LinearLayout llSignWIthOther;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        provider = OAuthProvider.newBuilder("github.com");
        txtRegistrati = findViewById(R.id.txtRegistrati);
        txtAccedi = findViewById(R.id.txtAccedi);
        llSignWIthOther = findViewById(R.id.llSignWIthOther);
        signInButtonGit = findViewById(R.id.signInGit);
        cardLogin = findViewById(R.id.cardLogin);
        cardRegistrati = findViewById(R.id.cardRegister);
        edtEmailAccedi = findViewById(R.id.edtEmailAccedi);
        edtPasswordAccedi = findViewById(R.id.edtPasswordAccedi);
        edtNomeRegistrati = findViewById(R.id.edtNomeRegistrati);
        edtCognomeRegistrati = findViewById(R.id.edtCognomeRegistrati);
        edtEmailRegistrati = findViewById(R.id.edtEmailRegistrati);
        edtPasswordRegistrati = findViewById(R.id.edtPasswordRegistrati);
        edtDataRegistrati = findViewById(R.id.edtDataRegistrati);
        signInButton = findViewById(R.id.signInGoogle);
        btRegistrati = findViewById(R.id.btRegistrati);
        btAccedi = findViewById(R.id.btAccedi);
        mMessaging = FirebaseMessaging.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        centerToRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pushright);
        leftToCenter = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fromleft);

        txtRegistrati.setOnClickListener(this);
        txtAccedi.setOnClickListener(this);
        btRegistrati.setOnClickListener(this);
        signInButtonGit.setOnClickListener(this);
        btAccedi.setOnClickListener(this);
        signInButton.setOnClickListener(this);

        edtPasswordRegistrati.setOnKeyListener((v, keyCode, event) -> {
            ((TextInputLayout)findViewById(v.getId()).getParent().getParent()).setEndIconVisible(true);
            return false;
        });

        edtPasswordAccedi.setOnKeyListener((v, keyCode, event) -> {
            ((TextInputLayout)findViewById(v.getId()).getParent().getParent()).setEndIconVisible(true);
            return false;
        });

        edtDataRegistrati.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                ((TextInputLayout) findViewById(edtEmailRegistrati.getId()).getParent().getParent()).setEndIconVisible(true);
                saveDataRegistrazione(edtDataRegistrati);
                edtDataRegistrati.clearFocus();
            }
        });

        edtDataRegistrati.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                saveDataRegistrazione(edtDataRegistrati);
            return true;
        });

    }

    @SuppressLint("SimpleDateFormat")
    private void saveDataRegistrazione(TextInputEditText edt) {
        try {
            Date date = Objects.requireNonNull(edt.getText()).toString().length() == 0 ? Utils.getFirstDayOfYear(20) : new SimpleDateFormat("dd/MM/yyyy").parse(edt.getText().toString());
            DateTimePickerDialog dateTimePickerDialog = new DateTimePickerDialog(this, "Data di nascita", date);
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

    private void disableAllerror() {
        edtPasswordAccedi.setError(null);
        edtEmailAccedi.setError(null);
        edtEmailRegistrati.setError(null);
        edtPasswordRegistrati.setError(null);
        edtNomeRegistrati.setError(null);
        edtDataRegistrati.setError(null);
        edtCognomeRegistrati.setError(null);
        edtPasswordAccedi.setText("");
        edtEmailAccedi.setText("");
        edtEmailRegistrati.setText("");
        edtPasswordRegistrati.setText("");
        edtNomeRegistrati.setText("");
        edtDataRegistrati.setText("");
        edtCognomeRegistrati.setText("");
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signInGitHub(){
        mAuth.startActivityForSignInWithProvider(this, provider.build())
            .addOnSuccessListener(authResult -> {
                Log.e("RES" , authResult.getUser().getEmail());
                new RegisterWorker(this,createUserWithImage(authResult.getUser(),""),TypeLogin.GIT,iAccessOperation).execute();
            })
            .addOnFailureListener(e -> {
                e.printStackTrace();
                new CustomDialog(this,"Attenzione","Non è stato trovato nessun utente con i dati inseriti", TypeDialog.WARING).show();
            });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken(),account.getPhotoUrl().toString());
            } catch (ApiException e) {
                new CustomDialog(this,"Attenzione","Non è stato trovato nessun utente con i dati inseriti", TypeDialog.WARING).show();
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken,String urlImage) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d("TAG", "signInWithCredential:success");
                FirebaseUser user = mAuth.getCurrentUser();
                new RegisterWorker(this,createUserWithImage(user,urlImage),TypeLogin.GOOGLE,iAccessOperation).execute();
            } else {
                Log.w("TAG", "signInWithCredential:failure", task.getException());
                new CustomDialog(this, "Attenzione", "Non è stato trovato nessun utente con i dati inseriti", TypeDialog.WARING).show();
            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtAccedi:
                cardLogin.setVisibility(View.VISIBLE);
                cardRegistrati.setVisibility(View.GONE);
                cardLogin.startAnimation(leftToCenter);
                cardRegistrati.startAnimation(centerToRight);
                llSignWIthOther.setVisibility(View.VISIBLE);
                disableAllerror();
                break;
            case R.id.txtRegistrati:
                llSignWIthOther.setVisibility(View.GONE);
                cardLogin.setVisibility(View.GONE);
                cardRegistrati.setVisibility(View.VISIBLE);
                cardLogin.startAnimation(centerToRight);
                cardRegistrati.startAnimation(leftToCenter);
                txtRegistrati.setTag(false);
                disableAllerror();
                break;
            case R.id.btRegistrati:
                boolean blRegistrati = true;
                if (edtDataRegistrati.getText().toString().length() == 0) {
                    edtDataRegistrati.setError("");
                    blRegistrati = false;
                }

                if (edtNomeRegistrati.getText().toString().length() == 0 || edtNomeRegistrati.getText().toString().length() > 20) {
                    edtNomeRegistrati.setError("Inserire un nome valido");
                    blRegistrati = false;
                }
                if (edtCognomeRegistrati.getText().toString().length() == 0 || edtCognomeRegistrati.getText().toString().length() > 20) {
                    edtCognomeRegistrati.setError("Inserire un cognome valido");
                    blRegistrati = false;
                }
                if (edtEmailRegistrati.getText().toString().length() == 0 || Utils.valideEmail(edtEmailRegistrati.getText().toString())) {
                    edtEmailRegistrati.setError("Inserire una email valida");
                    blRegistrati = false;
                }

                if (edtPasswordRegistrati.getText().toString().length() == 0 || edtPasswordRegistrati.getText().toString().length() > 20) {
                    blRegistrati = false;
                    TextInputLayout textInputLayout = (TextInputLayout) findViewById(edtPasswordRegistrati.getId()).getParent().getParent();
                    textInputLayout.setEndIconVisible(false);
                    edtPasswordRegistrati.setError("Inserire una password valida");
                }

                String data = edtDataRegistrati.getText().toString() ;
                if(StringUtils.isEmpty(data)) {
                    new CustomDialog(this, "Attenzione", "Si prega di inserire una data!", TypeDialog.WARING).show();
                    return;
                }
                try{
                    data = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd/MM/yyyy").parse(data));
                }catch (Exception ex){
                    ex.printStackTrace();
                    new CustomDialog(this,"Errore","Si è verificato un errore", TypeDialog.ERROR).show();
                    blRegistrati = false;
                }

                if (!blRegistrati) return;

                String finalData = data;
                mAuth.createUserWithEmailAndPassword(edtEmailRegistrati.getText().toString().trim(),edtPasswordRegistrati.getText().toString().trim())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            new RegisterWorker(this,createUser(user,edtCognomeRegistrati.getText().toString() + " " + edtNomeRegistrati.getText().toString(), finalData),TypeLogin.EMIAL,iAccessOperation).execute();
                        } else
                            new CustomDialog(this,"Attenzione","Utente già presente!", TypeDialog.WARING).show();
                    });
                break;
            case R.id.btAccedi:
                boolean blAccedi = true;
                if (edtEmailAccedi.getText().toString().length() == 0 || Utils.valideEmail(edtEmailAccedi.getText().toString())) {
                    edtEmailAccedi.setError("Inserire una email valida");
                    blAccedi = false;
                }

                if (edtPasswordAccedi.getText().toString().length() == 0 || edtPasswordAccedi.getText().toString().length() > 20) {
                    blAccedi = false;
                    TextInputLayout textInputLayout = (TextInputLayout) findViewById(edtPasswordAccedi.getId()).getParent().getParent();
                    textInputLayout.setEndIconVisible(false);
                    edtPasswordAccedi.setError("Inserire una password valida");
                }

                if (!blAccedi) return;

                mAuth.signInWithEmailAndPassword(edtEmailAccedi.getText().toString(), edtPasswordAccedi.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            new LoginWorker(this,createUserWithImage(user,""), TypeLogin.EMIAL,iAccessOperation).execute();
                        } else {
                            new CustomDialog(this,"Attenzione","Non è stato trovato nessun utente con i dati inseriti", TypeDialog.WARING).show();
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                        }
                    });
                break;
            case R.id.signInGoogle:
                signInGoogle();
                break;
            case R.id.signInGit:
                signInGitHub();
                break;
        }
    }

    private Utente createUserWithImage(FirebaseUser firebaseUser,String urlImage){
        return new Utente(firebaseUser.getDisplayName(),firebaseUser.getEmail(),"",TypeUtente.NOCOMPLETED,firebaseUser.getUid(),urlImage);
    }
    private Utente createUser(FirebaseUser firebaseUser,String displayName,String Data){
        return new Utente(displayName,firebaseUser.getEmail(),Data, TypeUtente.NOCOMPLETED,firebaseUser.getUid(),"");
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }


    private final IAccessOperation iAccessOperation = new IAccessOperation() {
        @Override
        public void OnCompleteOperation(JSONObject response) {
            try {
                int result = response.getInt("result");
                CustomDialog customDialog;
                switch (ResponseAccess.fromValue(result)){
                    case SUCCESS:
                        Utente utente = TraduceComunication.getUtente(response.getJSONObject("Utente"));
                        reloadToken(utente.getId());
                        DBHandler.getIstance(LoginActivity.this).logginUser(utente);
                        new LoadVarchiWoker(LoginActivity.this,0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                        break;
                    case ERR_PARAM:
                        customDialog = new CustomDialog(LoginActivity.this,"Errore","Errore nell'inserimento dei parametri di accesso", TypeDialog.ERROR);
                        customDialog.show();
                        break;
                    case NOT_CREATED:
                        customDialog = new CustomDialog(LoginActivity.this,"Errore","Si è verificato un errore durante la creazione dell'utente!", TypeDialog.ERROR);
                        customDialog.show();
                        break;
                    case NOT_FIND:
                        customDialog = new CustomDialog(LoginActivity.this,"Attenzione","Non è stato trovato nessun utente con i dati inseriti", TypeDialog.WARING);
                        customDialog.show();
                        break;
                }
            }catch (Exception ex){
                ex.printStackTrace();
                CustomDialog customDialog = new CustomDialog(LoginActivity.this,"Attenzione","Si è verificato un errore", TypeDialog.WARING);
                customDialog.show();
            }

        }

        @Override
        public void OnError() {
            CustomDialog customDialog = new CustomDialog(LoginActivity.this,"Errore","Errore durante la comunicazione con il server!", TypeDialog.ERROR);
            customDialog.show();
        }

    };

    private void reloadToken(int idUtente){
        mMessaging.getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("TAG", "Fetching FCM registration token failed", task.getException());
                return;
            }

            String token = task.getResult();
            new UpdateTokenWorker(this,idUtente,token).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        });
    }

}