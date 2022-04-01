package client.utils.communication;

import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.io.*;
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
}
