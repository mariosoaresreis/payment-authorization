package com.fiserv.payment.domain.ports;

import com.fiserv.payment.domain.model.Merchant;
import java.util.Optional;

/**
 * Input port for querying merchant information.
 */
public interface MerchantService {

    Optional<Merchant> getMerchantById(String merchantId);

    boolean isMerchantActive(String merchantId);
}

