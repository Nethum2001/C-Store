package com.cstore.domain.cart.update;

import com.cstore.dao.cart.CartDao;
import com.cstore.dao.inventory.InventoryDao;
import com.cstore.dto.CartItem_;
import com.cstore.exception.NoSuchVariantException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartUpdatingService {
    private final CartDao cartDao;
    private final InventoryDao inventoryDao;

    public Long addVariant(Long userId, CartItem_ cartItem_) {
        return null;
        /*List<Long> propertyIds = cartItem_.getPropertyIds();
        Set<Long> variantIds = new HashSet<Long>();

        for (Long propertyId : propertyIds) {
            List<Variant> variants = variantDao.findByProperty(propertyId);
            for (Variant variant : variants) {
                variantIds.add(variant.getVariantId());
            }
        }

        if (variantIds.size() != 1) {
            throw new NoSuchVariantException("No unique variant with the given set of properties found.");
        }
        Long variantId = variantIds.iterator().next();

        Integer stockCount = variantDao.countStocks(variantId);
        if (stockCount < cartItem_.getQuantity()) {
            throw new SparseStocksException("Only " + stockCount + " stocks available.");
        }

        int status = 0;
        while (status == 0) {
            status = cartDao.addToCart(userId, variantId, cartItem_.getQuantity());
        }

        return variantId;*/
    }

    public void removeVariant(Long userId, Long variantId) {
        if (cartDao.removeFromCart(userId, variantId) != 1) {
            throw new NoSuchVariantException("User with id " + userId + " has no variant of id " + variantId + " in his cart.");
        }
    }

    public List<CartRefreshment> refreshCart(Long userId, List<CartRefreshment> cartItems) {
        // TODO: Find if the user's cart contains the variants in CARTITEMS.

        List<CartRefreshment> erroneousCartItems = new ArrayList<>();

        for (CartRefreshment cartRefreshment : cartItems) {
            Integer availableCount = inventoryDao.findCountByVariantId(cartRefreshment.getVariantId());

            if (availableCount < cartRefreshment.getCount()) {
                cartRefreshment.setCount(availableCount);
                erroneousCartItems.add(cartRefreshment);
            }
        }

        return erroneousCartItems;
    }
}