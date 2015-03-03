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

package de.uni_weimar.m18.exkursion;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TestLevelFragment extends Fragment
 /*   implements QuizMultipleChoiceFragment.OnFragmentInteractionListener */ {

    private static final String LOG_TAG = TestLevelFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";


    // TODO: Rename and change types of parameters
    private String mText;
    private int mPageNum;
    private String mBasePath;

    private ArrayList<Fragment> mChildFragments = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public static TestLevelFragment newInstance(String text, int pageNum, String basePath) {
        TestLevelFragment fragment = new TestLevelFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, text);
        args.putInt(ARG_PARAM2, pageNum);
        args.putString(ARG_PARAM3, basePath);
        fragment.setArguments(args);
        return fragment;
    }

    public TestLevelFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mText = getArguments().getString(ARG_PARAM1);
            mPageNum = getArguments().getInt(ARG_PARAM2);
            mBasePath = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test_level, container, false);

        TextView tv = (TextView)view.findViewById(R.id.text_header);
        tv.setText(mText);

        populateLayoutFromXML();


        return view;
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

    void removeChildFragments() {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        for (Fragment fragment : mChildFragments) {
            ft.remove(fragment);
            mChildFragments.remove(fragment);
        }
        ft.commitAllowingStateLoss();
        //mChildFragments.clear();
    }

    void addText(String value) {
        TextFragment textFragment = TextFragment.newInstance(value);
        mChildFragments.add(textFragment);
    }

    void addImage(String src) {
        ImageFragment imageFragment = ImageFragment.newInstance(mBasePath + "/" + src);
        mChildFragments.add(imageFragment);
    }

    void addQuizMultipleChoice(String button1text, String button2text, String button3text,
                               String button4text, int correctAnswer) {
        QuizMultipleChoiceFragment quizFragment = QuizMultipleChoiceFragment.newInstance(
                button1text, button2text, button3text, button4text, correctAnswer);
        mChildFragments.add(quizFragment);
    }

    private void populateLayoutFromXML() {
        try {
            LevelStateManager stateManager =
                    ((MainApplication) getActivity().getApplicationContext()).getStateManager();
            Document levelXml = stateManager.getLevelXML();
            Element rootElement = levelXml.getDocumentElement();
            Log.v(LOG_TAG, "RootElement: " + rootElement.getTagName());
            NodeList pageList = rootElement.getElementsByTagName("page");
            Node page = pageList.item(mPageNum);
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
                if (item.getNodeName().equals("quiz")) {
                    NamedNodeMap attributes = item.getAttributes();
                    Node button1 = attributes.getNamedItem("button1");
                    Node button2 = attributes.getNamedItem("button2");
                    Node button3 = attributes.getNamedItem("button3");
                    Node button4 = attributes.getNamedItem("button4");
                    Node correctAnswer = attributes.getNamedItem("correctAnswer");

                    addQuizMultipleChoice(button1.getNodeValue(), button2.getNodeValue(),
                            button3.getNodeValue(), button4.getNodeValue(),
                            Integer.parseInt(correctAnswer.getNodeValue()));
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error! Exception " + e.getMessage());
            e.printStackTrace();
        }

        commitChildFragments();
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void switchToNextPage();
        public void switchToTarget(int pageNum);
    }

}
