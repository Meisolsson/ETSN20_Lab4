package com.olssonhenrik;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Grep {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";

    public static void main(String[] args) {
        if (args.length > 0) {
            if ("--help".equals(args[0])) {
                System.out.println("search <pattern> <file> [options]\n");
                System.out.println("Options:");
                System.out.println("  -i -- Search case insensitive");
                return;
            }
        }

        if (args.length < 3 || !"search".equals(args[0])) {
            System.out.println("Incorrect use of program. Syntax is: search <pattern> <file>");
            return;
        }

        String pattern = args[1];
        String fileName = args[2];
        boolean caseInsensitive = false;
        for (int i = 3; i < args.length; i++) {
            if ("-o".equals(args[i])) {
                caseInsensitive = true;
                break;
            }
        }

        try (FileInputStream fis = new FileInputStream(fileName);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {
            int options = 0;
            if (caseInsensitive) {
                options |= Pattern.CASE_INSENSITIVE;
            }

            Pattern regexPattern = Pattern.compile("(" + pattern + ")", options);
            Matcher matcher = null;
            String temp;

            while (reader.ready()) {
                temp = reader.readLine();
                if (matcher == null) {
                    matcher = regexPattern.matcher(temp);
                } else {
                    matcher.reset(temp);
                }
                if (matcher.find()) {
                    System.out.println(matcher.replaceAll(ANSI_RED + "$1" + ANSI_RESET));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File was not found: '" + fileName + "'");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
