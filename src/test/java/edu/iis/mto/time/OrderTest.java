package edu.iis.mto.time;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Executable;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@ExtendWith(MockitoExtension.class)
class OrderTest {

    @Mock
    private Clock clockMock;

    @Test
    public void shouldThrowOrderExpiredExceptionWhenConfirmingxExpiredOrder() {
        // give
        Order order = new Order(clockMock);

        Instant instantAtSubmission = Instant.parse("2000-01-01T10:00:00Z");
        Instant instantAtConfirmationAfterExpiryTime = instantAtSubmission.plus(Order.VALID_PERIOD_HOURS + 1, ChronoUnit.HOURS);

        when(clockMock.getZone()).thenReturn(ZoneId.systemDefault());
        when(clockMock.instant()).thenReturn(instantAtSubmission)
                                 .thenReturn(instantAtConfirmationAfterExpiryTime);
        // when
        order.submit();

        try {
            order.confirm();

            // then
            fail("Exception OrderExpiredException not thrown");
        } catch (OrderExpiredException ignored) {}
    }

    @Test
    public void shouldChangeOrderStatusToCanceledWhenConfirmingExpiredOrder() {
        // give
        Order order = new Order(clockMock);

        Instant instantAtSubmission = Instant.parse("2000-01-01T10:00:00Z");
        Instant instantAtConfirmationAfterExpiryTime = instantAtSubmission.plus(Order.VALID_PERIOD_HOURS + 1, ChronoUnit.HOURS);

        when(clockMock.getZone()).thenReturn(ZoneId.systemDefault());
        when(clockMock.instant()).thenReturn(instantAtSubmission)
                                 .thenReturn(instantAtConfirmationAfterExpiryTime);

        Order.State expectedOrderState = Order.State.CANCELLED;
        // when
        order.submit();

        try {
            order.confirm();

            // then
            fail("Exception OrderExpiredException not thrown");
        } catch (OrderExpiredException ignored) {
            assertEquals(expectedOrderState, order.getOrderState());
        }
    }



}
