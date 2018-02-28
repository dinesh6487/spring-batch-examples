package com.main.java.ngt.mts.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.main.java.ngt.mts.model.FxMarketEvent;
import com.main.java.ngt.mts.model.Trade;


/**
 * The Class FxMarketEventProcessor.
 * 
 * @author ashraf
 */
public class FxMarketEventProcessor implements ItemProcessor<FxMarketEvent, Trade> {

	private static final Logger log = LoggerFactory.getLogger(FxMarketEventProcessor.class);

	@Override
	public Trade process(final FxMarketEvent fxMarketEvent) throws Exception {

		final String stock = fxMarketEvent.getStock();
		final String time = fxMarketEvent.getTime();
		final double price = Double.valueOf(fxMarketEvent.getPrice());
		final long shares = Long.valueOf(fxMarketEvent.getShares());
		final Trade trade = new Trade(stock, time, price, shares);

		log.trace("Converting (" + fxMarketEvent + ") into (" + trade + ")");

		return trade;
	}

}
