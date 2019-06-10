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
    public List<Document> previousDocs = new ArrayList<>();
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

            if (currentPage <= 0)
                isPrinting = false;
        }
        return printedPages;
    }

    /**
     * Removes current document from printer
     * @return Removed document
     */
    public Document removeDoc() {
        if (currentDoc != null) {
            previousDocs.add(currentDoc);
            Document removedDoc = currentDoc;
            currentDoc = null;
            return removedDoc;
        } else {
            return null;
        }
    }

    /**
     * Adds document to printer
     */
    public StringBuilder addDoc(Document doc) {
        currentDoc = doc;
        isPrinting = true;
        currentPage = doc.numPages;

        // Prints the newly added and previously printed documents
        StringBuilder outputString = new StringBuilder();
        outputString.append("[" + name + "] " + currentDoc.name + "-" + currentDoc.numPages + "p");
        for (int i = previousDocs.size()-1; i >= 0; i--) {
            outputString.append(", " + previousDocs.get(i).name + "-" + previousDocs.get(i).numPages + "p");
        }
        outputString.append("\n");
        return outputString;
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
    public static List<Printer> printers = new ArrayList<>();
    public static int numDocuments;
    public static List<Document> documents = new ArrayList<>();
    public static int numPagesPrinted = 0;
    public static List<Document> previouslyPrintedDocs = new ArrayList<>();

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
            out.print(content);
        }
    }

    /**
     * Prints all documents in the queue
     */
    private static StringBuilder runPrinters() {
        StringBuilder output = new StringBuilder();
        boolean isAnyPrinterRunning = false;
        while (documents.size() > 0 || isAnyPrinterRunning) {
            isAnyPrinterRunning = false;
            for (Printer printer : printers) {

                // Prints page if there's any in the printer
                if (printer.isPrinting)
                    numPagesPrinted += printer.printPage();
                
                if (!printer.isPrinting) {

                    // Removes document from the printer if all pages
                    // have been printed
                    if (printer.currentDoc != null)
                        previouslyPrintedDocs.add(printer.removeDoc());

                    // If there's any document in the queue,
                    // transfers it to the printer
                    if (documents.size() > 0) {
                        output.append(printer.addDoc(documents.get(0)));
                        documents.remove(0);
                    }
                }

                // Repeats the loop while there are documents being printed
                if (printer.isPrinting)
                    isAnyPrinterRunning = printer.isPrinting;
            }
        }
        return output;
    }

    /**
     * Prints all the previously printed documents
     */
    private static StringBuilder getPreviouslyPrintedDocs() {
        StringBuilder output = new StringBuilder();
        for (int i = previouslyPrintedDocs.size() - 1; i >= 0; i--) {
            if (i > 0)
                output.append(previouslyPrintedDocs.get(i).name + "-" + previouslyPrintedDocs.get(i).numPages + "p\n");
            else
                output.append(previouslyPrintedDocs.get(i).name + "-" + previouslyPrintedDocs.get(i).numPages + "p");
        }
        return output;
    }
    
    public static void main(String[] args) {
        try {
            StringBuilder outputString = new StringBuilder();

            // Reads the content from the input file
            readFile(args[0]);

            // Runs the printers
            outputString.append(runPrinters());

            // Prints the total number of pages printed
            outputString.append(numPagesPrinted + "p\n");

            // Prints the stack of finished documents
            outputString.append(getPreviouslyPrintedDocs());

            // Writes the output of the program to the output file
            writeToFile(args[1], outputString.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}