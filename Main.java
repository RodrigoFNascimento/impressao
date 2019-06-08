import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Printer {
    public String name;
    public Document currentDoc;
    public int currentPage;
    public List<Document> previousDocs;
    public boolean isPrinting = false;

    public Printer(String name) {
        this.name = name;
    }

    /**
     * Prints one page of the current document.
     * If the printed page was the last, makes the printer avaiable
     * and adds the finished document to the pile of
     * printed documents.
     * @return Number of pages printed
     */
    public int printPage() {
        int printedPages = 0;
        if (isPrinting && currentPage > 0) {

            currentPage--;
            printedPages++;

            if (currentPage <= 0) {
                isPrinting = false;
                if (previousDocs == null) previousDocs = new ArrayList<>();
                previousDocs.add(currentDoc);
            }
        }
        return printedPages;
    }

    /**
     * Adds document to printer
     */
    public void addDoc(Document doc) {
        currentDoc = doc;
        isPrinting = true;
        currentPage = doc.numPages;
    }
}

class Document {
    public String name;
    public int numPages;

    public Document(String name, int numPages) {
        this.name = name;
        this.numPages = numPages;
    }
}

public class Main {

    public static int numPrinters;
    public static List<Printer> printers;
    public static int numDocuments;
    public static List<Document> documents;
    public static int numPagesPrinted = 0;

    /**
     * Reads file and returns it's content
     * @param file File to be read
     * @return The content of the file
     * @throws IOException
     * @throws FileNotFoundException
     */
    private static void readFile(String file) throws IOException, FileNotFoundException {

        // Opens the file
        try (FileInputStream inputStream = new FileInputStream(file)) {
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            // Reads the number of printers
            numPrinters = Integer.parseInt(reader.readLine());
            // Reads the names of the printers
            Printer newPrinter;
            printers = new ArrayList<>();
            for (int i = 0; i < numPrinters; i++) {
                line = reader.readLine();
                newPrinter = new Printer(line);
                printers.add(newPrinter);
            }

            // Reads the number of documents
            numDocuments = Integer.parseInt(reader.readLine());
            // Reads the names of the documents
            Document newDocument;
            documents = new ArrayList<>();
            String regex = "^(\\w+)\\s(\\d+)";
            Pattern p = Pattern.compile(regex);
            Matcher m;
            for (int i = 0; i < numDocuments; i++) {
                line = reader.readLine();
                m = p.matcher(line);
                m.matches();
                newDocument = new Document(m.group(1), Integer.parseInt(m.group(2)));
                documents.add(newDocument);
            }
        }
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

    /**
     * If the are queued documents, sends the first one to the
     * first avaiable printer
     */
    private static void fillPrinters() {

        for (Printer printer : printers) {

            if (documents.size() == 0) break;
                    
            if (!printer.isPrinting) {
                printer.addDoc(documents.get(0));
                documents.remove(0);
            }
        }
    }

    /**
     * Prints one page on every printer currently printing a document
     */
    private static boolean printOnePageEveryPrinter() {
        boolean isAnyPrinterRunning = false;
        for (Printer printer : printers) {
            numPagesPrinted += printer.printPage();
            if (printer.isPrinting)
                isAnyPrinterRunning = true;
        }
        return isAnyPrinterRunning;
    }
    
    public static void main(String[] args) {
        
        try {
            // Checks if any file was passed
            if (args.length == 0) {
                throw new RuntimeException("You forgot the file");
            }

            // Reads content
            readFile(args[0]);

            // Prints all the documents
            boolean isAnyPrinterRunning = true;
            while (documents.size() > 0 || isAnyPrinterRunning) {

                fillPrinters();
                isAnyPrinterRunning = printOnePageEveryPrinter();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    
}