package schwarz.jobs.interview.coupon.core.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import schwarz.jobs.interview.coupon.core.domain.Coupon;
import schwarz.jobs.interview.coupon.core.repository.CouponRepository;
import schwarz.jobs.interview.coupon.core.domain.Basket;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponRepository couponRepository;

    public Basket apply(final Basket basket, final String code) {
        Coupon coupon = getCoupon(code).orElseThrow(() -> new NoSuchElementException("Coupon not found: " + code));
        validateBasket(basket, coupon);
        basket.applyDiscount(coupon.getDiscount());
        return basket;
    }

    private Optional<Coupon> getCoupon(final String code) {
        return couponRepository.findByCode(code);
    }

    private void validateBasket(Basket basket, Coupon coupon) {
        BigDecimal basketValue = basket.getValue();

        if (basketValue.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("Attempted to apply a discount to a basket with a negative value: {}", basketValue);
            throw new IllegalArgumentException("Basket value cannot be negative");
        }

        if (basketValue.compareTo(coupon.getMinBasketValue()) < 0) {
            log.warn("Basket value {} is less than the minimum required value {} for coupon code: {}",
                    basketValue, coupon.getMinBasketValue(), coupon.getCode());
            throw new IllegalArgumentException("Basket value does not meet the minimum required value for this coupon");
        }
    }

    public void createCoupon(final CouponDTO couponDTO) {
        Coupon coupon = Coupon.builder()
                .code(couponDTO.getCode().toLowerCase())
                .discount(couponDTO.getDiscount())
                .minBasketValue(couponDTO.getMinBasketValue())
                .build();

        couponRepository.save(coupon);
    }

    public List<Coupon> getCoupons(final List<String> codes) {
        return couponRepository.findByCodeIn(codes);
    }
}
