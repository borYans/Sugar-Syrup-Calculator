package com.boryans.sugarsyrupcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.boryans.sugarsyrupcalculator.ui.MainViewModel
import com.boryans.sugarsyrupcalculator.ui.MeasurementType
import com.boryans.sugarsyrupcalculator.ui.RatioType
import com.boryans.sugarsyrupcalculator.ui.ResultPlaceHolderText
import com.boryans.sugarsyrupcalculator.ui.ResultScreen
import com.boryans.sugarsyrupcalculator.ui.isValidHiveNumber
import com.boryans.sugarsyrupcalculator.ui.isValidSyrupVolume
import com.boryans.sugarsyrupcalculator.ui.theme.SugarSyrupCalculatorTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      SugarSyrupCalculatorTheme {
        val systemUiController = rememberSystemUiController()
        SideEffect {
          systemUiController.isSystemBarsVisible = false
          systemUiController.isStatusBarVisible = false
          systemUiController.isNavigationBarVisible = false
        }
        SugarSyrupCalculatorTheme(darkTheme = false) {

          Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
          ) {
            MainScreen()
          }
        }
      }
    }
  }
}

@Composable
fun MainScreen(
  mainViewModel: MainViewModel = viewModel()
) {
  val resultState by mainViewModel.calculationState.observeAsState()
  val scrollState = rememberScrollState()

  Column(
    modifier = Modifier
      .verticalScroll(scrollState)
      .padding(25.dp)
      .fillMaxSize(),
    verticalArrangement = Arrangement.Top,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    SyrupCalculatorTitle()
    Spacer(modifier = Modifier.height(15.dp))
    SugarWaterRatioGroup(
      onSelectedRatioChange = {
        mainViewModel.setSelectedRatioOption(it)
        mainViewModel.validateAndCalculateRatioResult()
      }
    )
    Spacer(modifier = Modifier.height(10.dp))
    SegmentedControl()
    Spacer(modifier = Modifier.height(5.dp))
    HivesNumberInput(
      onHivesNumberChange = { mainViewModel.setHivesNumber(it) }
    )
    Spacer(modifier = Modifier.height(5.dp))
    SyrupVolumeInput(
      onSyrupVolumeChange = { mainViewModel.setSyrupVolume(it) }
    )
    Spacer(modifier = Modifier.height(15.dp))
    CalculateButton()

    when (resultState) {
      is MainViewModel.CalculationState.SuccessCalculationState -> {
        val resultData =
          (resultState as MainViewModel.CalculationState.SuccessCalculationState)
        Spacer(modifier = Modifier.height(10.dp))
        ResultScreen(resultData.resultData)
      }
      is MainViewModel.CalculationState.MissingInfoState -> {
        Spacer(modifier = Modifier.height(25.dp))
        ResultPlaceHolderText()
      }
      else -> {
        Spacer(modifier = Modifier.height(25.dp))
        ResultPlaceHolderText()
      }
    }

  }
}

@Composable
fun HivesNumberInput(
  mainViewModel: MainViewModel = viewModel(),
  onHivesNumberChange: (String) -> Unit
) {
  val hiveNumber by mainViewModel.hivesNumber
  val focusManager = LocalFocusManager.current

  val validStateColor = if (hiveNumber.isValidHiveNumber()) {
    Color.Black
  } else {
    Color.Red
  }

  OutlinedTextField(
    modifier = Modifier.fillMaxWidth(),
    value = hiveNumber,
    trailingIcon = {
      Icon(
        imageVector = ImageVector.vectorResource(
          id = if (!hiveNumber.isValidHiveNumber()) {
            R.drawable.ic_hive
          } else {
            R.drawable.ic_correct_input
          }
        ),
        contentDescription = "Hive icon"
      )
    },
    keyboardOptions = KeyboardOptions(
      keyboardType = KeyboardType.Number,
      imeAction = ImeAction.Done
    ),
    colors = TextFieldDefaults.outlinedTextFieldColors(
      focusedBorderColor = validStateColor
    ),
    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
    textStyle = TextStyle(
      color = Color.Black,
      fontWeight = FontWeight.Bold,
      fontSize = 20.sp
    ),
    onValueChange = onHivesNumberChange,
    label = {
      Text(
        text = stringResource(R.string.number_of_hives_hint),
        color = Color.Black
      )
    }
  )
}

@Composable
fun SyrupVolumeInput(
  mainViewModel: MainViewModel = viewModel(),
  onSyrupVolumeChange: (String) -> Unit
) {
  val syrupVolume by mainViewModel.syrupVolume
  val measurementType by mainViewModel.measurementType
  val focusManager = LocalFocusManager.current

  val validStateColor = if (syrupVolume.isValidSyrupVolume(measurementType)) {
    Color.Black
  } else {
    Color.Red
  }

  OutlinedTextField(
    modifier = Modifier.fillMaxWidth(),
    value = syrupVolume,
    trailingIcon = {
      Icon(
        imageVector = ImageVector.vectorResource(
          id = if (!syrupVolume.isValidHiveNumber()) {
            R.drawable.ic_droplet
          } else {
            R.drawable.ic_correct_input
          }
        ),
        contentDescription = "Droplet icon"
      )
    },
    colors = TextFieldDefaults.outlinedTextFieldColors(
      focusedBorderColor = validStateColor
    ),
    keyboardOptions = KeyboardOptions(
      keyboardType = KeyboardType.Number,
      imeAction = ImeAction.Done
    ),
    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
    textStyle = TextStyle(
      color = Color.Black,
      fontWeight = FontWeight.Bold,
      fontSize = 20.sp
    ),
    onValueChange = onSyrupVolumeChange,
    label = {
      Text(
        text = if (measurementType == MeasurementType.LITRES) {
          stringResource(id = R.string.volume_of_syrup_hint_in_litres)
        } else {
          stringResource(R.string.volume_of_syrup_hint)
        },
        color = Color.Black
      )
    }
  )
}

@Composable
fun SugarWaterRatioGroup(
  mainViewModel: MainViewModel = viewModel(),
  onSelectedRatioChange: (RatioType) -> Unit
) {
  Row(
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight()
  ) {
    mainViewModel.syrupRatioOptionsList.forEach { ratioType ->
      Row(
        modifier = Modifier
          .padding(all = 2.dp)
          .weight(1f)
      ) {
        Text(
          text = stringResource(id = getRatioTypeTitle(ratioType)),
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center,
          color = Color.White,
          modifier = Modifier
            .fillMaxWidth()
            .clip(
              shape = RoundedCornerShape(size = 8.dp)
            )
            .clickable { onSelectedRatioChange(ratioType) }
            .background(
              if (ratioType == mainViewModel.selectedRatioOption.value) Color.Black else Color.LightGray
            )
            .padding(vertical = 12.dp)
        )
      }
    }
  }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CalculateButton(
  mainViewModel: MainViewModel = viewModel()
) {
  val hiveNumber by mainViewModel.hivesNumber
  val syrupVolume by mainViewModel.syrupVolume
  val measurementType by mainViewModel.measurementType

  val isValidInput =
    hiveNumber.isValidHiveNumber() && syrupVolume.isValidSyrupVolume(measurementType)

  val keyboardController = LocalSoftwareKeyboardController.current
  Button(
    modifier = Modifier.fillMaxWidth(),
    onClick = {
      mainViewModel.validateAndCalculateRatioResult()
      keyboardController?.hide()
    },
    enabled = isValidInput,
    border = BorderStroke(2.dp, if (isValidInput) Color.Black else Color.LightGray),
    colors = ButtonDefaults.outlinedButtonColors(
      contentColor = Color.White
    ),
    shape = RoundedCornerShape(8.dp)
  ) {
    Text(
      text = stringResource(R.string.calculate_button_text),
      fontSize = 25.sp,
      color = if (isValidInput) Color.Black else Color.LightGray,
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
fun SegmentedControl(
  mainViewModel: MainViewModel = viewModel()
) {
  val index = remember { mutableStateOf(0) }
  Row(
    modifier = Modifier
      .fillMaxWidth(),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .weight(1f)
        .wrapContentHeight()
        .clip(shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
        .background(
          color = if (index.value == 0) Color.Black else Color.LightGray
        )
        .clickable {
          index.value = 0
          mainViewModel.setMeasurementType(MeasurementType.LITRES)
          mainViewModel.validateAndCalculateRatioResult()
        }
        .padding(vertical = 12.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        stringResource(R.string.litres_text),
        textAlign = TextAlign.Center,
        color = Color.White,
        fontWeight = FontWeight.Bold
      )
    }
    Box(
      modifier = Modifier
        .weight(1f)
        .wrapContentHeight()
        .clip(shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
        .background(
          color = if (index.value == 1) Color.Black else Color.LightGray
        )
        .clickable {
          index.value = 1
          mainViewModel.setMeasurementType(MeasurementType.MILLILITRES)
          mainViewModel.validateAndCalculateRatioResult()
        }
        .padding(vertical = 12.dp),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        stringResource(R.string.millilitres_text),
        textAlign = TextAlign.Center,
        color = Color.White,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

@Composable
fun SyrupCalculatorTitle() {
  Text(
    text = stringResource(R.string.syrup_calculator_title),
    fontWeight = FontWeight.Bold,
    fontSize = 30.sp
  )
}

fun getRatioTypeTitle(ratioType: RatioType): Int {
  return when (ratioType) {
    RatioType.ONE_TO_ONE -> R.string.one_one_ratio
    RatioType.TWO_TO_ONE -> R.string.two_one_ratio
    RatioType.THREE_TO_TWO -> R.string.three_two_ratio
  }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
  MainScreen()
}