package com.example.myhealthlife.ui.home;

import com.example.myhealthlife.R;
import com.example.myhealthlife.domain.model.TipoDato;

public class HomeCardMapper {

    public static HomeCardState from(
            TipoDato tipo
    ) {
        switch (tipo) {
            case ECG:
                return new HomeCardState(
                        R.drawable.heartbeat,
                        R.string.ecg,
                        "--",
                        ""
                );
        }
        return null;
    }
}
