package info.thereisonlywe.core.essentials;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

public class IOEssentialsAndroid {

	public static final File PATH_EXTERNAL_STORAGE = Environment
			.getExternalStorageDirectory();

	// http://stackoverflow.com/a/10043533/1341947
	public static String readString(String assetName, Context context)
	{
		StringBuilder ReturnString = new StringBuilder();
		InputStream fIn = null;
		InputStreamReader isr = null;
		BufferedReader input = null;
		try
		{
			fIn = context.getResources().getAssets()
					.open(assetName, Context.MODE_PRIVATE);
			isr = new InputStreamReader(fIn);
			input = new BufferedReader(isr);
			String line = "";
			while ((line = input.readLine()) != null)
			{
				ReturnString.append(line);
			}
		}
		catch (Exception e)
		{
			e.getMessage();
		}
		finally
		{
			try
			{
				if (isr != null) isr.close();
				if (fIn != null) fIn.close();
				if (input != null) input.close();
			}
			catch (Exception e2)
			{
				e2.getMessage();
			}
		}
		return ReturnString.toString();
	}

	@SuppressLint("NewApi")
	public static void download(Context context, Uri dest, Uri source,
			String title, String description)
	{
		DownloadManager.Request request = new DownloadManager.Request(source);
		request.setDescription(description);
		request.setTitle(title);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			request.allowScanningByMediaScanner();
			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		}
		request.setDestinationUri(dest);

		DownloadManager manager = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);
		manager.enqueue(request);
	}

	public static boolean isDownloadManagerAvailable(Context context)
	{
		try
		{
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) { return false; }
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setClassName("com.android.providers.downloads.ui",
					"com.android.providers.downloads.ui.DownloadList");
			List<ResolveInfo> list = context.getPackageManager()
					.queryIntentActivities(intent,
							PackageManager.MATCH_DEFAULT_ONLY);
			return list.size() > 0;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public static boolean gotInternetConnection(ConnectivityManager conMgr)
	{
		NetworkInfo i = conMgr.getActiveNetworkInfo();
		if (i == null) return false;
		if (!i.isConnected()) return false;
		if (!i.isAvailable()) return false;
		return IOEssentials.gotInternetConnection();
	}

}
