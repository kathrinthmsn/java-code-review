package schwarz.jobs.interview.coupon.web;


import java.util.List;
import java.util.NoSuchElementException;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import schwarz.jobs.interview.coupon.core.domain.Coupon;
import schwarz.jobs.interview.coupon.core.services.CouponService;
import schwarz.jobs.interview.coupon.core.domain.Basket;
import schwarz.jobs.interview.coupon.web.dto.CouponApplicationRequestDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponRequestDTO;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
@Slf4j
public class CouponResource {

    private final CouponService couponService;


    //@ApiOperation(value = "Applies currently active promotions and coupons from the request to the requested Basket - Version 1")
    @PostMapping(value = "/apply")
    public ResponseEntity<Basket> apply(
            //@ApiParam(value = "Provides the necessary basket and customer information required for the coupon application", required = true)
            @RequestBody @Valid final CouponApplicationRequestDTO applicationRequestDTO) {

        log.info("Applying coupon with code: {}", applicationRequestDTO.getCode());

        try {
            Basket basket = couponService.apply(applicationRequestDTO.getBasket(), applicationRequestDTO.getCode());
            log.info("Successfully applied coupon with code: {}", applicationRequestDTO.getCode());
            return ResponseEntity.ok(basket);
        } catch (NoSuchElementException e) {
            log.warn("Coupon not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid argument: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Unexpected error while applying coupon: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid final CouponDTO couponDTO) {
        couponService.createCoupon(couponDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<Coupon> getCoupons(@Valid final CouponRequestDTO couponRequestDTO) {
        return couponService.getCoupons(couponRequestDTO.getCodes());
    }
}
