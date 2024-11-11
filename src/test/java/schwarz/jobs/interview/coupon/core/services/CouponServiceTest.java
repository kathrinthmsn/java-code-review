package schwarz.jobs.interview.coupon.core.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import schwarz.jobs.interview.coupon.core.domain.Coupon;
import schwarz.jobs.interview.coupon.core.repository.CouponRepository;
import schwarz.jobs.interview.coupon.core.domain.Basket;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;

@ExtendWith(SpringExtension.class)
public class CouponServiceTest {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;

    @Test
    public void applyCouponSuccessfullyValueBiggerThanDiscount() {
        //Given
        final Basket basket = Basket.builder()
                .value(BigDecimal.valueOf(100))
                .build();
        when(couponRepository.findByCode("1111")).thenReturn(Optional.of(Coupon.builder()
                .code("1111")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build()));
        //When
        Basket updatedBasket = couponService.apply(basket, "1111");
        //Then
        assertThat(updatedBasket.getAppliedDiscount()).isEqualTo(BigDecimal.TEN);
        assertThat(updatedBasket.getValue()).isEqualTo(BigDecimal.valueOf(90));
    }

    @Test
    public void applyCouponSuccessfullyDiscountBiggerThanValue() {
        //Given
        final Basket basket = Basket.builder()
                .value(BigDecimal.valueOf(5))
                .build();
        when(couponRepository.findByCode("1111")).thenReturn(Optional.of(Coupon.builder()
                .code("1111")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(1))
                .build()));
        //When
        Basket updatedBasket = couponService.apply(basket, "1111");
        //Then
        assertThat(updatedBasket.getAppliedDiscount()).isEqualTo(BigDecimal.valueOf(5));
        assertThat(updatedBasket.getValue()).isEqualTo(BigDecimal.valueOf(0));
    }

    @Test
    public void shouldThrowExceptionWhenBasketValueIsLessThanMinBasketValue() {
        Basket basket = Basket.builder()
                .value(BigDecimal.valueOf(10))
                .build();
        when(couponRepository.findByCode("1111")).thenReturn(Optional.of(Coupon.builder()
                .code("1111")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build()));

        assertThatThrownBy(() -> couponService.apply(basket, "1111"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Basket value does not meet the minimum required value for this coupon");
    }

    @Test
    public void shouldThrowExceptionWhenBasketValueIsNegative() {
        Basket basket = Basket.builder()
                .value(BigDecimal.valueOf(-1))
                .build();
        when(couponRepository.findByCode("1111")).thenReturn(Optional.of(Coupon.builder()
                .code("1111")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build()));

        assertThatThrownBy(() -> couponService.apply(basket, "1111"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Basket value cannot be negative");
    }

    @Test
    public void shouldThrowExceptionWhenCouponNotFound() {
        Basket basket = Basket.builder()
                .value(BigDecimal.valueOf(100))
                .build();

        assertThatThrownBy(() -> couponService.apply(basket, "nonexistent"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Coupon not found: nonexistent");
    }

    @Test
    public void createCouponSuccessfully() {
        CouponDTO dto = CouponDTO.builder()
                .code("12345")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build();

        couponService.createCoupon(dto);

        verify(couponRepository, times(1)).save(any());
    }

    @Test
    public void shouldReturnCouponsForValidCodes() {
        List<String> codes = Arrays.asList("1111", "1234");
        Coupon coupon1 = Coupon.builder()
                .code("1111")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build();
        Coupon coupon2 = Coupon.builder()
                .code("1234")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build();

        when(couponRepository.findByCodeIn(codes)).thenReturn(List.of(coupon1, coupon2));

        List<Coupon> returnedCoupons = couponService.getCoupons(codes);

        assertThat(returnedCoupons).hasSize(2);
        assertThat(returnedCoupons.get(0).getCode()).isEqualTo("1111");
        assertThat(returnedCoupons.get(1).getCode()).isEqualTo("1234");
    }

    @Test
    public void shouldReturnEmptyListWhenNoCouponsFound() {
        List<String> codes = Arrays.asList("1111", "1234");
        when(couponRepository.findByCodeIn(codes)).thenReturn(List.of());

        List<Coupon> returnedCoupons = couponService.getCoupons(codes);

        assertThat(returnedCoupons).isEmpty();
    }
}
