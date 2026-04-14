package com.cesae.dogbreeds.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cesae.dogbreeds.data.model.Breed

@Composable
fun BreedRowItem(breed: Breed, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = breed.image?.url,
            contentDescription = breed.name,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = breed.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            breed.breedGroup?.let { group ->
                Spacer(modifier = Modifier.height(4.dp))
                GroupBadge(group = group)
            }
        }
    }
}

@Composable
fun GroupBadge(group: String) {
    val color = when {
        group.contains("Toy", ignoreCase = true) -> Color(0xFFFF69B4)
        group.contains("Herding", ignoreCase = true) -> Color(0xFF4CAF50)
        group.contains("Working", ignoreCase = true) -> Color(0xFF2196F3)
        group.contains("Sporting", ignoreCase = true) -> Color(0xFFFF9800)
        group.contains("Hound", ignoreCase = true) -> Color(0xFF795548)
        group.contains("Terrier", ignoreCase = true) -> Color(0xFFFFEB3B)
        group.contains("Non-Sporting", ignoreCase = true) -> Color(0xFF9C27B0)
        else -> Color(0xFF9E9E9E)
    }
    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            text = group,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            fontSize = 12.sp,
            color = color.copy(alpha = 0.9f)
        )
    }
}
