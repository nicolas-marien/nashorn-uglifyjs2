package com.nico29.nashornuglifyjs2;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Minifier {

    public static void main(String[] args) {
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
            System.exit(-1);
        }

        // prepare to call a javascript function
        Invocable invocable = (Invocable) engine;

        String minifiedCode = null;
        try {
            minifiedCode = (String) invocable.invokeFunction("minify", minifier.getContentAsStringFromFile(args[0]));
        } catch (NoSuchMethodException | IOException | ScriptException e) {
            e.printStackTrace();
        }

        minifier.writeOutputToFile(minifiedCode, args[1]);
    }

    /**
     * Get Reader from resource name
     * @param resourceName
     * @return reader
     */
    private Reader getResourceFileReader(String resourceName) {
        return new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(resourceName)));
    }

    /**
     * Get the content from a file as a UTF-8 encoded String
     * @param name
     * @return the string content
     * @throws IOException
     */
    private String getContentAsStringFromFile(String name) throws IOException {
        if (name == null) {
            System.err.println("Cannot access the file to minify");
            System.exit(-1);
        }
        Path path = Paths.get(name);
        File file = path != null ? path.toFile() : null;
        if (file != null && file.isFile() && file.canRead()) {
            return new String(Files.readAllBytes(path));
        } else {
            throw new IOException("Unable to read file");
        }
    }

    private void writeOutputToFile(String output, String outputLocation) {
        try {
            Files.write(Paths.get(outputLocation), output.getBytes());
        } catch (IOException e) {
            System.err.println("Unable to write output file");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
