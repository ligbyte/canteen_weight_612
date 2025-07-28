package com.stkj.plate.weight.base.ui.widget.transformerstip.gravity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;


@IntDef({
        TipGravity.TO_TOP_TO_START,
        TipGravity.TO_TOP_ALIGN_START,
        TipGravity.TO_TOP_CENTER,
        TipGravity.TO_TOP_ALIGN_END,
        TipGravity.TO_TOP_TO_END,

        TipGravity.ALIGN_TOP_TO_START,
        TipGravity.ALIGN_TOP_ALIGN_START,
        TipGravity.ALIGN_TOP_CENTER,
        TipGravity.ALIGN_TOP_ALIGN_END,
        TipGravity.ALIGN_TOP_TO_END,

        TipGravity.CENTER_TO_START,
        TipGravity.CENTER_ALIGN_START,
        TipGravity.CENTER_CENTER,
        TipGravity.CENTER_ALIGN_END,
        TipGravity.CENTER_TO_END,

        TipGravity.ALIGN_BOTTOM_TO_START,
        TipGravity.ALIGN_BOTTOM_ALIGN_START,
        TipGravity.ALIGN_BOTTOM_CENTER,
        TipGravity.ALIGN_BOTTOM_ALIGN_END,
        TipGravity.ALIGN_BOTTOM_TO_END,

        TipGravity.TO_BOTTOM_TO_START,
        TipGravity.TO_BOTTOM_ALIGN_START,
        TipGravity.TO_BOTTOM_CENTER,
        TipGravity.TO_BOTTOM_ALIGN_END,
        TipGravity.TO_BOTTOM_TO_END
})
@Retention(RetentionPolicy.SOURCE)
public @interface TipGravity {
    int TO_TOP_TO_START = VerticalGravity.TO_TOP | HorizontalGravity.TO_START;
    int TO_TOP_ALIGN_START = VerticalGravity.TO_TOP | HorizontalGravity.ALIGN_START;
    int TO_TOP_CENTER = VerticalGravity.TO_TOP | HorizontalGravity.CENTER;
    int TO_TOP_ALIGN_END = VerticalGravity.TO_TOP | HorizontalGravity.ALIGN_END;
    int TO_TOP_TO_END = VerticalGravity.TO_TOP | HorizontalGravity.TO_END;

    int ALIGN_TOP_TO_START = VerticalGravity.ALIGN_TOP | HorizontalGravity.TO_START;
    int ALIGN_TOP_ALIGN_START = VerticalGravity.ALIGN_TOP | HorizontalGravity.ALIGN_START;
    int ALIGN_TOP_CENTER = VerticalGravity.ALIGN_TOP | HorizontalGravity.CENTER;
    int ALIGN_TOP_ALIGN_END = VerticalGravity.ALIGN_TOP | HorizontalGravity.ALIGN_END;
    int ALIGN_TOP_TO_END = VerticalGravity.ALIGN_TOP | HorizontalGravity.TO_END;

    int CENTER_TO_START = VerticalGravity.CENTER | HorizontalGravity.TO_START;
    int CENTER_ALIGN_START = VerticalGravity.CENTER | HorizontalGravity.ALIGN_START;
    int CENTER_CENTER = VerticalGravity.CENTER | HorizontalGravity.CENTER;
    int CENTER_ALIGN_END = VerticalGravity.CENTER | HorizontalGravity.ALIGN_END;
    int CENTER_TO_END = VerticalGravity.CENTER | HorizontalGravity.TO_END;

    int ALIGN_BOTTOM_TO_START = VerticalGravity.ALIGN_BOTTOM | HorizontalGravity.TO_START;
    int ALIGN_BOTTOM_ALIGN_START = VerticalGravity.ALIGN_BOTTOM | HorizontalGravity.ALIGN_START;
    int ALIGN_BOTTOM_CENTER = VerticalGravity.ALIGN_BOTTOM | HorizontalGravity.CENTER;
    int ALIGN_BOTTOM_ALIGN_END = VerticalGravity.ALIGN_BOTTOM | HorizontalGravity.ALIGN_END;
    int ALIGN_BOTTOM_TO_END = VerticalGravity.ALIGN_BOTTOM | HorizontalGravity.TO_END;

    int TO_BOTTOM_TO_START = VerticalGravity.TO_BOTTOM | HorizontalGravity.TO_START;
    int TO_BOTTOM_ALIGN_START = VerticalGravity.TO_BOTTOM | HorizontalGravity.ALIGN_START;
    int TO_BOTTOM_CENTER = VerticalGravity.TO_BOTTOM | HorizontalGravity.CENTER;
    int TO_BOTTOM_ALIGN_END = VerticalGravity.TO_BOTTOM | HorizontalGravity.ALIGN_END;
    int TO_BOTTOM_TO_END = VerticalGravity.TO_BOTTOM | HorizontalGravity.TO_END;
}
