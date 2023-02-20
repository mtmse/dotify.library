package net.davidashen.text;

import net.davidashen.util.ErrorHandler;

import java.util.logging.Logger;

/**
 * TODO: write javadoc.
 */
public class HyphenatorUI {
    public static final Logger LOGGER = Logger.getLogger(HyphenatorUI.class.getCanonicalName());

    /**
     * Simple command-line invocation -- serves as example.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Hyphenator hyphenator = new Hyphenator();
        hyphenator.setErrorHandler(new ErrorHandler() {
            public void debug(String guard, String s) {
            }

            public void info(String s) {
                LOGGER.info(s);
            }

            public void warning(String s) {
                LOGGER.warning("WARNING: " + s);
            }

            public void error(String s) {
                LOGGER.severe("ERROR: " + s);
            }

            public void exception(String s, Exception e) {
                LOGGER.severe("ERROR: " + s);
                LOGGER.throwing(e.getClass().getName(),  "exception", e);
            }

            public boolean isDebugged(String guard) {
                return false;
            }
        });
        if (args.length != 2 && args.length != 3) {
            LOGGER.info("call: java net.davidashen.text.Hyphenator word table.tex [codes.txt]");
            System.exit(1);
        }
        java.io.InputStream table = null;
        try {
            table = new java.io.BufferedInputStream(new java.io.FileInputStream(args[1]));
        } catch (java.io.IOException e) {
            LOGGER.severe("cannot open hyphenation table " + args[1] + ": " + e.toString());
            System.exit(1);
        }
        int[] codelist = new int[256];
        for (int i = 0; i != 256; ++i) {
            codelist[i] = i;
        }
        if (args.length == 3) {
            java.io.BufferedReader codes = null;
            try {
                codes = new java.io.BufferedReader(new java.io.FileReader(args[2]));
            } catch (java.io.IOException e) {
                LOGGER.severe("cannot open code list" + args[2] + ": " + e.toString());
                System.exit(1);
            }
            try {
                String line;
                while ((line = codes.readLine()) != null) {
                    java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(line);
                    String token;
                    if (tokenizer.hasMoreTokens()) { // skip empty lines
                        token = tokenizer.nextToken();
                        if (!token.startsWith("%")) { // lines starting with %
                            // are comments
                            int key = Integer.decode(token).intValue(), value = key;
                            if (tokenizer.hasMoreTokens()) {
                                token = tokenizer.nextToken();
                                value = Integer.decode(token).intValue();
                            }
                            codelist[key] = value;
                        }
                    }
                }
                codes.close();
            } catch (java.io.IOException e) {
                LOGGER.severe("error reading code list: " + e);
                System.exit(1);
            }
        }

        try {
            hyphenator.loadTable(table, codelist);
            table.close();
        } catch (java.io.IOException e) {
            LOGGER.severe("error loading hyphenation table: " + e);
            System.exit(1);
        }

        LOGGER.info(args[0] + " -> " + hyphenator.hyphenate(args[0]));
    }
}
