package com.ads.loadoutsmanager.data.database

import com.ads.loadoutsmanager.data.database.dao.ItemDao
import com.ads.loadoutsmanager.data.database.entity.ItemCosmeticsEntity
import com.ads.loadoutsmanager.data.database.entity.ItemEntity
import com.ads.loadoutsmanager.data.database.entity.LoadoutEntity
import com.ads.loadoutsmanager.data.database.entity.SubclassEntity
import com.ads.loadoutsmanager.data.model.*

/**
 * Extension functions to map between domain models and database entities
 */

// Item mappings
fun DestinyItem.toEntity(): ItemEntity {
    return ItemEntity(
        itemInstanceId = itemInstanceId,
        itemHash = itemHash,
        bucketHash = bucketHash,
        location = location,
        iconUrl = iconUrl,
        transferStatus = transferStatus,
        lockable = lockable,
        state = state,
        cosmetics = cosmetics?.let {
            ItemCosmeticsEntity(
                ornamentHash = it.ornamentHash,
                shaderHash = it.shaderHash
            )
        }
    )
}

fun ItemEntity.toDomain(): DestinyItem {
    return DestinyItem(
        itemInstanceId = itemInstanceId,
        itemHash = itemHash,
        bucketHash = bucketHash,
        location = location,
        iconUrl = iconUrl,
        transferStatus = transferStatus,
        lockable = lockable,
        state = state,
        cosmetics = cosmetics?.let {
            ItemCosmetics(
                ornamentHash = it.ornamentHash,
                shaderHash = it.shaderHash
            )
        }
    )
}

// Subclass mappings
fun SubclassInfo.toEntity(): SubclassEntity {
    return SubclassEntity(
        subclassHash = subclassHash,
        damageType = damageType,
        superHash = superHash,
        aspectHashes = aspectHashes.joinToString(","),
        fragmentHashes = fragmentHashes.joinToString(","),
        grenadeHash = grenadeHash,
        meleeHash = meleeHash,
        classAbilityHash = classAbilityHash
    )
}

fun SubclassEntity.toDomain(): SubclassInfo {
    return SubclassInfo(
        subclassHash = subclassHash,
        damageType = damageType,
        superHash = superHash,
        aspectHashes = aspectHashes?.split(",")?.mapNotNull { it.toLongOrNull() } ?: emptyList(),
        fragmentHashes = fragmentHashes?.split(",")?.mapNotNull { it.toLongOrNull() } ?: emptyList(),
        grenadeHash = grenadeHash,
        meleeHash = meleeHash,
        classAbilityHash = classAbilityHash
    )
}

// Loadout mappings
fun DestinyLoadout.toEntity(): LoadoutEntity {
    return LoadoutEntity(
        id = id,
        name = name,
        description = description,
        characterId = characterId,
        itemIds = equipment.joinToString(",") { it.itemInstanceId },
        subclass = subclass?.toEntity(),
        isEquipped = isEquipped,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

suspend fun LoadoutEntity.toDomain(itemDao: ItemDao): DestinyLoadout {
    val itemIdsList = itemIds.split(",").filter { it.isNotEmpty() }
    val items = itemDao.getItems(itemIdsList).map { it.toDomain() }
    
    return DestinyLoadout(
        id = id,
        name = name,
        description = description,
        characterId = characterId,
        equipment = items,
        subclass = subclass?.toDomain(),
        isEquipped = isEquipped,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
