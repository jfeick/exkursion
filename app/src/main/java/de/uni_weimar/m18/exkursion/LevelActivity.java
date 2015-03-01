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

import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class LevelActivity extends ActionBarActivity {

    private static final String LOG_TAG = LevelActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        if (savedInstanceState == null) {
            LevelFragment level = new LevelFragment();
            level.setArguments(getIntent().getExtras());
            Log.v(LOG_TAG, "Receiced extras in activity: " + getIntent().getExtras().getString("level_path"));

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, level).commit();
            //getSupportFragmentManager().beginTransaction()
            //        .add(R.id.container, new LevelFragment())
            //        .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_level, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class LevelFragment extends Fragment {

        private static final String LOG_TAG = LevelPrepareFragment.class.getSimpleName();

        private WeakReference<LevelParserTask> asyncTaskWeakRef;

        private String mPath;

        public LevelFragment() {
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setRetainInstance(true);
            mPath = getActivity().getIntent().getExtras().getString("level_path");
            Log.v(LOG_TAG, "Fragment received levelPath: " + mPath);
            LevelParserTask levelParserTask = new LevelParserTask(this);
            this.asyncTaskWeakRef = new WeakReference<LevelParserTask>(levelParserTask);
            levelParserTask.execute(mPath);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_level, container, false);

            LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.layout_container);
            /*for(int i = 0; i < 100; ++i) {
                //TextView newText = new TextView(getActivity().getApplicationContext());
                //newText.setText("Dynamic Textview No " + Integer.toString(i));
                //ll.addView(newText);
                TextFragment textFragment = TextFragment.newInstance("DemoText der viel viel länger ist, und somit über die nächste Zeile reicht.... hoffentlich! " + Integer.toString(i));
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                ft.add(R.id.layout_container, textFragment);
                ft.commit();
            }*/
            return rootView;
        }
    }

    private static class LevelParserTask extends AsyncTask<String, Void, File> {
        private WeakReference<LevelFragment> fragmentWeakRef;

        Document xml;

        private LevelParserTask(LevelFragment fragment) {
            this.fragmentWeakRef = new WeakReference<LevelFragment>(fragment);
        }

        @Override
        protected File doInBackground(String... path) {
            String root = Environment.getExternalStorageDirectory().toString();
            File appFolder = new File(root + "/Android/data/de.uni_weimar.m18.exkursion" + "/" + path[0]);
            File file = new File(appFolder, "level.xml");
            if (!file.exists()) {
                Log.e(LOG_TAG, "Error: File not found: " + file);
                // level file is missing!?
                return null;
            }
            try {
                return file;
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            return null;
        }

        private Document processLevelXML(File levelFile) throws Exception {
            Log.v(LOG_TAG, "Processing XML file for level layout");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document xmlDocument = documentBuilder.parse(levelFile);

            xml = xmlDocument;
            return xmlDocument;
        }

        void addText(String value) {

            // TODO dont commit transaction
            TextFragment textFragment = TextFragment.newInstance(value);
            FragmentTransaction ft = this.fragmentWeakRef.get().getChildFragmentManager().beginTransaction();
            ft.add(R.id.layout_container, textFragment);
            ft.commit();
        }

        void addImage() {
            // TODO dont commit transaction yet
            ImageFragment imageFragment = ImageFragment.newInstance();
            FragmentTransaction ft = this.fragmentWeakRef.get().getChildFragmentManager().beginTransaction();
            ft.add(R.id.layout_container, imageFragment);
            ft.commit();
        }

        void addQuizMultipleChoice(String button1text, String button2text, String button3text,
                                   String button4text, int correctAnswer) {
            // TODO dont commit
            QuizMultipleChoiceFragment quizFragment = QuizMultipleChoiceFragment.newInstance(
                    button1text, button2text, button3text, button4text, correctAnswer);
            FragmentTransaction ft = this.fragmentWeakRef.get().getChildFragmentManager().beginTransaction();
            ft.add(R.id.layout_container, quizFragment);
            ft.commit();
        }

        @Override
        protected void onPostExecute(File file) {
            //super.onPostExecute(xmlDocument);

            try {
                Document xmlDocument = processLevelXML(file);

                Element rootElement = xmlDocument.getDocumentElement();
                Log.v(LOG_TAG, "RootElement: " + rootElement.getTagName());
                NodeList pageList = rootElement.getElementsByTagName("page");
                Node currentPage = null;

                //Toast.makeText(this.fragmentWeakRef.get().getActivity(), "Found " +
                //        Integer.toString(pageList.getLength()) + " pages in Level", Toast.LENGTH_LONG);
                //TextView tv = (TextView) this.fragmentWeakRef.get().getActivity().findViewById(R.id.text_header);
                //tv.setText("Found " + Integer.toString(pageList.getLength()) + " pages in Level");
                Log.v(LOG_TAG, "Found " + Integer.toString(pageList.getLength()) + " pages in Level");
                if (pageList.getLength() > 0) {
                    Node page1 = pageList.item(0);
                    Log.v(LOG_TAG, "page1: nodename: " + page1.getNodeName());
                    NodeList childNodes = page1.getChildNodes();
                    for (int i = 0; i < childNodes.getLength(); ++i) {
                        Log.v(LOG_TAG, "at page-childnode " + Integer.toString(i));
                        Node item = childNodes.item(i);
                        if (item.getNodeName().equals("text")) {
                            Log.v(LOG_TAG, "text node found - value: " + item.getTextContent());
                            addText(item.getTextContent());
                        }
                        if (item.getNodeName().equals("image")) {
                            addImage();
                        }
                        if (item.getNodeName().equals("quiz")) {
                            NamedNodeMap attributes = item.getAttributes();
                            Node button1 = attributes.getNamedItem("button1");
                            //Log.v(LOG_TAG, "button1: " + button1.getNodeValue());
                            Node button2 = attributes.getNamedItem("button2");
                            //Log.v(LOG_TAG, "button2: " + button2.getNodeValue());
                            Node button3 = attributes.getNamedItem("button3");
                            //Log.v(LOG_TAG, "button3: " + button3.getNodeValue());
                            Node button4 = attributes.getNamedItem("button4");
                            //Log.v(LOG_TAG, "button4: " + button4.getNodeValue());
                            Node correctAnswer = attributes.getNamedItem("correctAnswer");
                            //Log.v(LOG_TAG, "correctAnswer: " + correctAnswer.getNodeValue());

                            addQuizMultipleChoice(button1.getNodeValue(), button2.getNodeValue(),
                                    button3.getNodeValue(), button4.getNodeValue(),
                                    Integer.parseInt(correctAnswer.getNodeValue()));
                            //addQuizMultipleChoice("Häuser", "Brunnen", "Laternen", "Hundekot", 2);
                        }
                    }
                }

        } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
