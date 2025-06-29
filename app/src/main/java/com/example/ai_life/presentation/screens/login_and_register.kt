package com.example.ai_life.presentation.screens

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.foundation.layout.width
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.ai_life.data.auth.FirebaseAuthRepository
import com.example.ai_life.data.user.FirebaseUserRepository
import com.example.ai_life.domain.model.User
import com.example.ai_life.domain.usecase.RegisterUserUseCase
import com.example.ai_life.domain.usecase.SaveUserUseCase
import com.example.ai_life.domain.usecase.LoginUserUseCase
import com.example.ai_life.domain.usecase.GetUserUseCase
import com.example.ai_life.R
import com.example.ai_life.presentation.screens.viewmodel.LoginViewModel
import com.example.ai_life.presentation.screens.viewmodel.RegisterViewModel
import java.util.Calendar


@Composable
fun loginScreen (navController: NavHostController, initialTab: String = "Ingresar") {
    val loginViewModel: LoginViewModel = viewModel()
    var selectedTab by remember { mutableStateOf(initialTab) }
    val scrollState = rememberScrollState()
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
            logo(modifier = Modifier.weight(3f))
            TabSelector(selectedTab = selectedTab) {
                selectedTab = it
            }
            Spacer(modifier = Modifier.height(20.dp))
            when (selectedTab) {
                "Ingresar" -> LoginForm(viewModel = loginViewModel)
                "Registrarte" -> RegisterForm()
            }
        }
    }
}

@Composable
fun logo(modifier: Modifier = Modifier) {
    Box(modifier = Modifier.fillMaxWidth().padding(start = 80.dp, end = 80.dp,top=60.dp , bottom = 30.dp)) {
        Column(modifier = Modifier.fillMaxWidth()
            , verticalArrangement = Arrangement.Top, horizontalAlignment =Alignment.CenterHorizontally ) {
            val image = painterResource(id = R.drawable.logo)
            Image(
                painter = image,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth,
                contentDescription = null,
            )
            Spacer(modifier=Modifier.padding(15.dp))
            Text(
                "AI LIVE",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1B3CAA)
            )
        }
    }
}

@Composable
fun TabSelector(selectedTab: String, onTabSelected: (String) -> Unit) {
    val backgroundColor = Color(0xFF040A7E)
    val shape = RoundedCornerShape(50)

    Row(
        modifier = Modifier
            .fillMaxWidth().padding(10.dp)
            .clip(shape)
            .background(backgroundColor)
            .height(40.dp),
    ) {
        listOf("Ingresar", "Registrarte").forEach { tab ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(tab) }
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = tab,
                    color = Color.White,
                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                )
                if (selectedTab == tab) {
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .fillMaxWidth(0.6f)
                            .background(Color(0xFFFFB400))
                    )
                }
            }
        }
    }
}

@Composable
fun LoginForm(
    viewModel: LoginViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Repos y casos de uso
    val authRepo = remember { FirebaseAuthRepository() }
    val loginUseCase = remember { LoginUserUseCase(authRepo) }
    val userRepo = remember { FirebaseUserRepository() }
    val getUserUseCase = remember { GetUserUseCase(userRepo) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        textFieldWithError(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            placeholder = "Email",
            error = viewModel.emailError,
            showError = viewModel.showErrors
        )

        Spacer(modifier = Modifier.height(16.dp))

        textFieldWithError(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            placeholder = "Contraseña",
            error = viewModel.passwordError,
            showError = viewModel.showErrors
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = viewModel.rememberMe,
                onCheckedChange = { viewModel.rememberMe = it }
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Recordar",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF040A7E)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.showErrors = true
                if (!viewModel.validate()) return@Button

                coroutineScope.launch {
                    try {
                        // 1) Login en Firebase Auth y obtener UID real
                        val uid = loginUseCase(viewModel.email, viewModel.password)
                            .getOrThrow()
                        Log.d("Login", "Auth exitoso, uid=$uid")

                        // 2) Leer perfil en RTDB con ese mismo UID
                        val user = getUserUseCase(uid)
                            .getOrThrow()
                            ?: throw Exception("No existe perfil para uid=$uid")
                        Log.d("Login", "Usuario cargado: $user")

                        Toast.makeText(
                            context,
                            "¡Bienvenido ${user.nombres}!",
                            Toast.LENGTH_LONG
                        ).show()
                        // Aquí puedes navegar a la pantalla principal

                    } catch (e: Exception) {
                        // Muestra el error real de Firebase Auth o de RTDB
                        Log.e("LoginError", "Error en login/getUser", e)
                        Toast.makeText(
                            context,
                            e.localizedMessage ?: "Error desconocido",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF040A7E),
                contentColor = Color.White
            )
        ) {
            Text("Acceder", fontSize = 17.sp)
        }
    }
}

@Composable
fun RegisterForm(
    viewModel: RegisterViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        textFieldWithError(
            value = viewModel.nombres,
            onValueChange = { viewModel.nombres = it },
            placeholder = "Nombres",
            error = viewModel.errorNombres,
            showError = viewModel.showErrors
        )
        Spacer(modifier = Modifier.height(10.dp))

        textFieldWithError(
            value = viewModel.apellidos,
            onValueChange = { viewModel.apellidos = it },
            placeholder = "Apellidos",
            error = viewModel.errorApellidos,
            showError = viewModel.showErrors
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.weight(1f)) {
                textFieldWithError(
                    value = viewModel.dni,
                    onValueChange = { viewModel.dni = it },
                    placeholder = "DNI",
                    error = viewModel.errorDni,
                    showError = viewModel.showErrors
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Box(Modifier.weight(1f)) {
                LocalidadDropdown(viewModel)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.weight(1f)) {
                FechaNacimientoPicker(viewModel)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Box(Modifier.weight(1f)) {
                SexoDropdown(viewModel)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        textFieldWithError(
            value = viewModel.correo,
            onValueChange = { viewModel.correo = it },
            placeholder = "Correo",
            error = viewModel.errorCorreo,
            showError = viewModel.showErrors
        )
        Spacer(modifier = Modifier.height(10.dp))

        textFieldWithError(
            value = viewModel.contrasena,
            onValueChange = { viewModel.contrasena = it },
            placeholder = "Contraseña",
            error = viewModel.errorContrasena,
            showError = viewModel.showErrors
        )
        Spacer(modifier = Modifier.height(16.dp))

        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current

        val authRepo = remember { FirebaseAuthRepository() }
        val userRepo = remember { FirebaseUserRepository() }
        val registerUseCase = remember { RegisterUserUseCase(authRepo) }
        val saveUserUseCase = remember { SaveUserUseCase(userRepo) }

        Button(
            onClick = {
                viewModel.showErrors = true
                if (!viewModel.validar()) return@Button

                coroutineScope.launch {
                    try {
                        // 1) Crear cuenta en Firebase Auth y obtener UID
                        val uid = registerUseCase(
                            User(
                                uid             = "",                     // Importante: inicializado vacío
                                nombres         = viewModel.nombres,
                                apellidos       = viewModel.apellidos,
                                dni             = viewModel.dni,
                                localidad       = viewModel.localidad,
                                fechaNacimiento = viewModel.fechaNacimiento,
                                sexo            = viewModel.sexo,
                                email           = viewModel.correo
                            ),
                            viewModel.contrasena
                        ).getOrThrow()

                        Log.d("Register", "Auth creado con uid=$uid")

                        // 2) Guardar perfil en RTDB bajo ese mismo UID
                        saveUserUseCase(
                            User(
                                uid             = uid,
                                nombres         = viewModel.nombres,
                                apellidos       = viewModel.apellidos,
                                dni             = viewModel.dni,
                                localidad       = viewModel.localidad,
                                fechaNacimiento = viewModel.fechaNacimiento,
                                sexo            = viewModel.sexo,
                                email           = viewModel.correo
                            )
                        ).getOrThrow()

                        Log.d("Register", "Perfil guardado en RTDB")
                        Toast.makeText(context, "Registro exitoso", Toast.LENGTH_LONG).show()

                    } catch (e: Exception) {
                        Log.e("RegisterError", "Fallo al registrar", e)
                        Toast.makeText(
                            context,
                            e.localizedMessage ?: "Error desconocido al registrar",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF040A7E))
        ) {
            Text("Registrarse", color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun textFieldWithError(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String,
    showError: Boolean
) {
    Column {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Black) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF1F3FF)),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = TextStyle(color = Color.Black),
            singleLine = true
        )
        if (showError && error.isNotEmpty()) {
            Text(text = error, color = Color.Red, fontSize = 12.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SexoDropdown(viewModel: RegisterViewModel) {
    val options = listOf("Masculino", "Femenino", "Otro")
    var expanded by remember { mutableStateOf(false) }
    val isError = viewModel.showErrors && viewModel.errorSexo.isNotEmpty()

    Column(modifier = Modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = viewModel.sexo,
                onValueChange = {},
                readOnly = true,
                isError = isError,
                label = { Text("Sexo", color = Color.Black) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F3FF)),
                colors = TextFieldDefaults.textFieldColors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Black,
                    errorTextColor = Color.Black,
                    containerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedIndicatorColor = if (isError) Color.Red else Color.Transparent,
                    unfocusedIndicatorColor = if (isError) Color.Red else Color.Transparent,
                    errorIndicatorColor = Color.Red,
                    disabledIndicatorColor = Color.Transparent,
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            viewModel.sexo = option
                            expanded = false
                        }
                    )
                }
            }
        }

        if (isError) {
            Text(
                text = viewModel.errorSexo,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalidadDropdown(viewModel: RegisterViewModel) {
    val localidades = listOf(
        "Amazonas", "Áncash", "Apurímac", "Arequipa", "Ayacucho", "Cajamarca",
        "Callao", "Cusco", "Huancavelica", "Huánuco", "Ica", "Junín", "La Libertad",
        "Lambayeque", "Lima", "Loreto", "Madre de Dios", "Moquegua", "Pasco", "Piura",
        "Puno", "San Martín", "Tacna", "Tumbes", "Ucayali", "Otro"
    )

    var expanded by remember { mutableStateOf(false) }
    val isError = viewModel.showErrors && viewModel.errorLocalidad.isNotEmpty()

    Column(modifier = Modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = viewModel.localidad,
                onValueChange = {},
                readOnly = true,
                isError = isError,
                label = { Text("Localidad", color = Color.Black) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F3FF)),
                colors = TextFieldDefaults.textFieldColors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Black,
                    errorTextColor = Color.Black,
                    containerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedIndicatorColor = if (isError) Color.Red else Color.Transparent,
                    unfocusedIndicatorColor = if (isError) Color.Red else Color.Transparent,
                    errorIndicatorColor = Color.Red,
                    disabledIndicatorColor = Color.Transparent,

                    )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                localidades.forEach { ciudad ->
                    DropdownMenuItem(
                        text = { Text(ciudad) },
                        onClick = {
                            viewModel.localidad = ciudad
                            expanded = false
                        }
                    )
                }
            }
        }

        if (isError) {
            Text(
                text = viewModel.errorLocalidad,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FechaNacimientoPicker(viewModel: RegisterViewModel) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val interactionSource = remember { MutableInteractionSource() }

    val isError = viewModel.showErrors && viewModel.errorFechaNacimiento.isNotEmpty()

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = interactionSource
                ) {
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            viewModel.fechaNacimiento = "$day/${month + 1}/$year"
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
        ) {
            OutlinedTextField(
                value = viewModel.fechaNacimiento,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                isError = isError,
                label = { Text("F.N.", color = Color.Black) },
                trailingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F3FF)),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = if (isError) Color.Red else Color.Transparent,
                    unfocusedIndicatorColor = if (isError) Color.Red else Color.Transparent,
                    errorIndicatorColor = Color.Red,
                    disabledIndicatorColor = Color.Transparent,
                    disabledTextColor = Color.Black
                )
            )
        }

        if (isError) {
            Text(
                text = viewModel.errorFechaNacimiento,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}



@Preview(showBackground = true, name = "Vista Ingresar")
@Composable
fun PreviewLogin() {
    val navController = rememberNavController()
    loginScreen(navController = navController, initialTab = "Ingresar")
}

@Preview(showBackground = true, name = "Vista Registrarte",heightDp = 1050)
@Composable
fun PreviewRegister() {
    val navController = rememberNavController()
    loginScreen(navController = navController, initialTab = "Registrarte")
}