package com.main.java.ngt.mts;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import com.main.java.ngt.mts.listener.JobCompletionNotificationListener;
import com.main.java.ngt.mts.model.FxMarketEvent;
import com.main.java.ngt.mts.model.FxMarketVolumeStore;
import com.main.java.ngt.mts.model.Trade;
import com.main.java.ngt.mts.processor.FxMarketEventProcessor;
import com.main.java.ngt.mts.reader.FxMarketEventReader;
import com.main.java.ngt.mts.writer.StockVolumeAggregator;



/**
 * The Class BatchConfiguration.
 * 
 * @author ashraf
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Bean
	public FxMarketVolumeStore fxMarketPricesStore() {
		return new FxMarketVolumeStore();
	}

	// FxMarketEventReader (Reader)
	@Bean
	public FxMarketEventReader fxMarketEventReader() {
		return new FxMarketEventReader();
	}

	// FxMarketEventProcessor (Processor)
	@Bean
	public FxMarketEventProcessor fxMarketEventProcessor() {
		return new FxMarketEventProcessor();
	}

	// StockVolumeAggregator (Writer)
	@Bean
	public StockVolumeAggregator stockVolumeAggregator() {
		return new StockVolumeAggregator();
	}

	// JobCompletionNotificationListener (File loader)
	@Bean
	public JobExecutionListener listener() {
		return new JobCompletionNotificationListener();
	}

	// Configure job step
	@Bean
	public Job fxMarketPricesETLJob() {
		return jobBuilderFactory.get("FxMarket Volume ETL Job").incrementer(new RunIdIncrementer()).listener(listener())
				.flow(etlStep()).end().build();
	}
	
	@Bean
	public TaskExecutor taskExecutor(){
	    SimpleAsyncTaskExecutor asyncTaskExecutor=new SimpleAsyncTaskExecutor("spring_batch");
	    asyncTaskExecutor.setConcurrencyLimit(5);
	    
	    return asyncTaskExecutor;
	}
    
	@Bean
	public Step etlStep() {
		return stepBuilderFactory.get("Ext	ract -> Transform -> Aggregate -> Load").<FxMarketEvent, Trade> chunk(10000)
				.reader(fxMarketEventReader()).processor(fxMarketEventProcessor())
				.writer(stockVolumeAggregator())
				.taskExecutor(taskExecutor()).throttleLimit(10).build();
	}

}
