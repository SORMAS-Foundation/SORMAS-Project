package de.symeda.sormas.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
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
		WebSettings settings = binding.webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.enableSmoothTransition();
		String url = getIntent().getStringExtra(URL);
		binding.webView.loadUrl(url);
		binding.webView.setWebViewClient(new CustomWebViewClient());
		binding.viewOnBrowser.setOnClickListener(l -> {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(browserIntent);
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
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			hideProgressBar();
			super.onPageStarted(view, url, favicon);
		}
	}
}
