package com.example.uli2.userprofilemgmt;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.uli2.userprofilemgmt.Persistence.AppDatabase;
import com.example.uli2.userprofilemgmt.UtilitiesHelperAdapter.AsyncResponse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by uli on 27/07/17.
 */

public class Singleton {
    private static  Singleton mInstance = null;

    private  String mString;
    ConnectionClass mConnection;

    Connection con = null;
    Statement stmt = null;
    String db = "jdbc:jtds:sqlserver://ptmpgesqlsvrdev.pertamina.com:1433/UserProfileManagement";
    String uname = "sa";
    String pass = "sqlserver2012PGE";

    public List<List<String>> results = new ArrayList<List<String>>();
    public ArrayList<String> input = new ArrayList<>();

    public ArrayList<ConnectionClass> ConnectionList = new ArrayList<>();
    public HashMap<String, List<List<String>>> hashMap = new HashMap<>();

    public List<List<String>> AnnuallyTotalUtilization = new ArrayList<>();
    public List<List<String>> AnnuallyAverageUtilization = new ArrayList<>();


    public List<List<String>> MonthlyTotalUtilization = new ArrayList<>();
    public List<List<String>> MonthlyAppUtilization = new ArrayList<>();
    public List<List<String>> MonthlyTopApplication = new ArrayList<>();
    public List<List<String>> MonthlyTopUser = new ArrayList<>();
    public List<List<String>> MonthlyAverageUtilization = new ArrayList<>();

    public List<List<String>> DailyTotalUtilization = new ArrayList<>();
    public List<List<String>> DailyAverageUtilization = new ArrayList<>();

    public List<List<String>> AnnuallyVisitor = new ArrayList<>();
    public List<List<String>> MonthlyVisitor = new ArrayList<>();
    public List<List<String>> DailyVisitor = new ArrayList<>();





    private Singleton() {
        mString = "Hello";
        mConnection = new ConnectionClass();

    }

    public static Singleton getInstance() {
        if(mInstance == null) {
            synchronized (Singleton.class) {
                if(mInstance == null) {
                    mInstance = new Singleton();
                }
            }
        }
        return mInstance;
    }

    public void newSingleton() {
        mInstance = new Singleton();
    }

    private class ConnectionClass extends AsyncTask<String, String, String> {
        public AsyncResponse delegate = null;
        Dialog dialog;
        String z="";
        int y = 0;
        @Override
        protected String doInBackground(String... query) {

            try{
                String hashIndex = query[0]; //get index
                String q = query[1]; // da query
                List<String> attributeNames = new ArrayList<String>();
                for(int i = 2; i < query.length; i++) {   //iterate until index n
                    attributeNames.add(query[i]);
                }
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                con = DriverManager.getConnection(db, uname, pass);
                if(con == null) {
                    z = "Cannot connect. Check your internet access!";
                }
                else {
                    results = new ArrayList<List<String>>();
                    for(int j = 0; j < attributeNames.size(); j++) {
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(q);
                        input = new ArrayList<>();
                        Log.d("myTag", "ugh heeelp");

                        while (rs.next()) {
                            String value = rs.getString(attributeNames.get(j));
                            Log.d("myTag", "query ga ada isinya");
                            input.add(""+value);
                        }
//                        results.add(input);
                        hashMap.get(hashIndex).add(input);
                        Log.d("myTag", "klo ini ke print, input added to hashmap");

                        if(j == attributeNames.size()-1) {
                            rs.close();
                            con.close();
                            Log.d("myTag", "closed da KONEKSHION");

                        }
                    }

                }
            }
            catch (Exception e) {
                e.getMessage();
            }
            return z;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            delegate.processFinish(result);
        }
    }

    public void setDelegate(AsyncResponse output) {
        mConnection.delegate = output;
    }
    public String getString() {
        return this.mString;
    }

    public void setString(String value) {
        mString = value;
    }

    public void setMonthlyTotalUtilization(String currdate) {
        ConnectionList.add(new ConnectionClass());
        ConnectionList.get(ConnectionList.size()-1).delegate = mConnection.delegate;

        hashMap.put("MTU", MonthlyTotalUtilization);
        ConnectionList.get(ConnectionList.size()-1).executeOnExecutor(AsyncTask
                        .THREAD_POOL_EXECUTOR, "MTU", "exec " + "dbo" +
                        ".stp_GetMonthlyTotalUtilization '"+ currdate +"'", "Label", "Value");

    }

    public void getApplicationActivity() {
        hashMap.put("MAU", MonthlyAppUtilization);
        mConnection.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "MAU",
                "exec dbo.stp_GetListMonthlyApplicationUtilization '2017-06-01'",
                "ApplicationName", "UserActual", "UserRegistered", "Utilization");

    }

    public void getTopMonthlyApplication(String currdate) {
        ConnectionList.add(new ConnectionClass());
        ConnectionList.get(ConnectionList.size()-1).delegate = mConnection.delegate;

        hashMap.put("MTA", MonthlyTopApplication);
        ConnectionList.get(ConnectionList.size()-1).executeOnExecutor(AsyncTask
                        .THREAD_POOL_EXECUTOR, "MTA",
                "exec dbo .stp_GetListTopMonthlyApplicationAccess '"+ currdate +"'", "Label",
                "Value");
    }

    public void getTopMonthlyUser(String currdate) {
        ConnectionList.add(new ConnectionClass());
        ConnectionList.get(ConnectionList.size()-1).delegate = mConnection.delegate;

        hashMap.put("MU", MonthlyTopUser);
        ConnectionList.get(ConnectionList.size()-1).executeOnExecutor(AsyncTask
                        .THREAD_POOL_EXECUTOR, "MU", "exec dbo.stp_GetListTopMonthlyUserAccess " +
                        "'2017-06-01'", "Label", "Value");
    }

    public void setAnnuallyTotalUtilization() {
        ConnectionList.add(new ConnectionClass());
        ConnectionList.get(ConnectionList.size()-1).delegate = mConnection.delegate;

        hashMap.put("ATU", AnnuallyTotalUtilization);
        ConnectionList.get(ConnectionList.size()-1).executeOnExecutor(AsyncTask
                .THREAD_POOL_EXECUTOR, "ATU", "exec " + "dbo" +
                ".stp_GetAnnuallyTotalUtilization", "Label", "Value");
    }

    public void setDailyTotalUtilization(String currdate) {
        ConnectionList.add(new ConnectionClass());
        ConnectionList.get(ConnectionList.size()-1).delegate = mConnection.delegate;

        hashMap.put("DTU", DailyTotalUtilization);
        ConnectionList.get(ConnectionList.size()-1).executeOnExecutor(AsyncTask
                .THREAD_POOL_EXECUTOR, "DTU", "exec " + "dbo" +
                ".stp_GetDailyTotalUtilization '"+ currdate +"'", "Label", "Value");
    }

    public void setCurrentUser() {
        ConnectionList.add(new ConnectionClass());
        ConnectionList.get(ConnectionList.size()-1).delegate = mConnection.delegate;

        hashMap.put("PU", DailyTotalUtilization);
        ConnectionList.get(ConnectionList.size()-1).executeOnExecutor(AsyncTask
                .THREAD_POOL_EXECUTOR, "PU", "exec " + "dbo" +
                ".stp_GetPagingUser", "UserName", "UserDisplayName");
    }

    public void setAnnuallyVisitor() {
        ConnectionList.add(new ConnectionClass());
        ConnectionList.get(ConnectionList.size()-1).delegate = mConnection.delegate;

        hashMap.put("AV", AnnuallyVisitor);
        ConnectionList.get(ConnectionList.size()-1).executeOnExecutor(AsyncTask
                .THREAD_POOL_EXECUTOR, "AV", "exec " + "dbo" +
                ".stp_GetListAnnuallyVisitor", "Label", "Value");

    }

    public void setMonthlyVisitor(String currdate) {
        ConnectionList.add(new ConnectionClass());
        ConnectionList.get(ConnectionList.size()-1).delegate = mConnection.delegate;

        hashMap.put("MV", MonthlyVisitor);
        ConnectionList.get(ConnectionList.size()-1).executeOnExecutor(AsyncTask
                .THREAD_POOL_EXECUTOR, "MV", "exec " + "dbo" +
                ".stp_GetListMonthlyVisitor '"+ currdate +"'", "Label", "Value");

    }

    public void setDailyVisitor(String currdate) {
        ConnectionList.add(new ConnectionClass());
        ConnectionList.get(ConnectionList.size()-1).delegate = mConnection.delegate;

        hashMap.put("DV", MonthlyVisitor);
        ConnectionList.get(ConnectionList.size()-1).executeOnExecutor(AsyncTask
                .THREAD_POOL_EXECUTOR, "DV", "exec " + "dbo" +
                ".stp_GetListDailyVisitor '"+ currdate +"'", "Label", "Value");

    }

    public void setAnnuallyAverageUtilization() {
        ConnectionList.add(new ConnectionClass());
        ConnectionList.get(ConnectionList.size()-1).delegate = mConnection.delegate;

        hashMap.put("AAU", AnnuallyAverageUtilization);
        ConnectionList.get(ConnectionList.size()-1).executeOnExecutor(AsyncTask
                .THREAD_POOL_EXECUTOR, "AAU", "exec " + "dbo" +
                ".stp_GetAnnuallyAverageUtilization", "Value4");
    }

    public void setMonthlyAverageUtilization(String currdate) {
        ConnectionList.add(new ConnectionClass());
        ConnectionList.get(ConnectionList.size()-1).delegate = mConnection.delegate;

        hashMap.put("MAAU", MonthlyAverageUtilization);
        ConnectionList.get(ConnectionList.size()-1).executeOnExecutor(AsyncTask
                .THREAD_POOL_EXECUTOR, "MAAU", "exec " + "dbo" +
                ".stp_GetMonthlyAverageUtilization'"+ currdate +"'", "Value4");
    }

    public void setDailyAverageUtilization(String currdate) {
        ConnectionList.add(new ConnectionClass());
        ConnectionList.get(ConnectionList.size()-1).delegate = mConnection.delegate;

        hashMap.put("DAU", DailyAverageUtilization);
        ConnectionList.get(ConnectionList.size()-1).executeOnExecutor(AsyncTask
                .THREAD_POOL_EXECUTOR, "DAU", "exec " + "dbo" +
                ".stp_GetDailyAverageUtilization'"+ currdate +"'", "Value4");
    }

}
