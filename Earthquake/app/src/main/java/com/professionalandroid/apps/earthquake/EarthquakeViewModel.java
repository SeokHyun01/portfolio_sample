package com.professionalandroid.apps.earthquake;

import android.annotation.SuppressLint;
import android.app.Application;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class EarthquakeViewModel extends AndroidViewModel {

    private LiveData<List<Earthquake>> earthquakes;

    public EarthquakeViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Earthquake>> getEarthquakes() {
        if (earthquakes == null) {
            earthquakes = EarthquakeDatabaseAccessor
                    .getInstance(getApplication())
                    .earthquakeDAO()
                    .loadAllEarthquakes();

            loadEarthquakes();
        }

        return earthquakes;
    }

    public void loadEarthquakes() {
        EarthquakeUpdateWorker.scheduleUpdateJob(getApplication());
    }
}
