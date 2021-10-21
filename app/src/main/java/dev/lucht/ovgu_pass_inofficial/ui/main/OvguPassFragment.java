package dev.lucht.ovgu_pass_inofficial.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.net.URI;
import java.net.URISyntaxException;

import dev.lucht.ovgu_pass_inofficial.MainActivity;
import dev.lucht.ovgu_pass_inofficial.OvguPassApplication;
import dev.lucht.ovgu_pass_inofficial.R;
import dev.lucht.ovgu_pass_inofficial.databinding.FragmentOvgupassBinding;

import dev.lucht.ovgu_pass_inofficial.databinding.FragmentOvgupassBinding;

public class OvguPassFragment extends Fragment {

    private WebView ovguPassWebViev;

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    private FragmentOvgupassBinding binding;

    public static OvguPassFragment newInstance(int index) {
        OvguPassFragment fragment = new OvguPassFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentOvgupassBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });

        ovguPassWebViev = (WebView) root.findViewById(R.id.ovgupass);

        if (!getUsername(getActivity().getApplication().getApplicationContext()).equals("")&&!getPassword(getActivity().getApplication().getApplicationContext()).equals("")){
            openOvguPass();
        }
        else {
/*
            SettingsFragment fragment = SettingsFragment.newInstance(0);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.ovgupass, fragment, "TAG");
            transaction.commit();
*/
            ((MainActivity)getActivity()).setCurrentTab(1);
        }

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    protected void openOvguPass(){

        Log.i("Activity: ", this.getActivity().toString());
        Log.i("Application: ", this.getActivity().getApplication().toString());

        String username;
        String password;

        username = getUsername(getActivity().getApplication().getApplicationContext());
        password = getPassword(getActivity().getApplication().getApplicationContext());

//        while (username.equals("")||password.equals("")){
//
//            username = getUsername(getActivity().getApplication().getApplicationContext());
//            password = getPassword(getActivity().getApplication().getApplicationContext());
//        }


        String finalUsername = username;
        String finalPassword = password;
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
                    Log.i("Website", "Filling login Data");
                    //Fill in login information
                    view.loadUrl("javascript:  document.getElementById('username').value = '" + finalUsername + "';" +
                            " document.getElementById('password').value = '" + finalPassword + "';" +
                                    "var z = document.getElementsByName('_eventId_proceed')[0].click();"
//                            "var z = document.getElementsByClassName('form-element form-button')[0].click();"
                    );
                }
                if ("idp-serv.uni-magdeburg.de".equals(uri.getHost())) {
                    Log.i("Website", "Pressing Accept");
                    view.loadUrl("javascript: document.getElementsByName('_eventId_proceed')[0].click();"
//                            "var z = document.getElementsByClassName('form-element form-button')[0].click();"
                    );
                }
            }
        });

        ovguPassWebViev.loadUrl("https://pass.ovgu.de/");
        ovguPassWebViev.getSettings().setJavaScriptEnabled(true);

    }

    public static String getUsername(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("myAppPackage", 0);
        return prefs.getString("username", "");
    }
    public static String getPassword(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("myAppPackage", 0);
        return prefs.getString("password", "");
    }
}
