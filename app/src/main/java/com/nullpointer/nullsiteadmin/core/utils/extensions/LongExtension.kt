package com.nullpointer.nullsiteadmin.core.utils.extensions

import java.util.Date


fun Long.toDate(): Date {
    return Date(this)
}