package com.krishnen.demo.batches;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Constants;

import javax.batch.api.Batchlet;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by User on 26/01/2016.
 * This class handles the processing of the file uploaded as a batch job asynchronously
 */
@Dependent
@Named("process_file")
public class ProcessFileBatchlet implements Batchlet {

    @Inject
    private JobContext jobContext;

    private BufferedReader reader;
    String  currentLine = null;


    @Override
    public String process() throws Exception {
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        Properties jobParameters = jobOperator.getParameters(jobContext.getExecutionId());
        String file_name = (String)jobParameters.get("filepath");
        try{
            reader = new BufferedReader(
                    new FileReader(file_name)
            );
            while ((currentLine = reader.readLine()) != null) {
                // Process each line and do something relevant here like saving in DB
            }
        }
        catch(IOException e)
        {
            jobContext.setExitStatus("file_process_error");
        }
        return "Batch Job Processed";
    }

    @Override
    public void stop() throws Exception {

    }

}