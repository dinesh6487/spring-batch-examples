package com.main.java.ngt.mts.writer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.main.java.ngt.mts.model.FxMarketVolumeStore;
import com.main.java.ngt.mts.model.StockVolume;
import com.main.java.ngt.mts.model.Trade;


/**
 * The Class StockVolumeAggregator.
 * 
 * @author ashraf
 */
public class StockVolumeAggregator implements ItemWriter<Trade> {

	@Autowired
	private FxMarketVolumeStore fxMarketVolumeStore;

	private static final Logger log = LoggerFactory.getLogger(StockVolumeAggregator.class);

	@Override
	public void write(List<? extends Trade> trades) throws Exception {
		trades.forEach(t -> {
			if (fxMarketVolumeStore.containsKey(t.getStock())) {
				StockVolume stockVolume = fxMarketVolumeStore.get(t.getStock());
				long newVolume = stockVolume.getVolume() + t.getShares();
				// Increment stock volume
				stockVolume.setVolume(newVolume);
			} else {
				log.trace("Adding new stock {}", t.getStock());
				fxMarketVolumeStore.put(t.getStock(),
						new StockVolume(t.getStock(), t.getShares()));
			}
		});
	}

}
