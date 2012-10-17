package info.thereisonlywe.hadisbulur.data;

/**
 * 
 * @author thereisonlywe
 */
public class Hadith {

	private final String narrator;
	private final String source;
	private final String text;

	@SuppressWarnings("unused")
	private Hadith()
	{
		narrator = null;
		source = null;
		text = null;
	}

	protected Hadith(String text, String narrator, String source)
	{
		this.narrator = narrator;
		this.source = source;
		this.text = text;
	}

	public String getNarrator()
	{
		return narrator;
	}

	public String getSource()
	{
		return source;
	}

	public String getText()
	{
		return text;
	}

	public String toPrint()
	{
		return "Ravi" + ": " + narrator + "\n\n" + text + "\n\n" + "Kaynak"
				+ ": " + source + "\n\n";
	}

	@Override
	public String toString()
	{
		return text + "|" + narrator + "|" + source;
	}

}
