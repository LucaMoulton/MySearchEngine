package com.example.searchfiles;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class SearchFiles2 
{

	//declaring the functions
    private static JFrame frame;
    private static JTextField searchTermField;
    private static JTextArea resultArea;
    private static JButton fileChooserButton;
    private static JFileChooser fileChooser;
    private static java.util.List<File> selectedFiles;

    public static void main(String[] args)  
    {
    	
    	//This runs the GUI creation in the event dispatch thread
    	
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI()  
    {
    	//This creates the window frame for display
    	
        frame = new JFrame("file search engine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);

        
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        
        //This creates the input panel with the text field and buttons
        
        JPanel inputPanel = new JPanel();
        searchTermField = new JTextField(30);
        JButton searchButton = new JButton("search");
        searchButton.addActionListener(e -> performSearch());

        
        fileChooserButton = new JButton("file selector");
        fileChooserButton.addActionListener(e -> chooseFiles());
        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        
        
        inputPanel.add(new JLabel("search term/phrase:"));
        inputPanel.add(searchTermField);
        inputPanel.add(fileChooserButton);
        inputPanel.add(searchButton);

        
        //creates the text are for results to be displayed
        
        resultArea = new JTextArea();
        resultArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(resultArea);

        contentPane.add(inputPanel, BorderLayout.NORTH);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    //this is for choosing the files to search
    
    private static void chooseFiles() 
    {
        int returnValue = fileChooser.showOpenDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) 
        {
            selectedFiles = Arrays.asList(fileChooser.getSelectedFiles());
        }
    }

    //This is where the search itself is performed in the selected files
    
    private static void performSearch() 
    {
        String searchTerm = searchTermField.getText();
        if (searchTerm.isEmpty()) 
        {
            JOptionPane.showMessageDialog(frame, "Please enter a search term.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selectedFiles == null || selectedFiles.isEmpty()) 
        {
            JOptionPane.showMessageDialog(frame, "Please select files to search.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Map<String, Integer> resultMap = new TreeMap<>();
            for (File file : selectedFiles) 
            {
                int count = searchInFile(file.getPath(), searchTerm);
                resultMap.put(file.getName(), count);
            }
            displayResults(resultMap);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error reading files.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //This is where the method of searching for a term in a single file is done
    //On line 115 and 116 this is where the wild card system is used to catch them
    
    private static int searchInFile(String filePath, String searchTerm) throws IOException 
    {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        String wildcardRegex = searchTerm.replaceAll("\\*", ".*");
        Pattern pattern = Pattern.compile(wildcardRegex, Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(content);
        int count = 0;
        while (matcher.find()) 
        {
            count++;
        }
        return count;
    }

    //this is where we display the search results
    //Line 132 is where the result map is sorted in descending order by value or number of occourences
    private static void displayResults(Map<String, Integer> resultMap) 
    {
        StringBuilder sb = new StringBuilder();
        resultMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n"));

        // This sets the sorted results text to the resultArea and JTextArea
        resultArea.setText(sb.toString());
    }
}

