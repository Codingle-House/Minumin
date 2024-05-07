package id.co.minumin.presentation.home.settings

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.google.api.services.drive.Drive
import dagger.hilt.android.AndroidEntryPoint
import id.co.minumin.R
import id.co.minumin.base.BaseFragment
import id.co.minumin.const.MinuminConstant
import id.co.minumin.const.MinuminConstant.BACKUP_DRIVE
import id.co.minumin.const.MinuminConstant.MAN
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
import id.co.minumin.presentation.dialog.LoadingDialog
import id.co.minumin.presentation.dialog.MetricSelectionDialog
import id.co.minumin.presentation.dialog.UserSuggestionDialog
import id.co.minumin.presentation.dialog.WaterConsumptionDialog
import id.co.minumin.presentation.notification.NotificationActivity
import id.co.minumin.presentation.pro.ProActivity
import id.co.minumin.presentation.splashscreen.SplashscreenActivity
import id.co.minumin.presentation.widgetpreview.WidgetPreviewActivity
import id.co.minumin.util.DateTimeUtil
import id.co.minumin.util.DateTimeUtil.DEFAULT_TIME
import id.co.minumin.util.DateTimeUtil.DEFAULT_TIME_FULL
import id.co.minumin.util.DriveFileUtil
import id.co.minumin.util.DriveServiceHelper
import id.co.minumin.util.LocaleHelper
import ir.androidexception.roomdatabasebackupandrestore.Backup
import ir.androidexception.roomdatabasebackupandrestore.Restore
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
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
                        .secretKey(MinuminConstant.SECRET_KEY)
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

    private val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(requireContext())
    }

    private var userRegisterDto: UserRegisterDto? = null

    private var filePath: String? = null
    private var selectedAction: Int = -1

    private var selectedLanguageDto: LanguageDto = ENGLISH
    private var purchaseStatus: Boolean = false

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
            observePurchaseStatus().onResult { handlePurchaseStatusLiveData(it) }
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

        binding.settingsTextviewWidget.setOnClickListener {
            checkPurchaseStatus {
                startActivity(Intent(requireContext(), WidgetPreviewActivity::class.java))
                activity?.overridePendingTransition(
                    R.anim.anim_fade_in,
                    R.anim.anim_fade_out
                )
            }
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
            getPurchaseStatus()
        }
    }


    private fun handleOtherInformationListener() {
        binding.settingsTextviewReport.setOnClickListener {
            composeEmail(arrayOf(MinuminConstant.SUPPORT_EMAIL), MinuminConstant.SUPPORT_SUBJECT)
        }

        binding.settingsTextviewSuggestion.setOnClickListener {
            UserSuggestionDialog(requireContext()).apply {
                setListener {

                }
                show()
            }
        }

        binding.settingsTextviewDrivebackup.setOnClickListener {
            checkPurchaseStatus {
                checkStoragePermission {
                    settingsViewModel.doBackup(BACKUP_DRIVE)
                }
            }
        }

        binding.settingsTextviewLocalbackup.setOnClickListener {
            checkPurchaseStatus {
                checkStoragePermission {
                    settingsViewModel.doBackup(MinuminConstant.BACKUP_LOCAL)
                }
            }
        }

        binding.settingsTextviewDriverestore.setOnClickListener {
            checkPurchaseStatus {
                checkStoragePermission {
                    selectedAction = MinuminConstant.ACTION_RESTORE
                    requestSignIn()
                }
            }
        }

        binding.settingsTextviewLocalrestore.setOnClickListener {
            checkPurchaseStatus {
                checkStoragePermission {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                        .addCategory(Intent.CATEGORY_OPENABLE)
                        .setType(MIME_TXT)
                        .setAction(Intent.ACTION_GET_CONTENT)

                    filePickerResult.launch(intent)
                }
            }
        }

        binding.settingsTextviewAbout.setOnClickListener {
            val intent = Intent(requireContext(), AboutUsActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out)
        }

        binding.settingsTextviewRate.setOnClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=${activity?.packageName}")
                    )
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=${activity?.packageName}")
                    )
                )
            }
        }
    }

    private fun composeEmail(
        addresses: Array<String?>?,
        subject: String?
    ) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, addresses)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        if (activity?.packageManager?.let { intent.resolveActivity(it) } != null) {
            startActivity(intent)
        }
    }

    @AfterPermissionGranted(Permission.STORAGE)
    private fun checkStoragePermission(onHasPermission: () -> Unit) {
        val perms = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (EasyPermissions.hasPermissions(requireContext(), *perms)) {
            onHasPermission.invoke()
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                this,
                "",
                Permission.STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
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
                .secretKey(MinuminConstant.SECRET_KEY)
                .onWorkFinishListener { success, message ->
                    if (!success) {
                        context?.showToast(message)
                        return@onWorkFinishListener
                    }
                    when (data.first) {
                        MinuminConstant.BACKUP_LOCAL -> {
                            BackupSuccessDialog.newInstance(requireContext()).apply {
                                setListener { startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)) }
                                show()
                            }
                        }

                        else -> {
                            selectedAction = MinuminConstant.ACTION_BACKUP
                            requestSignIn()
                        }
                    }
                }.execute()
        }
    }

    private fun requestSignIn() {

    }

    private fun handleSignInResult(result: Intent, onLogin: (googleDriveService: Drive) -> Unit) {

    }

    private fun handleUploadToGoogleDrive(drive: Drive) {
        loadingDialog.show()
        DriveServiceHelper(drive).apply {
            uploadFile(File(filePath.orEmpty()))?.addOnSuccessListener { dto ->
                loadingDialog.dismiss()
                context?.showToast(
                    getString(
                        R.string.settings_text_successdrive,
                        dto?.name.orEmpty()
                    )
                )
            }?.addOnFailureListener { ex ->
                loadingDialog.dismiss()
                context?.showToast(ex.localizedMessage.orEmpty())
            }
        }
    }

    private fun handlePurchaseStatusIcon() {
        val iconEnd = if (purchaseStatus) {
            R.drawable.general_ic_chevron_right
        } else {
            R.drawable.general_ic_lock
        }

        binding.settingsTextviewWidget.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.general_ic_crown, 0, iconEnd, 0
        )
        binding.settingsTextviewDrivebackup.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.general_ic_drive, 0, iconEnd, 0
        )
        binding.settingsTextviewLocalbackup.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.general_ic_phone, 0, iconEnd, 0
        )
        binding.settingsTextviewLocalrestore.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.general_ic_restore, 0, iconEnd, 0
        )
    }

    private fun handlePurchaseStatusLiveData(status: Boolean) {
        purchaseStatus = status
        handlePurchaseStatusIcon()
    }

    private fun checkPurchaseStatus(onPurchase: () -> Unit) {
        if (purchaseStatus) {
            onPurchase.invoke()
        } else {
            val intent = Intent(context, ProActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(
                R.anim.anim_fade_in,
                R.anim.anim_fade_out
            )
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