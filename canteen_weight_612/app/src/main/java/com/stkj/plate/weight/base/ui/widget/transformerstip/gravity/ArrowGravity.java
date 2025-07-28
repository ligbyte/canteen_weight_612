package com.stkj.plate.weight.base.ui.widget.transformerstip.gravity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;


@IntDef({
        ArrowGravity.TO_TOP_ALIGN_START,
        ArrowGravity.TO_TOP_CENTER,
        ArrowGravity.TO_TOP_ALIGN_END,

        ArrowGravity.ALIGN_TOP_TO_START,
        ArrowGravity.ALIGN_TOP_TO_END,

        ArrowGravity.CENTER_TO_START,
        ArrowGravity.CENTER_TO_END,

        ArrowGravity.ALIGN_BOTTOM_TO_START,
        ArrowGravity.ALIGN_BOTTOM_TO_END,

        ArrowGravity.TO_BOTTOM_ALIGN_START,
        ArrowGravity.TO_BOTTOM_CENTER,
        ArrowGravity.TO_BOTTOM_ALIGN_END
})
@Retention(RetentionPolicy.SOURCE)
public @interface ArrowGravity {
    int TO_TOP_ALIGN_START = VerticalGravity.TO_TOP | HorizontalGravity.ALIGN_START;
    int TO_TOP_CENTER = VerticalGravity.TO_TOP | HorizontalGravity.CENTER;
    int TO_TOP_ALIGN_END = VerticalGravity.TO_TOP | HorizontalGravity.ALIGN_END;

    int ALIGN_TOP_TO_START = VerticalGravity.ALIGN_TOP | HorizontalGravity.TO_START;
    int ALIGN_TOP_TO_END = VerticalGravity.ALIGN_TOP | HorizontalGravity.TO_END;

    int CENTER_TO_START = VerticalGravity.CENTER | HorizontalGravity.TO_START;
    int CENTER_TO_END = VerticalGravity.CENTER | HorizontalGravity.TO_END;

    int ALIGN_BOTTOM_TO_START = VerticalGravity.ALIGN_BOTTOM | HorizontalGravity.TO_START;
    int ALIGN_BOTTOM_TO_END = VerticalGravity.ALIGN_BOTTOM | HorizontalGravity.TO_END;

    int TO_BOTTOM_ALIGN_START = VerticalGravity.TO_BOTTOM | HorizontalGravity.ALIGN_START;
    int TO_BOTTOM_CENTER = VerticalGravity.TO_BOTTOM | HorizontalGravity.CENTER;
    int TO_BOTTOM_ALIGN_END = VerticalGravity.TO_BOTTOM | HorizontalGravity.ALIGN_END;
}
