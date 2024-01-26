package com.nullpointer.nullsiteadmin.exception

sealed class NullAppException : Exception() {

    sealed class AuthException : NullAppException() {
        data object UserNotFound : NullAppException()
        data object Authenticated : NullAppException()
    }

    data object NoInternetConnection : NullAppException()
    data object ServerTimeOut : NullAppException()

}