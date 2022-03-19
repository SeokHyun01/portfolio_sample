package com.professionalandroid.apps.earthquake;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

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
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class EarthquakeUpdateWorker extends Worker {
    public static final String KEY_LARGEST_EARTHQUAKE_ID = "largest_earthquake_id";
    public static final String KEY_MINIMUM_MAGNITUDE = "minimum_magnitude";

    private static final String TAG = "EarthquakeUpdateJob";

    private static final String KEY_WORK_NAME = "key_work_name";

    private static final String UPDATE_WORK_NAME = "update_work";
    private static final String PERIODIC_UPDATE_WORK_NAME = "periodic_update_work";
    private static final String NOTIFICATION_WORK_NAME = "notification_work";


    public EarthquakeUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Result ArrayList of parsed earthquakes.
        ArrayList<Earthquake> earthquakes = new ArrayList<>();

        // Get the XML
        URL url;
        try {
            String quakeFeed = getApplicationContext().getString(R.string.earthquake_feed);

            url = new URL(quakeFeed);

            URLConnection connection;
            connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;

            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpConnection.getInputStream();
                DocumentBuilderFactory dbf =
                        DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();

                // Parse the earthquake feed.
                Document dom = db.parse(in);
                Element docEle = dom.getDocumentElement();

                // Get a list of each earthquake entry.
                NodeList nl = docEle.getElementsByTagName("entry");
                if (nl != null && nl.getLength() > 0) {
                    for (int i = 0; i < nl.getLength(); i++) {
                        Element entry =
                                (Element) nl.item(i);
                        Element id =
                                (Element) entry.getElementsByTagName("id").item(0);
                        Element title =
                                (Element) entry.getElementsByTagName("title").item(0);
                        Element g =
                                (Element) entry.getElementsByTagName("georss:point")
                                        .item(0);
                        Element when =
                                (Element) entry.getElementsByTagName("updated").item(0);
                        Element link =
                                (Element) entry.getElementsByTagName("link").item(0);

                        String idString = id.getFirstChild().getNodeValue();
                        String details = title.getFirstChild().getNodeValue();
                        String hostname = "http://earthquake.usgs.gov";
                        String linkString = hostname + link.getAttribute("href");
                        String point = g.getFirstChild().getNodeValue();
                        String dt = when.getFirstChild().getNodeValue();

                        SimpleDateFormat sdf =
                                new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
                        Date qdate = new GregorianCalendar(0, 0, 0).getTime();

                        try {
                            qdate = sdf.parse(dt);
                        } catch (ParseException e) {
                            Log.e(TAG, "Date parsing exception.", e);
                        }

                        String[] location = point.split(" ");
                        Location l = new Location("dummyGPS");
                        l.setLatitude(Double.parseDouble(location[0]));
                        l.setLongitude(Double.parseDouble(location[1]));

                        String magnitudeString = details.split(" ")[1];
                        int end = magnitudeString.length() - 1;
                        double magnitude =
                                Double.parseDouble(magnitudeString.substring(0, end));

                        if (details.contains("-"))
                            details = details.split("-")[1].trim();
                        else
                            details = "";

                        final Earthquake earthquake = new Earthquake(idString,
                                qdate,
                                details, l,
                                magnitude,
                                linkString);

                        // Add the new earthquake to our result array.
                        earthquakes.add(earthquake);
                    }
                }
            }
            httpConnection.disconnect();

            Earthquake largestEarthquake = findLargestNewEarthquake(earthquakes);

            EarthquakeDatabaseAccessor
                    .getInstance(getApplicationContext())
                    .earthquakeDAO()
                    .insertEarthquakes(earthquakes);

            Context context = getApplicationContext();
            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(context);
            if (getInputData().getString(KEY_WORK_NAME).equals(UPDATE_WORK_NAME)) {

                int updateFreq = Integer.parseInt(
                        prefs.getString(PreferencesActivity.PREF_UPDATE_FREQ, "60"));
                boolean autoUpdateChecked = prefs.getBoolean(PreferencesActivity.PREF_AUTO_UPDATE, false);

                if (autoUpdateChecked) {
                    Constraints constraints = new Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build();

                    PeriodicWorkRequest request =
                            new PeriodicWorkRequest.Builder(EarthquakeUpdateWorker.class, updateFreq, TimeUnit.MINUTES)
                                    .setConstraints(constraints)
                                    .build();

                    WorkManager.getInstance(context)
                            .enqueueUniquePeriodicWork(PERIODIC_UPDATE_WORK_NAME, ExistingPeriodicWorkPolicy.REPLACE, request);
                }

                Data data = new Data.Builder()
                        .putString(KEY_WORK_NAME, PERIODIC_UPDATE_WORK_NAME)
                        .build();
                return Result.success(data);
            }

            if (largestEarthquake != null) {
                String largestEarthquakeId = largestEarthquake.getId();

                int minimumMagnitude = Integer.parseInt(
                        prefs.getString(PreferencesActivity.PREF_MIN_MAG, "0"));

                Data data = new Data.Builder()
                        .putString(KEY_LARGEST_EARTHQUAKE_ID, largestEarthquakeId)
                        .putInt(KEY_MINIMUM_MAGNITUDE, minimumMagnitude)
                        .build();

                return Result.success(data);
            }

            if (getInputData().getString(KEY_WORK_NAME) == null) {
                Data data = new Data.Builder()
                        .putString(KEY_WORK_NAME, UPDATE_WORK_NAME)
                        .build();

                return Result.success(data);
            }

            return Result.success();
        } catch (MalformedURLException e) {
            Log.e(TAG, "Malformed URL Exception", e);
            return Result.failure();
        } catch (IOException e) {
            Log.e(TAG, "IO Exception", e);
            return Result.retry();
        } catch (ParserConfigurationException e) {
            Log.e(TAG, "Parser Configuration Exception", e);
            return Result.failure();
        } catch (SAXException e) {
            Log.e(TAG, "SAX Exception", e);
            return Result.failure();
        }
    }

    public static void scheduleUpdateJob(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest request =
                new OneTimeWorkRequest.Builder(EarthquakeUpdateWorker.class)
                        .setConstraints(constraints)
                        .build();

        WorkManager.getInstance(context)
                .enqueueUniqueWork(UPDATE_WORK_NAME, ExistingWorkPolicy.KEEP, request);
    }

    private Earthquake findLargestNewEarthquake(List<Earthquake> newEarthquakes) {
        Context context = getApplicationContext();

        List<Earthquake> earthquakes = EarthquakeDatabaseAccessor
                .getInstance(context)
                .earthquakeDAO()
                .loadAllEarthquakesBlocking();

        Earthquake largestNewEarthquake = null;

        for (Earthquake earthquake : newEarthquakes) {
            if (earthquakes.contains(earthquake)) {
                continue;
            }

            if (largestNewEarthquake == null ||
                    earthquake.getMagnitude() > largestNewEarthquake.getMagnitude()) {
                largestNewEarthquake = earthquake;
            }
        }
        return largestNewEarthquake;
    }
}