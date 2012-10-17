package info.thereisonlywe.hadisbulur.data;

import info.thereisonlywe.core.io.PackageIO;
import info.thereisonlywe.core.toolkit.StringToolkit;

/**
 * 
 * @author thereisonlywe
 */
public class HadithCollection {

	private static Hadith[] hadiths;
	public static final int HADITH_COUNT = 5972;
	private static boolean isInit = false;

	public static void init()
	{
		String path = "/info/thereisonlywe/hadisbulur/resources/Hadiths.txt";
		String read[] = StringToolkit.splitLines(PackageIO.read(
				HadithCollection.class, path));
		hadiths = new Hadith[HADITH_COUNT];
		for (int i = 0; i < read.length; i++)
		{
			hadiths[i] = parseHadith(read[i]);

		}
		isInit = true;
	}

	public static boolean isInit()
	{
		return isInit;
	}

	private static Hadith parseHadith(String info)
	{
		String text, source, narrator;
		String tmp[] = StringToolkit.splitStatement(info, '|');
		text = tmp[0];
		tmp = StringToolkit.splitStatement(tmp[1], '|');
		narrator = tmp[0];
		source = tmp[1];
		return new Hadith(text, narrator, source);
	}

	public static Hadith getHadith(int no)
	{
		return hadiths[no];
	}

}
