package knf.tools.indeterminatebuttons

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.view.View


/**
 * Created by sevar on 08.10.16.
 */
internal class IndeterminateSavedState : View.BaseSavedState {
    var indeterminate = false

    /**
     * Constructor called from [IndeterminateRadioButton.onSaveInstanceState]
     */
    constructor(superState: Parcelable?) : super(superState) {}

    /**
     * Constructor called from [.CREATOR]
     */
    private constructor(`in`: Parcel) : super(`in`) {
        indeterminate = `in`.readInt() != 0
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeInt(if (indeterminate) 1 else 0)
    }

    override fun toString(): String {
        return ("IndetermSavedState.SavedState{"
                + Integer.toHexString(System.identityHashCode(this))
                + " indeterminate=" + indeterminate + "}")
    }

    companion object {
        @JvmField val CREATOR: Creator<IndeterminateSavedState?> = object : Creator<IndeterminateSavedState?> {
            override fun createFromParcel(`in`: Parcel): IndeterminateSavedState {
                return IndeterminateSavedState(`in`)
            }

            override fun newArray(size: Int): Array<IndeterminateSavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}
