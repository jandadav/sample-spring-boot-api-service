/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.sample.apiservice.wto;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * z/OS implementation calling the native, OS-linkage service WTO via
 * a "shared object" loaded at server runtime.
 */
@Profile("zos")
@Service
public class ZosWto implements Wto {
    private static String JNI_SHARED_LIBRARY = "wtojni";

    static {
        System.loadLibrary(ZosWto.JNI_SHARED_LIBRARY);
    }

    private native int wto(int id, String content);

    private String message; // String class variable set in JNI code

    public WtoResponse call(int id, String content) {
        int rc = wto(id, content);
        return new WtoResponse(id, content, rc, message);
    }
}
