package us.akana.tools.maximoIds;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * All possible values of {@link Main#info}
 * @author Jaden
 */
enum InfoText {
	SELECT_PROMPT, ERROR, DONE, LOAD_SHEETS, CACHING_NUMS, WRITING_IDS, SAVING_SHEET
}

/**
 * Maximo ID tool - references reassessment maximo mapping file to write new maximo ids to inventory file using id numbers
 * 
 * See <a href="https://github.com/Jaden-Unruh/maximo-id-tool">the github</a> for more
 * @author Jaden
 */
public class Main {

	/**
	 * Formatter to pull a String from a cell in an XSSFSheet
	 */
	static final DataFormatter FORMATTER = new DataFormatter();
	
	/**
	 * Primary program window
	 */
	static JFrame options;
	/**
	 * Info panel at the bottom of the primary program window - content given by {@link InfoText}
	 */
	static JLabel info = new JLabel();
	
	/**
	 * Text currently in {@link Main#info}
	 */
	static InfoText infoText;
	
	/**
	 * The two files currently selected for the program - start out as null
	 */
	static File[] selectedFiles = new File[2];
	
	/**
	 * The program-readable copies of {@link Main#selectedFiles}, from the apache poi libraries
	 */
	static XSSFWorkbook inventoryBook, reassessmentBook;
	
	/**
	 * A mapping of the asset numbers, from the old 7 digit ids ('NEW' removed) to maximo ids (AB######)
	 */
	private static HashMap<String, String> assetNumMap = new HashMap<String, String>();

	/**
	 * Entry method, calls {@link Main#openWindow()}
	 * @param args unused
	 */
	public static void main(String[] args) {
		openWindow();
	}
	
	/**
	 * Defines, fills, and opens the primary program window
	 */
	private static void openWindow() {
		options = new JFrame(Messages.getString("Main.Window.Title")); //$NON-NLS-1$
		options.setSize(800, 700);
		options.setLayout(new GridBagLayout());
		options.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		options.add(new JLabel(Messages.getString("Main.Window.inventoryPrompt")), //$NON-NLS-1$
				new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, 0, new Insets(0, 0, 0, 0), 0, 0));

		JButton selectASPx = new SelectButton(0);
		options.add(selectASPx,
				new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, 0, new Insets(0, 0, 0, 0), 0, 0));

		options.add(new JLabel(Messages.getString("Main.Window.reassessmentPrompt")), //$NON-NLS-1$
				new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, 0, new Insets(0, 0, 0, 0), 0, 0));

		JButton selectCa = new SelectButton(1);
		options.add(selectCa,
				new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, 0, new Insets(0, 0, 0, 0), 0, 0));

		JButton cancel = new JButton(Messages.getString("Main.Window.Close")); //$NON-NLS-1$
		options.add(cancel,
				new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.CENTER, 0, new Insets(0, 0, 0, 0), 0, 0));

		final JButton run = new JButton(Messages.getString("Main.Window.Open")); //$NON-NLS-1$
		options.add(run,
				new GridBagConstraints(1, 4, 1, 1, 0, 0, GridBagConstraints.CENTER, 0, new Insets(0, 0, 0, 0), 0, 0));

		options.add(info,
				new GridBagConstraints(0, 5, 2, 1, 0, 0, GridBagConstraints.CENTER, 0, new Insets(0, 0, 0, 0), 0, 0));

		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (checkCorrectSelections()) {
					SwingWorker<Boolean, Void> sw = new SwingWorker<Boolean, Void>() {
						@Override
						protected Boolean doInBackground() throws Exception {
							openFiles();
							cacheAssetNums();
							writeNewIDs();
							saveSheet();
							updateInfo(InfoText.DONE);
							run.setEnabled(true);
							return true;
						}

						@Override
						protected void done() {
							try {
								get();
							} catch (InterruptedException e) {
								e.printStackTrace();
							} catch (ExecutionException e) {
								e.getCause().printStackTrace();
								String[] choices = { Messages.getString("Main.Window.Error.Close"), //$NON-NLS-1$
										Messages.getString("Main.Window.Error.More") }; //$NON-NLS-1$
								updateInfo(InfoText.ERROR);
								run.setEnabled(true);
								if (JOptionPane.showOptionDialog(options,
										String.format(Messages.getString("Main.Window.Error.ProblemLabel"), //$NON-NLS-1$
												e.getCause().toString()),
										Messages.getString("Main.Window.Error.Error"), JOptionPane.DEFAULT_OPTION, //$NON-NLS-1$
										JOptionPane.ERROR_MESSAGE, null, choices, choices[0]) == 1) {
									StringWriter sw = new StringWriter();
									e.printStackTrace(new PrintWriter(sw));
									JTextArea jta = new JTextArea(25, 50);
									jta.setText(String.format(Messages.getString("Main.Window.Error.FullTrace"), //$NON-NLS-1$
											sw.toString()));
									jta.setEditable(false);
									JOptionPane.showMessageDialog(options, new JScrollPane(jta),
											Messages.getString("Main.Window.Error.Error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
								}
							}
						}
					};
					run.setEnabled(false);
					sw.execute();
				} else
					updateInfo(InfoText.SELECT_PROMPT);
			}
		});

		options.pack();
		options.setVisible(true);
	}
	
	/**
	 * 'Opens' the {@link Main#selectedFiles} into memory-readable files {@link Main#inventoryBook} and {@link Main#reassessmentBook}
	 * @throws FileNotFoundException if either file is not selected
	 * @throws IOException if there's an error reading from the files
	 */
	private static void openFiles() throws FileNotFoundException, IOException {
		updateInfo(InfoText.LOAD_SHEETS);
		inventoryBook = new XSSFWorkbook(new FileInputStream(selectedFiles[0]));
		reassessmentBook = new XSSFWorkbook(new FileInputStream(selectedFiles[1]));
	}
	
	/**
	 * Fills {@link Main#assetNumMap} from {@link Main#reassessmentBook}
	 */
	private static void cacheAssetNums() {
		updateInfo(InfoText.CACHING_NUMS);
		XSSFSheet assetsSheet = reassessmentBook.getSheet("Assets");
		if (assetsSheet == null)
			throw new IllegalArgumentException(Messages.getString("Main.Error.NoAssets")); //$NON-NLS-1$
		int rows = assetsSheet.getPhysicalNumberOfRows();
		for (int i = 1; i < rows; i++) {
			XSSFRow activeRow = assetsSheet.getRow(i);
			String col1 = FORMATTER.formatCellValue(activeRow.getCell(1));
			if (Pattern.matches("\\d{7}NEW", col1)) //$NON-NLS-1$
				assetNumMap.put(col1.substring(0, 7), FORMATTER.formatCellValue(activeRow.getCell(2)));
			else
				writeComment(reassessmentBook, assetsSheet, i, 1, Messages.getString("Main.Comment.TempAssetFormat")); //$NON-NLS-1$
		}
	}
	
	/**
	 * Writes column 20 of {@link Main#inventoryBook} by referencing column 19 and {@link Main#assetNumMap}
	 */
	private static void writeNewIDs() {
		updateInfo(InfoText.WRITING_IDS);
		XSSFSheet inventorySheet = inventoryBook.getSheetAt(0);
		int rows = inventorySheet.getPhysicalNumberOfRows();
		for (int i = 1; i < rows; i++) {
			XSSFRow activeRow = inventorySheet.getRow(i);
			String newId = assetNumMap.get(FORMATTER.formatCellValue(activeRow.getCell(18)));
			if (newId == null)
				writeComment(inventoryBook, inventorySheet, i, 18, Messages.getString("Main.Comment.IDNotFound")); //$NON-NLS-1$
			else
				activeRow.getCell(19).setCellValue(newId);
		}
	}
	
	/**
	 * Saves {@link Main#inventoryBook} to {@link Main#selectedFiles}[0]
	 * @throws IOException if there's an error writing
	 */
	private static void saveSheet() throws IOException {
		updateInfo(InfoText.SAVING_SHEET);
		FileOutputStream out = new FileOutputStream(selectedFiles[0]);
		inventoryBook.write(out);
		out.close();
		inventoryBook.close();
	}
	
	/**
	 * Checks if the {@link Main#selectedFiles} are not null and are .xlsx files
	 * 
	 * @return true if both files are .xlsx
	 */
	static boolean checkCorrectSelections() {
		try {
			return isXLSX(selectedFiles[0]) && isXLSX(selectedFiles[1]);
		} catch (NullPointerException e) {
			return false;
		}
	}
	
	/**
	 * Checks if the given file is of type XLSX (a microsoft excel workbook)
	 * 
	 * @param file the file to check
	 * @return true if the file is .xlsx
	 * @throws NullPointerException if the File is null
	 */
	static boolean isXLSX(File file) throws NullPointerException {
		return file.getName().toLowerCase().endsWith(".xlsx"); //$NON-NLS-1$
	}
	
	/**
	 * Updates the text of {@link Main#info}
	 * 
	 * @param text the new text
	 */
	static void updateInfo(InfoText text) {
		infoText = text;
		info.setText(getInfoText());
		options.pack();
	}
	
	/**
	 * The String associated with {@link Main#infoText}
	 * 
	 * @return the String currently showing in {@link Main#info}
	 * @see Main#infoText
	 */
	static String getInfoText() {
		switch (infoText) {
		case SELECT_PROMPT:
			return Messages.getString("Main.Info.SelectPrompt"); //$NON-NLS-1$
		case ERROR:
			return Messages.getString("Main.Info.Error"); //$NON-NLS-1$
		case DONE:
			return Messages.getString("Main.Info.Done"); //$NON-NLS-1$
		case LOAD_SHEETS:
			return Messages.getString("Main.Info.LoadSheets"); //$NON-NLS-1$
		case CACHING_NUMS:
			return Messages.getString("Main.Info.CachingNums"); //$NON-NLS-1$
		case WRITING_IDS:
			return Messages.getString("Main.Info.WritingIds"); //$NON-NLS-1$
		case SAVING_SHEET:
			return Messages.getString("Main.Info.SavingSheet"); //$NON-NLS-1$
		}
		return null;
	}
	
	/**
	 * Writes a comment to the given sheet
	 * 
	 * @param book    the parent book of the sheet to write to
	 * @param sheet   the sheet to write the comment to
	 * @param row     the row of the cell to write the comment to
	 * @param col     the column of the cell to write the comment to
	 * @param message the desired contents of the commment
	 */
	static void writeComment(XSSFWorkbook book, XSSFSheet sheet, int row, int col, String message) {
		CreationHelper factory = book.getCreationHelper();
		ClientAnchor anchor = factory.createClientAnchor();
		anchor.setCol1(col + 1);
		anchor.setCol2(col + 3);
		anchor.setRow1(row + 1);
		anchor.setRow2(row + 3);
		XSSFDrawing drawing = sheet.createDrawingPatriarch();
		Comment comment = drawing.createCellComment(anchor);
		comment.setString(factory.createRichTextString(message));
		comment.setAuthor(Messages.getString("Main.Comment.Author")); //$NON-NLS-1$
		sheet.getRow(row).getCell(col).setCellComment(comment);
	}

}
