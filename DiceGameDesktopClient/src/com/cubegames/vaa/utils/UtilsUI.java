package com.cubegames.vaa.utils;

import com.cubegames.engine.domain.rest.responses.BasicResponse;
import com.cubegames.vaa.client.exceptions.ClientException;
import com.google.common.base.Strings;

public class UtilsUI {

    public static void checkServiceError(BasicResponse response) {
        if (response == null) {
            throw new ClientException("Empty response");
        }

        if (!Strings.isNullOrEmpty(response.getError())) {
            throw new ClientException(response.getError());
        }

        if (response.getErrorCode() != 0) {
            throw new ClientException("Error code: " + response.getErrorCode());
        }
    }

}
