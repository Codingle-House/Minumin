package id.co.minumin.data.preference

import android.content.Context
import androidx.datastore.dataStore
import id.co.minumin.data.dto.BackupUserSettingDto
import id.co.minumin.data.dto.CupSelectionDto
import id.co.minumin.data.dto.LanguageDto
import id.co.minumin.data.dto.PhysicalActivitiesDto
import id.co.minumin.data.dto.UserNavigationDto
import id.co.minumin.data.dto.UserRegisterDto
import id.co.minumin.data.dto.WeatherConditionDto
import id.co.minumin.data.mapper.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Created by pertadima on 31,January,2021
 */

class UserPreferenceManager(private val context: Context, private val dataMapper: DataMapper) {

    private val Context.dataStore by dataStore(
        fileName = "user_setting_prefs",
        serializer = UserSerializer,
    )

    suspend fun updateUserNavigation(navigation: UserNavigationDto) {
        context.dataStore.updateData { user ->
            user.toBuilder().setNavigation(dataMapper.convertNavigationDtoToProto(navigation))
                .build()
        }
    }

    fun getUserNavigation() = context.dataStore.data.map {
        dataMapper.convertToNavigationDto(it.navigation)
    }

    suspend fun registerUser(userRegisterDto: UserRegisterDto) {
        context.dataStore.updateData { user ->
            user.toBuilder().apply {
                gender = userRegisterDto.gender
                sleepTime = userRegisterDto.sleepTime
                wakeUpTime = userRegisterDto.wakeUpTime
                weight = userRegisterDto.weight
                waterNeeds = userRegisterDto.waterNeeds
                isNotificationActive = userRegisterDto.isNotificationActive
            }.build()
        }
    }

    fun getUserRegisterData() = context.dataStore.data.map {
        dataMapper.convertUserRegisterDto(it)
    }

    suspend fun updateWeatherCondition(weather: WeatherConditionDto): Flow<WeatherConditionDto> {
        context.dataStore.updateData { user ->
            user.toBuilder().apply {
                weatherCondition = dataMapper.convertWeatherDtoToProto(weather)
            }.build()
        }
        return context.dataStore.data.map {
            dataMapper.convertToWeatherDto(it.weatherCondition)
        }
    }

    fun getWeatherCondition() = context.dataStore.data.map {
        dataMapper.convertToWeatherDto(it.weatherCondition)
    }

    suspend fun updatePhysicalActivities(weather: PhysicalActivitiesDto): Flow<PhysicalActivitiesDto> {
        context.dataStore.updateData { user ->
            user.toBuilder().apply {
                physicalCondition = dataMapper.convertPhysicalDtoToProto(weather)
            }.build()
        }
        return context.dataStore.data.map {
            dataMapper.convertToPhysycalDto(it.physicalCondition)
        }
    }

    fun getPhysicalActivities() = context.dataStore.data.map {
        dataMapper.convertToPhysycalDto(it.physicalCondition)
    }


    suspend fun updateCupSelection(cup: CupSelectionDto): Flow<CupSelectionDto> {
        context.dataStore.updateData { user ->
            user.toBuilder().apply { glassSelection = dataMapper.convertCupDtoToProto(cup) }.build()
        }
        return context.dataStore.data.map { dataMapper.convertToCupDto(it.glassSelection) }
    }

    fun getCupSelection() = context.dataStore.data.map {
        dataMapper.convertToCupDto(it.glassSelection)
    }

    suspend fun updateLanguageSelection(language: LanguageDto) {
        context.dataStore.updateData { user ->
            user.toBuilder().setLanguage(dataMapper.convertLanguageDtoToProto(language)).build()
        }
    }

    fun getLanguageSelection() =
        context.dataStore.data.map { dataMapper.convertToLanguageDto(it.language) }

    fun getUserData() = context.dataStore.data.map { dataMapper.convertBackupUserSettingDto(it) }

    suspend fun restoreUserSetting(backupUserSettingDto: BackupUserSettingDto) {
        context.dataStore.updateData { user ->
            user.toBuilder().apply {
                gender = backupUserSettingDto.gender
                sleepTime = backupUserSettingDto.sleepTime
                wakeUpTime = backupUserSettingDto.wakeUpTime
                weight = backupUserSettingDto.weight
                waterNeeds = backupUserSettingDto.waterNeeds
                isNotificationActive = backupUserSettingDto.isNotificationActive
                navigation =
                    dataMapper.convertNavigationDtoToProto(UserNavigationDto.values()[backupUserSettingDto.navigation])
                language =
                    dataMapper.convertLanguageDtoToProto(LanguageDto.values()[backupUserSettingDto.language])
                weatherCondition =
                    dataMapper.convertWeatherDtoToProto(WeatherConditionDto.values()[backupUserSettingDto.weatherCondition])
                physicalCondition =
                    dataMapper.convertPhysicalDtoToProto(PhysicalActivitiesDto.values()[backupUserSettingDto.physicalCondition])
                glassSelection =
                    dataMapper.convertCupDtoToProto(CupSelectionDto.values()[backupUserSettingDto.glassSelection])
            }.build()
        }
    }

    fun getPurchaseStatus() = context.dataStore.data.map { it.isPro }

    suspend fun updatePurchaseStatus(purchaseStatus: Boolean) =
        context.dataStore.updateData { user ->
            user.toBuilder().apply { isPro = purchaseStatus }.build()
        }

    companion object {
        const val TAG = "UserPreferenceManager"
    }
}
