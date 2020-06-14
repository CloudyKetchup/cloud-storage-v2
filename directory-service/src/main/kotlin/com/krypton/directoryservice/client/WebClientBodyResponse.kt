package com.krypton.directoryservice.client

/**
 * Response wrapper for web client requests
 *
 * @param status	http status code
 * @param body		any [T] object, may be null
 * */
data class WebClientBodyResponse<T>(val status : Int, val body : T? = null)
