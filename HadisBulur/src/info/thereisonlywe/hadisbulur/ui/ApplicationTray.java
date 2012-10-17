package info.thereisonlywe.hadisbulur.ui;

import info.thereisonlywe.core.logging.LiteLogger;
import info.thereisonlywe.core.toolkit.PrimitiveToolkit;

import java.awt.AWTException;
import java.awt.Frame;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

public class ApplicationTray {

	private static TrayIcon trayIcon = null;
	private static SystemTray systemTray = null;

	public static void init()
	{
		if (SystemTray.isSupported())
		{
			if (trayIcon != null || systemTray != null) return; // already init
			systemTray = SystemTray.getSystemTray();
			if (systemTray.getTrayIcons().length > 0)
				systemTray.remove(systemTray.getTrayIcons()[0]);
			int[] sizes = new int[] { 16, 24, 32, 48, 64, 96, 128 };
			int index = PrimitiveToolkit.getClosestToRadix(sizes,
					systemTray.getTrayIconSize().height);
			trayIcon = new TrayIcon(
					ApplicationFrame.getIconImages().get(index),
					"Hadis Bulur\nhttp://www.thereisonlywe.info", null);
			ActionListener actionListener = new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					// popupMenu_Tray.setVisible(false);
					ApplicationFrame.setVisible(true);
					ApplicationFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
					ApplicationFrame.requestFocus();
				}
			};
			trayIcon.setImageAutoSize(true);
			trayIcon.addActionListener(actionListener);
			try
			{
				systemTray.add(trayIcon);
			}
			catch (AWTException e)
			{
				LiteLogger.log(Level.SEVERE, e.getMessage());
			}
		}
		else
		{
			trayIcon = null;
			systemTray = null;
		}
	}

	public static SystemTray getSystemTray()
	{
		return systemTray;
	}

	public static TrayIcon getTrayIcon()
	{
		return trayIcon;
	}

}
