package com.krishnen.demo.batches;

import javax.batch.api.AbstractBatchlet;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Properties;

/**
 * Created by User on 26/01/2016.
 */
@Dependent
@Named("process_file_complete")
public class ProcessFileCompleted extends AbstractBatchlet {

    @Inject
    private JobContext jobContext;

    private JobOperator jobOperator = BatchRuntime.getJobOperator();


    @Override
    public String process() {
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        Properties jobParameters = jobOperator.getParameters(jobContext.getExecutionId());
        String file_name = (String)jobParameters.get("filepath");
        return "Batch Job COMPLETED - file " + file_name;
    }

}
