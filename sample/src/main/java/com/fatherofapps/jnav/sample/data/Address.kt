package com.fatherofapps.jnav.sample.data

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    val id: Int = -1,
    val street: String = ""
): Parcelable{
    override fun toString(): String {
        val adapter = namiNavParameterAdapter()
        return adapter.toJson(this)
    }

    companion object {
        fun from(value: String): Address {
            val adapter = namiNavParameterAdapter()
            return adapter.fromJson(value) ?: Address()
        }

        fun namiNavParameterAdapter(): JsonAdapter<Address> =
            Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                .adapter(Address::class.java)
    }
}

class AddressNavType: NavType<Address>(isNullableAllowed = true) {
    override fun get(bundle: Bundle, key: String): Address? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): Address {
        return Address.from(value)
    }

    override fun put(bundle: Bundle, key: String, value: Address) {
        bundle.putParcelable(key, value)
    }
}