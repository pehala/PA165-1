package cz.muni.fi.pa165.currency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

/**
 * This is base implementation of {@link CurrencyConvertor}.
 *
 * @author petr.adamek@embedit.cz
 */
public class CurrencyConvertorImpl implements CurrencyConvertor {

    private final ExchangeRateTable exchangeRateTable;
    private final Logger logger = LoggerFactory.getLogger(CurrencyConvertorImpl.class);

    public CurrencyConvertorImpl(ExchangeRateTable exchangeRateTable) {
        this.exchangeRateTable = exchangeRateTable;
    }

    @Override
    public BigDecimal convert(Currency sourceCurrency, Currency targetCurrency, BigDecimal sourceAmount) {
        if (sourceCurrency == null) {
            throw new IllegalArgumentException("Source currency cannot be null");
        }
        if (targetCurrency == null) {
            throw new IllegalArgumentException("Target currency cannot be null");
        }
        if (sourceAmount == null) {
            throw new IllegalArgumentException("Source amount cannot be null");
        }

        logger.trace(String.format("Converting amount %s Currency %s to Currency %s",
                sourceAmount.toString(), sourceCurrency.getCurrencyCode(), targetCurrency.getCurrencyCode()));
        try {
            BigDecimal rate = exchangeRateTable.getExchangeRate(sourceCurrency, targetCurrency);
            if (rate == null) {
                logger.warn(String.format("Unable to find exchange rate between %s and %s",
                        sourceCurrency, targetCurrency));
                throw new UnknownExchangeRateException("Error occurred when trying to retrieve exchange rate");
            }

            return rate.multiply(sourceAmount).setScale(2, RoundingMode.HALF_EVEN);
        } catch (ExternalServiceFailureException e) {
            logger.error(String.format("Error occurred when finding exchange rate between %s and %s",
                    sourceCurrency, targetCurrency));
            throw new UnknownExchangeRateException("Error occurred when trying to retrieve exchange rate");
        }
    }

}
