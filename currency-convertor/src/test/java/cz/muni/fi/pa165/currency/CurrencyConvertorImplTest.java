package cz.muni.fi.pa165.currency;

import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CurrencyConvertorImplTest {

    private static Currency cur1, cur2;
    private static BigDecimal amount;
    @BeforeClass
    public static void beforeAll() {
        cur1 = Currency.getInstance(Locale.UK);
        cur2 = Currency.getInstance(Locale.US);
        amount = new BigDecimal("5.5");
    }

    @Test
    public void testConvert() throws ExternalServiceFailureException {
        BigDecimal exchangeRate = new BigDecimal("17.589751253");

        ExchangeRateTable rates = mock(ExchangeRateTable.class);
        when(rates.getExchangeRate(cur1, cur2)).thenReturn(exchangeRate);

        CurrencyConvertor converter = new CurrencyConvertorImpl(rates);
        BigDecimal expectedResult = exchangeRate.multiply(amount).setScale(2, RoundingMode.HALF_EVEN);

//      rounding is not correct
        assertThat(converter.convert(cur1, cur2, amount))
                .isEqualTo(expectedResult);
    }

    @Test
    public void testConvertWithNullSourceCurrency() {
        ExchangeRateTable rates = mock(ExchangeRateTable.class);

        CurrencyConvertor converter = new CurrencyConvertorImpl(rates);

        assertThatThrownBy(() -> converter.convert(null, cur2, amount))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testConvertWithNullTargetCurrency() {
        ExchangeRateTable rates = mock(ExchangeRateTable.class);

        CurrencyConvertor converter = new CurrencyConvertorImpl(rates);

        assertThatThrownBy(() -> converter.convert(cur1, null, amount))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testConvertWithNullSourceAmount() {
        ExchangeRateTable rates = mock(ExchangeRateTable.class);

        CurrencyConvertor converter = new CurrencyConvertorImpl(rates);

        assertThatThrownBy(() -> converter.convert(cur1, cur2, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testConvertWithUnknownCurrency() throws ExternalServiceFailureException {
        ExchangeRateTable rates = mock(ExchangeRateTable.class);
        when(rates.getExchangeRate(cur1, cur2)).thenReturn(null);

        CurrencyConvertor converter = new CurrencyConvertorImpl(rates);

        assertThatThrownBy(() -> converter.convert(cur1, cur2, amount))
                .isInstanceOf(UnknownExchangeRateException.class);
    }

    @Test
    public void testConvertWithExternalServiceFailure() throws ExternalServiceFailureException {
        ExchangeRateTable rates = mock(ExchangeRateTable.class);
        when(rates.getExchangeRate(cur1, cur2)).thenThrow(ExternalServiceFailureException.class);

        CurrencyConvertor converter = new CurrencyConvertorImpl(rates);

        assertThatThrownBy(() -> converter.convert(cur1, cur2, amount))
                .isInstanceOf(UnknownExchangeRateException.class);
    }

}
