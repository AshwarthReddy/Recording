package com.anr.recording

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.anr.recording.model.RegisteredCall

class CallerInfoActivity : AppCompatActivity() {


    companion object{
        private const val callerId = "callerId"
        fun intent(context: Context, tvShow: RegisteredCall)=
            Intent(context,CallerInfoActivity::class.java).apply {
                putExtra(callerId,tvShow)
            }
    }
    private val tvShow : RegisteredCall by lazy {
        intent?.getSerializableExtra(callerId) as RegisteredCall
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent(){

        }
    }
}


@Composable
fun ViewMoreInfo(call: RegisteredCall) {
    val scrollState = rememberScrollState()
    Card(
        modifier = Modifier.padding(10.dp),
        shape = RoundedCornerShape(corner = CornerSize(10.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.sampleuser1),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = call.mobileNumber,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = call.callType.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Original release : ${call.duration}",
                style = MaterialTheme.typography.bodyLarge
            )
            Divider(modifier = Modifier.height(16.dp))
            Text(
                text = "call recording history-1",
                style = MaterialTheme.typography.bodyLarge
            )
            Divider(modifier = Modifier.height(16.dp))
            Text(
                text = "call recording history-2",
                style = MaterialTheme.typography.bodyLarge
            )

        }
    }
}

