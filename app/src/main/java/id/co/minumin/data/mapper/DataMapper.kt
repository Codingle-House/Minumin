package id.co.minumin.data.mapper

import id.co.minumin.data.dto.*
import id.co.minumin.data.local.entity.BackupUserSettingEntity
import id.co.minumin.data.local.entity.DrinkEntity
import id.co.minumin.datastore.UserSetting
import id.co.minumin.util.DateTimeUtil

/**
 * Created by pertadima on 01,February,2021
 */

object DataMapper {

    fun convertToNavigationDto(navigation: UserSetting.Navigation): UserNavigationDto {
        return UserNavigationDto.values()[navigation.ordinal]
    }

    fun convertNavigationDtoToProto(navigation: UserNavigationDto): UserSetting.Navigation {
        return UserSetting.Navigation.values()[navigation.ordinal]
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
        return WeatherConditionDto.values()[weather.ordinal]
    }


    fun convertWeatherDtoToProto(weather: WeatherConditionDto): UserSetting.WeatherCondition {
        return UserSetting.WeatherCondition.values()[weather.ordinal]
    }

    fun convertToPhysycalDto(weather: UserSetting.PhysicalCondition): PhysicalActivitiesDto {
        return PhysicalActivitiesDto.values()[weather.ordinal]
    }


    fun convertPhysicalDtoToProto(weather: PhysicalActivitiesDto): UserSetting.PhysicalCondition {
        return UserSetting.PhysicalCondition.values()[weather.ordinal]
    }

    fun convertToCupDto(cup: UserSetting.GlassSelection): CupSelectionDto {
        return CupSelectionDto.values()[cup.ordinal]
    }

    fun convertCupDtoToProto(cup: CupSelectionDto): UserSetting.GlassSelection {
        return UserSetting.GlassSelection.values()[cup.ordinal]
    }

    fun convertDrinkDtoToEntity(drinkDto: DrinkDto): DrinkEntity {
        return DrinkEntity(
            id = drinkDto.id,
            date = DateTimeUtil.convertDate(drinkDto.date),
            time = drinkDto.time,
            consumption = drinkDto.consumption,
            isDeleted = drinkDto.isDeleted
        )
    }

    fun convertDrinkEntityToDto(drinkEntity: DrinkEntity): DrinkDto {
        return DrinkDto(
            id = drinkEntity.id,
            date = DateTimeUtil.convertDate(drinkEntity.date?.time ?: 0L).orEmpty(),
            time = drinkEntity.time,
            consumption = drinkEntity.consumption,
            isDeleted = drinkEntity.isDeleted
        )
    }

    fun convertToLanguageDto(language: UserSetting.Language): LanguageDto {
        return LanguageDto.values()[language.ordinal]
    }

    fun convertLanguageDtoToProto(language: LanguageDto): UserSetting.Language {
        return UserSetting.Language.values()[language.ordinal]
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