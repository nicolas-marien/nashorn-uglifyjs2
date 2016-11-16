package com.nico29.nashornuglifyjs2;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.nio.file.Files;

public class Minifier {

    private static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public static void main (String[] args) {
        Minifier minifier = new Minifier();
        // setup the nashorn engine
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        // load the javascript minifier library
        try {
            engine.eval(minifier.getResourceFileReader("/uglify.min.js"));
        } catch (ScriptException e) {
            System.out.println("Cannot load UglifyJS2 library from resources");
            e.printStackTrace();
            System.exit(-1);
        }
        try {
            engine.eval(minifier.getResourceFileReader("/minify.js"));
        } catch (ScriptException e) {
            e.printStackTrace();
            e.printStackTrace();
            System.exit(-1);
        }

        // load the javascript file to minify
        File codeToMinify = new File(args[0]);

        // prepare to call a javascript function
        Invocable invocable = (Invocable) engine;

        String minifiedCode = null;
        try {
            minifiedCode = (String) invocable.invokeFunction("minify", minifier.getContentAsStringFromFile(codeToMinify));
        } catch (Exception e) {
            System.out.println("Unable to miniy the provided script");
            e.printStackTrace();
            System.exit(-1);
        }

        minifier.writeOutputToFile(minifiedCode, args[1]);
    }

    /**
     * Get Reader from resource name
     * @param resourceName
     * @return reader
     */
    private Reader getResourceFileReader (String resourceName) {
        return new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(resourceName)));
    }

    /**
     * Get the content from a file as a UTF-8 encoded String
     * @param file
     * @return the string content
     * @throws IOException
     */
    private String getContentAsStringFromFile (File file) throws IOException {
        StringWriter writer = new StringWriter();
        BufferedReader reader = Files.newBufferedReader(file.toPath());

        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        int n;

        while (EOF != (n = reader.read(buffer))) { writer.write(buffer, 0, n); }

        return writer.toString();
    }

    private void writeOutputToFile (String output, String ouputLocation) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(ouputLocation));
            writer.write(output);
        } catch (IOException e) {
            System.out.println("Cannot write output file");
            e.printStackTrace();
            System.exit(-1);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
