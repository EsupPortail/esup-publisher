package org.esupportail.publisher.web.rest;

import org.esupportail.publisher.web.rest.util.ISO8601LocalDateTimeXmlAdapter;
import org.esupportail.publisher.web.rest.util.RFC822LocalDateTimeXmlAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@RunWith(SpringJUnit4ClassRunner.class)
public class DateAdaptersTest {
    private final String RFC822_DATE = "Sun, 9 Feb 2020 11:10:12 +0100";
    private final String ISO8601_DATE = "2020-02-09T11:10:12+01:00";

    @Test
    public void testRFC822() throws Exception {
        Instant mydate = Instant.parse("2020-02-09T10:10:12.000Z");
        RFC822LocalDateTimeXmlAdapter adapter = new RFC822LocalDateTimeXmlAdapter();
        Assert.assertEquals(RFC822_DATE, adapter.marshal(mydate));

        Assert.assertEquals(mydate, adapter.unmarshal(RFC822_DATE));
    }

    @Test
    public void testISO8601() throws Exception {
        Instant mydate = Instant.parse("2020-02-09T10:10:12Z");
        ISO8601LocalDateTimeXmlAdapter adapter = new ISO8601LocalDateTimeXmlAdapter();
        Assert.assertEquals(ISO8601_DATE, adapter.marshal(mydate));

        Assert.assertEquals(mydate, adapter.unmarshal(ISO8601_DATE));
    }

    @Test
    public void testLocalDateToISO8601() throws Exception {
        ISO8601LocalDateTimeXmlAdapter adapter = new ISO8601LocalDateTimeXmlAdapter();
        Instant mydate = LocalDate.of(2020,02,10).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Assert.assertEquals("2020-02-10T00:00:00+01:00", adapter.marshal(mydate));
    }

}
