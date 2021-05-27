package knf.tools.indeterminatebuttons

import android.content.Context
import android.os.Build
import android.os.Parcelable
import android.util.AttributeSet
import android.view.ViewDebug.ExportedProperty
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatCheckBox


/**
 * A CheckBox with additional 3rd "indeterminate" state.
 * By default it is in "determinate" (checked or unchecked) state.
 * @author Svetlozar Kostadinov (sevarbg@gmail.com)
 */
class IndeterminateCheckBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.checkboxStyle
) : AppCompatCheckBox(context, attrs, defStyleAttr), IndeterminateCheckable {
    private var mIndeterminate = false

    @Transient
    private var mBroadcasting = false

    @Transient
    private var mOnStateChangedListener: OnStateChangedListener? = null

    /**
     * Interface definition for a callback to be invoked when the checked state changed.
     */
    interface OnStateChangedListener {
        /**
         * Called when the indeterminate state has changed.
         *
         * @param checkBox The checkbox view whose state has changed.
         * @param newState The new state of checkBox. Value meanings:
         * null = indeterminate state
         * true = checked state
         * false = unchecked state
         */
        fun onStateChanged(checkBox: IndeterminateCheckBox?, @Nullable newState: Boolean?)
    }

    override fun toggle() {
        if (mIndeterminate) {
            isChecked = true
        } else {
            super.toggle()
        }
    }

    override fun setChecked(checked: Boolean) {
        val checkedChanged = isChecked != checked
        super.setChecked(checked)
        val wasIndeterminate = isIndeterminate
        setIndeterminateImpl(false, false)
        if (wasIndeterminate || checkedChanged) {
            notifyStateListener()
        }
    }

    @get:ExportedProperty
    var isIndeterminate: Boolean
        get() = mIndeterminate
        set(indeterminate) {
            setIndeterminateImpl(indeterminate, true)
        }

    private fun setIndeterminateImpl(indeterminate: Boolean, notify: Boolean) {
        if (mIndeterminate != indeterminate) {
            mIndeterminate = indeterminate
            refreshDrawableState()
            if (notify) {
                /*notifyViewAccessibilityStateChangedIfNeeded(
                        AccessibilityEvent.CONTENT_CHANGE_TYPE_UNDEFINED); */
                notifyStateListener()
            }
        }
    }

    @get:ExportedProperty
    override var state: Boolean?
        get() = if (mIndeterminate) null else isChecked
        set(state) {
            if (state != null) {
                isChecked = state
            } else {
                isIndeterminate = true
            }
        }

    /**
     * Register a callback to be invoked when the indeterminate or checked state changes.
     * The standard `OnCheckedChangedListener` will still be called prior to
     * OnStateChangedListener.
     *
     * @param listener the callback to call on indeterminate or checked state change
     */
    fun setOnStateChangedListener(listener: OnStateChangedListener?) {
        mOnStateChangedListener = listener
    }

    override fun getAccessibilityClassName(): CharSequence = IndeterminateCheckBox::class.java.name

    private fun notifyStateListener() {
        // Avoid infinite recursions if state is changed from a listener
        if (mBroadcasting) {
            return
        }
        mBroadcasting = true
        if (mOnStateChangedListener != null) {
            mOnStateChangedListener!!.onStateChanged(this, state)
        }
        mBroadcasting = false
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState: IntArray = super.onCreateDrawableState(extraSpace + 1)
        if (state == null) {
            mergeDrawableStates(drawableState, INDETERMINATE_STATE_SET)
        }
        return drawableState
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState: Parcelable? = super.onSaveInstanceState()
        val ss = IndeterminateSavedState(superState)
        ss.indeterminate = mIndeterminate
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val ss: IndeterminateSavedState = state as IndeterminateSavedState

        // This temporarily disallows our callback notification, we will call it below if needed
        mBroadcasting = true
        super.onRestoreInstanceState(ss.superState)
        mBroadcasting = false
        mIndeterminate = ss.indeterminate
        // Both "indeterminate" and "checked" state are considered "excited" states. "Excited" state
        // is state that is different from the default "unchecked". On view restoration CompoundButton
        // notifies for change if the restored state is non-default. So, we will do the same for our merged state.
        if (mIndeterminate || isChecked) {
            notifyStateListener()
        }
    }

    companion object {
        private val INDETERMINATE_STATE_SET = intArrayOf(
            R.attr.state_indeterminate
        )
    }

    init {
        if (Build.VERSION.SDK_INT >= 23) {
            setButtonDrawable(R.drawable.btn_checkmark)
        } else {
            //setSupportButtonTintList(ContextCompat.getColorStateList(context, R.color.control_checkable_material));
            buttonDrawable = Utils.tintDrawable(this, R.drawable.btn_checkmark)
        }
        val a = context.obtainStyledAttributes(attrs, R.styleable.IndeterminateCheckable)
        try {
            // Read the XML attributes
            val indeterminate = a.getBoolean(
                R.styleable.IndeterminateCheckable_indeterminate, false
            )
            if (indeterminate) {
                isIndeterminate = true
            }
        } finally {
            a.recycle()
        }
    }
}