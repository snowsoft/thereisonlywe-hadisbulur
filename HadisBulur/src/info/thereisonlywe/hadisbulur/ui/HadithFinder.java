package info.thereisonlywe.hadisbulur.ui;

import info.thereisonlywe.core.project.ProjectConstants;
import info.thereisonlywe.core.project.ProjectLocalization;
import info.thereisonlywe.core.search.SearchModifier;
import info.thereisonlywe.core.search.SearchOperator;
import info.thereisonlywe.core.search.SearchResult;
import info.thereisonlywe.core.search.SearchScope;
import info.thereisonlywe.core.search.engine.LiteSearchEngine;
import info.thereisonlywe.core.toolkit.GUIToolkit;
import info.thereisonlywe.core.toolkit.StringToolkit;
import info.thereisonlywe.core.toolkit.ThreadToolkit;
import info.thereisonlywe.core.ui.swing.extensions.table.TextAreaRenderer;
import info.thereisonlywe.core.ui.swing.extensions.textfield.DBTextField;
import info.thereisonlywe.hadisbulur.data.Hadith;
import info.thereisonlywe.hadisbulur.data.HadithCollection;
import info.thereisonlywe.hadisbulur.data.HadithSearchResult;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ToolTipManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumnModel;

public class HadithFinder extends DBTextField {

	private static final long serialVersionUID = 4183730681712497351L;

	private final JPopupMenu popupMenu_Input = new JPopupMenu();

	private final JPopupMenu popupMenu_Results = new JPopupMenu();

	private final Font font = new Font("Dialog", Font.BOLD, 13);

	public HadithFinder()
	{
		super();
		table.getColumnModel().getColumn(0)
				.setCellRenderer(new TableRenderer());
		setAlphabeticalSort(true);
		setSimilaritySort(true);
		setRefreshWaitTime(333);
		setScope(SearchScope.NO_ACCENTS_AND_DIACRITICS);
		setModifier(SearchModifier.STARTS_WITH);
		setCaseSensitive(false);
		table.setSelectionBackground(Color.LIGHT_GRAY);
		table.setSelectionForeground(Color.black);
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		ToolTipManager.sharedInstance().unregisterComponent(table);
		textField.setFont(font);
		table.setFont(font);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		createPopupMenus();
		attachListeners();
	}

	@Override
	public boolean hasFocus()
	{
		if (textField.hasFocus()) return true;
		else return false;
	}

	protected void addText(String s)
	{
		textField.setText(textField.getText() + s);
	}

	/**
	 * Sort by hadith strength. We base that on source length.
	 */
	@Override
	protected void alphabeticalSort()
	{
		Collections.sort(results, new Comparator<SearchResult>()
		{
			@Override
			public int compare(SearchResult o1, SearchResult o2)
			{
				return ((HadithSearchResult) o1).getHadith().getSource()
						.length() > ((HadithSearchResult) o2).getHadith()
						.getSource().length() ? -1 : 1;
			}
		});
	}

	private void attachListeners()
	{

		addFocusListener(new FocusAdapter()
		{

			@Override
			public void focusGained(FocusEvent e)
			{
				if (getResultCount() <= 0) textField.requestFocus();
			}
		});

		textField.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					refreshInputPopup();
					popupMenu_Input.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					refreshInputPopup();
					popupMenu_Input.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					popupMenu_Results.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					popupMenu_Results.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}

	private void createPopupMenus()
	{
		JMenuItem item;

		item = new JMenuItem(ProjectLocalization.getString(
				ProjectConstants.CORE_PROJECT, "Copy"));
		item.setAccelerator(KeyStroke.getKeyStroke("ctrl C"));
		item.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				GUIToolkit.setClipboardContents(HadithCollection.getHadith(
						((HadithSearchResult) getResult(getSelectedRows()[0]))
								.getHadithIndex()).toPrint());
			}
		});
		popupMenu_Results.add(item);

		item = new JMenuItem(ProjectLocalization.getString(
				ProjectConstants.CORE_PROJECT, "Paste"));
		item.setAccelerator(KeyStroke.getKeyStroke("ctrl V"));
		item.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String s = GUIToolkit.getClipboardContents();
				if (s != null && s.length() > 0)
				{
					addText(s);
				}
				refresh();
			}
		});
		popupMenu_Input.add(item);
		popupMenu_Input.addSeparator();

		item = new JMenuItem(ProjectLocalization.getString(
				ProjectConstants.CORE_PROJECT, "SelectAll"));
		item.setAccelerator(KeyStroke.getKeyStroke("ctrl A"));
		item.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				textField.selectAll();
			}
		});
		popupMenu_Input.add(item);
	}

	private void refreshInputPopup()
	{
		String s = GUIToolkit.getClipboardContents();
		if (s != null && s.length() > 0)
		{
			popupMenu_Input.getComponent(0).setEnabled(true);
		}
		else popupMenu_Input.getComponent(0).setEnabled(false);
		if (textField != null && !textField.getText().isEmpty()) popupMenu_Input
				.getComponent(1).setEnabled(true);
		else popupMenu_Input.getComponent(1).setEnabled(false);
	}

	public void deleteLast()
	{
		String s = textField.getText();
		if (s != null && s.length() > 0)
			textField.setText(new String(s.substring(0, s.length() - 1)));
	}

	@Override
	protected void showTable()
	{
		scrollPane.setVisible(true);
	}

	@Override
	protected void listenTable()
	{
		table.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					if (results.isEmpty()) return;
					else
					{
					}
				}
			}
		});
	}

	@Override
	protected void listenTextField()
	{
		textField.addKeyListener(new KeyListener()
		{

			@Override
			public void keyTyped(KeyEvent e)
			{
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER
						|| e.getKeyCode() == KeyEvent.VK_DOWN
						|| e.getKeyCode() == KeyEvent.VK_UP
						|| e.getKeyCode() == KeyEvent.VK_LEFT
						|| e.getKeyCode() == KeyEvent.VK_RIGHT
						|| e.getKeyCode() == KeyEvent.VK_SPACE)
				{ // do nothing
				}
				else if (textField.getText().length() > 1)
				{
					requestRefresh();
				}
			}

			@Override
			public void keyPressed(KeyEvent e)
			{
			}
		});
	}

	@Override
	protected ArrayList<SearchResult> getMatches(String text)
	{
		while (!HadithCollection.isInit())
		{
			ThreadToolkit.sleep(250);
		}

		ArrayList<SearchResult> result = new ArrayList<SearchResult>();

		String words[] = text.split(" ");
		ArrayList<String> tempList = new ArrayList<String>();
		ArrayList<String> wordList = new ArrayList<String>();
		for (int i = 0; i < words.length; i++)
		{
			if (words[i].equals(SearchOperator.OR.toString())
					|| words[i].equals("OR"))
			{
				String temp = StringToolkit.glue(tempList, " ");
				if (scope.equals(SearchScope.NO_ACCENTS_AND_DIACRITICS)) temp = StringToolkit
						.removeDiacritics(temp);
				else if (scope.equals(SearchScope.ASCII_ONLY))
					temp = StringToolkit.toASCII(temp);
				if (!caseSensitive) temp = temp.toLowerCase();
				wordList.add(temp);
				tempList.clear();
				if (i + 1 == words.length) // bad input
					return new ArrayList<SearchResult>();
			}
			else
			{
				tempList.add(words[i]);
			}
		}

		String temp = StringToolkit.glue(tempList, " ");
		if (scope.equals(SearchScope.NO_ACCENTS_AND_DIACRITICS)) temp = StringToolkit
				.removeDiacritics(temp);
		else if (scope.equals(SearchScope.ASCII_ONLY))
			temp = StringToolkit.toASCII(temp);
		if (!caseSensitive) temp = temp.toLowerCase();
		wordList.add(temp);
		tempList.clear();

		OUTER: for (int i = 0; i < HadithCollection.HADITH_COUNT; i++)
		{

			if (sigTerm) break;

			String content = HadithCollection.getHadith(i).getText();
			String s1 = content;

			if (s1 == null || s1.isEmpty()) continue;
			if (scope.equals(SearchScope.NO_ACCENTS_AND_DIACRITICS)) s1 = StringToolkit
					.removeDiacritics(s1);
			else if (scope.equals(SearchScope.ASCII_ONLY))
				s1 = StringToolkit.toASCII(s1);
			if (!caseSensitive) s1 = s1.toLowerCase();

			for (int j = 0; j < wordList.size(); j++)
			{
				String phrase = wordList.get(j);

				if (modifier.equals(SearchModifier.STARTS_WITH)
						&& LiteSearchEngine.startsWith(s1, phrase))
				{
					result.add(new HadithSearchResult(content, i, 2));
					continue OUTER;
				}
				else if (modifier.equals(SearchModifier.CONTAINS)
						&& LiteSearchEngine.contains(s1, phrase))
				{
					result.add(new HadithSearchResult(content, i, 2));
					continue OUTER;
				}
				else if (modifier.equals(SearchModifier.ENDS_WITH)
						&& LiteSearchEngine.endsWith(s1, phrase))
				{
					result.add(new HadithSearchResult(content, i, 2));
					continue OUTER;
				}
				else if (modifier.equals(SearchModifier.EXACT)
						&& LiteSearchEngine.exact(s1, phrase))
				{
					result.add(new HadithSearchResult(content, i, 2));
					continue OUTER;
				}

				String[] parts = phrase.split(" ");
				int found = 0;
				for (int l = 0; l < parts.length; l++)
				{
					if (modifier.equals(SearchModifier.STARTS_WITH)
							&& LiteSearchEngine.startsWith(s1, parts[l]))
					{
						found++;
					}

					else if (modifier.equals(SearchModifier.CONTAINS)
							&& LiteSearchEngine.contains(s1, parts[l]))
					{
						found++;
					}

					else if (modifier.equals(SearchModifier.ENDS_WITH)
							&& LiteSearchEngine.endsWith(s1, parts[l]))
					{
						found++;
					}

					else if (modifier.equals(SearchModifier.EXACT)
							&& LiteSearchEngine.exact(s1, parts[l]))
					{
						found++;
					}
				}
				if (found == parts.length)
				{
					result.add(new HadithSearchResult(content, i, 0));
					continue OUTER;
				}
			}
		}
		return result;
	}

	private class TableRenderer extends TextAreaRenderer {

		private static final long serialVersionUID = -6922420086464341228L;

		private final TitledBorder tb = BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "0",
				TitledBorder.LEFT, TitledBorder.TOP, font, Color.black);

		public TableRenderer()
		{
			super();
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object obj, boolean isSelected, boolean hasFocus, int row,
				int column)
		{
			adaptee.getTableCellRendererComponent(table, obj, isSelected,
					hasFocus, row, column);
			setForeground(adaptee.getForeground());
			setBackground(adaptee.getBackground());
			setFont(adaptee.getFont());
			if (!adaptee.getText().equals(getText()))
				setText(adaptee.getText());
			if (getResultCount() > 0)
			{
				Hadith h = HadithCollection
						.getHadith(((HadithSearchResult) results.get(row))
								.getHadithIndex());
				tb.setTitle(new String(String.valueOf((row + 1)) + ") {"
						+ h.getNarrator() + "} " + h.getSource()));
			}
			else
			{
				tb.setTitle(null);
			}
			setBorder(tb);
			TableColumnModel columnModel = table.getColumnModel();
			setSize(columnModel.getColumn(column).getWidth(), 100000);
			int height_wanted = (int) getPreferredSize().getHeight();
			addSize(table, row, column, height_wanted);
			height_wanted = findTotalMaximumRowSize(table, row);
			if (height_wanted != table.getRowHeight(row))
			{
				table.setRowHeight(row, height_wanted);
			}
			return this;
		}

		@Override
		public void invalidate()
		{
		}

		@Override
		public void repaint()
		{
		}
	}

}
