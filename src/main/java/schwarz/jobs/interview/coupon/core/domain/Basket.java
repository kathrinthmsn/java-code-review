package schwarz.jobs.interview.coupon.core.domain;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Basket {

    @NotNull
    private BigDecimal value;

    private BigDecimal appliedDiscount;

    public void applyDiscount(final BigDecimal discount) {
        if (discount.compareTo(this.value) > 0) {
            this.appliedDiscount = value;
            this.value = BigDecimal.ZERO;
        } else {
            this.value = this.value.subtract(discount);
            this.appliedDiscount = discount;
        }
    }

}
