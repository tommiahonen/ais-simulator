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
            file.write(filename);

            System.out.println("File " + filename + "was uploaded succesfully.");

            return Response.ok().entity("File " + filename + " was uploaded successfully.").build();
        }
        catch (IOException | ServletException ex){
            //Do some exception management
            ex.printStackTrace();
            return Response.status(404).entity("Error while saving file " + ex.getMessage() + " to filesystem on host. Does directory exist and are you able to write to it?").build();
        }
    }
}
