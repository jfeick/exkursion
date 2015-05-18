package de.uni_weimar.m18.anatomiederstadt.user;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasIOException;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.RequestToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import de.uni_weimar.m18.anatomiederstadt.LevelSelectActivity;
import de.uni_weimar.m18.anatomiederstadt.R;


public class LoginActivity extends AppCompatActivity {

    private final static String SIGNUP_TOKEN_KEY = "signup_token_key";
    public static final String EXTRA_USERNAME = "de.uni_weimar.m18.exkursion.username.EXTRA";

    private String mUsername;
    private String mPassword;

    private AutoCompleteTextView mUserView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;

    private RequestToken mSignupOrLogin;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState != null) {
            mSignupOrLogin = savedInstanceState.getParcelable(SIGNUP_TOKEN_KEY);
        }

        mUsername = getIntent().getStringExtra(EXTRA_USERNAME);
        mUserView = (AutoCompleteTextView) findViewById(R.id.email);
        mUserView.setText(mUsername);
        Account[] accounts = AccountManager.get(this).getAccounts();
        Set<String> emailSet = new HashSet<String>();
        for (Account account : accounts) {
            if (EMAIL_PATTERN.matcher(account.name).matches()) {
                emailSet.add(account.name);
            }
        }
        mUserView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(emailSet)));



        mLoginStatusView = findViewById(R.id.login_status);
        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.login || actionId == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });



        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        TextView registerLink = (TextView) findViewById(R.id.register_link);
        registerLink.setPaintFlags(registerLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        final Context context = this;
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        TextView lostPasswordLink = (TextView) findViewById(R.id.lost_password_link);
        lostPasswordLink.setPaintFlags(registerLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        lostPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LostPasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSignupOrLogin != null) {
            showProgress(false);
            mSignupOrLogin.suspend();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSignupOrLogin != null) {
            showProgress(true);
            mSignupOrLogin.resume(onComplete);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSignupOrLogin != null) {
            outState.putParcelable(SIGNUP_TOKEN_KEY, mSignupOrLogin);
        }
    }

    private void completeLogin(boolean success) {
        showProgress(false);
        mSignupOrLogin = null;
        if (success) {
            Intent intent = new Intent(this, LevelSelectActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            mPasswordView.setError(getString(R.string.username_or_password_incorrect));
            mPasswordView.requestFocus();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    public void attemptLogin() {
        // Reset errors
        mUserView.setError(null);
        mPasswordView.setError(null);

        // store values at time of login attempt
        mUsername = mUserView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // check for valid password
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.password_empty));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 4) {
            mPasswordView.setError(getString(R.string.password_too_short));
            focusView = mPasswordView;
            cancel = true;
        }

        // check for valid email address
        if (TextUtils.isEmpty(mUsername)) {
            mUserView.setError(getString(R.string.username_empty));
            focusView = mUserView;
            cancel = true;
        } else if (!mUsername.endsWith(getString(R.string.required_email_suffix))) {
            mUserView.setError(getString(R.string.request_required_email_suffix));
            focusView = mUserView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mLoginStatusMessageView.setText(getString(R.string.signing_in));
            showProgress(true);
            loginWithBaasBox();
        }
    }

    private void loginWithBaasBox() {
        BaasUser user = BaasUser.withUserName(mUsername);
        user.setPassword(mPassword);
        mSignupOrLogin = user.login(onComplete);
    }

    private final BaasHandler<BaasUser> onComplete = new BaasHandler<BaasUser>() {
        @Override
        public void handle(BaasResult<BaasUser> baasResult) {
            mSignupOrLogin = null;
            if (baasResult.isFailed()) {
                Log.d("ERROR", "ERROR", baasResult.error());

            }

            if (baasResult.error() instanceof BaasIOException) {
                // TODO - display error that network is down
                Log.e("ERROR", "Network is down!");
            }

            completeLogin(baasResult.isSuccess());
        }
    };

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginStatusView.setVisibility(View.VISIBLE);
        mLoginStatusView.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });

        mLoginFormView.setVisibility(View.VISIBLE);
        mLoginFormView.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 0 : 1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
