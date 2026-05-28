package com.example.myandroidapp.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.myandroidapp.R
import com.example.myandroidapp.ui.theme.ChartBlue
import com.example.myandroidapp.ui.theme.ChartGreen
import com.example.myandroidapp.ui.theme.ChartOrange
import com.example.myandroidapp.ui.theme.ChartPink
import com.example.myandroidapp.ui.theme.ChartPurple

@Composable
fun ChartDemoScreen(modifier: Modifier = Modifier) {
    val demoData = remember {
        listOf(
            1 to 30,
            2 to 25,
            3 to 20,
            4 to 15,
            5 to 10
        )
    }

    val chartColors = remember {
        listOf(
            ChartBlue,
            ChartOrange,
            ChartGreen,
            ChartPink,
            ChartPurple
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.chart_homework_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = stringResource(R.string.chart_homework_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        Box(
            modifier = Modifier.size(280.dp)
        ) {
            CustomChartView(
                sectors = demoData,
                colors = chartColors,
                gapAngle = 6f,
                thickness = 50.dp
            )
        }
    }
}