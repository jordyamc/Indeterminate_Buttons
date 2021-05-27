package knf.tools.indeterminatebuttons

import android.widget.Checkable

internal interface IndeterminateCheckable : Checkable {
    var state: Boolean?
}