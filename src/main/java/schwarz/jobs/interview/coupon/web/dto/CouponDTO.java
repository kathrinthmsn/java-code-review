package schwarz.jobs.interview.coupon.web.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class CouponDTO {

    @NotNull(message = "The discount cannot be null.")
    private BigDecimal discount;

    @NotBlank(message = "Coupon code cannot be blank.")
    private String code;

    private BigDecimal minBasketValue;

}
