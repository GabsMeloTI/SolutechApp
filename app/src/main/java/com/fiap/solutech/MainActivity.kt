package com.fiap.solutech

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fiap.solutech.ui.theme.SolutechTheme
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class User(val id: String = "", val nome: String, val email: String, val senha: String)
data class Noticia(val id: String = "", val urlImage: String, val description: String)
data class User2(val nome: String)
val user2 = User2("João")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = Firebase.database

        setContent {
            SolutechTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AppNavigation(navController = navController, modifier = Modifier.weight(1f))
                        Footer()
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Menu(navController = navController)

        NavHost(navController = navController, startDestination = "home") {
            composable("home") { HomeScreen() }
            composable("currency_converter") { CurrencyConverterScreen() }
            composable("calculation") { CalculationScreen() }
            composable("about_us") { AboutUsScreen() }
            composable("edit_user") { EditUserScreen(navController) }
            composable("sign_in") { SignInScreen(navController) }
            composable("crud_user") { CRUDUserScreen(navController) }
            composable("crud_noticia") { CRUDNoticiaScreen(navController) }
        }
    }
}

@Composable
fun Footer() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Divider()
        Text(
            text = "Solutech © 2024",
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "Todos os direitos reservados",
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Menu(navController: NavHostController) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
                .padding(bottom = 20.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TextButton(onClick = { navController.navigate("home") }) { Text("Notícias") }
            TextButton(onClick = { navController.navigate("currency_converter") }) { Text("Conversor") }
            TextButton(onClick = { navController.navigate("calculation") }) { Text("Cálculos") }
            TextButton(onClick = { navController.navigate("about_us") }) { Text("Sobre Nós") }
            user2?.let {
                TextButton(onClick = { navController.navigate("edit_user") }) { Text(it.nome) }
            } ?: run {
                TextButton(onClick = { navController.navigate("sign_in") }) { Text("Entrar") }
            }
        }
    }
}


@Composable
fun CRUDUserScreen(navController: NavHostController) {
    val dbRef = FirebaseDatabase.getInstance().getReference("usuarios")
    val scope = rememberCoroutineScope()
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var userList by remember { mutableStateOf(listOf<User>()) }

    LaunchedEffect(Unit) {
        scope.launch {
            val dataSnapshot = dbRef.get().await()
            val data = dataSnapshot.children.map { snapshot ->
                snapshot.getValue(User::class.java) ?: User("", "", "", "")
            }
            userList = data
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") })
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = senha, onValueChange = { senha = it }, label = { Text("Senha") })

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            scope.launch {
                val id = dbRef.push().key ?: ""
                val user = User(id, nome, email, senha)
                dbRef.child(id).setValue(user)
            }
        }) {
            Text("Salvar Usuário")
        }

        LazyColumn {
            items(userList) { user ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = user.nome)
                    IconButton(onClick = {
                        scope.launch {
                            dbRef.child(user.id).removeValue()
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir")
                    }
                }
            }
        }
    }
}

@Composable
fun CRUDNoticiaScreen(navController: NavHostController) {
    val dbRef = FirebaseDatabase.getInstance().getReference("noticias")
    val scope = rememberCoroutineScope()
    var descricao by remember { mutableStateOf("") }
    var urlImage by remember { mutableStateOf("") }
    var noticiaList by remember { mutableStateOf(listOf<Noticia>()) }

    LaunchedEffect(Unit) {
        dbRef.get().addOnSuccessListener {
            val data = it.children.map { snapshot ->
                snapshot.getValue(Noticia::class.java) ?: Noticia("", "", "")
            }
            noticiaList = data
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(value = descricao, onValueChange = { descricao = it }, label = { Text("Descrição") })
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = urlImage, onValueChange = { urlImage = it }, label = { Text("URL da Imagem") })

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            scope.launch {
                val id = dbRef.push().key ?: ""
                val noticia = Noticia(id, urlImage, descricao)
                dbRef.child(id).setValue(noticia)
            }
        }) {
            Text("Salvar Notícia")
        }

        LazyColumn {
            items(noticiaList) { noticia ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = noticia.description)
                    IconButton(onClick = {
                        scope.launch {
                            dbRef.child(noticia.id).removeValue()
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir")
                    }
                }
            }
        }
    }
}


@Composable
fun HomeScreen() {
    val noticias = listOf(
        Noticia(id = "1", urlImage = "https://example.com/noticia1.jpg", description = "Notícia 1: Aprenda sobre investimentos."),
        Noticia(id = "2", urlImage = "https://example.com/noticia2.jpg", description = "Notícia 2: Dicas para começar a investir."),
        Noticia(id = "3", urlImage = "https://example.com/noticia3.jpg", description = "O que você precisa saber sobre o mercado financeiro.")
    )

    val currentDate = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "NOTÍCIAS DO DIA",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        Text(
            text = currentDate,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        LazyColumn {
            items(noticias) { noticia ->
                NoticiaItem(noticia)
            }
        }
    }
}

@Composable
fun NoticiaItem(noticia: Noticia) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = noticia.description,
                contentScale = ContentScale.Crop,
                modifier = Modifier.height(150.dp).fillMaxWidth()
            )
            Text(
                text = noticia.description,
                modifier = Modifier.padding(8.dp),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun CurrencyConverterScreen() {
    var valorReal by remember { mutableStateOf("") }
    var valorDolar by remember { mutableStateOf("") }
    val exchangeRate = 5.13

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Conversor de Moedas", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = valorReal,
            onValueChange = {
                valorReal = it
                val valorEmReal = it.toDoubleOrNull() ?: 0.0
                valorDolar = if (valorEmReal > 0) {
                    (valorEmReal * exchangeRate).toString()
                } else {
                    ""
                }
            },
            label = { Text("Digite o valor em Real") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = valorDolar,
            onValueChange = {},
            label = { Text("Valor em Dólar") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        )
    }
}

@Composable
fun CalculationScreen() {
    var investimentoInicial by remember { mutableStateOf("") }
    var investimentoMensal by remember { mutableStateOf("") }
    var prazo by remember { mutableStateOf("") }
    var rentabilidade by remember { mutableStateOf("") }
    var resultadoFinal by remember { mutableStateOf(0.0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Cálculo de Investimento", fontSize = 26.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = investimentoInicial,
            onValueChange = { investimentoInicial = it },
            label = { Text("Investimento Inicial") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = investimentoMensal,
            onValueChange = { investimentoMensal = it },
            label = { Text("Investimento Mensal") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = prazo,
            onValueChange = { prazo = it },
            label = { Text("Prazo (anos)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = rentabilidade,
            onValueChange = { rentabilidade = it },
            label = { Text("Rentabilidade Anual (%)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val investimentoInicialValue = investimentoInicial.toDoubleOrNull() ?: 0.0
            val investimentoMensalValue = investimentoMensal.toDoubleOrNull() ?: 0.0
            val prazoValue = prazo.toDoubleOrNull() ?: 0.0
            val rentabilidadeValue = rentabilidade.toDoubleOrNull() ?: 0.0

            resultadoFinal = investimentoInicialValue * Math.pow(1 + rentabilidadeValue / 100, prazoValue) +
                    (investimentoMensalValue * ((Math.pow(1 + rentabilidadeValue / 100, prazoValue) - 1) / (rentabilidadeValue / 100)))
        }) {
            Text(text = "Calcular")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Resultado Final: R$ ${String.format("%.2f", resultadoFinal)}", fontSize = 20.sp)
    }
}

@Composable
fun AboutUsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SOBRE NÓS",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Conheça mais sobre nossa história.",
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Text(
            text = "Bem-vindo à Solutech!",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text(
            text = "Na Solutech, estamos comprometidos em transformar a experiência de investimentos dos nossos clientes através de soluções inovadoras e personalizadas. Nossa missão é oferecer suporte abrangente e educativo para investidores de todos os níveis, desde iniciantes até os mais experientes.",
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "Imagem de investimento",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )
    }
}


@Composable
fun EditUserScreen(navController: NavHostController) {
    var nome by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Editar Usuário", fontSize = 26.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate("crud_user")
        }) {
            Text(text = "Salvar")
        }
    }
}

@Composable
fun SignInScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Entrar", fontSize = 26.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = senha, onValueChange = { senha = it }, label = { Text("Senha") })

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate("home")
        }) {
            Text(text = "Entrar")
        }
    }
}

fun saveUserData(context: Context, user: User) {
    val sharedPref = context.getSharedPreferences("user_pref", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putString("nome", user.nome)
        putString("email", user.email)
        putString("senha", user.senha)
        apply()
    }
}

fun getUserData(context: Context): User? {
    val sharedPref = context.getSharedPreferences("user_pref", Context.MODE_PRIVATE)
    val nome = sharedPref.getString("nome", null)
    val email = sharedPref.getString("email", null)
    val senha = sharedPref.getString("senha", null)
    return if (nome != null && email != null && senha != null) {
        User(nome = nome, email = email, senha = senha)
    } else null
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SolutechTheme {
        HomeScreen()
    }
}

