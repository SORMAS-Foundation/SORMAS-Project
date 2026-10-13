package de.symeda.sormas.app;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.databinding.ActivityCustomWebViewBinding;

public class CustomWebView extends AppCompatActivity {

	private static final String URL = "URL";
	ActivityCustomWebViewBinding binding;

	public static void startActivity(Context fromActivity, String url) {
		Intent intent = new Intent(fromActivity, CustomWebView.class);
		intent.putExtra(URL, url);
		fromActivity.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityCustomWebViewBinding.inflate(getLayoutInflater());
		showProgressBar();
		setContentView(binding.getRoot());
		String url = getIntent().getStringExtra(URL);
		// only http(s) links are loaded, other schemes like "javascript:", "file:" or "content:" are rejected
		if (!DataHelper.isHttpUrl(url)) {
			Log.w(getClass().getSimpleName(), "Refusing to open non http(s) link: " + url);
			finish();
			return;
		}
		WebSettings settings = binding.webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setAllowFileAccess(false);
		settings.setAllowContentAccess(false);
		settings.enableSmoothTransition();
		binding.webView.setWebViewClient(new CustomWebViewClient());
		binding.webView.loadUrl(url);
		binding.viewOnBrowser.setOnClickListener(l -> {
			try {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			} catch (ActivityNotFoundException e) {
				Log.w(getClass().getSimpleName(), "No browser available to open " + url, e);
			}
		});
	}

	private void showProgressBar() {
		binding.progressBar.setVisibility(View.VISIBLE);
	}

	private void hideProgressBar() {
		binding.progressBar.setVisibility(View.GONE);
	}

	private class CustomWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
			// keep navigation inside the web view restricted to http(s)
			return !DataHelper.isHttpUrl(request.getUrl().toString());
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			hideProgressBar();
			super.onPageStarted(view, url, favicon);
		}
	}
}
