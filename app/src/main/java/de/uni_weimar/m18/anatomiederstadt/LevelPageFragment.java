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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.uni_weimar.m18.anatomiederstadt.element.ButtonFragment;
import de.uni_weimar.m18.anatomiederstadt.element.ImageFragment;
import de.uni_weimar.m18.anatomiederstadt.element.InputFragment;
import de.uni_weimar.m18.anatomiederstadt.element.LatexFragment;
import de.uni_weimar.m18.anatomiederstadt.element.LocationFragment;
import de.uni_weimar.m18.anatomiederstadt.element.Quiz4Buttons;
import de.uni_weimar.m18.anatomiederstadt.element.QuizMulti;
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
        View view = inflater.inflate(R.layout.fragment_level_page, container, false);

        TextView tv = (TextView)view.findViewById(R.id.text_header);
        tv.setText(mPageId);

        if(savedInstanceState != null) {
            mPageId = getArguments().getString(ARG_PARAM1);
            mBasePath = getArguments().getString(ARG_PARAM2);
        } else {
            populateLayoutFromXML();
        }


        return view;
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

    private void addSlider(int min, int max, String suffix) {
        SliderFragment sliderFragment = SliderFragment.newInstance(min, max, suffix);
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

    private void addQuizMulti(ArrayList<String> options, String targetId) {
        QuizMulti quizMulti = QuizMulti.newInstance(options, targetId);
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
                    Node suffix = attributes.getNamedItem("suffix");
                    addSlider(Integer.parseInt(min.getNodeValue()),
                            Integer.parseInt(max.getNodeValue()),
                            suffix.getNodeValue());
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
                if(item.getNodeName().equals("quizmulti")) {
                    NamedNodeMap attributes = item.getAttributes();
                    Node target = attributes.getNamedItem("target");
                    //NodeList optionsList = item.getElementsByTagName("option");
                    Node parent = item.getParentNode();
                    ArrayList<String> options = new ArrayList<>();
                    if (parent instanceof Element) {
                        final Element e = (Element) parent;
                        NodeList optionsList = e.getElementsByTagName("option");
                        for(int k = 0; k < optionsList.getLength(); ++k) {
                            Node option = optionsList.item(k);
                            options.add(option.getTextContent());
                        }
                        addQuizMulti(options, target.getNodeValue());
                    }
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error! Exception " + e.getMessage());
            e.printStackTrace();
        }
        commitChildFragments();
    }

    private void storeVariable(String name, String value) {
        LevelStateManager stateManager =
                ((AnatomieDerStadtApplication) getActivity().getApplicationContext()).getStateManager(getActivity());
        stateManager.saveFloat(name, Float.parseFloat(value));
    }

    private void evaluateMath(String var, String expression) {
        Matcher m = Pattern.compile("\\$([_a-zA-Z][_a-zA-Z0-9]{0,30})").matcher(expression);
        LevelStateManager stateManager =
                ((AnatomieDerStadtApplication) getActivity().getApplicationContext()).getStateManager(getActivity());
        String replacedExpr = expression;
        while(m.find()) {
            replacedExpr = replacedExpr.replace(m.group(0),
                    Float.toString(stateManager.getFloat(m.group(1))));
        }
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
