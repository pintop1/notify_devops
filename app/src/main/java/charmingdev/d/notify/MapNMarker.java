package charmingdev.d.notify;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import charmingdev.d.notify.fragments.BrowseReports;
import charmingdev.d.notify.fragments.CreateReport;
import charmingdev.d.notify.fragments.Login;
import charmingdev.d.notify.fragments.MapFragment;
import charmingdev.d.notify.fragments.MyReports;
import charmingdev.d.notify.fragments.Register;
import charmingdev.d.notify.prefs.UserInfo;
import charmingdev.d.notify.prefs.UserSession;
import de.hdodenhof.circleimageview.CircleImageView;


public class MapNMarker extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    FragmentManager fragmentManager;
    UserInfo userInfo;
    UserSession userSession;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_nmarker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        userInfo = new UserInfo(this);
        userSession = new UserSession(this);
        progressDialog = new ProgressDialog(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        hideMenu();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_root,
                new MapFragment()).commit();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(userSession.isUserLoggedIn()) {
            TextView FULLNAME = (TextView) navigationView.getHeaderView(0).findViewById(R.id.fullName);

            CircleImageView PHOTO = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);

            String Image = userInfo.getKeyPhoto();
            String fullname = userInfo.getKeyFirstname() + " " + userInfo.getKeyLastname();

            //decode base64 string to image
            byte[] imageBytes = Base64.decode(Image, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            PHOTO.setImageBitmap(decodedImage);

            FULLNAME.setText(fullname.toUpperCase());
        } else {

        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_nmarker, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            fragmentManager.beginTransaction().replace(R.id.container_root,
                    new MapFragment()).addToBackStack("connection").commit();
        }else if(id == R.id.nav_mine){
            fragmentManager.beginTransaction().replace(R.id.container_root,
                    new MyReports()).addToBackStack("connection").commit();
        }else if(id == R.id.nav_browse){
            fragmentManager.beginTransaction().replace(R.id.container_root,
                    new BrowseReports()).addToBackStack("connection").commit();
        }else if(id == R.id.nav_report){
            fragmentManager.beginTransaction().replace(R.id.container_root,
                    new CreateReport()).addToBackStack("connection").commit();
        }else if(id == R.id.nav_weather){
            fragmentManager.beginTransaction().replace(R.id.container_root,
                    new WeatherFrag()).addToBackStack("connection").commit();
        }else if(id == R.id.nav_login){
            fragmentManager.beginTransaction().replace(R.id.container_root,
                    new Login()).addToBackStack("connection").commit();
        }else if(id == R.id.nav_register){
            fragmentManager.beginTransaction().replace(R.id.container_root,
                            new Register()).addToBackStack("connection").commit();
        }else if(id == R.id.nav_logout){
            if(userSession.isUserLoggedIn()){
                userSession.setLoggedIn(false);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("logging out.....");
                progressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        startActivity(new Intent(MapNMarker.this, MapNMarker.class));
                        finish();

                    }
                }, 4000);

            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void hideMenu(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_menu = navigationView.getMenu();
        if(userSession.isUserLoggedIn()){
            nav_menu.findItem(R.id.nav_login).setVisible(false);
            nav_menu.findItem(R.id.nav_register).setVisible(false);
            nav_menu.findItem(R.id.nav_logout).setVisible(true);
        }else {
            nav_menu.findItem(R.id.nav_login).setVisible(true);
            nav_menu.findItem(R.id.nav_register).setVisible(true);
            nav_menu.findItem(R.id.nav_logout).setVisible(false);
        }
    }


    private void enterSessions(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        Boolean session = userSession.isUserLoggedIn();
        CircleImageView DP = (CircleImageView) navigationView.findViewById(R.id.profile_image);

        TextView FULLNAME = (TextView) navigationView.findViewById(R.id.fullName);
        if(session){
            String Image = userInfo.getKeyPhoto();
            String fullname = userInfo.getKeyFirstname() + " " + userInfo.getKeyLastname();
            FULLNAME.setText(fullname.toUpperCase());

            byte[] decodeString = Base64.decode(Image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
            DP.setImageBitmap(decodedByte);
        }
    }
}
