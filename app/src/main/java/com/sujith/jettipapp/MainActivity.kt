package com.sujith.jettipapp

import android.accessibilityservice.AccessibilityService.SoftKeyboardController
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sujith.jettipapp.components.MyInputField
import com.sujith.jettipapp.ui.theme.JetTipAppTheme
import com.sujith.jettipapp.util.calculateTotalTip
import com.sujith.jettipapp.widgets.MyRoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    JetTipAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}

//@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 125.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .height(150.dp)
            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(
                text = "Total per person",
                color = Color.Black,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "$$total",
                color = Color.Black,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Preview
@Composable
fun MainContent() {
    Column {
        TopHeader()
        BillForm() {
            Log.e("FOO", "MainContent: $it")
        }
    }

}

@Composable
fun BillForm(modifier: Modifier = Modifier, onValChange: (String) -> Unit = {}) {
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val splitState = remember {
        mutableIntStateOf(1)
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val sliderState = remember {
        mutableFloatStateOf(0f)
    }
    val tipPercentage = (sliderState.floatValue * 100.0).toInt()
    val tipAmountState = remember {
        mutableDoubleStateOf(0.0)
    }
    val splitRange = IntRange(start = 1, endInclusive = 10)
    // TopHeader()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        shape = RoundedCornerShape(CornerSize(8.dp)),
        border = BorderStroke(2.dp, Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            MyInputField(
                valueState = totalBillState,
                labelId = "Enter bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValChange(totalBillState.value.trim())
                    keyboardController?.hide()
                }
            )

            //  if (validState) {
            Row(modifier = Modifier.padding(3.dp), horizontalArrangement = Arrangement.Start) {
                Text(text = "Split", modifier = Modifier.align(Alignment.CenterVertically))
                Spacer(modifier = Modifier.width(120.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 3.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    MyRoundIconButton(
                        imageVector = Icons.Default.Remove,
                        onClick = { if (splitState.intValue > 1) splitState.intValue-- })
                    Text(
                        text = splitState.intValue.toString(),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 9.dp, end = 9.dp)
                    )
                    MyRoundIconButton(
                        imageVector = Icons.Default.Add,
                        onClick = { if (splitState.intValue < splitRange.last) splitState.intValue++ })
                }
            }
            //Tip UI
            Row(modifier = Modifier.padding(horizontal = 3.dp, vertical = 12.dp)) {
                Text(
                    text = "Tip",
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(200.dp))
                Text(
                    text = "$ ${tipAmountState.doubleValue}",
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "$tipPercentage%")
                Spacer(modifier = Modifier.height(14.dp))
                Slider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    steps = 5,
                    value = sliderState.floatValue,
                    onValueChange = { newValue ->
                        sliderState.floatValue = newValue
                        tipAmountState.doubleValue =
                            calculateTotalTip(
                                billAmount = totalBillState.value.toDouble(),
                                tipPercentage = (newValue * 100.0).toInt()
                            )
                    })
            }
//            } else {
//                Box() {}
//            }
        }
    }
}



//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    MyApp {
//        TopHeader()
//    }
//}