package com.cinemamod.downloader;

import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class CinemaModDownloader extends Thread {

    private Properties versions;
    private JFrame frame;
    private JLabel taskLabel;
    private JLabel jcefVersionLabel;
    private JLabel fileLabel;
    private JProgressBar progressBar;

    private int totalJcefFiles;

    public CinemaModDownloader(JFrame frame, JLabel taskLabel, JLabel jcefVersionLabel, JLabel fileLabel, JProgressBar progressBar) {
        this.frame = frame;
        this.taskLabel = taskLabel;
        this.jcefVersionLabel = jcefVersionLabel;
        this.fileLabel = fileLabel;
        this.progressBar = progressBar;
    }

    private void fetchVersions() throws IOException {
        versions = new Properties();

        URL versionsURL = new URL(Resource.CINEMAMOD_VERSIONS_URL);
        try (InputStream inputStream = versionsURL.openStream()) {
            try (Scanner scanner = new Scanner(inputStream)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    try {
                        String library = line.split(" ")[0];
                        String version = line.split(" ")[1];
                        versions.put(library, version);
                    } catch (IndexOutOfBoundsException e) {
                        // ignore
                    }
                }
            }
        }
    }

    // checks if the file at filePath is on disk and the hash matches
    private boolean ensureLibFile(String sha1hash, String relPath) {
        Path librariesPath = Paths.get(System.getProperty("cinemamod.libraries.path"));
        File libFile = new File(librariesPath + relPath);

        boolean result = false;

        if (libFile.exists()) {
            // check hash of existing file on disk
            try {
                String onDiskHash = HashUtil.sha1Hash(libFile);

                if (sha1hash.equals(onDiskHash)) {
                    // file is good
                    result = true;
                } else {
                    System.out.println(libFile + " hash mismatch, will update");
                }
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private void downloadLibFile(String remotePath, String relPath) throws IOException {
        Path librariesPath = Paths.get(System.getProperty("cinemamod.libraries.path"));
        fileLabel.setText("Downloading " + remotePath);
        System.out.println(fileLabel.getText());
        File localFile = new File(librariesPath + relPath);
        FileUtils.copyURLToFile(new URL(remotePath), localFile);

        // set appropriate files as executable on linux
        if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("linux")) {
            if (localFile.toString().contains("chrome-sandbox") || localFile.toString().contains("jcef_helper")) {
                Set<PosixFilePermission> perms = new HashSet<>();
                perms.add(PosixFilePermission.OWNER_READ);
                perms.add(PosixFilePermission.OWNER_WRITE);
                perms.add(PosixFilePermission.OWNER_EXECUTE);

                try {
                    Files.setPosixFilePermissions(localFile.toPath(), perms);
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    private void ensureJcef(String cefBranch, String platform) throws IOException {
        String jcefUrlString = Resource.getCinemamodJcefUrl(cefBranch, platform);
        String jcefManifestUrlString = jcefUrlString + "/manifest.txt";

        System.out.println("JCEF Manifest location " + jcefManifestUrlString);

        URL jcefManifestURL = new URL(jcefManifestUrlString);

        Map<String, String> jcefFiles = new HashMap<>();

        try (InputStream inputStream = jcefManifestURL.openStream()) {
            try (Scanner scanner = new Scanner(inputStream)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String sha1hash = line.split(" ")[0];
                    String filePath = line.split(" ")[2].substring(1); // substring to remove the leading "."

                    jcefFiles.put(sha1hash, filePath);

                    totalJcefFiles++;
                }
            }
        }

        int fileCount = 0;
        for (Map.Entry<String, String> entry : jcefFiles.entrySet()) {
            fileCount++;

            int value = (int) ((fileCount / (double) totalJcefFiles) * 100);

            progressBar.setValue(value);

            String sha1hash = entry.getKey();
            String filePath = entry.getValue();

            fileLabel.setText("Found " + filePath.substring(1)); // substring to remove leading "/"

            if (!ensureLibFile(sha1hash, filePath)) {
                String remotePath = jcefUrlString + filePath;
                fileLabel.setText(remotePath);
                downloadLibFile(remotePath, filePath);
            }
        }
    }

    @Override
    public void run() {
        taskLabel.setText("Fetching mod version info...");

        try {
            fetchVersions();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("CinemaMod library versions " + versions.toString());

        jcefVersionLabel.setText("Current CinemaDisplays CEF branch: " + versions.getProperty("jcef"));

        final String platform;

        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);

        if (os.contains("win")) {
            platform = "win64";
        } else if (os.contains("mac")) {
            platform = "mac64";
        } else if (os.contains("linux")) {
            platform = "linux64";
        } else {
            platform = "unknown";
        }

        taskLabel.setText("Verifying library files...");

        try {
            ensureJcef(versions.getProperty("jcef"), platform);
        } catch (IOException e) {
            e.printStackTrace();
        }

        frame.setVisible(false);
        frame.dispose();
    }

    public static void main(String[] args) {
        if (System.getProperty("cinemamod.libraries.path") == null) {
            System.out.println("Not running inside Minecraft");
            return;
        }

        System.setProperty("java.awt.headless", "false");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame();
        frame.setSize(700, 250);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 1));

        // Task panel
        JPanel taskPanel = new JPanel();
        taskPanel.setLayout(new FlowLayout());

        JLabel nameLabel = new JLabel("CinemaDisplays");
        taskPanel.add(nameLabel);

        JLabel taskLabel = new JLabel("Setting up...");
        taskPanel.add(taskLabel);

        mainPanel.add(taskPanel);

        // Progress panel
        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new FlowLayout());

        JLabel fileLabel = new JLabel();
        progressPanel.add(fileLabel);

        JProgressBar progressBar = new JProgressBar(1, 100);
        progressBar.setValue(0);
        progressPanel.add(progressBar);

        mainPanel.add(progressPanel);

        // Version panel
        JPanel versionPanel = new JPanel();
        versionPanel.setLayout(new FlowLayout());

        JLabel jcefVersionLabel = new JLabel();
        versionPanel.add(jcefVersionLabel);

        mainPanel.add(versionPanel);

        frame.add(mainPanel);
        frame.setVisible(true);

        CinemaModDownloader downloader = new CinemaModDownloader(frame, taskLabel, jcefVersionLabel, fileLabel, progressBar);
        downloader.start();

        try {
            downloader.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.setProperty("java.awt.headless", "true");
    }

}
