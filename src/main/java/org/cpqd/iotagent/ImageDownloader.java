package org.cpqd.iotagent;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.eclipsesource.json.Json;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONArray;

import java.awt.*;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Iterator;

public class ImageDownloader {

    private String imageUrl;

    public ImageDownloader(String imageManagerUrl) {
        this.imageUrl = imageManagerUrl + "/image/";
    }

    public void FetchImage(String service, String deviceLabel, String version) {
        String token = GetJwtToken(service);
        String imageID = GetImageId(deviceLabel, version, token);
    }

    private String GetImageId(String deviceLabel, String version, String token) {
        try {

            HttpResponse<JsonNode> response = Unirest.get(imageUrl)
                    .header("Authorization", "Bearer " + token).asJson();

            JsonNode imageList = response.getBody();
            Iterator<?> images = imageList.getArray().iterator();
            while (images.hasNext()){
                System.out.println(images.next());
            }


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return "OK";
    }

    private void DownloadImage(String imageId, String token) {
        try {
            HttpResponse<InputStream> fwInStream = Unirest.get(imageUrl + imageId + "/binary")
                    .header("Authorization", "Bearer " + token)
                    .asBinary();

            InputStream in = fwInStream.getBody();
            Path path = FileSystems.getDefault().getPath("./fw");
            Files.copy(in, path);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }


    private static String GetJwtToken(String service) {
        String token = "";
        Integer[] group = new Integer[1];
        group[0] = 1;

        // TODO(jsiloto) Substitute mocked values with reasonable ones
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            token = JWT.create()
                    .withClaim("userid", 1)
                    .withClaim("name", "Admin (superuser)")
                    .withArrayClaim("groups", group)
                    .withIssuedAt(new Date(1517339633))
                    .withExpiresAt(new Date(1517340053))
                    .withClaim("email", "admin@noemail.com")
                    .withClaim("profile", "admin")
                    .withIssuer("eGfIBvOLxz5aQxA92lFk5OExZmBMZDDh")
                    .withClaim("service", service)
                    .withJWTId("7e3086317df2c299cef280932da856e5")
                    .withClaim("username", "admin")
                    .sign(algorithm);
        } catch (UnsupportedEncodingException exception) {
            //UTF-8 encoding not supported
        } catch (JWTCreationException exception) {
            //Invalid Signing configuration / Couldn't convert Claims.
        }
        return token;
    }

}
