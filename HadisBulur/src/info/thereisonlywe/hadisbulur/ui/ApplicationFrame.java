package info.thereisonlywe.hadisbulur.ui;

import info.thereisonlywe.core.project.ProjectConstants;
import info.thereisonlywe.core.project.ProjectLocalization;
import info.thereisonlywe.core.project.ProjectSettings;
import info.thereisonlywe.core.toolkit.GUIToolkit;
import info.thereisonlywe.hadisbulur.data.HadithCollection;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.SystemTray;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class ApplicationFrame {

	private static JFrame frame;

	private static HadithFinder hf;

	private static boolean fullScreen = false;

	public static void main(String[] args)
	{
		Runnable r = new Runnable()
		{
			@Override
			public void run()
			{
				HadithCollection.init();
			}

		};
		new Thread(r).start();

		ProjectSettings.setVersion("1.0");
		System.setProperty("awt.useSystemAAFontSettings", "lcd");
		ProjectSettings.setLocalizationCache(true);
		ProjectSettings.setLocale(new Locale("tr"));
		ProjectLocalization
				.registerProjectForLocalization(ProjectConstants.CORE_PROJECT);
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					ApplicationFrame.init();
					frame.setVisible(true);
					frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
					if (SystemTray.isSupported())
					{
						ApplicationTray.init();
					}
					hf.requestFocus();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	public static ArrayList<BufferedImage> getIconImages()
	{
		ArrayList<BufferedImage> images = new ArrayList<>();
		String path = "/info/thereisonlywe/hadisbulur/resources/";
		try
		{
			images.add(ImageIO.read(ApplicationFrame.class.getResource(path
					+ "Icon16.png")));
			images.add(ImageIO.read(ApplicationFrame.class.getResource(path
					+ "Icon24.png")));
			images.add(ImageIO.read(ApplicationFrame.class.getResource(path
					+ "Icon32.png")));
			images.add(ImageIO.read(ApplicationFrame.class.getResource(path
					+ "Icon48.png")));
			images.add(ImageIO.read(ApplicationFrame.class.getResource(path
					+ "Icon64.png")));
			images.add(ImageIO.read(ApplicationFrame.class.getResource(path
					+ "Icon96.png")));
			images.add(ImageIO.read(ApplicationFrame.class.getResource(path
					+ "Icon128.png")));
		}
		catch (IOException e)
		{
		}
		return images;
	}

	public static void setVisible(boolean val)
	{
		frame.setVisible(val);
	}

	public static void requestFocus()
	{
		frame.requestFocus();
	}

	public static void setExtendedState(int state)
	{
		frame.setExtendedState(state);
	}

	public static void toggleFullScreen()
	{
		if (fullScreen)
		{
			frame.setVisible(false);
			frame.dispose();
			frame.setUndecorated(false);
			frame.setVisible(true);
			fullScreen = !fullScreen;
		}
		else
		{
			frame.setVisible(false);
			frame.dispose();
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			frame.setUndecorated(true);
			frame.setVisible(true);
			fullScreen = !fullScreen;
		}
	}

	private static void init()
	{
		frame = new JFrame();
		frame.setIconImages(getIconImages());
		frame.setTitle("Hadis Bulur");
		frame.setSize(new Dimension(480, 320));
		frame.setPreferredSize(new Dimension(480, 320));
		frame.setLocation(GUIToolkit.getCenteredWindowPosition(480, 320));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		KeyboardFocusManager manager = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new KeyDispatcher());
		hf = new HadithFinder();
		frame.add(hf);

		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
			}

			@Override
			public void windowIconified(WindowEvent e)
			{
				if (SystemTray.isSupported())
				{
					frame.setVisible(false);
				}
			}

			@Override
			public void windowDeiconified(WindowEvent e)
			{
				if (SystemTray.isSupported())
				{
					frame.setVisible(true);
					setExtendedState(Frame.MAXIMIZED_BOTH);
					requestFocus();
				}
			}

		});

		frame.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				hf.requestFocus();
			}
		});

		frame.addComponentListener(new ComponentAdapter()
		{

			@Override
			public void componentShown(ComponentEvent e)
			{
				hf.requestFocus();

			}

			@Override
			public void componentResized(ComponentEvent e)
			{
				hf.requestFocus();

			}

			@Override
			public void componentMoved(ComponentEvent e)
			{
				hf.requestFocus();

			}

		});

		frame.addWindowFocusListener(new WindowFocusListener()
		{

			@Override
			public void windowLostFocus(WindowEvent e)
			{

			}

			@Override
			public void windowGainedFocus(WindowEvent e)
			{
				hf.requestFocus();

			}
		});
	}

	private static class KeyDispatcher implements KeyEventDispatcher {

		@Override
		public boolean dispatchKeyEvent(final KeyEvent e)
		{
			if (e.getID() == KeyEvent.KEY_PRESSED)
			{
				char c = e.getKeyChar();
				if ((Character.isLetter(c) || Character.isDigit(c))
						&& !hf.hasFocus()) hf.addText(String.valueOf(c));
			}
			else if (e.getID() == KeyEvent.KEY_RELEASED)
			{
				if (e.getKeyCode() == KeyEvent.VK_F11)
				{
					toggleFullScreen();
				}
			}
			return false;
		}
	}

}
