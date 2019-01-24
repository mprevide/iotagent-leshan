package org.cpqd.iotagent;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import com.cpqd.app.auth.Auth;

/**
 * This class abstracts everything related to the image-manager,
 * it should have no knowledge of anything LWM2M related
 */

public class ImageDownloader {
    private Logger mLogger = Logger.getLogger(ImageDownloader.class);

    private String mImageManagerUri;
    private String mFileServerAddress;
    private String mFileServerUnsecurePort;
    private String mFileServerSecurePort;
    private String mDataDir;
    private static final String IMAGE_EXTENSION = ".hex";

    public ImageDownloader(String imageManagerUri, String dataDir,
        String fileServerAddress, int fileServerUnsecurePort, int fileServerSecurePort) {

        this.mImageManagerUri = imageManagerUri + "/image";
        this.mFileServerAddress = fileServerAddress;
        this.mFileServerUnsecurePort = Integer.toString(fileServerUnsecurePort);
        this.mFileServerSecurePort = Integer.toString(fileServerSecurePort);
        this.mDataDir = dataDir;
    }

    public String downloadImageAndGenerateUri(String tenant, String imageLabel,
        String version, boolean isSecure) {

        String imageFile = null;
        try {
            imageFile = this.fetchImage(tenant, imageLabel, version);
        } catch (Exception e) {
            this.mLogger.error(e.getMessage());
            throw new RuntimeException("Failed to download and generate URI");
        }

        if (isSecure) {
            return "coaps://" + this.mFileServerAddress + ":" +
                this.mFileServerSecurePort + "/" + this.mDataDir + "/" + imageFile;
        }
        return "coap://" + this.mFileServerAddress + ":" +
            this.mFileServerUnsecurePort + "/" + this.mDataDir + "/" + imageFile;
    }

    private String fetchImage(String tenant, String imageLabel, String version) throws RuntimeException {

        String imageId = null;
        this.mLogger.debug("Fetching image with label: " + imageLabel + " at version " + version);
        try {
            String token = Auth.getInstance().getToken(tenant);
            imageId = this.getImageId(imageLabel, version, token);
            this.downloadImage(imageId, tenant + "-" + imageId + IMAGE_EXTENSION, token);
        } catch (Exception e) {
            this.mLogger.error(e.getMessage());
            throw new RuntimeException("Failed to fetch image");
        }

        return tenant + "-" + imageId + IMAGE_EXTENSION;
    }

    private String getImageId(String imageLabel, String version, String token) throws RuntimeException {
        try {
            HttpResponse<JsonNode> response = Unirest.get(this.mImageManagerUri)
                .header("Authorization", "Bearer " + token).asJson();

            if (response.getStatus() != 200) {
                this.mLogger.warn("Failed to request image id to image manager. " +
                "Http status: " + Integer.toString(response.getStatus()));
            } else {
                JsonNode imageList = response.getBody();
                JSONArray images = imageList.getArray();
                for (int i = 0; i < images.length(); i++) {
                    JSONObject image = images.getJSONObject(i);
                    String d = image.getString("label");
                    String f = image.getString("fw_version");
                    Boolean haveBinary = image.getBoolean("confirmed");
                    if (d.equals(imageLabel) && f.equals(version) && haveBinary) {
                        return image.getString("id");
                    }
                }
            }
        } catch (Exception e) {
            this.mLogger.error(e.getMessage());
        }
        throw new RuntimeException("Failed to get image id");
    }

    private void downloadImage(String imageId, String filename, String token) throws RuntimeException {
        this.mLogger.debug("Downloading image (" + imageId + ") as " + filename);
        try {
            HttpResponse<InputStream> fwInStream = 
                Unirest.get(this.mImageManagerUri + "/" + imageId + "/binary")
                    .header("Authorization", "Bearer " + token)
                    .asBinary();
            if (fwInStream.getStatus() != 200) {
                this.mLogger.warn("Failed to download the image from image manager. " +
                    "Http status: " + Integer.toString(fwInStream.getStatus()));
            } else {
                InputStream in = fwInStream.getBody();
                Path path = FileSystems.getDefault().getPath("./" + this.mDataDir + "/" + filename);

                Path DestinationPath = FileSystems.getDefault().getPath("./" + this.mDataDir);
                if (!Files.exists(DestinationPath)) {
                    Files.createDirectory(DestinationPath);
                }
                if (!Files.exists(path)) {
                    Files.createFile(path);
                }
                Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
                this.mLogger.info("Image " + imageId + " successfully downloaded.");
                return;
            }
        } catch (Exception e) {
            this.mLogger.error(e.getMessage());
        }
        throw new RuntimeException("Failed to download image");
    }


}
