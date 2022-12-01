package com.boryans.sugarsyrupcalculator.ui

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun String.isValidHiveNumber(): Boolean {
  val input = this.trim()
  if (input.isBlank() || input.length > 5 ) return false
  return input.toInt() >= 1
}

fun String.isValidSyrupVolume(measurementType: MeasurementType): Boolean {
  val input = this.trim()
  if (input.isBlank() || input.length > 5 ) return false
  return if(measurementType == MeasurementType.MILLILITRES) {
    input.toInt() >= 50
  } else {
    input.toInt() >= 1
  }
}

fun String.shouldSetInput(): Boolean {
  val pattern = Regex("^[0-9]*$")
  return pattern.containsMatchIn(this) && this.length <= 5
}

/**
 * Hides keyboard for a given view.
 */
fun Context.hideKeyboard(view: View) {
  val inputManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
  inputManager.hideSoftInputFromWindow(view.windowToken, 0)
}