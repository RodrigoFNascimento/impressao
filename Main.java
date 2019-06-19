import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

class Printer {
    public String name;
    public Document currentDoc;
    public int currentPage;
    public Document previousDocs;
    public boolean isPrinting = false;
    public Printer next;

    public Printer() {}

    public Printer(String name) {
        this.name = name;
        next = null;
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
            previousDocs.append(currentDoc);
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

    /**
     * Appends a node to the end of the list
     * @param newPrinter Printer to be added
     */
    public void append(Printer newPrinter) {
        Printer temp = this.next;
        while (temp != null) {
            temp = temp.next;
        }
        temp = newPrinter;

    }
}

class Document {
    public String name;
    public int numPages;
    public Document next;

    public Document() {}

    public Document(String name, int numPages) {
        this.name = name;
        this.numPages = numPages;
        next = null;
    }

    /**
     * Appends a node to the end of the list
     * @param newDocument Document to be added
     */
    public void append(Document newDocument) {
        Document temp = next;
        while (temp != null) {
            temp = temp.next;
        }
        temp = newDocument;
    }

    /**
     * Returns the size of the Document list
     * @return Size of the list
     */
    public int size() {
        int i = 0;
        Document temp = this.next;
        while (temp != null) {
            temp = temp.next;
            i++;
        }
        return i;
    }

    /**
     * Gets a document by index
     * @param index Index of the Document
     * @return Document in index
     */
    public Document get(int index) {
        int i = 0;
        Document temp = this.next;
        while (temp != null) {
            if (index == i) {
                return temp;
            }
            temp = temp.next;
            i++;
        }
        return null;
    }

    public void remove(int index) {
        int i = 0;
        Document temp = this;
        while (temp != null) {
            if (index == i) {
                if (temp.next != null) {
                    temp.next = temp.next.next;
                }
            }
            temp = temp.next;
            i++;
        }
    }
}

public class Main {

    public static int numPrinters;
    public static Printer printers = new Printer();
    public static int numDocuments;
    public static Document documents = new Document();
    public static int numPagesPrinted = 0;
    public static Document previouslyPrintedDocs = new Document();

    private static Document addToEndOfList(Document head, Document currentNode, Document newNode) {
        
        if (currentNode.next == null) {
            currentNode.next = newNode;
        } else {
            addToEndOfList(head, currentNode.next, newNode);
        }
        return head;
    }

    private static Printer addToEndOfList(Printer head, Printer currentNode, Printer newNode) {
        
        if (currentNode.next == null) {
            currentNode.next = newNode;
        } else {
            addToEndOfList(head, currentNode.next, newNode);
        }
        return head;
    }

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
            for (int i = 0; i < numPrinters; i++) {
                line = reader.readLine();
                newPrinter = new Printer(line);
                printers = addToEndOfList(printers, printers, newPrinter);
            }

            // Reads the number of documents
            numDocuments = Integer.parseInt(reader.readLine());
            // Reads the names of the documents
            Document newDocument;
            for (int i = 0; i < numDocuments; i++) {
                line = reader.readLine();
                int indexOfFirstSpace = line.indexOf(" ");
                String name = line.substring(0, indexOfFirstSpace);
                int pages = Integer.parseInt(line.substring(++indexOfFirstSpace));
                newDocument = new Document(name, pages);
                documents = addToEndOfList(documents, documents, newDocument);
            }
        }
    }

    /**
     * Writes content to file
     * @param fileName Name of the file (with extension) to be writen
     * @param content Content to be writen on the file
     * @throws FileNotFoundException
     */
    private static void writeToFile(String fileName, StringBuilder content) throws FileNotFoundException {

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
            
            Printer printer = printers.next;
            while (printer != null) {

                // Prints page if there's any in the printer
                if (printer.isPrinting)
                    numPagesPrinted += printer.printPage();
                
                if (!printer.isPrinting) {

                    // Removes document from the printer if all pages
                    // have been printed
                    if (printer.currentDoc != null)
                        previouslyPrintedDocs = addToEndOfList(previouslyPrintedDocs, previouslyPrintedDocs, printer.removeDoc());

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

                printer = printer.next;
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

            // Printes the output to the terminal
            System.out.println(outputString);
            
            // Writes the output of the program to the output file
            //writeToFile(args[1], outputString);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}