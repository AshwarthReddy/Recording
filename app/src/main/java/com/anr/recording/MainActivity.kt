package com.anr.recording

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.provider.CallLog
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallMissed
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anr.recording.model.CallType
import com.anr.recording.model.RegisteredCall
import com.anr.recording.ui.theme.RecordioTheme
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.TimeZone


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RecordioTheme(darkTheme = false) {
                val scaffoldState = rememberScaffoldState()
                val snackbarCoroutineScope = rememberCoroutineScope()
                val callHistory: List<RegisteredCall> = getCallHistory(applicationContext)

                Scaffold(
                    scaffoldState = scaffoldState,
                    topBar = { TopBar("Recordio") }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier.padding(innerPadding),
                        color = MaterialTheme.colors.background
                    ) {
                        CallList(callHistory) { _, call ->
                                startActivity(CallerInfoActivity.intent(this, call))
                            }
                        }
                    }
                }
            }
        }
    }


@Composable
fun TopBar(title: String, onBack: (() -> Unit)? = null) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = onBack?.let {
            {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Navigate Back")
                }
            }
        }
    )
}


@Composable
fun CallList(
    calls: List<RegisteredCall>,
    onClick: (index: Int, registeredCall: RegisteredCall) -> Unit,
) {
    val scrollState = rememberLazyListState()

    LazyColumn(state = scrollState) {
        itemsIndexed(calls) { index, recording ->
            CallItem(recording) { onClick(index, recording) }
        }
    }
}


@Composable
fun CallItem(
    registeredCall: RegisteredCall,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colors.background),
                contentAlignment = Alignment.Center
            ) {
                registeredCall.callType.Icon()
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = registeredCall.mobileNumber,
                style = MaterialTheme.typography.h6
                )
                Text(
                    text = registeredCall.duration,
                    style = MaterialTheme.typography.subtitle2
                )
            }
        }
    }
}





@Composable
fun CallType.Icon() {
    return when {
        this == CallType.INCOMING -> Icon(
            Icons.Default.CallMade,
            contentDescription = "Incoming Call",
            tint = androidx.compose.ui.graphics.Color.Green
        )
        this == CallType.OUTGOING -> Icon(
            Icons.Default.CallReceived,
            contentDescription = "Outgoing Call",
            tint = androidx.compose.ui.graphics.Color.Blue
        )
        else -> Icon(
            Icons.Default.CallMissed,
            contentDescription = "Missed Call",
            tint = androidx.compose.ui.graphics.Color.Blue
        )
    }
}


@SuppressLint("Recycle")
fun getCallHistory(ctx: Context): List<RegisteredCall> {
    var res: String? = null
    val callsHistory : ArrayList<RegisteredCall> = ArrayList()

    val query = ctx.contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, null)
    query.apply {
        val managedCursor = this;

        val number: Int = managedCursor!!.getColumnIndex(CallLog.Calls.NUMBER)
        val type: Int = managedCursor.getColumnIndex(CallLog.Calls.TYPE)
        val date: Int = managedCursor.getColumnIndex(CallLog.Calls.DATE)
        val duration: Int = managedCursor.getColumnIndex(CallLog.Calls.DURATION)
        var count: Int = 1;
        while (managedCursor.moveToNext()) {
            val phNumber: String = managedCursor.getString(number)
            val callType: String = managedCursor.getString(type)
            val callDuration: String = managedCursor.getString(duration)
            val date: Long = managedCursor.getLong(date)

            var dir: CallType? = null
            val dircode = callType.toInt()
            when (dircode) {
                CallLog.Calls.OUTGOING_TYPE -> dir = CallType.OUTGOING
                CallLog.Calls.INCOMING_TYPE -> dir = CallType.INCOMING
                CallLog.Calls.MISSED_TYPE -> dir = CallType.MISSED
            }
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(date),
                TimeZone.getDefault().toZoneId())
            if (dir != null) {
               val call: RegisteredCall = RegisteredCall(count, phNumber, dir,  LocalDateTime.ofInstant(
                   Instant.ofEpochMilli(date),
                   TimeZone.getDefault().toZoneId()).toString() );
                callsHistory.add(call)
            };
            count++
        }

    }

    return callsHistory;
}
