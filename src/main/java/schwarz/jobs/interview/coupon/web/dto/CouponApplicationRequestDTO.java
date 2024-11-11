package schwarz.jobs.interview.coupon.web.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import schwarz.jobs.interview.coupon.core.domain.Basket;

@Data
@Builder
public class CouponApplicationRequestDTO {

    @NotBlank
    private String code;

    @NotNull
    private Basket basket;

}
