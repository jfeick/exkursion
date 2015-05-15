package de.uni_weimar.m18.anatomiederstadt;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasIOException;
import com.baasbox.android.BaasResult;
import com.baasbox.android.RequestToken;
import com.baasbox.android.json.JsonObject;
import com.baasbox.android.net.HttpRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;


public class LostPasswordActivity extends AppCompatActivity {

    private final static String SIGNUP_TOKEN_KEY = "signup_token_key";
    public static final String EXTRA_USERNAME = "de.uni_weimar.m18.exkursion.username.EXTRA";

    private String mUsername;

    private AutoCompleteTextView mUserView;
    private View mLostPasswordFormView;
    private View mLostPasswordStatusView;
    private TextView mLoginStatusMessageView;

    private RequestToken mSignupOrLogin;

    private Context mContext;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_password);

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

        mLostPasswordFormView = findViewById(R.id.lost_password_form);
        mLoginStatusMessageView = (TextView) findViewById(R.id.lost_password_status_message);
        mLostPasswordStatusView = findViewById(R.id.lost_password_status);

        mContext = this;

        findViewById(R.id.lost_password_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLostPassword();
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

    private void showLoginActivity() {
        showProgress(false);
        mSignupOrLogin = null;

        Intent intent = new Intent(this, LevelSelectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    public void attemptLostPassword() {
        // Reset errors
        mUserView.setError(null);

        // store values at time of login attempt
        mUsername = mUserView.getText().toString();

        boolean cancel = false;
        View focusView = null;

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
            lostPasswordWithBaasBox();
        }
    }

    private void lostPasswordWithBaasBox() {
        //BaasUser user = BaasUser.withUserName(mUsername);
        BaasBox cli = BaasBox.getDefault();
        cli.rest(HttpRequest.GET,
                "user/" + mUsername + "/password/reset",
                new JsonObject(),
                false,
                onComplete
        );
    }

    private final BaasHandler<JsonObject> onComplete = new BaasHandler<JsonObject>() {
        @Override
        public void handle(BaasResult<JsonObject> baasResult) {

            if (baasResult.error() instanceof BaasIOException) {
                // TODO - display error that network is down
                Log.e("ERROR", "Network is down!");
            }

            if (baasResult.isFailed()) {
                Log.d("ERROR", "ERROR", baasResult.error());

            }

            if (baasResult.isSuccess()) {
                new MaterialDialog.Builder(mContext)
                        .title(getString(R.string.lost_password_successfull_title))
                        .content(getString(R.string.lost_password_successfull_content))
                        .positiveText(R.string.lost_password_successfull_dismiss)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                showLoginActivity();
                            }
                        })
                        .show();
            }
        }
    };

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLostPasswordStatusView.setVisibility(View.VISIBLE);
        mLostPasswordStatusView.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLostPasswordStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });

        mLostPasswordFormView.setVisibility(View.VISIBLE);
        mLostPasswordFormView.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 0 : 1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLostPasswordFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
