package dev.lucht.ovgu_pass_inofficial.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.net.URI;
import java.net.URISyntaxException;

import dev.lucht.ovgu_pass_inofficial.R;
import dev.lucht.ovgu_pass_inofficial.databinding.FragmentMainBinding;

import dev.lucht.ovgu_pass_inofficial.databinding.FragmentMainBinding;

public class OvguPassFragment extends Fragment {

    private WebView ovguPassWebViev;

    private String username = "";
    private String password = "";


    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    private FragmentMainBinding binding;

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

        binding = FragmentMainBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });

        View view = inflater.inflate(R.layout.fragment_ovgupass, container, false);
        ovguPassWebViev = (WebView) view.findViewById(R.id.ovgupass);

        openOvguPass();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    protected void openOvguPass(){

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
//                    view.loadUrl("javascript:  document.getElementById('username').value = '" + username + "';" +
//                            " document.getElementById('password').value = '" + password + "';" +
//                            "var z = document.getElementById('submit').click();"
//                    );
                }
            }
        });

        ovguPassWebViev.loadUrl("https://pass.ovgu.de/");
        ovguPassWebViev.getSettings().setJavaScriptEnabled(true);

    }
}
