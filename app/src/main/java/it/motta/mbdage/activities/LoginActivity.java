package it.motta.mbdage.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import it.motta.mbdage.R;
import it.motta.mbdage.dialog.DateTimePickerDialog;
import it.motta.mbdage.utils.Parameters;
import it.motta.mbdage.utils.Utils;

@SuppressLint({"ResourceType","ClickableViewAccessibility","NonConstantResourceId"})
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;
    private SignInButton  signInButton;
    private TextInputEditText edtEmailAccedi, edtPasswordAccedi,
            edtNomeRegistrati, edtCognomeRegistrati, edtEmailRegistrati, edtPasswordRegistrati, edtDataRegistrati;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private Animation centerToRight,leftToCenter;
    private Button btAccedi, btRegistrati;
    private TextView txtRegistrati, txtDesc;
    private CardView cardLogin, cardRegistrati;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        txtRegistrati = findViewById(R.id.txtRegistrati);
        txtDesc = findViewById(R.id.txtDesc);

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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        txtRegistrati.setTag(true);
        centerToRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pushright);
        leftToCenter = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fromleft);

        txtRegistrati.setOnClickListener(this);
        btRegistrati.setOnClickListener(this);
        btAccedi.setOnClickListener(this);
        signInButton.setOnClickListener(this);

        edtPasswordRegistrati.setOnKeyListener((v, keyCode, event) -> {
            ((TextInputLayout) findViewById(v.getId()).getParent().getParent()).setEndIconVisible(true);
            return false;
        });

        edtPasswordAccedi.setOnKeyListener((v, keyCode, event) -> {
            ((TextInputLayout) findViewById(v.getId()).getParent().getParent()).setEndIconVisible(true);
            return false;
        });

        edtDataRegistrati.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                ((TextInputLayout) findViewById(edtEmailRegistrati.getId()).getParent().getParent()).setEndIconVisible(true);
                saveDataRegistrazione(edtDataRegistrati);
                edtDataRegistrati.clearFocus();
            }
        });

        edtDataRegistrati.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                saveDataRegistrazione(edtDataRegistrati);
            }
            return true;
        });

    }

    @SuppressLint("SimpleDateFormat")
    private void saveDataRegistrazione(TextInputEditText edt) {
        try {

            Date date = Objects.requireNonNull(edt.getText()).toString().length() == 0 ? Utils.getFirstDayOfYear(20) : new SimpleDateFormat("dd/MM/yyyy").parse(edt.getText().toString());
            DateTimePickerDialog dateTimePickerDialog = new DateTimePickerDialog(this, "Data di nascita", date);
            dateTimePickerDialog.setOnDismissListener(dialog -> {
                if (dateTimePickerDialog.getDateChoosed() != null) {
                    edt.setText(new SimpleDateFormat("dd/MM/yyyy").format(dateTimePickerDialog.getDateChoosed()));
                }
            });
            dateTimePickerDialog.show();
        } catch (ParseException e) {
            if (Parameters.DEBUG_MODE) e.printStackTrace();

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

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            // reload();
        }
    }
    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("TAG", "signInWithCredential:success");
                FirebaseUser user = mAuth.getCurrentUser();

                // updateUI(user);
            } else {
                // If sign in fails, display a message to the user.
                Log.w("TAG", "signInWithCredential:failure", task.getException());
                // updateUI(null);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtRegistrati:
                if ((boolean) view.getTag()) {
                    cardLogin.setVisibility(View.GONE);
                    cardRegistrati.setVisibility(View.VISIBLE);
                    cardLogin.startAnimation(centerToRight);
                    cardRegistrati.startAnimation(leftToCenter);
                    txtRegistrati.setTag(false);
                    txtRegistrati.setText("Accedi");
                    txtDesc.setText("Hai già un account ?");
                } else {
                    txtRegistrati.setTag(true);
                    txtDesc.setText("Non hai ancora un account ?");
                    txtRegistrati.setText("Registrati");
                    cardLogin.setVisibility(View.VISIBLE);
                    cardRegistrati.setVisibility(View.GONE);
                    cardLogin.startAnimation(leftToCenter);
                    cardRegistrati.startAnimation(centerToRight);
                }
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

                if (!blRegistrati) return;

                mAuth.createUserWithEmailAndPassword(edtEmailRegistrati.getText().toString().trim(),  edtPasswordRegistrati.getText().toString().trim())
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }
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
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("TAG", "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    // updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("TAG", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    // updateUI(null);
                                }
                            }
                        });
                break;
            case R.id.signInGoogle:
                signInGoogle();
        }

    }
}