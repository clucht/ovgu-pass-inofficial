package dev.lucht.ovgu_pass_inofficial;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URI;
import java.net.URISyntaxException;

import dev.lucht.ovgu_pass_inofficial.ui.main.SectionsPagerAdapter;
import dev.lucht.ovgu_pass_inofficial.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private WebView ovguPassWebViev;

    private String username = "";
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        openOvguPass();
    }

    protected void openOvguPass(){
        ovguPassWebViev = findViewById(R.id.ovgupass);
        ovguPassWebViev.loadUrl("https://pass.ovgu.de/");
        ovguPassWebViev.getSettings().setJavaScriptEnabled(true);

        ovguPassWebViev.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if ("pass.ovgu.de".equals(request.getUrl().getHost())) {
                    return false;
                }
                else if ("idp-serv.uni-magdeburg.de".equals(request.getUrl().getHost())) {
                    return false;
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                URI uri = null;
                try {
                    uri = new URI(url);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                if ("idp-serv.uni-magdeburg.de".equals(uri.getHost())) {
                    //TODO Fill in login information
                    view.loadUrl("javascript:  document.getElementById('username').value = '" + username + "';" +
                            " document.getElementById('password').value = '" + password + "';" +
                            "var z = document.getElementById('submit').click();"
                    );
                }
            }
        });

    }

}