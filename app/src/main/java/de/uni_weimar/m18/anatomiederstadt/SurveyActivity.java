package de.uni_weimar.m18.anatomiederstadt;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;


public class SurveyActivity extends AppCompatActivity {

    RadioButton mRadioAgainYes;
    RadioButton mRadioAgainNo;
    RadioButton mRadioContentYes;
    RadioButton mRadioContentMaybe;
    RadioButton mRadioContentNo;
    EditText mEditTextGood;
    EditText mEditTextBad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        mRadioAgainYes = (RadioButton) findViewById(R.id.radioAgainYes);
        mRadioAgainNo = (RadioButton) findViewById(R.id.radioAgainNo);
        mRadioContentYes = (RadioButton) findViewById(R.id.radioContentYes);
        mRadioContentMaybe = (RadioButton) findViewById(R.id.radioContentMaybe);
        mRadioContentNo = (RadioButton) findViewById(R.id.radioContentNo);
        mEditTextGood = (EditText) findViewById(R.id.editTextGood);
        mEditTextBad = (EditText) findViewById(R.id.editTextBad);

        Button submitButton = (Button) findViewById(R.id.submitButton);
        final Context context = this;
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String again = "";
                if(mRadioAgainYes.isChecked()) again = "yes";
                else if(mRadioAgainNo.isChecked()) again = "no";
                String content = "";
                if(mRadioContentYes.isChecked()) content = "yes";
                else if(mRadioContentMaybe.isChecked()) content = "maybe";
                else if(mRadioContentNo.isChecked()) content = "no";

                String good = mEditTextGood.getText().toString();
                String bad = mEditTextBad.getText().toString();

                BaasDocument doc = new BaasDocument("survey");
                doc.put("again", again);
                doc.put("content", content);
                doc.put("good", good);
                doc.put("bad", bad);


                doc.save(new BaasHandler<BaasDocument>() {
                    @Override
                    public void handle(BaasResult<BaasDocument> baasResult) {
                        if(baasResult.isSuccess()) {
                            new MaterialDialog.Builder(context)
                                    .title("Danke")
                                    .content("Vielen Dank für Dein Feedback!")
                                    .positiveText("OK")
                                    .callback(new MaterialDialog.ButtonCallback() {
                                        @Override
                                        public void onPositive(MaterialDialog dialog) {
                                            Intent intent = new Intent(context, LevelSelectActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .show();
                        } else {
                            new MaterialDialog.Builder(context)
                                    .title("Fehler")
                                    .content("Bei der Übertragung ist leider ein Fehler aufgetreten :(")
                                    .positiveText("OK")
                                    .callback(new MaterialDialog.ButtonCallback() {
                                        @Override
                                        public void onPositive(MaterialDialog dialog) {
                                            Intent intent = new Intent(context, LevelSelectActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .show();
                        }
                    }
                });
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_survey, menu);
        return true;
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
