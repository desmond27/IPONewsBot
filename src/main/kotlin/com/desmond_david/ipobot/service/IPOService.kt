package com.desmond_david.ipobot.service

import com.desmond_david.ipobot.database.IpoDto
import java.time.LocalDate

interface IPOService {

    /**
     * Return the name of this service. This is used whenever the name of the service needs to be printed or logged.
     */
    fun getServiceName(): String

    /**
     * Returns the data from the external IPO service as a list of [IpoDto].
     */
    fun getData(): List<IpoDto>

    /**
     * Saves the data to a configured local datastore.
     */
    fun saveData(): Int?

    /**
     * Fetches a list of IPOs that are closing on the given date.
     */
    fun getIposClosingOn(localDate: LocalDate): List<IpoDto>

    /**
     * Fetches the next closing IPO from the current date.
     */
    fun getIpoClosingNext(): IpoDto?
}