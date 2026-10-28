package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.data.auth.QuickBiteAuthManager

@Composable
fun GoogleSignInButton(
    modifier: Modifier = Modifier,
    text: String = "Sign in with Google",
    isOutlined: Boolean = false,
    onAuthSuccess: () -> Unit,
    onAuthError: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var isLoading by remember { mutableStateOf(false) }

    if (isOutlined) {
        OutlinedButton(
            onClick = {
                isLoading = true
                QuickBiteAuthManager.onGoogleSignInClicked(
                    context = context,
                    credentialManager = credentialManager,
                    onAuthSuccess = {
                        isLoading = false
                        onAuthSuccess()
                    },
                    onAuthError = { errorMsg ->
                        isLoading = false
                        onAuthError(errorMsg)
                    },
                    scope = coroutineScope,
                    onAuthCancelled = { isLoading = false }
                )
            },
            enabled = !isLoading,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("google_signin_button")
        ) {
            SignInButtonContent(isLoading = isLoading, text = text)
        }
    } else {
        Button(
            onClick = {
                isLoading = true
                QuickBiteAuthManager.onGoogleSignInClicked(
                    context = context,
                    credentialManager = credentialManager,
                    onAuthSuccess = {
                        isLoading = false
                        onAuthSuccess()
                    },
                    onAuthError = { errorMsg ->
                        isLoading = false
                        onAuthError(errorMsg)
                    },
                    scope = coroutineScope,
                    onAuthCancelled = { isLoading = false }
                )
            },
            enabled = !isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("google_signin_button")
        ) {
            SignInButtonContent(isLoading = isLoading, text = text)
        }
    }
}

@Composable
private fun SignInButtonContent(
    isLoading: Boolean,
    text: String
) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.size(22.dp),
            strokeWidth = 2.dp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Signing in...")
    } else {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Google Account",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
