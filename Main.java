import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

class ListUtil {
    /**
     * Adds a new node to the end of the list.
     * @param head Head of the list
     * @param newNode Node to be added to the list
     */
    public static void append(Document head, Document newNode) {
        Document temp = head;
        while (temp.next != null) {
            temp = temp.next;
        }
        temp.next = newNode;
    }

    public static void append(Printer head, Printer newNode) {
        while (head.next != null) {
            head = head.next;
        }
        head.next = newNode;
    }

    /**
     * Removes from the list the node on the specified index.
     * @param head Head of the list
     * @param index Index of the node to be removed
     */
    public static void remove(Printer head, int index) {
        int i = 0;
        while (head.next != null && i != index) {
            head = head.next;
            i++;
        }

        if (i == index) {
            head.next = head.next.next;
        }
    }

    public static void remove(Document head, int index) {
        int i = 0;
        while (head.next != null && i != index) {
            head = head.next;
            i++;
        }

        if (i == index) {
            head.next = head.next.next;
        }
    }

    /**
     * Removes one node from the top of the stack.
     * @param head Head of the stack
     */
    public static void removeFromStack(Printer head) {
        while (head.next != null) {
            if (head.next.next == null) {
                head.next = null;
            } else {
                head = head.next;
            }
        }
    }

    public static void removeFromStack(Document head) {
        while (head.next != null) {
            if (head.next.next == null) {
                head.next = null;
            } else {
                head = head.next;
            }
        }
    }

    /**
     * Removes the first node of the queue.
     * @param head Head of the queue
     */
    public static void removeFromQueue(Printer head) {
        if (head.next != null) {
            head.next = head.next.next;
        }
    }

    public static void removeFromQueue(Document head) {
        if (head.next != null) {
            head.next = head.next.next;
        }
    }

    /**
     * Returns the node of the list located at the specified index
     * @param head Head of the list
     * @param index Index of the node
     * @return Node located at the specified index
     */
    public static Printer get(Printer head, int index) {
        int i = 0;
        while (head.next != null && i != index) {
            head = head.next;
            i++;
        }

        return head.next;
    }

    public static Document get(Document head, int index) {
        int i = 0;
        while (head.next != null && i != index) {
            head = head.next;
            i++;
        }

        return head.next;
    }

    /**
     * Returns the size of the list
     * @param head Head of the list
     * @return Size of the list
     */
    public static int size(Printer head) {
        int i = 0;
        while (head.next != null) {
            head = head.next;
            i++;
        }
        return i;
    }

    public static int size(Document head) {
        int i = 0;
        while (head.next != null) {
            head = head.next;
            i++;
        }
        return i;
    }
}

class Printer {
    public String name;
    public Document currentDoc;
    public int currentPage;
    public Document previousDocs;
    public boolean isPrinting = false;
    public Printer next;
    public String previousDocsNames;

    public Printer() {}

    public Printer(String name) {
        this.name = name;
        previousDocs = new Document();
        next = null;
        previousDocsNames = null;
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
}

public class Main {

    public static int numPrinters;
    public static Printer printers = new Printer(null);
    public static int numDocuments;
    public static Document documents = new Document(null, 0);
    public static int numPagesPrinted = 0;
    public static StringBuilder previouslyPrintedDocs = new StringBuilder();

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
                ListUtil.append(printers, newPrinter);
                //printers = addToEndOfList(printers, printers, newPrinter);
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
                ListUtil.append(documents, newDocument);
                //documents = addToEndOfList(documents, documents, newDocument);
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
        while (documents.next != null || isAnyPrinterRunning(printers)) {
            
            Printer printer = printers.next;
            while (printer != null) {

                if (printer.currentPage <= 0) {

                    printer.isPrinting = false;

                    // Adds the finished Document to the stacks of previously printed Docs
                    if (printer.currentDoc != null) {
                        ListUtil.append(printer.previousDocs, new Document(printer.currentDoc.name, printer.currentDoc.numPages));
                        if (previouslyPrintedDocs.length() == 0) {
                            previouslyPrintedDocs.insert(0, printer.currentDoc.name + "-" + printer.currentDoc.numPages + "p");
                        } else {
                            previouslyPrintedDocs.insert(0, printer.currentDoc.name + "-" + printer.currentDoc.numPages + "p\n");
                        }
                    }

                    if (documents.next != null) {
                        // Takes Document from Document queue to the printer
                        printer.currentDoc = new Document(documents.next.name, documents.next.numPages);
                        printer.currentPage = printer.currentDoc.numPages;
                        printer.isPrinting = true;
                        ListUtil.removeFromQueue(documents);


                        // Handles the printing of the names of documents of the printers
                        if (printer.previousDocsNames == null)
                            printer.previousDocsNames = printer.currentDoc.name + "-" + printer.currentDoc.numPages + "p";
                        else
                            printer.previousDocsNames = printer.currentDoc.name + "-" + printer.currentDoc.numPages + "p, " + printer.previousDocsNames;

                        output.append("[" + printer.name + "] ");
                        output.append(printer.previousDocsNames + "\n");

                    } else {
                        printer.currentDoc = null;
                    }
                    
                }
                
                // Refreshs the counters
                if (printer.currentPage > 0) {
                    printer.currentPage--;
                    numPagesPrinted++;
                }

                printer = printer.next;
            }
        }
        return output;
    }

    /**
     * Checks if any of the printers is still running.
     * @param head Head of the list
     * @return {@code true} if any printer is running, {@code false} otherwise
     */
    private static boolean isAnyPrinterRunning(Printer head) {
        boolean isRunning = false;
        while (head.next != null) {
            isRunning = isRunning || head.next.isPrinting;
            head = head.next;
        }
        return isRunning;
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
            outputString.append(previouslyPrintedDocs);

            // Printes the output to the terminal
            //System.out.println(outputString);
            
            // Writes the output of the program to the output file
            writeToFile(args[1], outputString);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}