package com.main.java.ngt.mts2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@SpringBootApplication
@EnableBatchProcessing
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class SbMultithreadStepEg2Application {
	
	public final static Logger logger = LoggerFactory.getLogger(SbMultithreadStepEg2Application.class);

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Value("${chunk-size}")
	private int chunkSize;

	@Value("${max-threads}")
	private int maxThreads;

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource batchDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	public AttemptReader processAttemptReader() {
		return new AttemptReader();
	}

	@Bean
	public AttemptProcessor processAttemptProcessor() {
		return new AttemptProcessor();
	}

	@Bean
	public AttemptWriter processAttemptWriter() {
		return new AttemptWriter();
	}

	@Bean
	public JobCompletionNotificationListener jobExecutionListener() {
		return new JobCompletionNotificationListener();
	}
	
	@Bean
	public StepExecutionNotificationListener stepExecutionListener() {
		return new StepExecutionNotificationListener();
	}
	
	@Bean
	public ChunkExecutionListener chunkListener() {
		return new ChunkExecutionListener();
	}

	@Bean
	public TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
		taskExecutor.setConcurrencyLimit(maxThreads);
		return taskExecutor;
	}

	@Bean
	public Job processAttemptJob() {
		return jobBuilderFactory.get("process-attempt-job")
				.incrementer(new RunIdIncrementer())
				.listener(jobExecutionListener())
				.flow(step()).end().build();
	}

	@Bean
	public Step step() {
		return stepBuilderFactory.get("step").<Attempt, Attempt>chunk(chunkSize)
				.reader(processAttemptReader())
				.processor(processAttemptProcessor())
				.writer(processAttemptWriter())
				.taskExecutor(taskExecutor())
				.listener(stepExecutionListener())
				.listener(chunkListener())
				.throttleLimit(maxThreads).build();
	}

	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		System.out.println(isPalindromeItr("TEET"));
		System.out.println(isPalindrome("TEST"));
		//SpringApplication.run(SbMultithreadStepEg2Application.class, args);
		time = System.currentTimeMillis() - time;
		//logger.info("Runtime: {} seconds.", ((double)time/1000));
		System.out.println("Runtime: {} seconds." +((double)time));
	}
	

	    public static boolean isPalindromeItr(String word) {
	    	if(word.length()<=0){
	        	return false;
	        }
	        StringBuilder sb = new StringBuilder();
	        char[] wordchar = word.toCharArray();
	        for (int i = wordchar.length-1; i >= 0 ; i--) {
	        	sb.append(wordchar[i]);
				
			}
	        return word.equalsIgnoreCase(sb.toString());	        
	    }
	    
	    public static boolean isPalindrome(String input) {
	        if (input == null) {
	            return false;
	        }
	        String reversed = reverse(input);

	        return input.equals(reversed);
	    }

	    public static String reverse(String str) {
	        if (str == null) {
	            return null;
	        }

	        if (str.length() <= 1) {
	            return str;
	        }

	        return reverse(str.substring(1)) + str.charAt(0);
	    }
	   
	    
}
