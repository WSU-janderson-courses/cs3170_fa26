package com.example.superheroes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.superheroes.data.HeroesRepository
import com.example.superheroes.model.Hero
import com.example.superheroes.ui.theme.SuperheroesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuperheroesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SuperheroesApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SuperheroesApp(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {SuperHeroTopAppBar()}
    ) { contentPadding ->
        HeroesList(
            heroes = HeroesRepository.heroes,
            modifier = Modifier.padding(contentPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperHeroTopAppBar(
    modifier: Modifier = Modifier,
){
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ){
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.card_height))
                        .padding(dimensionResource(R.dimen.padding_small))
                )
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        modifier = modifier,
    )
}

@Composable
fun HeroesList(
    heroes: List<Hero>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(heroes) { hero ->
            HeroCard(
                hero = hero,
                modifier = Modifier
                    .padding(
                        vertical = dimensionResource(R.dimen.padding_small),
                        horizontal = dimensionResource(R.dimen.padding_medium)
                    )
            )
        }
    }
}

@Composable
fun HeroCard(
    hero: Hero,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.card_elevation)),
        modifier = modifier,

        ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium))
                .height(72.dp)
                .fillMaxWidth()
        ) {
            // text
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(hero.nameRes),
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    text = stringResource(hero.descriptionRes),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // image
            Image(
                painter = painterResource(hero.imageRes),
                contentDescription = stringResource(hero.nameRes),
                modifier = Modifier
                    .padding(start = 16.dp)
                    .clip(MaterialTheme.shapes.small)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SuperheroesAppPreview() {
    SuperheroesTheme(
        darkTheme = false,
        dynamicColor = false,
    ) {
        SuperheroesApp()
    }
}

@Preview(showBackground = true)
@Composable
fun HeroCardPreview() {
    SuperheroesTheme {
        HeroCard(
            hero = HeroesRepository.heroes.get(0)
        )
    }
}