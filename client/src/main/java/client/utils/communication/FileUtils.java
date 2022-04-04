package client.utils.communication;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Utilities for creating, editing and retrieving local files.
 */
public class FileUtils {
    /**
     * Saves the server path to a local file.
     *
     * @param serverPath the server path
     */
    public static void savePath(File localFile, String serverPath) {
        try {
            // Create a local file if it doesn't exist
            if (!localFile.exists()) {
                localFile.createNewFile();
            }
            // Update file with new server path
            PrintWriter writer = new PrintWriter(new FileWriter(localFile.getAbsolutePath()));
            writer.write(serverPath);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the user credentials to a local file.
     *
     * @param username the users username
     * @param password the users password
     */
    public static void saveCredentials(File localFile, String username, String password) {
        try {
            // Create a local file if it doesn't exist
            if (!localFile.exists()) {
                localFile.createNewFile();
            }
            // Update file with new server path
            PrintWriter writer = new PrintWriter(new FileWriter(localFile.getAbsolutePath()));
            writer.write(username);
            writer.write("\n" + password);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the server path from the local file.
     */
    public static String retrievePath(File localFile) {
        String serverPath = null;
        try {
            // Check if local file exists
            if (localFile.exists()) {
                Scanner scanner = new Scanner(localFile);
                // If server path exists then set the sever path in client and set checkbox to checked
                if (scanner.hasNextLine()) {
                    serverPath = scanner.nextLine();
                }
                scanner.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return serverPath;
    }

    /**
     * Retrieves the server path from the local file.
     */
    public static List<String> retrieveCredentials(File localFile) {
        String username = null;
        String password = null;
        try {
            // Check if local file exists
            if (localFile.exists()) {
                Scanner scanner = new Scanner(localFile);
                // If server path exists then set the sever path in client and set checkbox to checked
                if (scanner.hasNextLine()) {
                    username = scanner.nextLine();
                    password = scanner.nextLine();
                }
                scanner.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(Arrays.asList(username, password));
    }
}
