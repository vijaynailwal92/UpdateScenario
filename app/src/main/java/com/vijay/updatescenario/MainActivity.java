package com.vijay.updatescenario;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MainActivity extends AppCompatActivity {
    String currentVersion;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CheckPlayStoreVersion();
    }

    public void CheckPlayStoreVersion() {
        PackageManager pm = getApplicationContext().getPackageManager();
        PackageInfo pInfo = null;

        try {
            pInfo = pm.getPackageInfo(getApplicationContext().getPackageName(), 0);
            currentVersion = pInfo.versionName;
            Log.e("Current version", currentVersion);

        } catch (PackageManager.NameNotFoundException e1) {

            e1.printStackTrace();
        }
        new GetVersionCode().execute();


    }

    private void showUpdateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("A New Update is Available");
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.productions.spagroup.spaportfolio")));
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
            }
        });

        builder.create();
        dialog = builder.show();


    }

    public int compareVersions(String str1, String str2) {

        if (str1.equals(str2)) return 0;

        String[] vals1 = str1.split("\\.");
        String[] vals2 = str2.split("\\.");

        int i = 0;


        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) i++;
        if (i < vals1.length && i < vals2.length)
            return Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));

        if (i < vals1.length) {
            boolean allZeros = true;
            for (int j = i; allZeros & (j < vals1.length); j++)
                allZeros &= (Integer.parseInt(vals1[j]) == 0);
            return allZeros ? 0 : -1;
        }

        if (i < vals2.length) {
            boolean allZeros = true;
            for (int j = i; allZeros & (j < vals2.length); j++)
                allZeros &= (Integer.parseInt(vals2[j]) == 0);
            return allZeros ? 0 : 1;
        }
        return 0;
    }

    private class GetVersionCode extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String newVersion = "";
            try {
                Document doc = Jsoup.connect("https://play.google.com/store/apps/details?id=com.productions.spagroup.spaportfolio").get();
                String docstr = doc.toString();
                Log.e("Document", docstr);
                String latestVersion = doc.getElementsByClass("htlgb").get(6).text();
                return latestVersion;
            } catch (Exception e) {
                Log.e("Exception", e.toString());
                return newVersion;
            }
        }


        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);


            //to check statically put the value
            //version name are in three digit
            onlineVersion = "1.1.2";


            Log.e("Newer version", onlineVersion);
            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                int i = compareVersions(currentVersion, onlineVersion);
                Log.e("Int_com", String.valueOf(i));
                if (i == -1) {
                    showUpdateDialog();
                } else {

                }
          /*  if (Float.valueOf(currentVersion) < Float.valueOf(2)) {
                showUpdateDialog();
                //show dialog
            }*/
            }
            Log.e("update", "Current version " + currentVersion + "playstore version " + onlineVersion);
        }


    }

}
