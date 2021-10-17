package com.zalbyte.theinstaller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import window.fullscreen;

public class MainActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{
    fullscreen full;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    AppCompatSpinner spinner;
    MaterialButton btn_install;
    String url = "";


    ProgressDialog progressDialog;
    //Download stuff
    final String path = Environment.getExternalStorageDirectory().getPath() + "/the_installer/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        full = new fullscreen(MainActivity.this);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.main_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        spinner = findViewById(R.id.spinner);
        btn_install = findViewById(R.id.install_btn);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener( this);

        ArrayList<String> data = getData();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, data);
        spinner.setAdapter(adapter);


        checkDir(path);

        btn_install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDownload();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               switch (i)
               {
                   case 1:
                       //Weebs Edition
                       url = "https://github.com/Neko059/WeebPack/archive/refs/heads/main.zip";
                       break;
                   case 2:
                       //Hentai edition
                       Toast.makeText(MainActivity.this, "Unavailable...", Toast.LENGTH_SHORT).show();
                       break;
                   default:
                       break;
               }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void startDownload()
    {
        if( url != "")
        {
            new DownloadFile().execute(url);
        }
    }

    private ArrayList<String> getData()
    {
        ArrayList<String> datas = new ArrayList<>();
        datas.add("--- Select ---");
        datas.add("Weebs Edition");
        datas.add("Hentai Edition");
        return datas;
    }

    @Override
    public void onBackPressed()
    {
        if( drawerLayout.isDrawerOpen(GravityCompat.START) )
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }else
        {
            super.onBackPressed();
        }
    }





    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        switch (id)
        {
            case R.id.aboutme:
                Toast.makeText(this, "Developer : zal-byte ( github )", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }

        return true;
    }


    public boolean checkDir(String dir)
    {
        File file = new File(dir);
        if(!file.exists())
        {
            file.mkdirs();
            return true;
        }else
        {
            return false;
        }
    }
    @Override
    protected Dialog onCreateDialog(int id )
    {
        switch (id)
        {
            case 0:
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Downloading....");
                progressDialog.setIndeterminate(false);
                progressDialog.setMax(100);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                return progressDialog;

            default:
                return null;
        }
    }

    class Unzipper extends AsyncTask<Void, Void, Boolean>
    {
        String _zip_file, _output;
        ProgressDialog prog;
        public Unzipper(String _zip_file, String _output)
        {
            this._zip_file = _zip_file;
            this._output = _output;
        }
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            prog = new ProgressDialog(MainActivity.this);
            prog.setTitle("Extracting...");
            prog.setMessage("Please wait");
            prog.setCanceledOnTouchOutside(false);
            prog.setCancelable(false);
            prog.show();
        }
        @Override
        protected void onPostExecute(Boolean res)
        {
            super.onPostExecute(res);
            prog.dismiss();
        }
        @Override
        protected Boolean doInBackground(Void... voids) {

            checkDir(_output);
            try {
                File f = new File(_output);
                if(!f.isDirectory())
                {
                    f.mkdirs();
                }
                ZipInputStream zin = new ZipInputStream(new FileInputStream(_zip_file));

                try {
                    ZipEntry ze = null;
                    while((ze = zin.getNextEntry()) != null)
                    {
                        String paths = _output + File.separator + ze.getName();
                        System.out.println("ZIPPER_> "+paths);
                        if(ze.isDirectory())
                        {
                            File unzipFile = new File(paths);
                            if(!unzipFile.isDirectory())
                            {
                                unzipFile.mkdirs();
                            }
                        }else
                        {
                            FileOutputStream fout = new FileOutputStream(paths);
                            int c = 0;
                            byte[] buff = new byte[1024];
                            while((c = zin.read(buff)) != -1)
                            {
                                fout.write(buff, 0, c);
                            }
                            zin.closeEntry();
                            fout.close();
                        }
                    }
                }catch(Exception e)
                {
                    System.out.println(e.getMessage());
                }finally {
                    zin.close();
                }
            }catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
            return null;
        }
    }

    public void unzip(String _zip_file, String _output)
    {
        Unzipper unzipper = new Unzipper(_zip_file, _output);
        unzipper.execute();
    }


    class DownloadFile extends AsyncTask<String, String, String>
    {
        @Override
        protected  void onPreExecute()
        {
            super.onPreExecute();
            showDialog(0);
        }
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL u = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) u.openConnection();
                connection.connect();

                final int fileLength = connection.getContentLength();

                FileOutputStream fos = new FileOutputStream(new File(path+"file.zip"));
                InputStream is = new BufferedInputStream(connection.getInputStream());

                byte[] buffer = new byte[1024];
                int len1 = 0;
                long total = 0;

                while((len1 = is.read(buffer)) != -1 )
                {
                    total += len1;
                    publishProgress(""+(int)((total * 100 / fileLength )));
                    fos.write(buffer, 0, len1);
                }

                fos.close();



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }catch(Exception e)
            {
                Log.d("EXCEPTION_>", e.getMessage());
            }

            return null;
        }
        @Override
        protected void onProgressUpdate(String... progress)
        {
            super.onProgressUpdate(progress);
            Log.d("PROGRESS_>", progress[0]);
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }
        @Override
        protected void onPostExecute(String unused)
        {
            dismissDialog(0);
            unzip(path + "file.zip", Environment.getExternalStorageDirectory().getPath() + "/games/com.mojang/resource_packs/" );

        }

    }

}