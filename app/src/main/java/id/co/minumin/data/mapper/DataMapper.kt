package id.co.minumin.data.mapper

import id.co.minumin.data.dto.BackupUserSettingDto
import id.co.minumin.data.dto.CupSelectionDto
import id.co.minumin.data.dto.DrinkDto
import id.co.minumin.data.dto.LanguageDto
import id.co.minumin.data.dto.PhysicalActivitiesDto
import id.co.minumin.data.dto.UserNavigationDto
import id.co.minumin.data.dto.UserRegisterDto
import id.co.minumin.data.dto.WeatherConditionDto
import id.co.minumin.data.local.entity.BackupUserSettingEntity
import id.co.minumin.data.local.entity.DrinkEntity
import id.co.minumin.datastore.UserSetting
import id.co.minumin.util.DateTimeUtil.convertDate

/**
 * Created by pertadima on 01,February,2021
 */

object DataMapper {

    fun convertToNavigationDto(navigation: UserSetting.Navigation): UserNavigationDto {
        return UserNavigationDto.entries[navigation.ordinal]
    }

    fun convertNavigationDtoToProto(navigation: UserNavigationDto): UserSetting.Navigation {
        return UserSetting.Navigation.entries[navigation.ordinal]
    }

    fun convertUserRegisterDto(userSetting: UserSetting): UserRegisterDto {
        return UserRegisterDto(
            gender = userSetting.gender,
            weight = userSetting.weight,
            wakeUpTime = userSetting.wakeUpTime,
            sleepTime = userSetting.sleepTime,
            waterNeeds = userSetting.waterNeeds,
            isNotificationActive = userSetting.isNotificationActive
        )
    }

    fun convertToWeatherDto(weather: UserSetting.WeatherCondition): WeatherConditionDto {
        return WeatherConditionDto.entries[weather.ordinal]
    }


    fun convertWeatherDtoToProto(weather: WeatherConditionDto): UserSetting.WeatherCondition {
        return UserSetting.WeatherCondition.entries[weather.ordinal]
    }

    fun convertToPhysycalDto(weather: UserSetting.PhysicalCondition): PhysicalActivitiesDto {
        return PhysicalActivitiesDto.entries[weather.ordinal]
    }


    fun convertPhysicalDtoToProto(weather: PhysicalActivitiesDto): UserSetting.PhysicalCondition {
        return UserSetting.PhysicalCondition.entries[weather.ordinal]
    }

    fun convertToCupDto(cup: UserSetting.GlassSelection): CupSelectionDto {
        return CupSelectionDto.entries[cup.ordinal]
    }

    fun convertCupDtoToProto(cup: CupSelectionDto): UserSetting.GlassSelection {
        return UserSetting.GlassSelection.entries[cup.ordinal]
    }

    fun convertDrinkDtoToEntity(drinkDto: DrinkDto): DrinkEntity {
        return DrinkEntity(
            id = drinkDto.id,
            date = convertDate(drinkDto.date),
            time = drinkDto.time,
            consumption = drinkDto.consumption,
            isDeleted = drinkDto.isDeleted
        )
    }

    fun convertDrinkEntityToDto(drinkEntity: DrinkEntity): DrinkDto {
        return DrinkDto(
            id = drinkEntity.id,
            date = convertDate(drinkEntity.date?.time ?: 0L).orEmpty(),
            time = drinkEntity.time,
            consumption = drinkEntity.consumption,
            isDeleted = drinkEntity.isDeleted
        )
    }

    fun convertToLanguageDto(language: UserSetting.Language): LanguageDto {
        return LanguageDto.entries[language.ordinal]
    }

    fun convertLanguageDtoToProto(language: LanguageDto): UserSetting.Language {
        return UserSetting.Language.entries[language.ordinal]
    }

    fun convertBackupUserSettingDto(userSetting: UserSetting): BackupUserSettingDto {
        return BackupUserSettingDto(
            id = 0,
            gender = userSetting.gender,
            weight = userSetting.weight,
            wakeUpTime = userSetting.wakeUpTime,
            sleepTime = userSetting.sleepTime,
            waterNeeds = userSetting.waterNeeds,
            isNotificationActive = userSetting.isNotificationActive,
            navigation = userSetting.navigation.number,
            language = userSetting.language.number,
            weatherCondition = userSetting.weatherCondition.number,
            physicalCondition = userSetting.physicalCondition.number,
            glassSelection = userSetting.glassSelection.number
        )
    }

    fun convertBackupUserSettingEntity(userSetting: BackupUserSettingDto): BackupUserSettingEntity {
        return BackupUserSettingEntity(
            id = 0,
            gender = userSetting.gender,
            weight = userSetting.weight,
            wakeUpTime = userSetting.wakeUpTime,
            sleepTime = userSetting.sleepTime,
            waterNeeds = userSetting.waterNeeds,
            isNotificationActive = userSetting.isNotificationActive,
            navigation = userSetting.navigation,
            language = userSetting.language,
            weatherCondition = userSetting.weatherCondition,
            physicalCondition = userSetting.physicalCondition,
            glassSelection = userSetting.glassSelection
        )
    }

    fun convertBackupUserSettingEntityToDto(userSetting: BackupUserSettingEntity): BackupUserSettingDto {
        return BackupUserSettingDto(
            id = 0,
            gender = userSetting.gender,
            weight = userSetting.weight,
            wakeUpTime = userSetting.wakeUpTime,
            sleepTime = userSetting.sleepTime,
            waterNeeds = userSetting.waterNeeds,
            isNotificationActive = userSetting.isNotificationActive,
            navigation = userSetting.navigation,
            language = userSetting.language,
            weatherCondition = userSetting.weatherCondition,
            physicalCondition = userSetting.physicalCondition,
            glassSelection = userSetting.glassSelection
        )
    }
}