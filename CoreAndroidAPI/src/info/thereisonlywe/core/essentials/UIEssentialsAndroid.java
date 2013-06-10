package info.thereisonlywe.core.essentials;

import android.os.Looper;

public class UIEssentialsAndroid {

	public static boolean isOnUIThread()
	{
		return Looper.myLooper() == Looper.getMainLooper();
	}

}
