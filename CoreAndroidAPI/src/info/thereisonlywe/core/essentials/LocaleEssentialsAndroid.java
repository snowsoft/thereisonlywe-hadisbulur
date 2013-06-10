package info.thereisonlywe.core.essentials;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocaleEssentialsAndroid {

	private static final String TAG = "info.thereisonlywe.core.essentials.LocaleEssentialsAndroid";

	private static Location lastLocation;

	private static long lastLocationUpdate;

	/**
	 * Only use this if you have registered listener
	 * 
	 * @return last location received
	 */
	public static Location getLocation()
	{
		return lastLocation;
	}

	public static LocationListener createLocationListener(final long minDistance)
	{
		return new LocationListener()
		{

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras)
			{

			}

			@Override
			public void onProviderEnabled(String provider)
			{

			}

			@Override
			public void onProviderDisabled(String provider)
			{

			}

			@Override
			public void onLocationChanged(Location location)
			{
				// if this is a gps location, we can use it
				if (location.getProvider().equals(LocationManager.GPS_PROVIDER))
				{
					doLocationUpdate(location, minDistance, true);
				}

				else doLocationUpdate(location, minDistance, false);
			}
		};
	}

	// usage: pass in value returned by createLocationListener
	public static void registerLocationListener(
			LocationListener locationListener, final long checkInterval,
			final long minDistance, Timer timer, final Context context)
	{

		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		for (String s : locationManager.getAllProviders())
		{
			locationManager.requestLocationUpdates(s, checkInterval,
					minDistance, locationListener);
		}

		timer.scheduleAtFixedRate(new TimerTask()
		{
			@Override
			public void run()
			{
				Location location = getLastLocation(checkInterval, context);
				doLocationUpdate(location, minDistance, false);
			}
		}, 0, checkInterval);
	}

	public static void deRegisterLocationListener(
			LocationListener locationListener, Timer timer, Context context)
	{
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		timer.cancel();
		locationManager.removeUpdates(locationListener);
	}

	private static void doLocationUpdate(Location l, long minDistance,
			boolean force)
	{
		Log.d(TAG, "update received:" + l);
		if (l == null)
		{
			Log.d(TAG, "Empty location");
			return;
		}
		if (lastLocation != null)
		{
			float distance = l.distanceTo(lastLocation);
			Log.d(TAG, "Distance to last: " + distance);

			if (l.distanceTo(lastLocation) < minDistance && !force)
			{
				Log.d(TAG, "Position didn't change");
				return;
			}

			if (l.getAccuracy() >= lastLocation.getAccuracy()
					&& l.distanceTo(lastLocation) < l.getAccuracy() && !force)
			{
				Log.d(TAG,
						"Accuracy got worse and we are still within the accuracy range.. Not updating");
				return;
			}
			if (l.getTime() <= lastLocationUpdate && !force)
			{
				Log.d(TAG, "Timestamp not never than last");
				return;
			}
		}
		lastLocation = l;
		lastLocationUpdate = System.currentTimeMillis();
	}

	public static Location getLastLocation(long interval, Context context)
	{
		Location gpslocation = getLocationByProvider(
				LocationManager.GPS_PROVIDER, context);
		Location networkLocation = getLocationByProvider(
				LocationManager.NETWORK_PROVIDER, context);

		// if we have only one location available, the choice is easy
		if (gpslocation == null)
		{
			Log.d(TAG, "No GPS Location available.");
			return networkLocation;
		}
		if (networkLocation == null)
		{
			Log.d(TAG, "No Network Location available");
			return gpslocation;
		}

		// a locationupdate is considered 'old' if its older than the configured
		// update interval. this means, we didn't get a
		// update from this provider since the last check
		long old = System.currentTimeMillis() - interval;
		boolean gpsIsOld = (gpslocation.getTime() < old);
		boolean networkIsOld = (networkLocation.getTime() < old);

		// gps is current and available, gps is better than network
		if (!gpsIsOld)
		{
			Log.d(TAG, "Returning current GPS Location");
			return gpslocation;
		}

		// gps is old, we can't trust it. use network location
		if (!networkIsOld)
		{
			Log.d(TAG, "GPS is old, Network is current, returning network");
			return networkLocation;
		}

		// both are old return the newer of those two
		if (gpslocation.getTime() > networkLocation.getTime())
		{
			Log.d(TAG, "Both are old, returning gps(newer)");
			return gpslocation;
		}
		else
		{
			Log.d(TAG, "Both are old, returning network(newer)");
			return networkLocation;
		}
	}

	/**
	 * get the last known location from a specific provider (network/gps)
	 */
	private static Location getLocationByProvider(String provider,
			Context context)
	{
		Location location = null;
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		List<String> providers = locationManager.getProviders(true);

		if (!providers.contains(provider)) { return null; }

		try
		{
			if (locationManager.isProviderEnabled(provider))
			{

				location = locationManager.getLastKnownLocation(provider);

			}
		}
		catch (IllegalArgumentException e)
		{
			Log.d(TAG, "Cannot acces Provider " + provider);
		}
		return location;
	}

	// You need to have a registered location listener first
	public static double getAltitudeForCurrentLocation()
	{
		return getAltitude(lastLocation.getLatitude(),
				lastLocation.getLongitude());
	}

	public static double getAltitude(double latitude, double longitude)
	{
		double result = Double.NaN;
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		String url = "http://maps.googleapis.com/maps/api/elevation/"
				+ "xml?locations=" + String.valueOf(latitude) + ","
				+ String.valueOf(longitude) + "&sensor=true";
		HttpGet httpGet = new HttpGet(url);
		try
		{
			HttpResponse response = httpClient.execute(httpGet, localContext);
			HttpEntity entity = response.getEntity();
			if (entity != null)
			{
				StringBuilder respStr = new StringBuilder();
				InputStream instream = entity.getContent();
				int r = -1;
				while ((r = instream.read()) != -1)
					respStr.append((char) r);
				String tagOpen = "<elevation>";
				String tagClose = "</elevation>";
				if (respStr.indexOf(tagOpen) != -1)
				{
					int start = respStr.indexOf(tagOpen) + tagOpen.length();
					int end = respStr.indexOf(tagClose);
					String value = respStr.substring(start, end);
					result = (Double.parseDouble(value));
				}
				instream.close();
			}
		}
		catch (ClientProtocolException e)
		{
			return result;
		}
		catch (IOException e)
		{
			return result;
		}

		return result;
	}

}
