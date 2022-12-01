package com.boryans.sugarsyrupcalculator.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

  companion object {
    const val sugarFormulaOneToOne = 0.63
    const val waterFormulaOneToOne = 0.63

    const val sugarFormulaTwoToOne = 0.91
    const val waterFormulaTwoToOne = 0.45

    const val sugarFormulaThreeToTwo = 0.79
    const val waterFormulaThreeToTwo = 0.53
  }

  sealed class CalculationState {
    object MissingInfoState : CalculationState()
    data class SuccessCalculationState(val resultData: ResultData) : CalculationState()
  }

  var syrupRatioOptionsList = listOfNotNull(
    RatioType.ONE_TO_ONE,
    RatioType.TWO_TO_ONE,
    RatioType.THREE_TO_TWO,
  )

  var hivesNumber = mutableStateOf("")
  var syrupVolume = mutableStateOf("")
  var measurementType = mutableStateOf(MeasurementType.LITRES)
  val selectedRatioOption = mutableStateOf(syrupRatioOptionsList.first())

  private val _calculationState =
    MutableLiveData<CalculationState>(CalculationState.MissingInfoState)
  val calculationState: MutableLiveData<CalculationState> = _calculationState

  fun setHivesNumber(newNumber: String) {
    if (newNumber.trim().shouldSetInput()) {
      hivesNumber.value = newNumber
    }
  }

  fun setSyrupVolume(newVolume: String) {
    if (newVolume.trim().shouldSetInput()) {
      syrupVolume.value = newVolume
    }
  }

  fun setSelectedRatioOption(newOption: RatioType) {
    selectedRatioOption.value = newOption
  }

  fun setMeasurementType(measurement: MeasurementType) {
    measurementType.value = measurement
  }

  fun validateAndCalculateRatioResult() {
    when {
      !hivesNumber.value.isValidHiveNumber() || !syrupVolume.value.isValidSyrupVolume(
        measurementType.value
      ) -> {
        calculationState.value = CalculationState.MissingInfoState
        return
      }
      !hivesNumber.value.isValidHiveNumber() -> {
        calculationState.value = CalculationState.MissingInfoState
        return
      }
      !syrupVolume.value.isValidSyrupVolume(measurementType.value) -> {
        calculationState.value = CalculationState.MissingInfoState
        return
      }
    }

    when (selectedRatioOption.value) {
      RatioType.ONE_TO_ONE -> calculateOneToOneResult()
      RatioType.TWO_TO_ONE -> calculateTwoToOneResult()
      RatioType.THREE_TO_TWO -> calculateThreeToTwoResult()
    }
  }

  private fun calculateThreeToTwoResult() {
    val result = calculateResult(
      sugarConstant = sugarFormulaThreeToTwo,
      waterConstant = waterFormulaThreeToTwo
    )
    calculationState.value = CalculationState.SuccessCalculationState(result)
  }

  private fun calculateTwoToOneResult() {
    val result = calculateResult(
      sugarConstant = sugarFormulaTwoToOne,
      waterConstant = waterFormulaTwoToOne
    )
    calculationState.value = CalculationState.SuccessCalculationState(result)
  }

  private fun calculateOneToOneResult() {
    val result = calculateResult(
      sugarConstant = sugarFormulaOneToOne,
      waterConstant = waterFormulaOneToOne
    )
    calculationState.value = CalculationState.SuccessCalculationState(result)
  }

  private fun calculateResult(
    sugarConstant: Double,
    waterConstant: Double
  ): ResultData {
    val hives = hivesNumber.value.trim().toDouble()
    val syrup = syrupVolume.value.trim().toDouble()

    when (measurementType.value) {
      MeasurementType.LITRES -> {
        val syrupConvertedToMillis = syrup.times(1000)
        val totalMix = (hives.times(syrupConvertedToMillis))
        val sugarWeight = (sugarConstant.times(totalMix))
        val waterWeight = (waterConstant.times(totalMix))
        return ResultData(
          String.format("%.2f", sugarWeight.div(1000)),
          String.format("%.2f", waterWeight.div(1000)),
          String.format("%.2f", totalMix.div(1000))
        )
      }
      MeasurementType.MILLILITRES -> {
        val totalMix = (hives.times(syrup))
        val sugarWeight = (sugarConstant.times(totalMix)).div(1000)
        val waterWeight = (waterConstant.times(totalMix)).div(1000)
        return ResultData(
          String.format("%.2f", sugarWeight),
          String.format("%.2f", waterWeight),
          String.format("%.2f", totalMix.div(1000))
        )
      }
    }
  }
}