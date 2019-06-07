import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Main {

    /**
     * Reads file and returns it's content
     * @param file File to be read
     * @return The content of the file
     * @throws IOException
     * @throws FileNotFoundException
     */
    private static String readFile(String file) throws IOException, FileNotFoundException {

        StringBuilder content = new StringBuilder();

        // Opens the file
        try (FileInputStream inputStream = new FileInputStream(file)) {
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Reads the file line by line
            String line;
            while ((line = reader.readLine()) != null)   {
                // Adds the line to the rest of the content
                content.append(line);
            }            
        }

        // Returns all the file's content
        return content.toString();
    }

    /**
     * Writes content to file
     * @param fileName Name of the file (with extension) to be writen
     * @param content Content to be writen on the file
     * @throws FileNotFoundException
     */
    private static void writeToFile(String fileName, String content) throws FileNotFoundException {

        // Opens file
        try (PrintWriter out = new PrintWriter(fileName)) {
            // Writes content to file
            out.println(content);
        }
    }
    
    public static void main(String[] args) {
        
        try {
            // Checks if any file was passed
            if (args.length  < 2) {
                throw new RuntimeException("You forgot the file");
            }

            // Reads content
            String content = readFile(args[0]);
            // Writes content
            writeToFile(args[1], content);

        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
        }
        
    }
    
}