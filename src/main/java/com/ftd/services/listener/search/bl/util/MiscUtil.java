package com.ftd.services.listener.search.bl.util;

import com.ftd.services.listener.search.bl.dm.Context;
import com.ftd.services.listener.search.bl.exception.ValidationException;
import com.ftd.services.search.config.GlobalConstants;
import org.slf4j.Logger;

public class MiscUtil {
//    private static final Logger LOGGER = LoggerFactory.getLogger(MiscUtil.class);

    public static void throwCommonValidationException(Logger logger, Context context, String message) {
        String finalMessage = GlobalConstants.SITE_ID +
                GlobalConstants.COLON +
                context.getSiteId() +
                GlobalConstants.SPACE +
                GlobalConstants.PID +
                GlobalConstants.COLON +
                context.getPid() +
                GlobalConstants.SPACE +
                message;
        logger.error(finalMessage);
        throw new ValidationException(finalMessage);
    }
}
