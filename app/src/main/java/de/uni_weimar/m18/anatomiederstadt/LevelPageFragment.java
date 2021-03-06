/*
 * Copyright 2015. J.F.Eick
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package de.uni_weimar.m18.anatomiederstadt;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.json.JsonException;
import com.baasbox.android.json.JsonObject;
import com.baasbox.android.net.HttpRequest;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.EventListener;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.uni_weimar.m18.anatomiederstadt.element.ButtonFragment;
import de.uni_weimar.m18.anatomiederstadt.element.ImageFragment;
import de.uni_weimar.m18.anatomiederstadt.element.InputCheckFragment;
import de.uni_weimar.m18.anatomiederstadt.element.InputFragment;
import de.uni_weimar.m18.anatomiederstadt.element.LatexFragment;
import de.uni_weimar.m18.anatomiederstadt.element.LocationFragment;
import de.uni_weimar.m18.anatomiederstadt.element.Quiz4Buttons;
import de.uni_weimar.m18.anatomiederstadt.element.QuizMulti;
import de.uni_weimar.m18.anatomiederstadt.element.ScoreFragment;
import de.uni_weimar.m18.anatomiederstadt.element.SliderFragment;
import de.uni_weimar.m18.anatomiederstadt.element.TextFragment;
import de.uni_weimar.m18.anatomiederstadt.util.LevelStateManager;
import parsii.eval.Expression;
import parsii.eval.Parser;
import parsii.eval.Scope;
import parsii.tokenizer.ParseException;

import static android.content.SharedPreferences.*;

public class LevelPageFragment extends Fragment
 /*   implements Quiz4Buttons.OnFragmentInteractionListener */ {

    private static final String LOG_TAG = LevelPageFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mPageId;
    private String mBasePath;
    private boolean mIsCreated = false;

    private ArrayList<Fragment> mChildFragments = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    private View mContainer;

    public interface OnFragmentInteractionListener {
        /*
        public void switchToNextPage();
        public void switchToTarget(int pageNum);
        */
        public void switchToTarget(String id);
    }


    public static LevelPageFragment newInstance(String pageId, String basePath) {
        LevelPageFragment fragment = new LevelPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, pageId);
        args.putString(ARG_PARAM2, basePath);
        fragment.setArguments(args);
        return fragment;
    }

    public LevelPageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPageId = getArguments().getString(ARG_PARAM1);
            mBasePath = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContainer = inflater.inflate(R.layout.fragment_level_page, container, false);

        //TextView tv = (TextView)view.findViewById(R.id.text_header);
        //tv.setText(mPageId);


        if(savedInstanceState != null) {
            mPageId = getArguments().getString(ARG_PARAM1);
            mBasePath = getArguments().getString(ARG_PARAM2);
        } else {
            populateLayoutFromXML();
        }


        return mContainer;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_PARAM1, mPageId);
        outState.putString(ARG_PARAM2, mBasePath);
        outState.putBoolean("recreated", mIsCreated);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    void commitChildFragments() {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        for (Fragment fragment : mChildFragments) {
            ft.add(R.id.layout_container, fragment);
        }
        ft.commit();
    }

    void addText(String value) {
        value = replaceStringWithVars(value, true);
        TextFragment textFragment = TextFragment.newInstance(value);
        mChildFragments.add(textFragment);
    }

    void addImage(String src) {
        ImageFragment imageFragment = ImageFragment.newInstance(mBasePath + "/" + src);
        mChildFragments.add(imageFragment);
    }

    /*
    void addQuizMultipleChoice(String button1text, String button2text, String button3text,
                               String button4text, int correctAnswer, String correctTargetId) {
        Quiz4Buttons quizFragment = Quiz4Buttons.newInstance(
                button1text, button2text, button3text, button4text, correctAnswer,
                correctTargetId);
        mChildFragments.add(quizFragment);
    }
    */

    void addQuiz4Element(String target, String correct, String button1, String button2, String button3,
                         String button4, String hint, String points, String penalty) {
        int correctInt = Integer.parseInt(correct);
        int pointsInt  = Integer.parseInt(points);
        int penaltyInt = Integer.parseInt(penalty);
        Quiz4Buttons quiz4Buttons = Quiz4Buttons.newInstance(
                target, correctInt, button1, button2, button3, button4, hint, pointsInt, penaltyInt, mPageId
        );
        mChildFragments.add(quiz4Buttons);
    }

    private void addLatex(String latexCode) {
        LatexFragment latexFragment = LatexFragment.newInstance(latexCode);
        mChildFragments.add(latexFragment);
    }

    private void addSlider(int min, int max, float granularity, String suffix, String var) {
        SliderFragment sliderFragment = SliderFragment.newInstance(min, max, granularity, suffix, var);
        mChildFragments.add(sliderFragment);
    }

    private void addLocation(String latitude, String longitude, String targetId) {
        LocationFragment locationFragment =
                LocationFragment.newInstance(latitude, longitude, targetId);
        mChildFragments.add(locationFragment);
    }

    private void addButton(String caption, String targetId) {
        ButtonFragment buttonFragment =
                ButtonFragment.newInstance(caption, targetId);
        mChildFragments.add(buttonFragment);
    }

    private void addInput(String buttonCaption, String var, String targetId) {
        InputFragment inputFragment =
                InputFragment.newInstance(buttonCaption, var, targetId);
        mChildFragments.add(inputFragment);
    }

    private void addQuizMulti(ArrayList<String> options, ArrayList<String> correctList, String targetId, String points) {
        QuizMulti quizMulti = QuizMulti.newInstance(options, correctList, targetId, Integer.parseInt(points), mPageId);
        mChildFragments.add(quizMulti);
    }


    private void populateLayoutFromXML() {
        try {
            LevelStateManager stateManager =
                    ((AnatomieDerStadtApplication) getActivity().getApplicationContext()).getStateManager(getActivity());
            Document levelXml = stateManager.getLevelXML();
            Element rootElement = levelXml.getDocumentElement();
            Log.v(LOG_TAG, "RootElement: " + rootElement.getTagName());
            NodeList pageList = rootElement.getElementsByTagName("page");
            Node page = null; // = pageList.item(mPageNum);
            for(int i = 0; i < pageList.getLength(); ++i) {
                if(pageList.item(i).getAttributes().getNamedItem("id").getNodeValue().equals(mPageId)) {
                    page = pageList.item(i);
                    // save current page id in shared preferences
                    SharedPreferences sharedPref =
                            PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                    Editor editor = sharedPref.edit();
                    editor.putBoolean(getString(R.string.user_is_playing_boolean), true);
                    editor.putString(getString(R.string.resume_base_path), mBasePath);
                    editor.putString(getString(R.string.resume_page_id), mPageId);
                    editor.commit();
                }
            }
            if(page == null) {
                Log.e(LOG_TAG, "Error! Page with id " + mPageId + " could not be found in level.xml!");
                return;
            }

            Log.v(LOG_TAG, "page: nodename: " + page.getNodeName());
            NodeList childNodes = page.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); ++i) {
                Log.v(LOG_TAG, "at page-childnode " + Integer.toString(i));
                Node item = childNodes.item(i);
                if (item.getNodeName().equals("text")) {
                    Log.v(LOG_TAG, "text node found - value: " + item.getTextContent());
                    addText(item.getTextContent());
                }
                if (item.getNodeName().equals("image")) {
                    NamedNodeMap attributes = item.getAttributes();
                    Node src = attributes.getNamedItem("src");
                    addImage(src.getNodeValue());
                }
                if(item.getNodeName().equals("quiz")) {
                    String target = "";
                    String correct = "";
                    String button1 = "";
                    String button2 = "";
                    String button3 = "";
                    String button4 = "";
                    String hint = "";
                    String points = "";
                    String penalty = "";

                    NodeList quizParamaters = item.getChildNodes();
                    for (int k = 0; k < quizParamaters.getLength(); ++k) {
                        Node child = quizParamaters.item(k);
                        String quizParameter = child.getNodeName();
                        switch (quizParameter) {
                            case "target":
                                target = child.getTextContent();
                                break;
                            case "correct":
                                correct = child.getTextContent();
                                break;
                            case "button1":
                                button1 = child.getTextContent();
                                break;
                            case "button2":
                                button2 = child.getTextContent();
                                break;
                            case "button3":
                                button3 = child.getTextContent();
                                break;
                            case "button4":
                                button4 = child.getTextContent();
                                break;
                            case "hint":
                                hint = child.getTextContent();
                                break;
                            case "points":
                                points = child.getTextContent();
                                break;
                            case "penalty":
                                penalty = child.getTextContent();
                                break;
                            default:
                                break;
                                //throw new IllegalArgumentException("Invalid quiz parameter" + quizParameter);
                        }
                    }
                    addQuiz4Element(target, correct, button1, button2, button3, button4, hint, points, penalty);
                }
                /*
                if (item.getNodeName().equals("quiz")) {
                    NamedNodeMap attributes = item.getAttributes();
                    Node button1 = attributes.getNamedItem("button1");
                    Node button2 = attributes.getNamedItem("button2");
                    Node button3 = attributes.getNamedItem("button3");
                    Node button4 = attributes.getNamedItem("button4");
                    Node correctAnswer = attributes.getNamedItem("correctAnswer");
                    Node correctTarget = attributes.getNamedItem("correctTarget");

                    addQuizMultipleChoice(button1.getNodeValue(), button2.getNodeValue(),
                            button3.getNodeValue(), button4.getNodeValue(),
                            Integer.parseInt(correctAnswer.getNodeValue()),
                            correctTarget.getNodeValue());
                }
                */
                if(item.getNodeName().equals("latex")) {
                    addLatex(item.getTextContent());
                }
                if(item.getNodeName().equals("slider")) {
                    NamedNodeMap attributes = item.getAttributes();
                    Node min = attributes.getNamedItem("min");
                    Node max = attributes.getNamedItem("max");
                    Node granularity = attributes.getNamedItem("granularity");
                    Node suffix = attributes.getNamedItem("suffix");
                    Node var = attributes.getNamedItem("var");
                    addSlider(Integer.parseInt(min.getNodeValue()),
                            Integer.parseInt(max.getNodeValue()),
                            Float.parseFloat(granularity.getNodeValue()),
                            suffix.getNodeValue(),
                            var.getNodeValue());
                }
                if(item.getNodeName().equals("location")) {
                    NamedNodeMap attributes = item.getAttributes();
                    Node latitude = attributes.getNamedItem("latitude");
                    Node longitude = attributes.getNamedItem("longitude");
                    Node target = attributes.getNamedItem("target");
                    addLocation(latitude.getNodeValue(), longitude.getNodeValue(),
                            target.getNodeValue());
                }
                if(item.getNodeName().equals("button")) {
                    NamedNodeMap attributes = item.getAttributes();
                    Node caption = attributes.getNamedItem("caption");
                    Node target = attributes.getNamedItem("target");
                    addButton(caption.getNodeValue(), target.getNodeValue());
                }
                if(item.getNodeName().equals("evaluate")) {
                    NamedNodeMap attributes = item.getAttributes();
                    Node var = attributes.getNamedItem("var");
                    String expression = item.getTextContent();
                    evaluateMath(var.getNodeValue(), expression);
                }
                if(item.getNodeName().equals("var")) {
                    NamedNodeMap attributes = item.getAttributes();
                    Node name = attributes.getNamedItem("name");
                    storeVariable(name.getNodeValue(), item.getTextContent());
                }
                if(item.getNodeName().equals("input")) {
                    NamedNodeMap attributes = item.getAttributes();
                    Node buttonCaption = attributes.getNamedItem("caption");
                    Node target = attributes.getNamedItem("target");
                    Node var = attributes.getNamedItem("var");
                    addInput(buttonCaption.getNodeValue(), var.getNodeValue(), target.getNodeValue());
                }
                if(item.getNodeName().equals("inputcheck")) {
                    NamedNodeMap attributes = item.getAttributes();
                    Node buttonCaption = attributes.getNamedItem("caption");
                    Node target = attributes.getNamedItem("target");
                    Node var = attributes.getNamedItem("var");
                    Node correct = attributes.getNamedItem("correct");
                    Node points = attributes.getNamedItem("points");
                    Node hint = attributes.getNamedItem("hint");
                    addInputCheck(buttonCaption.getNodeValue(), var.getNodeValue(), target.getNodeValue(),
                            correct.getNodeValue(), points.getNodeValue(), hint.getNodeValue());
                }
                if(item.getNodeName().equals("quizmulti")) {
                    NamedNodeMap attributes = item.getAttributes();
                    Node target = attributes.getNamedItem("target");
                    Node points = attributes.getNamedItem("points");
                    //NodeList optionsList = item.getElementsByTagName("option");
                    Node parent = item.getParentNode();
                    ArrayList<String> options = new ArrayList<>();
                    ArrayList<String> correctList = new ArrayList<>();
                    if (parent instanceof Element) {
                        final Element e = (Element) parent;
                        NodeList optionsList = e.getElementsByTagName("option");
                        for(int k = 0; k < optionsList.getLength(); ++k) {
                            Node option = optionsList.item(k);
                            if(option.getAttributes().getNamedItem("correct").getNodeValue().equals("true"))
                                correctList.add("true");
                            else
                                correctList.add("false");
                            options.add(option.getTextContent());
                        }
                        addQuizMulti(options, correctList, target.getNodeValue(), points.getNodeValue());
                    }
                }
                if(item.getNodeName().equals("evalpoints")) {
                    NamedNodeMap attributes = item.getAttributes();
                    Node var = attributes.getNamedItem("var");
                    Node correct = attributes.getNamedItem("correct");
                    Node step = attributes.getNamedItem("step");
                    Node points = attributes.getNamedItem("points");
                    Node penalty = attributes.getNamedItem("penalty");
                    evaluatePoints(var.getNodeValue(), correct.getNodeValue(), step.getNodeValue(),
                            points.getNodeValue(), penalty.getNodeValue());
                }
                if(item.getNodeName().equals("score")) {
                    addScore();
                }
                if(item.getNodeName().equals("survey")) {
                    registerSurveyClickHandler();
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error! Exception " + e.getMessage());
            e.printStackTrace();
            new MaterialDialog.Builder(getActivity())
                    .title("Error")
                    .content("Fehler während des Verarbeitens der Level-Datei!\n" + e.getMessage())
                    .positiveText("OK")
                    .show();
        }
        commitChildFragments();
    }

    private void registerSurveyClickHandler() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                        askForSurvey();

                }
            }, 10000);

    }

    private void askForSurvey() {
        new MaterialDialog.Builder(getActivity())
                .title("Geschafft")
                .content("Würdest Du noch kurz an einer kleinen Umfrage zu dieser Software teilnehmen?")
                .positiveText("Ja")
                .negativeText("Lieber nicht")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        Intent intent = new Intent(getActivity(), SurveyActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        Intent intent = new Intent(getActivity(), LevelSelectActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    }
                })
                .show();

    }

    private void addScore() {
        ScoreFragment scoreFragment = ScoreFragment.newInstance();
        mChildFragments.add(scoreFragment);
        // delete resume state
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.user_is_playing_boolean), false);
    }

    private void addInputCheck(String caption, String var, String target, String correct, String points, String hint) {
        InputCheckFragment inputCheckFragment =
                InputCheckFragment.newInstance(caption, var, target, correct,
                        Integer.parseInt(points), hint, mPageId);
        mChildFragments.add(inputCheckFragment);
    }

    private void evaluatePoints(String var, String correct, String step, String points, String penalty) {
        LevelStateManager stateManager =
                ((AnatomieDerStadtApplication) getActivity().getApplicationContext()).getStateManager(getActivity());
        float variableF = stateManager.getFloat(var);
        float correctF  = Float.parseFloat(correct);
        float stepF     = Float.parseFloat(step);
        int pointsI     = Integer.parseInt(points);
        int penaltyI    = Integer.parseInt(penalty);
        float distance = Math.abs(correctF - variableF);
        float steps = distance / stepF;
        int final_penalty = penaltyI * (int) steps;
        int final_points = pointsI - final_penalty;

        String congratulations = String.format("%d Punkte", final_points);
        SnackbarManager.show(
                Snackbar.with(getActivity())
                        .position(Snackbar.SnackbarPosition.TOP)
                        .margin(32, 32)
                        .backgroundDrawable(R.drawable.points_snackbar_shape)
                        .text(congratulations)
        );
        submitPointsToBackend(final_points);
    }

    private void submitPointsToBackend(int points) {
        BaasBox cli = BaasBox.getDefault();
        LevelStateManager stateManager =
                ((AnatomieDerStadtApplication) getActivity().getApplicationContext()).getStateManager(getActivity());
        JsonObject parameters = new JsonObject();
        try {
            parameters.put("level", stateManager.getBasePath());
            parameters.put("question", "q" + mPageId);
            parameters.put("score", points);
        } catch(JsonException e) {

        }
        cli.rest(HttpRequest.POST,
                "plugin/updatescore.bb",
                parameters,
                true,
                new BaasHandler<JsonObject>() {
                    @Override
                    public void handle(BaasResult<JsonObject> baasResult) {
                        Log.d(LOG_TAG, "result: " + baasResult.toString());
                        if (baasResult.isSuccess()) {
                            Log.d(LOG_TAG, "Score for question " + mPageId + " submitted successfully.");
                        } else {
                            Log.d(LOG_TAG, "Error. Could not submit score.");
                        }
                    }

                }
        );
    }

    private void storeVariable(String name, String value) {
        LevelStateManager stateManager =
                ((AnatomieDerStadtApplication) getActivity().getApplicationContext()).getStateManager(getActivity());
        stateManager.saveFloat(name, Float.parseFloat(value));
    }

    private String replaceStringWithVars(String expression, boolean printFloat)
    {
        Matcher m = Pattern.compile("\\$([_a-zA-Z][_a-zA-Z0-9]{0,30})").matcher(expression);
        LevelStateManager stateManager =
                ((AnatomieDerStadtApplication) getActivity().getApplicationContext()).getStateManager(getActivity());
        String replacedExpr = expression;
        while(m.find()) {
            float value = stateManager.getFloat(m.group(1));
            if(printFloat) {
                String floatValue = new DecimalFormat("#.##").format(value);
                replacedExpr = replacedExpr.replace(m.group(0), floatValue);
            }
            else {
                replacedExpr = replacedExpr.replace(m.group(0), Float.toString(value));
            }
        }
        return replacedExpr;
    }

    private void evaluateMath(String var, String expression) {
        LevelStateManager stateManager =
                ((AnatomieDerStadtApplication) getActivity().getApplicationContext()).getStateManager(getActivity());
        String replacedExpr = replaceStringWithVars(expression, false);
        Scope scope = Scope.create();
        Expression expr;
        double result = 1.0;
        try {
            expr = Parser.parse(replacedExpr, scope);
            result = expr.evaluate();
        } catch (ParseException e) {
            Log.d(LOG_TAG, "Parser exception in expression");
        }
        Log.d(LOG_TAG, "Evaluation result:" + Double.toString(result));
        stateManager.saveFloat(var, (float) result);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        //removeChildFragments();
        super.onDetach();
        mListener = null;
    }

    /*
    @Override
    public void correctAnswerAction() {
        if(mListener != null) {
            mListener.switchToNextPage();
        }
    }
    */

}
