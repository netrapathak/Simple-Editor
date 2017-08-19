// Packages imported
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CannotRedoException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

/**The Text_Editor class is a simple text editor.
It has various features like open, save text files;
editing features like: cut, copy, paste, undo, redo, replace, print.

Instruction: Text_Editor.java requires the following files:
 * Data File.txt
 * Guide File.txt
 * images/print.jpg

Also, 'if' the user wants to open the temporary file i.e. of the latest version saved by the program 
to avoid loss of any unsaved changes, in cases of sudden device shut down;
the user needs to open the file named "null_Temp" if the file has not be saved once also.
 */

public class Text_Editor extends JFrame
{
    // The following are fields for the menu system.
    // First, the menu bar
    private JMenuBar menuBar;

    // The menus
    private JMenu fileMenu;
    private JMenu fontMenu;
    private JMenu editMenu;

    // The menu items
    private JMenuItem newItem;
    private JMenuItem openItem;
    private JMenuItem saveItem;
    private JMenuItem saveAsItem;
    private JMenuItem printItem;
    private JMenuItem exitItem;
    private JMenuItem cutItem;
    private JMenuItem copyItem;
    private JMenuItem pasteItem;
    private JMenuItem undoItem;
    private JMenuItem redoItem;
    private JMenuItem replaceItem;

    // The radio button menu items
    private JRadioButtonMenuItem monoItem;
    private JRadioButtonMenuItem serifItem;
    private JRadioButtonMenuItem sansSerifItem;

    // The checkbox menu items
    private JCheckBoxMenuItem italicItem;
    private JCheckBoxMenuItem boldItem;

    private String filename;     // To hold the text file name
    private JTextArea editorText;// To display the text
    private JScrollPane scrollPane;
    private JPanel panel;// To display the replace panel
    private JTextField fromField = new JTextField(8);// To hold the text to be replaced
    private JTextField toField = new JTextField(8);//,To hold the replacement text
    private final int NUM_LINES = 30;  // Lines to display
    private final int NUM_CHARS = 50;  // Chars per line
    private String Text = "";//,To save the current text for Print
    String str[] = new String[200];
    String temp = "";
    int i, m = 0;
    private final java.util.List<String> words;
    private static enum Mode {
        INSERT, COMPLETION
    };

    private Mode mode = Mode.INSERT;
    UndoManager undoManager = new UndoManager();//,Object Creation of Java built-in class to implement Undo-Redo
    //This creates a javax.swing.undo.UndoManager; A class that keeps a Stack of UndoableEdits and lets us invoke them

    /**
    Constructor 
     */

    public Text_Editor()
    {
        // Set the title.
        setTitle("Text_Editor");

        // Specify Exit when the close button is clicked.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the text area.
        editorText = new JTextArea(NUM_LINES, NUM_CHARS);

        // Turn line wrapping and word wrapping on.
        editorText.setLineWrap(true);
        editorText.setWrapStyleWord(true);

        // Set Undo-Redo limit upto 10
        undoManager.setLimit(10);

        /*Registering the UndoManager class object as a Listener on the TextArea.Document.
        The Document will create the UndoableEdit objects and send them to the UndoManager.*/
        editorText.getDocument().addUndoableEditListener(new UndoableEditListener() 
            {
                public void undoableEditHappened(UndoableEditEvent evt) {
                    undoManager.addEdit(evt.getEdit());
                }
            });

        // Create a scroll pane and add the text area to it.
        scrollPane=new JScrollPane(editorText);
        // Add the scroll pane to the content pane.
        add(scrollPane, BorderLayout.CENTER);
        // Create a panel 
        panel = new JPanel();
        // Add panel to the content pane
        add(panel, BorderLayout.SOUTH);
        // Set the panel visibility false
        panel.setVisible(false);

        // Build the menu bar.
        buildMenuBar();

        // Add Document Listener to the document from the textArea data; for file reovery function
        editorText.getDocument().addDocumentListener(new TempFileListener());

        // Pack and display the window.
        pack();
        setVisible(true);
        String fileName = "Auto.txt";
        // Variable to hold the one line data

        try {
            // Create object of FileReader
            FileReader inputFile = new FileReader(fileName);

            // Instantiate the BufferedReader Class
            BufferedReader bufferReader = new BufferedReader(inputFile);
            String line = "";
            // Read file line by line and print on the console
            while ((line = bufferReader.readLine()) != null) {

                temp += line; // + "\n";
            }
            // Close the buffer reader
            bufferReader.close();
        }

        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while reading file line by line:" + e.getMessage());
        }
        str = temp.split("[\\s]");

        words = new java.util.ArrayList<String>(str.length);
        for(String s : str)  {
            words.add(s);
        }
    }

    /**
    The buildMenuBar method creates a menu bar and calls all the Menu methods to create them
     */

    private void buildMenuBar()
    {
        // Build the file,font and edit menus.
        buildFileMenu();
        buildEditMenu();
        buildFontMenu();

        // Create the menu bar.
        menuBar = new JMenuBar();

        // Add the file, font and edit menus to the menu bar.
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(fontMenu);

        // Set the menu bar for this frame.
        setJMenuBar(menuBar);
    }

    /**
    The buildFileMenu method creates the file menu and populates it with its menu items.
     */

    private void buildFileMenu()
    {
        // Create the New menu item.
        newItem = new JMenuItem("New");
        // Accelerator that helps perform 'Control+key' shortcut functionality (Key capturing)
        newItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.Event.CTRL_MASK));
        // Add the functionality to be performed-action event to the menu item. Calling the object 
        newItem.addActionListener(new NewListener());

        // Create the Open menu item.
        openItem = new JMenuItem("Open");
        openItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.Event.CTRL_MASK));;
        openItem.addActionListener(new OpenListener());

        // Create the Save menu item.
        saveItem = new JMenuItem("Save");
        saveItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK));
        saveItem.addActionListener(new SaveListener());

        // Create the Save As menu item.
        saveAsItem = new JMenuItem("Save As");
        saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
        saveAsItem.addActionListener(new SaveListener());

        // Create the Print menu item.
        printItem = new JMenuItem("Print");
        printItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.Event.CTRL_MASK));
        printItem.addActionListener(new PrintListener());

        // Create the Exit menu item.
        exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic(KeyEvent.VK_X);
        exitItem.addActionListener(new ExitListener());

        // Create a menu for the items we just created.
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        // Add the items and some separator bars to the menu.
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.addSeparator();// Separator bar
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();// Separator bar
        fileMenu.add(printItem);
        fileMenu.addSeparator();// Separator bar
        fileMenu.add(exitItem);
    }

    /**
    The buildFontMenu method creates the font menu and populates it with its menu items.
     */

    private void buildFontMenu()
    {
        // Create the Monospaced menu item.
        monoItem = new JRadioButtonMenuItem("Monospaced");
        monoItem.addActionListener(new FontListener());

        // Create the Serif menu item.
        serifItem = new JRadioButtonMenuItem("Serif");
        serifItem.addActionListener(new FontListener());

        // Create the SansSerif menu item.
        sansSerifItem = new JRadioButtonMenuItem("SansSerif", true);
        sansSerifItem.addActionListener(new FontListener());

        // Group the radio button menu items.
        ButtonGroup group = new ButtonGroup();
        group.add(monoItem);
        group.add(serifItem);
        group.add(sansSerifItem);

        // Create the Italic menu item.
        italicItem = new JCheckBoxMenuItem("Italic");
        italicItem.addActionListener(new FontListener());

        // Create the Bold menu item.
        boldItem = new JCheckBoxMenuItem("Bold");
        boldItem.addActionListener(new FontListener());

        // Create a menu for the items we just created.
        fontMenu = new JMenu("Font");
        fontMenu.setMnemonic(KeyEvent.VK_T);

        // Add the items and some separator bars to the menu.
        fontMenu.add(monoItem);
        fontMenu.add(serifItem);
        fontMenu.add(sansSerifItem);
        fontMenu.addSeparator();// Separator bar
        fontMenu.add(italicItem);
        fontMenu.add(boldItem);
    }

    /**
    The buildEditMenu method creates the edit menu and populates it with its menu items.
     */

    private void buildEditMenu()
    {
        // Create the Cut menu item.
        cutItem = new JMenuItem("Cut");
        cutItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.Event.CTRL_MASK));
        cutItem.addActionListener(new CutListener()); 

        // Create the Copy menu item.
        copyItem = new JMenuItem("Copy");
        copyItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.Event.CTRL_MASK));
        copyItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    editorText.copy();
                }
            });

        // Create the Paste menu item.
        pasteItem = new JMenuItem("Paste");
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.Event.CTRL_MASK));
        pasteItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    editorText.paste();
                }
            });

        // Create the Undo menu item.
        undoItem = new JMenuItem("Undo");
        undoItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.Event.CTRL_MASK));
        editorText.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
        undoItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (undoManager.canUndo()) 
                        undoManager.undo();
                    else{
                        warn("Can't Undo");
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
            });

        // Create the Redo menu item.
        redoItem = new JMenuItem("Redo");
        redoItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.Event.CTRL_MASK));
        editorText.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
        redoItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (undoManager.canRedo()) 
                        undoManager.redo();
                    else{
                        warn("Can't Redo");
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
            });

        // Create the Replace menu item.
        replaceItem = new JMenuItem("Replace");
        replaceItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.Event.CTRL_MASK));
        replaceItem.addActionListener(new ReplaceListener()); 

        // Create a menu for the items we just created.
        editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);

        // Add the items and some separator bars to the menu.
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.addSeparator();// Separator bar
        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.addSeparator();// Separator bar
        editMenu.add(replaceItem);
    }

    private void warn(String msg)// Method that creates a dialogue box to display warning message
    {
        JOptionPane.showMessageDialog(this, "Warning: " + msg, "Warning",JOptionPane.WARNING_MESSAGE);
    }

    /**
    Private inner class that handles the event that is generated when there is any change made in the document.
    This class createa a temporary file, helping to perform file recovery feature.
     */

    private class TempFileListener implements DocumentListener
    {
        public void changedUpdate(DocumentEvent ev){  }

        /**
        The removeUpdate method updates the temorary file when anything is removed from the document.
        The method returns true if the file was opened and written successfully, or false if an error occurred.
         */

        public void removeUpdate(DocumentEvent ev) 
        {
            boolean success;
            String Text = editorText.getText();;
            FileWriter fwriter;
            PrintWriter outputFile;

            try {
                // Store the textArea data in a file, so as to help facilitate Printing operation
                File file = new File(filename+"_Temp");
                BufferedWriter output = new BufferedWriter(new FileWriter(file));
                output.write(Text); // Write data in the file
                output.close();// Close the file

            } catch ( IOException ex ) {
                ex.printStackTrace();
            }

        }

        /**
        The insertUpdate method updates the temorary file when anything new is added to the document.
        The method returns true if the file was opened and written successfully, or false if an error occurred.
         */

        public void insertUpdate(DocumentEvent ev) 
        {
            boolean success;
            String Text="";
            FileWriter fwriter;
            PrintWriter outputFile;
            if (ev.getLength() != 1) {
                return;
            }
            int pos = ev.getOffset();
            String content = null;

            try {
                // Store the textArea data in a file, so as to help facilitate Printing operation
                File file = new File(filename+"_Temp");
                BufferedWriter output = new BufferedWriter(new FileWriter(file));
                content = editorText.getText(0, pos + 1);
                Text = editorText.getText();
                output.write(Text); // Write data in the file
                output.close();// Close the file

            } catch ( IOException ex ) {
                ex.printStackTrace();
            }
            catch (BadLocationException e)
            {
                e.printStackTrace();
            }
            int w;
            for (w = pos; w >= 0; w--) {
                if (!Character.isLetter(content.charAt(w))) {
                    break;
                }
            }
            if (pos - w < 2) {
                // Too few chars
                return;
            }

            String prefix = content.substring(w + 1).toLowerCase();

            Boolean suggestionFound = false;
            for(String suggestion : words)  {
                if(suggestion.contains(prefix) && suggestion.indexOf(prefix) == 0)  {
                    String completion = suggestion.substring(pos - w);
                    SwingUtilities.invokeLater(new CompletionTask(completion, pos + 1));
                    break;
                }
            }
            if(!suggestionFound)  {
                // Nothing found
                mode = Mode.INSERT;
            }
        }
        private class CompletionTask implements Runnable {
            String completion;
            int position;

            CompletionTask(String completion, int position) {
                this.completion = completion;
                this.position = position;
            }

            public void run() {
                editorText.insert(completion, position);
                editorText.setCaretPosition(position + completion.length());
                editorText.moveCaretPosition(position);
                mode = Mode.COMPLETION;
            }
        }

    }

    /**
    Private inner class that handles the event that is generated when the user selects Cut from the edit menu.
     */

    private class CutListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            editorText.cut();
        }
    }

    /**
    Private inner class that handles the event that is generated when the user selects Print from the file menu.
     */

    private class PrintListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String text = editorText.getText();
            try {
                // Store the textArea data in a file, so as to help facilitate Printing operation
                File file = new File("Data File.txt");
                BufferedWriter output = new BufferedWriter(new FileWriter(file));
                output.write(text); // Write data in the file
                output.close();// Close the file
            } catch ( IOException ex ) {
                ex.printStackTrace();
            }
            Print.run();// Calling the run method of Print class to execute document Printing
        }
    }

    /**
    Private inner class that handles the event that is generated when the user selects Replace from the edit menu.
     */

    private class ReplaceListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            panel.setVisible(true);// Set the visibility of replace panel true. It contains the textfields for the replacement word information
            JButton replaceButton = new JButton("Replace");// Create the Replace button
            panel.add(replaceButton);
            replaceButton.addActionListener(new ActionListener()// For the action event of replace button. Replacement of the string once.
                {
                    public void actionPerformed(ActionEvent evt) {
                        String from = fromField.getText();
                        int start = editorText.getText().indexOf(from);
                        if (start >= 0 && from.length() > 0)
                            editorText.replaceRange(toField.getText(), start, start + from.length());
                    }
                });
            // Adding the labels and textfields to the panel
            panel.add(fromField);
            panel.add(new JLabel("with"));
            panel.add(toField);
        }
    }

    /**
    Private inner class that handles the event that is generated when the user selects New from the file menu.
     */

    private class NewListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            editorText.setText("");
            filename = null;
        }
    }

    /**
    Private inner class that handles the event that is generated when the user selects Open from the file menu.
     */

    private class OpenListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            int chooserStatus;

            JFileChooser chooser = new JFileChooser();
            //chooser.setFileSelectionMode(chooser.FILES_ONLY);
            chooserStatus = chooser.showOpenDialog(null);
            if (chooserStatus == chooser.APPROVE_OPTION)
            {
                // Get a reference to the selected file.
                File selectedFile = chooser.getSelectedFile();

                // Get the path of the selected file.
                filename = selectedFile.getPath();

                // Open the file.
                if (!openFile(filename))
                {
                    JOptionPane.showMessageDialog(null,"Error reading " +filename, "Error",JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        /**
        The openFile method opens the file specified by filename and reads its contents into the text area. 
        The method returns true if the file was opened and read successfully, or false if an error occurred.
         */

        private boolean openFile(String filename)
        {
            boolean success;
            String inputLine;
            FileReader freader;
            BufferedReader inputFile;

            try
            {
                // Open the file.
                freader = new FileReader(filename);
                inputFile = new BufferedReader(freader);

                // Read the file contents into the editor.
                Text="";
                editorText.setText("");
                //inputLine = inputFile.readLine();
                while ((inputLine =inputFile.readLine()) != null)
                {
                    Text = Text + inputLine + "\n";
                    //inputLine = inputFile.readLine();
                    editorText.append(Text);
                }

                // Close the file.
                inputFile.close();  

                // Indicate that everything went OK.
                success = true;
            }
            catch (IOException e)
            {
                // to catch error
                success = false;
                //JOptionPane.showMessageDialog(this, "Warning: " + msg, "Warning",JOptionPane.WARNING_MESSAGE);
                //JOptionPane.showMessageDialog(this , e.getMessage() , "File Open Error",JOptionPane.ERROR_MESSAGE);
            }

            // Return our status.
            return success;
        }
    }

    /**
    Private inner class that handles the event that is generated when the user selects Save or Save As from the file menu.
     */

    private class SaveListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            int chooserStatus;

            // If the user selected Save As, or the contents of the editor have not been saved, use a file chooser to get the file name.
            // Otherwise, save the file under the current file name.

            if (e.getActionCommand() == "Save As" || filename == null)
            {
                JFileChooser chooser = new JFileChooser();
                chooserStatus = chooser.showSaveDialog(null);
                if (chooserStatus == JFileChooser.APPROVE_OPTION)
                {
                    // Get a reference to the selected file.
                    File selectedFile = chooser.getSelectedFile();

                    // Get the path of the selected file.
                    filename = selectedFile.getPath();
                }
            }

            // Save the file.
            if (!saveFile(filename))
            {
                JOptionPane.showMessageDialog(null,"Error saving " + filename, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        /**
        The saveFile method saves the contents of the text area to a file. 
        The method returns true if the file was saved successfully, or false if an error occurred.
         */

        private boolean saveFile(String filename)
        {
            boolean success;
            String Text;
            FileWriter fwriter;
            PrintWriter outputFile;

            try
            {
                // Open the file.
                fwriter = new FileWriter(filename);
                outputFile = new PrintWriter(fwriter);

                // Write the contents of the text area to the file.
                Text = editorText.getText();
                outputFile.print(Text);

                // Close the file.
                outputFile.close();

                // Indicate that everything went OK.
                success = true;
            }
            catch (IOException e)
            {
                // Something went wrong.
                success = false;
            }

            // Return our status.
            return success;
        }
    }

    /**
    Private inner class that handles the event that is generated when the user selects Exit from the file menu.
     */

    private class ExitListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            System.exit(0);
        }
    }

    /**
    Private inner class that handles the event that is generated when the user selects an item from the font menu.
     */

    private class FontListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            // Get the current font.
            Font textFont = editorText.getFont();

            // Retrieve the font name and size.
            String fontName = textFont.getName();
            int fontSize = textFont.getSize();

            // Start with plain style.
            int fontStyle = Font.PLAIN;

            // Determine which font is selected.
            if (monoItem.isSelected())
                fontName = "Monospaced";
            else if (serifItem.isSelected())
                fontName = "Serif";
            else if (sansSerifItem.isSelected())
                fontName = "SansSerif";

            // Determine whether italic is selected.
            if (italicItem.isSelected())
                fontStyle += Font.ITALIC;

            // Determine whether bold is selected.
            if (boldItem.isSelected())
                fontStyle += Font.BOLD;

            // Set the font as selected.
            editorText.setFont(new Font(fontName, fontStyle, fontSize));
        }
    }

    /**
     * Main method
     */

    public static void main(String[] args)
    {
        Text_Editor te = new Text_Editor();
    }
}