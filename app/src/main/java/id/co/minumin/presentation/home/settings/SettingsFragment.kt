package id.co.minumin.presentation.home.settings

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager.ACTION_VIEW_DOWNLOADS
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.Intent.ACTION_VIEW
import android.content.Intent.CATEGORY_OPENABLE
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.co.minumin.R
import id.co.minumin.base.BaseFragment
import id.co.minumin.const.MinuminConstant
import id.co.minumin.const.MinuminConstant.ACTION_BACKUP
import id.co.minumin.const.MinuminConstant.ACTION_RESTORE
import id.co.minumin.const.MinuminConstant.BACKUP_DRIVE
import id.co.minumin.const.MinuminConstant.BACKUP_LOCAL
import id.co.minumin.const.MinuminConstant.MAN
import id.co.minumin.const.MinuminConstant.SECRET_KEY
import id.co.minumin.const.MinuminConstant.SUPPORT_EMAIL
import id.co.minumin.const.MinuminConstant.SUPPORT_SUBJECT
import id.co.minumin.core.ext.showToast
import id.co.minumin.data.dto.LanguageDto
import id.co.minumin.data.dto.LanguageDto.ENGLISH
import id.co.minumin.data.dto.LanguageDto.INDONESIA
import id.co.minumin.data.dto.UserRegisterDto
import id.co.minumin.data.local.AppDatabase
import id.co.minumin.databinding.FragmentSettingsBinding
import id.co.minumin.presentation.about.AboutUsActivity
import id.co.minumin.presentation.dialog.BackupSuccessDialog
import id.co.minumin.presentation.dialog.BodyWeightDialog
import id.co.minumin.presentation.dialog.GenderSelectionDialog
import id.co.minumin.presentation.dialog.LanguageSelectionDialog
import id.co.minumin.presentation.dialog.MetricSelectionDialog
import id.co.minumin.presentation.dialog.UserSuggestionDialog
import id.co.minumin.presentation.dialog.WaterConsumptionDialog
import id.co.minumin.presentation.notification.NotificationActivity
import id.co.minumin.presentation.splashscreen.SplashscreenActivity
import id.co.minumin.util.DateTimeUtil
import id.co.minumin.util.DateTimeUtil.DEFAULT_TIME
import id.co.minumin.util.DateTimeUtil.DEFAULT_TIME_FULL
import id.co.minumin.util.DriveFileUtil
import id.co.minumin.util.LocaleHelper
import ir.androidexception.roomdatabasebackupandrestore.Backup
import ir.androidexception.roomdatabasebackupandrestore.Restore
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.hasPermissions
import java.util.*
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MINUTE
import javax.inject.Inject


/**
 * Created by pertadima on 31,January,2021
 */

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSettingsBinding
        get() = FragmentSettingsBinding::inflate

    private val settingsViewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var appDatabase: AppDatabase

    @Inject
    lateinit var localeHelper: LocaleHelper

    private val filePickerResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    val path = if (DriveFileUtil.isGoogleDriveUri(uri)) {
                        DriveFileUtil.getPath(requireContext(), uri)
                    } else {
                        uri.path?.replace("/document/raw:/", "")
                    }

                    Restore.Init()
                        .database(appDatabase)
                        .backupFilePath(path.orEmpty())
                        .secretKey(SECRET_KEY)
                        .onWorkFinishListener { success, message ->
                            if (success) {
                                settingsViewModel.doRestoreUserSetting()
                            } else {
                                context?.showToast(message)
                            }
                        }.execute()
                }
            }
        }

    private var userRegisterDto: UserRegisterDto? = null

    private var filePath: String? = null
    private var selectedAction: Int = -1

    private var selectedLanguageDto: LanguageDto = ENGLISH

    override fun onViewReady() {
        changeStatusBarTextColor(true)
        changeStatusBarColor(R.color.white)
        initUi()
        notificationListener()
        personalInformationListener()
        generalInformationListener()
        handleOtherInformationListener()
    }

    override fun observeViewModel() {
        with(settingsViewModel) {
            observeUserCondition().onResult { handleUserConditionLiveData(it) }
            observeLanguageDto().onResult { handleLanguageLiveData(it) }
            observeBackupFileResult().onResult { handleBackupFileLiveData(it) }
            observeRestoreSuccess().onResult { restartApp() }
            observeLanguageSwitchDto().onResult { handleLanguageSwitchLiveData(it) }
        }
    }

    private fun initUi() {
        OverScrollDecoratorHelper.setUpOverScroll(binding.settingsScrollview)
    }


    private fun generalInformationListener() = with(binding) {
        settingsRelativelayoutLanguage.setOnClickListener {
            val code = localeHelper.convertLanguageDtoToCode(selectedLanguageDto)
            LanguageSelectionDialog.newInstance(requireContext(), code).apply {
                setListener {
                    val languageDto = localeHelper.convertLanguageCodeToDto(it)
                    settingsViewModel.changeLanguage(languageDto)
                }
                show()
            }
        }

        settingsRelativelayoutMetric.setOnClickListener {
            MetricSelectionDialog.newInstance(requireContext()).show()
        }

        settingsRelativelayoutTotal.setOnClickListener {
            WaterConsumptionDialog.newInstance(
                requireContext(),
                userRegisterDto?.waterNeeds ?: 0
            ).apply {
                setListener {
                    val newRegisterData = userRegisterDto?.copy(waterNeeds = it)
                    newRegisterData?.let { data -> settingsViewModel.changeUserData(data) }
                }
                show()
            }
        }
    }

    private fun notificationListener() {
        binding.settingsTextviewNotification.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
            activity?.overridePendingTransition(
                R.anim.anim_fade_in,
                R.anim.anim_fade_out
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun personalInformationListener() {
        binding.settingsRelativelayoutTimeWakeup.setOnClickListener {
            showTimePicker(onTimePickerListener = { _, hour, minute ->
                val minuteShow =
                    if (minute.toString().length < MINUTE_LENGTH) "0$minute" else minute.toString()
                val hourShow =
                    if (hour.toString().length < MINUTE_LENGTH) "0$hour" else hour.toString()

                val newRegisterData = userRegisterDto?.copy(wakeUpTime = "$hourShow:$minuteShow")
                newRegisterData?.let { data -> settingsViewModel.changeUserData(data) }

            })
        }

        binding.settingsRelativelayoutTimeSleep.setOnClickListener {
            showTimePicker(onTimePickerListener = { _, hour, minute ->
                val minuteShow =
                    if (minute.toString().length < MINUTE_LENGTH) "0$minute" else minute.toString()
                val hourShow =
                    if (hour.toString().length < MINUTE_LENGTH) "0$hour" else hour.toString()

                val newRegisterData = userRegisterDto?.copy(sleepTime = "$hourShow:$minuteShow")
                newRegisterData?.let { data -> settingsViewModel.changeUserData(data) }

            })
        }

        binding.settingsRelativelayoutGender.setOnClickListener {
            GenderSelectionDialog.newInstance(
                requireContext(),
                userRegisterDto?.gender.orEmpty()
            ).apply {
                setListener {
                    val newRegisterData = userRegisterDto?.copy(gender = it)
                    newRegisterData?.let { data -> settingsViewModel.changeUserData(data) }
                }
                show()
            }
        }

        binding.settingsRelativelayoutWeight.setOnClickListener {
            BodyWeightDialog.newInstance(requireContext(), userRegisterDto?.weight ?: 0).apply {
                setListener {
                    val newRegisterData = userRegisterDto?.copy(weight = it)
                    newRegisterData?.let { data -> settingsViewModel.changeUserData(data) }
                }
                show()
            }
        }
    }

    private fun showTimePicker(
        onTimePickerListener: TimePickerDialog.OnTimeSetListener
    ) {
        val calender = Calendar.getInstance()
        val hourNow: Int = calender.get(HOUR_OF_DAY)
        val minuteNow: Int = calender.get(MINUTE)

        val dialog = TimePickerDialog(
            context,
            R.style.DialogSlideAnim,
            { view, hour, minute -> onTimePickerListener.onTimeSet(view, hour, minute) },
            hourNow,
            minuteNow,
            true
        )
        dialog.show()
    }

    private fun loadData() {
        with(settingsViewModel) {
            getUserData()
            getLanguageSelection()
        }
    }


    private fun handleOtherInformationListener() = with(binding) {
        settingsCardviewBackup.isGone = true
        settingsTextviewReport.setOnClickListener {
            composeEmail(arrayOf(SUPPORT_EMAIL), SUPPORT_SUBJECT)
        }

        settingsTextviewSuggestion.setOnClickListener {
            UserSuggestionDialog(requireContext()).apply {
                setListener {}
                show()
            }
        }

        settingsTextviewDrivebackup.setOnClickListener {
            checkStoragePermission { settingsViewModel.doBackup(BACKUP_DRIVE) }
        }

        settingsTextviewLocalbackup.setOnClickListener {
            checkStoragePermission { settingsViewModel.doBackup(BACKUP_LOCAL) }
        }

        settingsTextviewDriverestore.setOnClickListener {
            checkStoragePermission { selectedAction = ACTION_RESTORE }
        }

        settingsTextviewLocalrestore.setOnClickListener {
            checkStoragePermission {
                val intent = Intent(ACTION_GET_CONTENT)
                    .addCategory(CATEGORY_OPENABLE)
                    .setType(MIME_TXT)
                    .setAction(ACTION_GET_CONTENT)

                filePickerResult.launch(intent)
            }
        }

        settingsTextviewAbout.setOnClickListener {
            val intent = Intent(requireContext(), AboutUsActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out)
        }

        settingsTextviewRate.setOnClickListener {
            try {
                startActivity(
                    Intent(
                        ACTION_VIEW,
                        Uri.parse("market://details?id=${activity?.packageName}")
                    )
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=${activity?.packageName}")
                    )
                )
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun composeEmail(
        addresses: Array<String?>?,
        subject: String?
    ) {
        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, addresses)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            if (activity?.packageManager?.let { resolveActivity(it) } != null) {
                startActivity(this)
            }
        }
    }

    @AfterPermissionGranted(Permission.STORAGE)
    private fun checkStoragePermission(onHasPermission: () -> Unit) {
        val perms = arrayOf(
            WRITE_EXTERNAL_STORAGE,
            READ_EXTERNAL_STORAGE
        )
        if (hasPermissions(requireContext(), *perms)) {
            onHasPermission.invoke()
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                this,
                "",
                Permission.STORAGE,
                WRITE_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun handleUserConditionLiveData(userConditionDto: UserRegisterDto) {
        userRegisterDto = userConditionDto
        binding.settingsTextviewTotal.text =
            getString(R.string.general_text_ml_day, userConditionDto.waterNeeds.toString())
        binding.settingsTextviewGender.text = when (userConditionDto.gender) {
            MAN -> getString(R.string.register_text_man)
            else -> getString(R.string.register_text_woman)
        }
        binding.settingsTextviewWeight.text =
            getString(R.string.general_text_unit_kg, userConditionDto.weight.toString())
        binding.settingsTextviewWakeup.text = DateTimeUtil.changeDateTimeFormat(
            userConditionDto.wakeUpTime,
            DEFAULT_TIME,
            DEFAULT_TIME_FULL
        )
        binding.settingsTextviewSleep.text = DateTimeUtil.changeDateTimeFormat(
            userConditionDto.sleepTime,
            DEFAULT_TIME,
            DEFAULT_TIME_FULL
        )
    }

    private fun handleLanguageLiveData(languageDto: LanguageDto) {
        selectedLanguageDto = languageDto
        binding.settingsTextviewLanguage.text = when (languageDto) {
            ENGLISH -> getString(R.string.dialog_text_language_english)
            INDONESIA -> getString(R.string.dialog_text_language_indonesia)
        }
    }

    private fun handleLanguageSwitchLiveData(languageDto: LanguageDto) {
        selectedLanguageDto = languageDto
        localeHelper.changeLocale(
            requireContext(),
            localeHelper.convertLanguageDtoToCode(languageDto)
        )
        restartApp()
    }

    private fun handleBackupFileLiveData(data: Pair<Int, Boolean>) {
        if (data.second) {
            val fileName = String.format(
                MinuminConstant.BACKUP_DB_NAME,
                DateTimeUtil.getCurrentDateString(DateTimeUtil.BACKUP_DATE)
            )

            val downloadFolder =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
            filePath = "${downloadFolder}/$fileName"

            Backup.Init()
                .database(appDatabase)
                .path(downloadFolder)
                .fileName(fileName)
                .secretKey(SECRET_KEY)
                .onWorkFinishListener { success, message ->
                    if (!success) {
                        context?.showToast(message)
                        return@onWorkFinishListener
                    }
                    when (data.first) {
                        BACKUP_LOCAL -> {
                            BackupSuccessDialog.newInstance(requireContext()).apply {
                                setListener { startActivity(Intent(ACTION_VIEW_DOWNLOADS)) }
                                show()
                            }
                        }

                        else -> {
                            selectedAction = ACTION_BACKUP
                        }
                    }
                }.execute()
        }
    }

    private fun restartApp() {
        val intent = Intent(requireContext(), SplashscreenActivity::class.java)
        startActivity(intent)
        activity?.overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out)
        activity?.finishAffinity()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    object Permission {
        const val STORAGE = 101
    }


    companion object {
        private const val MINUTE_LENGTH = 2
        private const val MIME_TXT = "text/plain"
    }
}