package org.cpqd.iotagent;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.eclipsesource.json.Json;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import java.awt.*;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.*;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ImageDownloader {
    private Logger mLogger = Logger.getLogger(ImageDownloader.class);

    private String imageUrl;

    public ImageDownloader(String imageManagerUrl) {
        this.imageUrl = imageManagerUrl + "/image/";
        try{
            Path path = FileSystems.getDefault().getPath("./fw/");
            if(!Files.exists(path)){
                Files.createDirectory(path);
            }
        }
        catch(Exception e){
        }
    }

    public String FetchImage(String service, String deviceLabel, String version) {
        String token = TenancyManager.GetJwtToken(service);
        String imageID = GetImageId(deviceLabel, version, token);
        DownloadImage(imageID, token);
        return imageID;
    }

    private String GetImageId(String deviceLabel, String version, String token) {
        try {

            HttpResponse<JsonNode> response = Unirest.get(imageUrl)
                    .header("Authorization", "Bearer " + token).asJson();

            JsonNode imageList = response.getBody();
            JSONArray images = imageList.getArray();
            for(int i =0; i<images.length(); i++){
                JSONObject image = images.getJSONObject(i);
                String d = image.getString("label");
                String f = image.getString("fw_version");
                Boolean haveBinary = image.getBoolean("confirmed");
                if(d.equals(deviceLabel) && f.equals(version) && haveBinary){
                    return image.getString("id");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            mLogger.error(e);
        }
        throw new NoSuchElementException("Image not on Database");
    }

    private void DownloadImage(String imageId, String token) {
        try {
            HttpResponse<InputStream> fwInStream = Unirest.get(imageUrl + imageId + "/binary")
                    .header("Authorization", "Bearer " + token)
                    .asBinary();

            InputStream in = fwInStream.getBody();
            Path path = FileSystems.getDefault().getPath("./fw/"+imageId+".hex");
            if(!Files.exists(path)){
                Files.createFile(path);
            }
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
            mLogger.error(e);
        }
    }




}
