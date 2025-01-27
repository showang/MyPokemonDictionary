package me.showang.mypokemon.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import me.showang.mypokemon.model.PokemonDetails

const val TABLE_NAME_POKEMON_DATA = "pokemon_data"

@Entity(tableName = TABLE_NAME_POKEMON_DATA, primaryKeys = ["id", "name"])
data class PokemonDataEntity(
    @ColumnInfo(name = "id", defaultValue = "")
    val id: Long,
    @ColumnInfo(name = "name", defaultValue = "")
    val name: String,
    @ColumnInfo(name = "image_url")
    val imageUrl: String,
    @ColumnInfo(name = "types_array")
    val typesArray: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "evolution_from")
    val evolutionFrom: String?,
) {
    companion object {
        fun from(detail: PokemonDetails): PokemonDataEntity {
            return PokemonDataEntity(
                id = detail.info.monsterId,
                name = detail.info.name,
                imageUrl = detail.info.imageUrl,
                typesArray = detail.types.joinToString(","),
                description = detail.description,
                evolutionFrom = detail.evolutionFrom
            )
        }
    }
}

fun PokemonDataEntity.toPokemonDetails(): PokemonDetails {
    return PokemonDetails(
        info = me.showang.mypokemon.model.PokemonInfo(
            monsterId = id,
            name = name,
            imageUrl = imageUrl
        ),
        types = typesArray.split(","),
        description = description,
        evolutionFrom = evolutionFrom
    )
}
