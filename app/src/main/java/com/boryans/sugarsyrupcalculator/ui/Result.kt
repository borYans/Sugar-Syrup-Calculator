package com.boryans.sugarsyrupcalculator.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.boryans.sugarsyrupcalculator.R

@Composable
fun ResultScreen(result: ResultData) {
  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    ResultOutputField(result = result.waterMass, title = stringResource(R.string.water_result_text))
    ResultOutputField(result = result.sugarMass, title = stringResource(R.string.sugar_result_text))
    ResultOutputField(result = result.totalMixMass, title = stringResource(R.string.total_syrup_result_text))
  }
}


@Composable
fun ResultOutputField(
  title: String,
  result: String
) {
  OutlinedTextField(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 12.dp),
    value = result,
    textStyle = TextStyle(
      color = Color.Black,
      fontSize = 20.sp,
      fontWeight = FontWeight.ExtraBold,
    ),
    enabled = false,
    onValueChange = { },
    label = {
      Text(
        text = title,
        color = Color.Black,
        fontWeight = FontWeight.Bold
      )
    }
  )
}

@Composable
fun ResultPlaceHolderText() {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = stringResource(R.string.result_placeholder),
      fontStyle = FontStyle.Italic,
      color = Color.DarkGray,
      fontSize = 20.sp,
      maxLines = 2
    )
  }
}