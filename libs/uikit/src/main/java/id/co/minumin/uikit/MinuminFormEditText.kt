package id.co.minumin.uikit

import Minumin.R
import Minumin.databinding.ViewFormeditBinding
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.InputType.TYPE_CLASS_NUMBER
import android.text.InputType.TYPE_CLASS_PHONE
import android.text.InputType.TYPE_CLASS_TEXT
import android.text.InputType.TYPE_NULL
import android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
import android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
import android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
import android.text.InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
import android.text.InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
import android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
import android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
import android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS
import android.text.InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE
import android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
import android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
import android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
import android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT
import android.text.InputType.TYPE_TEXT_VARIATION_FILTER
import android.text.InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE
import android.text.InputType.TYPE_TEXT_VARIATION_NORMAL
import android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
import android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME
import android.text.InputType.TYPE_TEXT_VARIATION_PHONETIC
import android.text.InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS
import android.text.InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE
import android.text.InputType.TYPE_TEXT_VARIATION_URI
import android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
import android.text.InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT
import android.text.InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS
import android.text.InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_UP
import android.widget.LinearLayout
import androidx.core.view.isGone
import id.co.minumin.core.ext.convertToMillis
import id.co.minumin.core.ext.getDrawableCompat
import id.co.minumin.core.ext.millisToDate
import id.co.minumin.core.ext.shakeAnimation
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.date
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.datetime
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.number
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.numberDecimal
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.numberPassword
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.numberSigned
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.phone
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.text
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.textAutoComplete
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.textAutoCorrect
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.textCapCharacters
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.textCapSentences
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.textCapWords
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.textEmailAddress
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.textEmailSubject
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.textFilter
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.textImeMultiLine
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.textLongMessage
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.textMultiLine
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.textNoSuggestions
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.textPassword
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.textPersonName
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.textPhonetic
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.textPostalAddress
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.textShortMessage
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.textUri
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.textVisiblePassword
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.textWebEditText
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.textWebEmailAddress
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.textWebPassword
import id.co.minumin.uikit.MinuminFormEditText.MinuminInputType.time
import java.util.*


/**
 * Created by pertadima on 29,January,2021
 */

class MinuminFormEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var dialog: Dialog? = null

    private val binding by lazy {
        ViewFormeditBinding.inflate(LayoutInflater.from(context), this, true)
    }

    init {
        attrs?.let {
            context.obtainStyledAttributes(it, R.styleable.MinuminFormEditText, 0, 0).apply {
                val errorText =
                    getString(R.styleable.MinuminFormEditText_minumin_text_error).orEmpty()
                val hintText =
                    getString(R.styleable.MinuminFormEditText_minumin_text_hint).orEmpty()
                val unitText =
                    getString(R.styleable.MinuminFormEditText_minumin_text_unit).orEmpty()
                val showError =
                    getBoolean(R.styleable.MinuminFormEditText_minumin_show_error, true)
                val inputType = getInteger(R.styleable.MinuminFormEditText_minumin_input_type, -1)
                val maxLength = getInteger(R.styleable.MinuminFormEditText_minumin_max_length, -1)
                setErrorText(errorText)
                setHintText(hintText)
                setUnitTest(unitText)
                setFormInputType(inputType)
                setDateInputTypeListener(inputType)
                binding.registerTextviewError.isGone = showError.not()
                if (maxLength > 0) {
                    binding.registerEdittextForm.filters =
                        arrayOf<InputFilter>(LengthFilter(maxLength))
                }
                recycle()
            }
        }
    }

    fun setText(text: String = "") {
        binding.registerEdittextForm.setText(text)
    }


    fun setHintText(hintText: String = "") {
        binding.registerEdittextForm.hint = hintText
    }

    fun setErrorText(errorText: String = "") {
        binding.registerTextviewError.text = errorText
    }

    fun setUnitTest(unitText: String = "") {
        binding.registerTextviewUnits.text = unitText
    }

    fun showError(isShow: Boolean) {
        binding.registerTextviewError.run {
            isGone = isShow.not()
            if (isShow) shakeAnimation()
        }
        binding.registerRelativelayoutForm.background =
            context.getDrawableCompat(if (isShow) R.drawable.uikit_bg_form_error else R.drawable.uikit_bg_form)

    }

    fun getFormText() = binding.registerEdittextForm.text.toString()

    private fun setFormInputType(code: Int) {
        val inputType = when (code) {
            text -> TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_NORMAL
            textCapCharacters -> TYPE_TEXT_FLAG_CAP_CHARACTERS
            textCapWords -> TYPE_TEXT_FLAG_CAP_WORDS
            textCapSentences -> TYPE_TEXT_FLAG_CAP_SENTENCES
            textAutoCorrect -> TYPE_TEXT_FLAG_AUTO_CORRECT
            textAutoComplete -> TYPE_TEXT_FLAG_AUTO_COMPLETE
            textMultiLine -> TYPE_TEXT_FLAG_MULTI_LINE
            textImeMultiLine -> TYPE_TEXT_FLAG_IME_MULTI_LINE
            textNoSuggestions -> TYPE_TEXT_FLAG_NO_SUGGESTIONS
            textUri -> TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_URI
            textEmailAddress -> TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            textEmailSubject -> TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_EMAIL_SUBJECT
            textShortMessage -> TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_SHORT_MESSAGE
            textLongMessage -> TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_LONG_MESSAGE
            textPersonName -> TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PERSON_NAME
            textPostalAddress -> TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_POSTAL_ADDRESS
            textPassword -> TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD
            textVisiblePassword -> TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            textWebEditText -> TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_WEB_EDIT_TEXT
            textFilter -> TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_FILTER
            textPhonetic -> TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PHONETIC
            textWebEmailAddress -> TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS
            textWebPassword -> TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_WEB_PASSWORD
            number -> TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
            numberSigned -> TYPE_CLASS_NUMBER or TYPE_NUMBER_FLAG_SIGNED
            numberDecimal -> TYPE_CLASS_NUMBER or TYPE_NUMBER_FLAG_DECIMAL
            numberPassword -> TYPE_CLASS_NUMBER or TYPE_NUMBER_VARIATION_PASSWORD
            phone -> TYPE_CLASS_PHONE
            else -> TYPE_NULL
        }
        binding.registerEdittextForm.inputType = inputType
    }

    @SuppressLint("SetTextI18n")
    private fun setDateInputTypeListener(code: Int) {
        when (code) {
            datetime, time, date -> {
                binding.registerEdittextForm.run {
                    setOnTouchListener { _, event ->
                        if (ACTION_UP == event.action) this@MinuminFormEditText.performClick()
                        true
                    }
                }
            }
        }
        setOnClickListener {
            when (code) {
                datetime -> {
                    showDatePickerDialog(onDateSetListener = { _, year, monthOfYear, dayOfMonth ->
                        showTimePicker(onTimePickerListener = { _, hour, minute ->
                            val minuteShow =
                                if (minute.toString().length < MINUTE_LENGTH) "0$minute" else minute.toString()

                            binding.registerEdittextForm.setText(
                                "${
                                    convertToMillis(dayOfMonth, monthOfYear, year)
                                        .millisToDate(DEFAULT_DATE_FORMAT).orEmpty()
                                } $hour:$minuteShow"
                            )

                        })
                    })
                }

                time -> {
                    showTimePicker(onTimePickerListener = { _, hour, minute ->
                        val minuteShow =
                            if (minute.toString().length < MINUTE_LENGTH) "0$minute" else minute.toString()
                        val hourShow =
                            if (hour.toString().length < MINUTE_LENGTH) "0$hour" else hour.toString()
                        binding.registerEdittextForm.setText("$hourShow:$minuteShow")
                        binding.registerTextviewUnits.text = if (hour < HOUR_DIVIDER) AM else PM
                    })
                }

                date -> {
                    showDatePickerDialog(onDateSetListener = { _, year, monthOfYear, dayOfMonth ->
                        binding.registerEdittextForm.setText(
                            convertToMillis(dayOfMonth, monthOfYear, year).millisToDate(
                                DEFAULT_DATE_FORMAT
                            ).orEmpty()
                        )
                    })
                }
            }
        }
    }

    private fun showDatePickerDialog(
        minDate: String? = null,
        onDateSetListener: DatePickerDialog.OnDateSetListener
    ) {
        val calender = Calendar.getInstance()
        dialog = DatePickerDialog(
            context,
            R.style.DialogSlideAnim,
            { view, year, monthOfYear, dayOfMonth ->
                onDateSetListener.onDateSet(view, year, monthOfYear, dayOfMonth)
            },
            calender.get(Calendar.YEAR),
            calender.get(Calendar.MONTH),
            calender.get(Calendar.DAY_OF_MONTH)
        )
        (dialog as DatePickerDialog).show()
    }

    private fun showTimePicker(
        onTimePickerListener: TimePickerDialog.OnTimeSetListener
    ) {
        val calender = Calendar.getInstance()
        val hourNow: Int = calender.get(Calendar.HOUR_OF_DAY)
        val minuteNow: Int = calender.get(Calendar.MINUTE)
        dialog = TimePickerDialog(
            context,
            R.style.DialogSlideAnim,
            { view, hour, minute ->
                onTimePickerListener.onTimeSet(view, hour, minute)
            },
            hourNow,
            minuteNow,
            true
        )
        (dialog as TimePickerDialog).show()
    }


    object MinuminInputType {
        const val text = 1
        const val textCapCharacters = 2
        const val textCapWords = 3
        const val textCapSentences = 4
        const val textAutoCorrect = 5
        const val textAutoComplete = 6
        const val textMultiLine = 7
        const val textImeMultiLine = 8
        const val textNoSuggestions = 9
        const val textUri = 10
        const val textEmailAddress = 11
        const val textEmailSubject = 12
        const val textShortMessage = 13
        const val textLongMessage = 14
        const val textPersonName = 15
        const val textPostalAddress = 16
        const val textPassword = 17
        const val textVisiblePassword = 18
        const val textWebEditText = 19
        const val textFilter = 20
        const val textPhonetic = 21
        const val textWebEmailAddress = 22
        const val textWebPassword = 23
        const val number = 24
        const val numberSigned = 25
        const val numberDecimal = 26
        const val numberPassword = 27
        const val phone = 28
        const val datetime = 29
        const val date = 30
        const val time = 31
    }

    companion object {
        private const val DEFAULT_DATE_FORMAT = "dd MMM yyyy"
        private const val AM = "AM"
        private const val PM = "PM"

        private const val HOUR_DIVIDER = 12
        private const val MINUTE_LENGTH = 2
    }
}