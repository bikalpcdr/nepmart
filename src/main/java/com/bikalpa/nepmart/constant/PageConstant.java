package com.bikalpa.nepmart.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageConstant {

    /* Pagination defaults */
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 10;

    /* Pagination bounds */
    public static final int MIN_SIZE = 1;
    public static final int MAX_SIZE = 100;
}
