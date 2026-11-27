package se.magnus.api.composite.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceAddresses {
    private String cmp;
    private String pro;
    private String rev;
    private String rec;

    public ServiceAddresses() {
        cmp = null;
        pro = null;
        rev = null;
        rec = null;
    }

    public ServiceAddresses(
            String compositeAddress,
            String productAddress,
            String reviewAddress,
            String recommendationAddress) {

        this.cmp = compositeAddress;
        this.pro = productAddress;
        this.rev = reviewAddress;
        this.rec = recommendationAddress;
    }
}