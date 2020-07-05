package org.cpqd.iotagent;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.apache.log4j.Logger;
import org.cpqd.iotagent.lwm2m.objects.FirmwareUpdatePath;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import com.cpqd.app.auth.Auth;

/**
 * This class abstracts everything related to the image-manager,
 * it should have no knowledge of anything LWM2M related
 */

public class ImageDownloader {
    private Logger mLogger = Logger.getLogger(ImageDownloader.class);
    
    private String mImageManagerUri;
    private String mFileServerAddress;
    private String mCoapPort;
    private String mCoapsPort;
    private String mHttpPort;
    private String mHttpsPort;
    private String mDataDir;
    private static final String IMAGE_EXTENSION = ".hex";

    public ImageDownloader(String imageManagerUri, String dataDir, 
        String fileServerAddress, int coapPort, int coapsPort,
        int httpPort, int httpsPort) {

        this.mImageManagerUri = imageManagerUri + "/image";
        this.mFileServerAddress = fileServerAddress;
        this.mCoapPort = Integer.toString(coapPort);
        this.mCoapsPort = Integer.toString(coapsPort);
        this.mHttpPort = Integer.toString(httpPort);
        this.mHttpsPort = Integer.toString(httpsPort);
        this.mDataDir = dataDir;
    }

	public String downloadImageAndGenerateUri(String tenant, String imageLabel, String version, int protocol,
			String imageId, Map<String, String> queryParams) {

        String imageFilename = null;
        try {
			imageFilename = imageId == null ? this.fetchImage(tenant, imageLabel, version)
					: this.fetchImage(tenant, imageId);
        } catch (Exception e) {
            this.mLogger.error(e.getMessage());
            throw new RuntimeException("Failed to download and generate URI");
        }

        String uri;
        switch(protocol) {
            case FirmwareUpdatePath.PROTOCOL_COAP:
                uri = "coap://" + this.mFileServerAddress + ":" + this.mCoapPort +
                    "/" + imageFilename;
            break;
            case FirmwareUpdatePath.PROTOCOL_COAPS:
                uri = "coaps://" + this.mFileServerAddress + ":" + this.mCoapsPort +
                    "/" + imageFilename;
            break;
            case FirmwareUpdatePath.PROTOCOL_HTTP:
                uri = "http://" + this.mFileServerAddress + ":" + this.mHttpPort +
                    "/" + imageFilename;
            break;
            case FirmwareUpdatePath.PROTOCOL_HTTPS:
                uri = "https://" + this.mFileServerAddress + ":" + this.mHttpsPort +
                    "/" + imageFilename;
                this.mLogger.error("HTTPS is not supported yet");
                throw new RuntimeException("HTTPS is not supported yet");
            default:
                this.mLogger.error("Unknown protocol: " + protocol);
                throw new RuntimeException("Failed to generate URI");
        }
		
		if (queryParams != null) {
			StringBuilder sb = new StringBuilder(uri);
			queryParams.forEach((key, value) -> sb.append("&" + key + "=" + value));
			uri = sb.toString();
		}
        
        return uri;
    }

    private String fetchImage(String tenant, String imageLabel, String version) throws RuntimeException {

        String imageId = null;
        this.mLogger.debug("Fetching image with label: " + imageLabel + " at version " + version);
        String filename = null;
        try {
            String token = Auth.getInstance().getToken(tenant);
            imageId = this.getImageId(imageLabel, version, token);
            filename = tenant + "-" + imageId + IMAGE_EXTENSION;
            this.downloadImage(imageId, filename, token);
        } catch (Exception e) {
            this.mLogger.error(e.getMessage());
            throw new RuntimeException("Failed to fetch image");
        }

        return filename;
    }
    
	private String fetchImage(String tenant, String imageId) throws RuntimeException {

		this.mLogger.debug("Fetching image with id: " + imageId);
		String filename = null;
		try {
			String token = Auth.getInstance().getToken(tenant);
			filename = tenant + "-" + imageId;
			this.downloadImage(imageId, filename, token);
		} catch (Exception e) {
			this.mLogger.error(e.getMessage());
			throw new RuntimeException("Failed to fetch image");
		}

		return filename;
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
                Path path = FileSystems.getDefault().getPath(this.mDataDir + "/" + filename);

                Path DestinationPath = FileSystems.getDefault().getPath(this.mDataDir);
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
