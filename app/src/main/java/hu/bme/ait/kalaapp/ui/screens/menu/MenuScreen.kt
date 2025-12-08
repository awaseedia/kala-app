package hu.bme.ait.kalaapp.ui.screens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import hu.bme.ait.kalaapp.R
import hu.bme.ait.kalaapp.data.model.Brand

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    onNavigateToBrand: (String) -> Unit,
    viewModel: MenuViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.menu_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                ErrorState(
                    message = uiState.error!!,
                    onRetry = { viewModel.loadBrands() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            uiState.brands.isEmpty() -> {
                EmptyBrandsState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            else -> {
                BrandsList(
                    brands = uiState.brands,
                    onBrandClick = onNavigateToBrand,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.menu_error),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRetry) {
            Text(stringResource(R.string.menu_retry))
        }
    }
}

@Composable
fun EmptyBrandsState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.menu_no_brands),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.menu_no_brands_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun BrandsList(
    brands: List<Brand>,
    onBrandClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = stringResource(R.string.menu_all_brands),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = stringResource(R.string.menu_brands_count, brands.size),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Brands list
        items(brands, key = { it.id }) { brand ->
            BrandCard(
                brand = brand,
                onClick = { onBrandClick(brand.id) }
            )
        }
    }
}

@Composable
fun BrandCard(
    brand: Brand,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Brand Image
            AsyncImage(
                model = brand.logoUrl,
                contentDescription = stringResource(R.string.cd_brand_card_image),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                androidx.compose.ui.graphics.Color.Transparent,
                                androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 100f
                        )
                    )
            )

            // Brand Info
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                // Brand Name
                Text(
                    text = brand.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = androidx.compose.ui.graphics.Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Location
                if (brand.location.isNotEmpty()) {
                    Text(
                        text = brand.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Brand Values
                if (brand.values.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        brand.values.take(3).forEach { value ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = value,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = androidx.compose.ui.graphics.Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================
// ALTERNATIVE: Grid Layout Version (Optional)
// ============================================
/*
If you want a 2-column grid instead of a list, use this:

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items

@Composable
fun BrandsGrid(
    brands: List<Brand>,
    onBrandClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(brands, key = { it.id }) { brand ->
            BrandGridCard(
                brand = brand,
                onClick = { onBrandClick(brand.id) }
            )
        }
    }
}

@Composable
fun BrandGridCard(
    brand: Brand,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Brand Image
            AsyncImage(
                model = brand.imageUrl,
                contentDescription = stringResource(R.string.cd_brand_card_image),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                androidx.compose.ui.graphics.Color.Transparent,
                                androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 80f
                        )
                    )
            )

            // Brand Name
            Text(
                text = brand.name,
                style = MaterialTheme.typography.titleMedium,
                color = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
*/