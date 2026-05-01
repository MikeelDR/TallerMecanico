package org.example.tallermecanico.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.example.tallermecanico.R
import org.example.tallermecanico.navigation.Screen

@Composable
fun TrabajadorRegistroAutoScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Row with Account and Home Icons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_account_icon),
                    contentDescription = "Account",
                    modifier = Modifier.size(40.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_home_icon),
                    contentDescription = "Home",
                    modifier = Modifier.size(40.dp)
                )
            }

            // Screen Title
            Text(
                text = "Consulta Vehículo",
                color = Color(0xFF1F41BB),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Subtitle
            Text(
                text = "Asigna el estado del vehículo del cliente",
                color = Color.Black,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Vehicle Details Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F4FF))
                    .padding(16.dp)
            ) {
                Column {
                    // Vehicle Name and Owner
                    Text(
                        text = "Sentra 2025 \"Ramiro Perez\"",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Vehicle Image
                    Image(
                        painter = painterResource(id = R.drawable.ic_vehicle_image),
                        contentDescription = "Vehicle",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    // Service Details
                    Text(
                        text = "Servicios:",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                    Text(
                        text = "- Cambio de aceite y filtro\n- Cambio de batería\n- Reparación de aire acondicionado",
                        color = Color.Black,
                        fontSize = 14.sp
                    )

                    // Additional Details
                    Text(
                        text = "Fecha de entrada: 05/02/2025",
                        color = Color.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    // Assign Status Button
                    Button(
                        onClick = { /* TODO: Implement status assignment logic */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1F41BB)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text(
                            text = "Asignar estado",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            // Service Number
            Text(
                text = "Número de servicio: #900243",
                color = Color.Black,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 16.dp)
            )

            // Customer Phone
            Text(
                text = "Teléfono del cliente: 81 1234567890",
                color = Color.Black,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrabajadorRegistroAutoScreenPreview() {
    TrabajadorRegistroAutoScreen(navController = rememberNavController())
}
