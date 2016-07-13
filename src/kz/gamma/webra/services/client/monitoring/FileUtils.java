package kz.gamma.webra.services.client.monitoring;

import org.apache.log4j.Logger;

import java.io.*;

/**
 * Created by i_nikulin
 * 10.04.2009 14:43:36
 */

/**
 * Утилиты для инсталлятора по работе с файлами
 */
public class FileUtils {
    protected static final Logger log = Logger.getLogger(FileUtils.class);

    public static void copy(File sourceLocation, File targetLocation) throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists())
                targetLocation.mkdir();

            String[] children = sourceLocation.list();
            for (String aChildren : children)
                copy(new File(sourceLocation, aChildren), new File(targetLocation, aChildren));

        } else {
            log.info("copy file:" + sourceLocation.getAbsolutePath() + "->" + targetLocation.getAbsolutePath());

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
                out.write(buf, 0, len);

            in.close();
            out.close();
        }
    }

    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }


}