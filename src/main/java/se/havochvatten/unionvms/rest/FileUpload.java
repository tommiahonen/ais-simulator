package se.havochvatten.unionvms.rest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.charset.StandardCharsets;

@Path("/files")
public class FileUpload {

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
            @Context HttpServletRequest request
    ) {
        String filename;
        try{
            Part file = request.getPart("newdatafile"); //file is the name of the parameter on the request.
            filename = file.getSubmittedFileName();

            String fullFilepath = new File(".").getCanonicalPath() + "/" + filename;
            file.write(filename);

            /*
            InputStream fileContent = file.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(fileContent, StandardCharsets.UTF_8.name()));
            StringBuilder value = new StringBuilder();

            char[] buffer = new char[1024];
            for (int length; (length = reader.read(buffer)) > 0; )
            {
                value.append(buffer, 0, length);
            }
            System.out.println(value);
            */

            return Response.ok().entity("File " + fullFilepath + " was uploaded successfully.").build();
        }
        catch (IOException | ServletException ex){
            //Do some exception management
            ex.printStackTrace();
            return Response.status(404).entity("Error while saving datafile.").build();
        }


    }
}
