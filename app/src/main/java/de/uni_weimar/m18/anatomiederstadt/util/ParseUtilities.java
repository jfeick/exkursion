package de.uni_weimar.m18.anatomiederstadt.util;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import de.uni_weimar.m18.anatomiederstadt.AnatomieDerStadtApplication;
import de.uni_weimar.m18.anatomiederstadt.data.level.LevelColumns;

public class ParseUtilities {
    private static final String LOG_TAG = ParseUtilities.class.getSimpleName();

    public static void parseLevelXML(String basePath, Activity activity) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        String levelFilename = "level.xml";
        File levelXmlFile = new File(activity.getExternalFilesDir(null)
                + "/" + basePath + "/" + levelFilename);
        if(!levelXmlFile.exists()) {
            return;
        }
        FileInputStream fstream = null;
        try{
            fstream = new FileInputStream(levelXmlFile);
            Document xmlDocument = documentBuilder.parse(new InputSource(fstream));
            LevelStateManager stateManager =
                    ((AnatomieDerStadtApplication) activity.getApplicationContext()).getStateManager(activity);
            stateManager.setLevelXML(xmlDocument);
            stateManager.setBasePath(basePath);
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "Error! FileNotFoundException: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            new MaterialDialog.Builder(activity)
                    .title("Error")
                    .content("Fehler während des Verarbeitens der Level-Datei!\n" + e.getMessage())
                    .positiveText("OK")
                    .show();
            Log.e(LOG_TAG, "Error! Could not parse level.xml file!");
            e.printStackTrace();
        }
    }
}
