package me.showang.mypokemon.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import me.showang.mypokemon.utils.uppercaseFirstChar

@Parcelize
data class PokemonInfo(
    val monsterId: Long,
    val name: String,
    val imageUrl: String,
) : Parcelable {
    val displayName: String
        get() = name.uppercaseFirstChar()
}
