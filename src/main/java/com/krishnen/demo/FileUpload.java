package com.krishnen.demo;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 * Created by User on 25/01/2016.
 */
public class FileUpload extends HttpServlet {

    private static final long serialVersionUID = 127759768879064589L;

    private static final Logger logger = Logger.getLogger(FileUpload.class.getName());

    // name of form fields which are looked up in multipart request
    public static final String INPUT_NAME = "file";

    // override 'POST' handler. Appliction will use 'POST' to send 'multipart/form-data'
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("Inside doPost fileupload servlet");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YMMddhhmmss");

        logger.info("uploadtype " + req.getParameter("uploadtype"));
        String currentFile = "/tmp/" + req.getParameter("uploadtype") + "_" + simpleDateFormat.format(new Date());


        final String reqContentType = req.getContentType();
        OutputStream outputStream =
                new FileOutputStream(new File(currentFile));


        logger.info("Request Content type " + reqContentType);

        if (!reqContentType.contains("multipart/form-data")) {
            logger.info("Inside multipart");
            logger.severe("Received request which is not multipart: " + reqContentType);
            resp.sendError(406, "Received request which is not multipart: " + reqContentType);
        }

         /*
         * In servlet 3.0, Parts carry form data. Get Parts
         * and perform some name & type checks. Parts contain all data sent in
         * form not only file, we need only file.
         */
        Collection<Part> fileParts = req.getParts();
        logger.info("Part size " + fileParts.size());

        if (fileParts != null && fileParts.size() > 0) {
            for (Part p : fileParts) {
                String partContentType = p.getContentType();
                logger.info("partContentType " + partContentType);
                String partName = p.getName();
                logger.info("partName " + partName);
                if (partContentType != null && partContentType.equals("application/octet-stream") && partName != null
                        && partName.equals(INPUT_NAME)) {
                    IOUtils.copy(p.getInputStream(), outputStream);
                }
            }
        }

        //Start a batch job to process the file
        JobOperator jo = BatchRuntime.getJobOperator();
        Properties properties = new Properties();
        properties.setProperty("filepath", currentFile);
        jo.start("file_process", properties);

        PrintWriter writer = resp.getWriter();
        writer.print("File being processed");
        writer.close();
    }


}
