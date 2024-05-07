package id.co.minumin.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import id.co.minumin.data.dto.LanguageDto
import id.co.minumin.data.dto.LanguageDto.INDONESIA
import id.co.minumin.util.LocaleHelper.LanguageCode.ENGLISH
import id.co.minumin.util.LocaleHelper.LanguageCode.INDONESIA
import java.util.*
import javax.inject.Inject


/**
 * Created by pertadima on 18,February,2021
 */

class LocaleHelper @Inject constructor() {

    fun changeLocale(ctx: Context, language: String?): Configuration {
        val res: Resources = ctx.resources
        val conf: Configuration = res.configuration.apply {
            setLocale(Locale(language.orEmpty()))
        }
        res.updateConfiguration(conf, res.displayMetrics)
        return conf
    }

    fun convertLanguageDtoToCode(languageDto: LanguageDto) = when (languageDto) {
        LanguageDto.ENGLISH -> ENGLISH
        LanguageDto.INDONESIA -> INDONESIA
    }

    fun convertLanguageCodeToDto(languageCode: String) = when (languageCode) {
        ENGLISH -> LanguageDto.ENGLISH
        else -> LanguageDto.INDONESIA
    }

    object LanguageCode {
        const val INDONESIA = "ind"
        const val ENGLISH = "en"
    }
}