package id.bubakangreen.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.bubakangreen.app.ui.theme.PrimaryGreen
import id.bubakangreen.app.ui.theme.TextPrimary
import id.bubakangreen.app.ui.theme.TextSecondary

/**
 * SectionHeader: Reusable section header adhering to Section 9 typography hierarchy.
 * Section title: 20–24sp Bold. Action cue: 14sp SemiBold.
 */
@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextSecondary
                )
            }
        }

        if (!actionText.isNullOrBlank() && onActionClick != null) {
            Text(
                text = actionText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryGreen,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = false, radius = 24.dp),
                        role = Role.Button,
                        onClick = onActionClick
                    )
                    .padding(vertical = 4.dp, horizontal = 4.dp)
            )
        }
    }
}
