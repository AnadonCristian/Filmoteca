package com.campusdigitalfpD.filmoteca
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.campusdigitalfpD.filmoteca.MainActivity.FilmDataSource.films
import com.campusdigitalfpD.filmoteca.ui.theme.FilmotecaTheme


const val RESULT_OK = 1
const val RESULT_CANCELED = 0

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FilmotecaTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "filmList",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("filmList") {
                            FilmListScreen(
                                navigateFilm = { film ->
                                    navController.navigate("filmData/${film.id}")
                                },
                                navigateAbout = { navController.navigate("about") }
                            )
                        }
                        composable(
                            "filmData/{filmId}",
                            arguments = listOf(navArgument("filmId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val filmId = backStackEntry.arguments?.getInt("filmId") ?: 0
                            val film = FilmDataSource.films.find { it.id == filmId }
                            film?.let {
                                FilmDataScreen(
                                    filmName = it.title ?: "",
                                    director = it.director ?: "",
                                    notas = it.comments ?: "",
                                    year = it.year.toString(),
                                    genero = it.genre.toString(),
                                    formato = it.format.toString(),
                                    imdbUrl = it.imdbUrl ?: "",
                                    imageResId = it.imageResId,
                                    navigateRelated = { navController.navigate("filmData/RelatedFilmName") },
                                    navigateEdit = { navController.navigate("filmEdit") },
                                    navigateBack = {
                                        navController.navigate("filmList") {
                                            popUpTo(navController.graph.startDestinationId) {
                                                inclusive = true
                                            }
                                        }
                                    },
                                )
                            }
                        }
                        composable("filmEdit") {
                            FilmEditScreen(
                                navController = navController,
                                onSave = {
                                    navController.previousBackStackEntry?.savedStateHandle?.set(
                                        "editResult",
                                        RESULT_OK
                                    )
                                    navController.popBackStack()
                                },
                                onCancel = {
                                    navController.previousBackStackEntry?.savedStateHandle?.set(
                                        "editResult",
                                        RESULT_CANCELED
                                    )
                                    navController.popBackStack()
                                },
                                navigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("about") {
                            AboutScreen(
                                modifier = Modifier.fillMaxSize(),
                                context = this@MainActivity,
                                navigateBack = { navController.popBackStack() }
                            )
                        }


                    }
                }
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AboutScreen(modifier: Modifier, context: Context, navigateBack: () -> Unit) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Datos de Pelicula") },
                    navigationIcon = {
                        Row {
                            Box(modifier = Modifier.clickable { navigateBack() }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                    contentDescription = "Ir a pantalla principal"
                                )
                            }
                            Button(onClick = { navigateBack() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Volver"
                                )
                            }
                        }

                    }
                )
            }

        ) { innerpadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerpadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.about_screen_title),
                    modifier = Modifier
                )
                Image(
                    painter = painterResource(id = R.drawable.perfil),
                    contentDescription = stringResource(id = R.string.about_screen_title),
                    modifier = Modifier.size(160.dp)
                )
                Button(onClick = { abrirPaginaWeb(context, "https://www.campusdigitalfp.es/") }) {
                    Text(stringResource(id = R.string.button_go_to_website))
                }
                Button(onClick = {
                    mandarEmail(
                        context, "anadon_91@hotmail.com",
                        context.getString(R.string.incidencia_con_filmoteca)
                    )
                }) {
                    Text(stringResource(id = R.string.button_support))
                }
                Button(onClick = { mostrarToast(context, "Función no implementada") }) {
                    Text(stringResource(id = R.string.button_back))
                }
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FilmListScreen(
        navigateFilm: (Film) -> Unit,
        navigateAbout: () -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }
        val context = LocalContext.current
        val films = FilmDataSource.films
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Film List") },
                    actions = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                onClick = {

                                    FilmDataSource.addFilm()
                                    expanded = false
                                    mostrarToast(context, "Película añadida")
                                }
                            ) {
                                Text("Añadir película")
                            }
                            DropdownMenuItem(
                                onClick = {
                                    navigateAbout()
                                    expanded = false
                                }
                            ) {
                                Text("Acerca de")
                            }
                        }
                    }
                )
            }
        )

        { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(50.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(films) { film ->
                    FilmItem(film = film, navigateFilm = { navigateFilm(film) })
                }
                item {
                    Button(onClick = navigateAbout) {
                        Text("Acerca de")
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FilmDataScreen(
        filmName: String,
        director: String,
        notas: String,
        year: String,
        genero: String,
        formato: String,
        imdbUrl: String,
        imageResId: Int,
        navigateRelated: () -> Unit,
        navigateEdit: () -> Unit,
        navigateBack: () -> Unit,
        editResult: Int? = null
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Datos de Pelicula") },
                    navigationIcon = {
                        Row {
                            Box(modifier = Modifier.clickable { navigateBack() }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                    contentDescription = "Ir a pantalla principal"
                                )
                            }
                            Button(onClick = { navigateBack() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Volver"
                                )
                            }
                        }
                    }
                )

            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top
            )
            {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = "imagen pelicula",
                    modifier = Modifier.size(160.dp)
                )


                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    Text(text = "Nombre: $filmName")
                    Text(text = "Director: $director")
                    Text(text = "Año de estreno: $year")
                    Text(text = "Género: $genero")
                    Text(text = "Formato: $formato")
                    Text(text = "Notas: $notas")

                    when (editResult) {
                        RESULT_OK -> {
                            Text("la pelicula ha sido guardada")
                        }

                        RESULT_CANCELED -> {
                            Text("No se ha editado")
                        }
                    }
                    Button(onClick = navigateRelated) {
                        Text("Ver en IMDB")
                    }
                    Button(onClick = navigateEdit) {
                        Text("Editar pelicula")
                    }
                    Button(onClick = navigateBack) {
                        Text("Volver a pagina principal")
                    }


                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FilmEditScreen(
        navController: NavController,
        onSave: () -> Unit,
        onCancel: () -> Unit,
        navigateBack: () -> Unit,
    ) {
        var titulo by remember { mutableStateOf("") }
        var director by remember { mutableStateOf("") }
        var anyo by remember { mutableStateOf(1997) }
        var gener by remember { mutableStateOf("") }
        var comentarios by remember { mutableStateOf("") }
        var imagen by remember { mutableStateOf("") }
        var url by remember { mutableStateOf("") }

        var expandedGenero by remember { mutableStateOf(false) }
        var expandedFormato by remember { mutableStateOf(false) }

        val context = LocalContext.current
        val generoList = listOf("Action", "comedia", "drama", "terror", "ciencia ficcion")
        val formatoList = listOf("DVD", "Blu-ray", "VHS", "Digital")

        var genero by remember { mutableStateOf(0) }
        var formato by remember { mutableStateOf(1) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Editar texto") },
                    navigationIcon = {
                        Row {
                            Box(modifier = Modifier.clickable { navigateBack() }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                    contentDescription = "Ir a pantalla principal"
                                )
                            }
                            Button(onClick = { navigateBack() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Volver"
                                )
                            }


                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.sonic),
                        contentDescription = "imagen pelicula",
                        modifier = Modifier.size(160.dp)
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    )

                    {
                        Button(onClick = { /* Implementar tomar fotografía */ }) {
                            Text("Tomar una fotografía")
                        }
                        Button(onClick = { /* Implementar seleccionar imagen */ }) {
                            Text("Seleccionar una imagen")
                        }
                    }
                }
                TextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título de la película") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = director,
                    onValueChange = { director = it },
                    label = { Text("Director de la película") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = anyo.toString(),
                    onValueChange = { if (it.toIntOrNull() != null) anyo = it.toInt() },
                    label = { Text("Año de estreno") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Box {
                    TextField(
                        value = generoList[genero],
                        onValueChange = {},
                        label = { Text("Género") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                            .clickable { expandedGenero = true }
                    )
                    DropdownMenu(
                        expanded = expandedGenero,
                        onDismissRequest = { expandedGenero = false }
                    ) {
                        generoList.forEachIndexed { index, label ->
                            DropdownMenuItem(
                                onClick = {
                                    genero = index
                                    expandedGenero = false
                                }
                            ) {
                                Text(text = label)
                            }
                        }
                    }
                }
                Box {
                    TextField(
                        value = formatoList[formato],
                        onValueChange = {},
                        label = { Text("Formato") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                            .clickable { expandedFormato = true }
                    )
                    DropdownMenu(
                        expanded = expandedFormato,
                        onDismissRequest = { expandedFormato = false }
                    ) {
                        formatoList.forEachIndexed { index, label ->
                            DropdownMenuItem(
                                onClick = {
                                    formato = index
                                    expandedFormato = false
                                }
                            ) {
                                Text(text = label)
                            }
                        }
                    }
                }
                TextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Enlace a IMDB") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = comentarios,
                    onValueChange = { comentarios = it },
                    label = { Text("Comentarios") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(onClick = onSave) {
                        Text("Guardar")
                    }
                    Button(onClick = onCancel) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }

    @Composable
    fun FilmItem(film: Film, navigateFilm: (Int) -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navigateFilm(film.id) }
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = film.imageResId),
                contentDescription = film.title,
                modifier = Modifier.size(80.dp)
            )
            Column {
                Text(text = film.title ?: "", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "Director: ${film.director ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    data class Film(
        var id: Int = 0,
        var imageResId: Int = 0,
        var title: String? = null,
        var director: String? = null,
        var year: Int = 0,
        var genre: Int = 0,
        var format: Int = 0,
        var imdbUrl: String? = null,
        var comments: String? = null
    ) {
        override fun toString(): String {

            return title ?: "<Sin título>"
        }

        companion object {
            const val F = 1
            const val FORMAT_DVD = 0
            const val FORMAT_BLURAY = 1
            const val FORMAT_DIGITAL = 2
            const val GENRE_ACTION = 0
            const val GENRE_COMEDY = 1
            const val GENRE_DRAMA = 2
            const val GENRE_SCIFI = 3
            const val GENRE_HORROR = 4
        }
    }

    object FilmDataSource {
        val films: MutableList<Film> = mutableListOf()

        init {
            // Primera película: Harry Potter y la piedra filosofal
            val f1 = Film()
            f1.id = films.size
            f1.title = "Harry Potter y la piedra filosofal"
            f1.director = "Chris Columbus"
            f1.imageResId = R.drawable.harrypotter
            f1.comments = "Una aventura mágica en Hogwarts."
            f1.format = Film.FORMAT_DVD
            f1.genre = Film.GENRE_ACTION
            f1.imdbUrl = "http://www.imdb.com/title/tt0241527"
            f1.year = 2001
            films.add(f1)

            // Segunda película: Regreso al futuro
            val f2 = Film()
            f2.id = films.size
            f2.title = "Regreso al futuro"
            f2.director = "Robert Zemeckis"
            f2.imageResId = R.drawable.regresoalfuturo
            f2.comments = "Una aventura de viajes en el tiempo."
            f2.format = Film.FORMAT_DIGITAL
            f2.genre = Film.GENRE_SCIFI
            f2.imdbUrl = "http://www.imdb.com/title/tt0088763"
            f2.year = 1985
            films.add(f2)

            // Tercera película: El rey león
            val f3 = Film()
            f3.id = films.size
            f3.title = "El rey león"
            f3.director = "Roger Allers, Rob Minkoff"
            f3.imageResId = R.drawable.reyleon
            f3.comments = "Una historia de crecimiento y responsabilidad."
            f3.format = Film.FORMAT_BLURAY
            f3.genre = Film.GENRE_ACTION
            f3.imdbUrl = "http://www.imdb.com/title/tt0110357"
            f3.year = 1994
            films.add(f3)
        }
    }

    private fun DropdownMenuItem(onClick: () -> Unit, interactionSource: @Composable () -> Unit) {

    }

    fun FilmDataSource.addFilm() {
        val newFilm = Film(
            id = films.size,
            title = "Nueva Película",
            director = "Desconocido",
            year = 2025,
            genre = Film.GENRE_ACTION,
            format = Film.FORMAT_DVD,
            imdbUrl = "http://www.imdb.com",
            comments = "Sin comentarios",
            imageResId = R.drawable.sonic
        )
        films.add(newFilm)
    }

    fun mandarEmail(context: Context, email: String, asunto: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_SUBJECT, asunto)
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity((intent))
        } else {
            mostrarToast(
                mensaje = "no hay aplicacion que pueda mandar el correo",
                context = context
            )
        }
    }

    fun abrirPaginaWeb(context: Context, urls: String) {
        val intent =
            Intent(Intent.ACTION_VIEW).apply { data = Uri.parse("https://www.campusdigitalfp.es/") }
        context.startActivity(intent)

    }


    fun mostrarToast(context: Context, mensaje: String) {
        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()

    }

    fun addFilm() {
        val newFilm = Film(
            id = films.size,
            title = "Nueva Película",
            director = "Desconocido",
            year = 2025,
            genre = Film.GENRE_ACTION,
            format = Film.F
        )
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Esta aplicación está creada por Cristian",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewAboutScreen() {
        FilmotecaTheme {
            val context = LocalContext.current
            AboutScreen(modifier = Modifier, context = context, navigateBack = {})
        }
    }
}
