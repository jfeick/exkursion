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

package de.uni_weimar.m18.exkursion.element;


import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import de.uni_weimar.m18.exkursion.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LatexFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LatexFragment extends Fragment {
    private static final String LOG_TAG = LatexFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";


     private String getLatexCode() {
        return mLatexCode;
    }

    private String mLatexCode;

    public static LatexFragment newInstance(String param1) {
        LatexFragment fragment = new LatexFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public LatexFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLatexCode = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_latex, container, false);
        WebView latexView = (WebView)root.findViewById(R.id.latexWebView);
        latexView.getSettings().setJavaScriptEnabled(true);
        latexView.getSettings().setBuiltInZoomControls(false);
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(R.color.windowBackgroundColor, typedValue, true);
        latexView.setBackgroundColor(typedValue.data);

        latexView.loadDataWithBaseURL("http://bar", "<script type='text/x-mathjax-config'>"
                +"MathJax.Hub.Config({ "
                +"showMathMenu: false, "
                +"jax: ['input/TeX','output/HTML-CSS'], "
                +"extensions: ['tex2jax.js'], "
                +"TeX: { extensions: ['AMSmath.js','AMSsymbols.js',"
                +"'noErrors.js','noUndefined.js'] } "
                +"});</script>"
                +"<script type='text/javascript' "
                +"src='file:///android_asset/MathJax/MathJax.js'"
                +"></script><span id='math'></span>","text/html","utf-8","");


        Log.v(LOG_TAG, "Loading latexCode: " + mLatexCode);
        latexView.setWebViewClient(new WebViewClient() {
            private String mLatexCode = null;
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(Build.VERSION.SDK_INT < 19) {
                    view.loadUrl("javascript:document.getElementById('math').innerHTML='\\\\["
                            + getLatexCode() + "\\\\]'");
                    view.loadUrl("javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);");
                } else {
                    view.evaluateJavascript("javascript:document.getElementById('math').innerHTML='\\\\["
                            + doubleEscapeTeX(getLatexCode()) + "\\\\]'", null);
                    view.evaluateJavascript("javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);"
                            , null);
                }
            }
        });


        return root;
    }

    private String doubleEscapeTeX(String s) {
        String t="";
        for (int i=0; i < s.length(); i++) {
            if (s.charAt(i) == '\'') t += '\\';
            if (s.charAt(i) != '\n') t += s.charAt(i);
            if (s.charAt(i) == '\\') t += "\\";
        }
        return t;
    }

}
