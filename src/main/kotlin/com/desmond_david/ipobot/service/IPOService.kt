package com.desmond_david.ipobot.service

import com.desmond_david.ipobot.database.IpoDto
import java.time.LocalDate

interface IPOService {

    fun getServiceName(): String

    fun getData(): List<IpoDto>

    fun saveData(): Int?

    fun getIposClosingOn(localDate: LocalDate): List<IpoDto>
}