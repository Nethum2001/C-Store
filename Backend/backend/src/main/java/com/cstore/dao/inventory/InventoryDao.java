package com.cstore.dao.inventory;

public interface InventoryDao {
    Integer findCountByVariantId(Long variantId);
}
